package com.habit.gold.feature.delivery.presentation

import androidx.lifecycle.viewModelScope
import com.habit.gold.core.network.fold
import com.habit.gold.core.presentation.mvi.MviViewModel
import com.habit.gold.core.session.SessionStore
import com.habit.gold.feature.delivery.data.DeliveryCheckoutTelemetry
import com.habit.gold.feature.delivery.data.PendingDeliveryCheckout
import com.habit.gold.feature.delivery.data.PendingDeliveryCheckoutStage
import com.habit.gold.feature.delivery.data.PendingDeliveryCheckoutStore
import com.habit.gold.feature.delivery.domain.model.ConfirmDeliveryOrderDto
import com.habit.gold.feature.delivery.domain.model.CreateDeliveryQuoteDto
import com.habit.gold.feature.delivery.domain.model.DeliveryCheckoutPhase
import com.habit.gold.feature.delivery.domain.model.DeliveryCheckoutQuote
import com.habit.gold.feature.delivery.domain.model.DeliveryOrderDto
import com.habit.gold.feature.delivery.domain.model.DeliveryPaymentLaunchResult
import com.habit.gold.feature.delivery.domain.model.PhysicalCoin
import com.habit.gold.feature.delivery.domain.usecase.ConfirmDeliveryOrderUseCase
import com.habit.gold.feature.delivery.domain.usecase.CreateDeliveryQuoteUseCase
import com.habit.gold.feature.delivery.domain.usecase.GetDeliveryOrderDetailsUseCase
import com.habit.gold.feature.delivery.domain.usecase.GetDeliveryProductsUseCase
import com.habit.gold.feature.trade.domain.usecase.GetSellAvailabilityUseCase
import com.habit.gold.feature.trade.domain.usecase.GetTradeAvailableCouponsUseCase
import com.habit.gold.feature.trade.domain.usecase.ValidateTradeCouponUseCase
import com.habit.gold.feature.trade.domain.model.TradeCouponOrderType
import com.habit.gold.feature.trade.domain.model.TradeCouponValidationRequest
import com.habit.gold.feature.trade.domain.model.TradeCouponType
import com.habit.gold.core.network.ApiResult
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.delivery_catalog_coupon_applied
import habitgoldmobile.composeapp.generated.resources.delivery_catalog_coupon_invalid
import habitgoldmobile.composeapp.generated.resources.delivery_catalog_coupon_only_valid
import habitgoldmobile.composeapp.generated.resources.delivery_checkout_missing_order_id
import habitgoldmobile.composeapp.generated.resources.delivery_checkout_payment_cancelled
import habitgoldmobile.composeapp.generated.resources.delivery_checkout_payment_failed
import habitgoldmobile.composeapp.generated.resources.delivery_checkout_quote_expired
import habitgoldmobile.composeapp.generated.resources.delivery_checkout_quote_unavailable
import habitgoldmobile.composeapp.generated.resources.delivery_checkout_select_address_coin
import habitgoldmobile.composeapp.generated.resources.delivery_checkout_timeout
import habitgoldmobile.composeapp.generated.resources.delivery_error_confirm_order
import habitgoldmobile.composeapp.generated.resources.delivery_error_create_quote
import habitgoldmobile.composeapp.generated.resources.delivery_error_load_products
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlin.time.Clock

class DeliveryCatalogViewModel(
    private val getDeliveryProductsUseCase: GetDeliveryProductsUseCase,
    private val createDeliveryQuoteUseCase: CreateDeliveryQuoteUseCase,
    private val confirmDeliveryOrderUseCase: ConfirmDeliveryOrderUseCase,
    private val pendingDeliveryCheckoutStore: PendingDeliveryCheckoutStore,
    private val deliveryCheckoutTelemetry: DeliveryCheckoutTelemetry,
    private val getSellAvailabilityUseCase: GetSellAvailabilityUseCase,
    private val getDeliveryOrderDetailsUseCase: GetDeliveryOrderDetailsUseCase,
    private val getTradeAvailableCouponsUseCase: GetTradeAvailableCouponsUseCase,
    private val validateTradeCouponUseCase: ValidateTradeCouponUseCase,
    private val sessionStore: SessionStore,
) : MviViewModel<DeliveryCatalogState, DeliveryIntent, DeliveryEffect>(DeliveryCatalogState()) {

    private var activeConfirmIdempotencyKey: String? = null

    init {
        restorePendingCheckout()
        onIntent(DeliveryIntent.RefreshGoldBalance)
        onIntent(DeliveryIntent.LoadProducts)
    }

    override fun onIntent(intent: DeliveryIntent) {
        when (intent) {
            is DeliveryIntent.LoadProducts -> loadProducts()
            is DeliveryIntent.UpdateQuantity -> updateQuantity(intent.coinId, intent.delta)
            is DeliveryIntent.SelectAddress -> selectAddress(intent.id)
            is DeliveryIntent.RefreshAddresses -> Unit
            is DeliveryIntent.RefreshGoldBalance -> refreshGoldBalance()
            is DeliveryIntent.PrepareQuote -> prepareQuote(intent.couponCode)
            is DeliveryIntent.ConfirmOrder -> confirmOrder()
            is DeliveryIntent.HandlePaymentResult -> handlePaymentResult(intent.result)
            is DeliveryIntent.DiscardCheckout -> discardCheckout()
            is DeliveryIntent.ClearError -> Unit
            is DeliveryIntent.ApplyCoupon -> applyCoupon(intent.code)
            is DeliveryIntent.RemoveCoupon -> removeCoupon()
        }
    }

    private fun loadProducts() {
        viewModelScope.launch {
            updateState { it.copy(isLoadingProducts = true) }
            getDeliveryProductsUseCase().fold(
                onSuccess = { payload ->
                    val products = parseDeliveryProducts(payload)
                    updateState {
                        it.copy(
                            isLoadingProducts = false,
                            coins = applyDeliveryProductFilter(products),
                        )
                    }
                    loadAvailableCoupons()
                },
                onFailure = { error ->
                    updateState { it.copy(isLoadingProducts = false) }
                    emitEffect(
                        DeliveryEffect.ShowError(
                            error.message?.asDeliveryUiText()
                                ?: DeliveryUiText.Resource(Res.string.delivery_error_load_products)
                        )
                    )
                }
            )
        }
    }

    private fun applyDeliveryProductFilter(products: List<PhysicalCoin>): List<PhysicalCoin> {
        val liveBuyPrice = state.value.liveBuyPricePerGram
        return products.filter {
            it.estimatedUnitSubtotalInr(liveBuyPrice) <= MAX_PRODUCT_UNIT_ESTIMATE_INR
        }
    }

    private fun loadAvailableCoupons() {
        val currentState = state.value
        val coins = currentState.coins
        val cartItems = currentState.cartItems

        val selectedCoin = coins.find { cartItems.containsKey(it.id) }
        val grams = selectedCoin?.weightGm
        val amount = cartItems.mapNotNull { (id, qty) ->
            coins.find { it.id == id }?.let { it.makingCharge * qty }
        }.sum().takeIf { it > 0.0 }
        
        val deliveryFee = 100.0 // Standard delivery charge

        viewModelScope.launch {
            when (val result = getTradeAvailableCouponsUseCase(
                orderType = TradeCouponOrderType.DELIVERY,
                amount = amount,
                grams = grams,
                deliveryFeeInr = deliveryFee
            )) {
                is ApiResult.Success -> {
                    updateState { it.copy(availableCoupons = result.value) }
                    autoApplyDeliveryCouponIfEligible()
                }
                is ApiResult.Failure -> {
                    // Fail silently for preloaded list
                }
            }
        }
    }

    private fun updateQuantity(coinId: String, delta: Int) {
        updateState { currentState ->
            val currentQty = currentState.cartItems[coinId] ?: 0
            val newQty = (currentQty + delta).coerceIn(0, 1)
            val newCart = if (newQty > 0) mapOf(coinId to newQty) else emptyMap()
            currentState.copy(cartItems = newCart)
        }

        // Cart changed -> reload available coupons with correct coin parameters
        loadAvailableCoupons()

        // If the cart is now empty, clear the coupon
        if (state.value.cartItems.isEmpty()) {
            updateState { it.copy(couponCode = null, couponDiscountInr = 0.0, couponType = null) }
        } else {
            // Re-validate or auto-apply coupon if cart changed
            val currentCoupon = state.value.couponCode
            if (currentCoupon != null) {
                // Re-validate existing coupon for new quantity/weight
                viewModelScope.launch {
                    val coins = state.value.coins
                    val cartItems = state.value.cartItems
                    val subtotalMakingCharge = cartItems.mapNotNull { (id, qty) ->
                        coins.find { it.id == id }?.let { it.makingCharge * qty }
                    }.sum()
                    val totalWeight = state.value.totalWeightGm

                    val request = TradeCouponValidationRequest(
                        orderType = TradeCouponOrderType.DELIVERY,
                        code = currentCoupon,
                        deliveryFeeInr = subtotalMakingCharge,
                        deliveryGrams = totalWeight,
                    )
                    when (val result = validateTradeCouponUseCase(request)) {
                        is ApiResult.Success -> {
                            val validation = result.value
                            val discount = validation.promotionalDeliveryDiscount.toDoubleOrNull() ?: 0.0
                            updateState { it.copy(couponDiscountInr = discount) }
                        }
                        is ApiResult.Failure -> {
                            // If it's no longer valid, remove it silently
                            updateState { it.copy(couponCode = null, couponDiscountInr = 0.0, couponType = null) }
                        }
                    }
                }
            } else {
                autoApplyDeliveryCouponIfEligible()
            }
        }
    }

    private fun refreshGoldBalance() {
        viewModelScope.launch {
            updateState { it.copy(isRefreshingBalance = true) }
            getSellAvailabilityUseCase().fold(
                onSuccess = { availability ->
                    updateState {
                        it.copy(
                            isRefreshingBalance = false,
                            totalGoldBalanceGrams = availability.totalGoldBalanceGrams,
                            redeemableGoldGrams = availability.sellableGoldBalanceGrams,
                        )
                    }
                },
                onFailure = {
                    updateState { it.copy(isRefreshingBalance = false) }
                }
            )
        }
    }

    private fun selectAddress(id: String) {
        updateState { it.copy(selectedAddressId = id) }
    }

    /**
     * Secures a pricing quote from the server for the active checkout parameters.
     * Validates that both a shipping address and a coin selection are present before dispatch.
     */
    private fun prepareQuote(couponCode: String?) {
        viewModelScope.launch {
            val addressId = state.value.selectedAddressId
            val cartItem = state.value.cartItems.entries.firstOrNull()
            if (addressId == null || cartItem == null) {
                emitEffect(
                    DeliveryEffect.ShowError(
                        DeliveryUiText.Resource(Res.string.delivery_checkout_select_address_coin)
                    )
                )
                return@launch
            }

            val effectiveCouponCode = couponCode ?: state.value.couponCode
            val current = state.value.checkoutQuote
            val now = Clock.System.now().toEpochMilliseconds()
            if (
                current != null &&
                current.addressId == addressId &&
                current.productId == cartItem.key &&
                !current.isExpired(now)
            ) {
                updateState { it.copy(checkoutPhase = DeliveryCheckoutPhase.REVIEW_READY) }
                emitEffect(DeliveryEffect.NavigateToCheckout)
                return@launch
            }

            updateState {
                it.copy(
                    isCheckingOut = true,
                    checkoutPhase = DeliveryCheckoutPhase.CONFIRMING,
                )
            }

            createDeliveryQuoteUseCase(
                CreateDeliveryQuoteDto(
                    addressId = addressId,
                    productId = cartItem.key,
                    couponCode = effectiveCouponCode,
                )
            ).fold(
                onSuccess = { response ->
                    activeConfirmIdempotencyKey = null
                    val quote = response.toUiQuote(cartItem.key, addressId)
                    val couponDiscount = (quote.mintingChargeInr - quote.payableChargeInr).coerceAtLeast(0.0)
                    
                    val coins = state.value.coins
                    val cartItems = state.value.cartItems
                    val subtotalMakingCharge = cartItems.mapNotNull { (id, qty) ->
                        coins.find { it.id == id }?.let { it.makingCharge * qty }
                    }.sum()

                    val couponType = state.value.availableCoupons.find { it.code.equals(effectiveCouponCode, ignoreCase = true) }?.type
                        ?: if (couponDiscount >= subtotalMakingCharge && subtotalMakingCharge > 0.0) TradeCouponType.FREE_DELIVERY else TradeCouponType.DELIVERY_DISCOUNT

                    updateState {
                        it.copy(
                            isCheckingOut = false,
                            checkoutQuote = quote,
                            couponCode = effectiveCouponCode,
                            couponDiscountInr = couponDiscount,
                            couponType = couponType,
                            checkoutPhase = DeliveryCheckoutPhase.REVIEW_READY,
                        )
                    }
                    persistPendingCheckout(
                        quote.toPendingCheckout(
                            couponCode = effectiveCouponCode,
                            stage = PendingDeliveryCheckoutStage.REVIEW,
                        )
                    )
                    deliveryCheckoutTelemetry.quoteCreated(quote.quoteId, cartItem.key, addressId)
                    emitEffect(DeliveryEffect.NavigateToCheckout)
                },
                onFailure = { error ->
                    updateState {
                        it.copy(
                            isCheckingOut = false,
                            checkoutPhase = DeliveryCheckoutPhase.IDLE,
                        )
                    }
                    emitEffect(
                        DeliveryEffect.ShowError(
                            error.message?.asDeliveryUiText()
                                ?: DeliveryUiText.Resource(Res.string.delivery_error_create_quote)
                        )
                    )
                }
            )
        }
    }

    /**
     * Initiates the checkout payment sequence with the gateway.
     * Prevents duplicate execution by checking the active transaction phase.
     */
    private fun confirmOrder() {
        val currentState = state.value
        if (currentState.isCheckingOut) return

        val phase = currentState.checkoutPhase
        if (
            phase == DeliveryCheckoutPhase.PAYMENT_LAUNCH_READY ||
            phase == DeliveryCheckoutPhase.PAYMENT_IN_PROGRESS ||
            phase == DeliveryCheckoutPhase.VERIFYING_ORDER
        ) {
            return
        }

        val quote = currentState.checkoutQuote
        if (quote == null) {
            viewModelScope.launch {
                emitEffect(
                    DeliveryEffect.ShowError(
                        DeliveryUiText.Resource(Res.string.delivery_checkout_quote_unavailable)
                    )
                )
            }
            return
        }

        val now = Clock.System.now().toEpochMilliseconds()
        if (quote.isExpired(now)) {
            viewModelScope.launch {
                emitEffect(
                    DeliveryEffect.ShowError(
                        DeliveryUiText.Resource(Res.string.delivery_checkout_quote_expired)
                    )
                )
            }
            return
        }

        val idempotencyKey = activeConfirmIdempotencyKey ?: generateIdempotencyKey().also {
            activeConfirmIdempotencyKey = it
        }

        updateState { it.copy(isCheckingOut = true) }
        viewModelScope.launch {
            confirmDeliveryOrderUseCase(
                idempotencyKey = idempotencyKey,
                body = ConfirmDeliveryOrderDto(quoteId = quote.quoteId),
            ).fold(
                onSuccess = { response ->
                    deliveryCheckoutTelemetry.confirmStarted(quote.quoteId, idempotencyKey)
                    val payload = response.sdkPayload
                    if (payload != null) {
                        updateState {
                            it.copy(
                                isCheckingOut = false,
                                pendingPaymentSdkPayloadJson = payload.toString(),
                                checkoutPhase = DeliveryCheckoutPhase.PAYMENT_LAUNCH_READY,
                            )
                        }
                        persistPendingCheckout(
                            quote.toPendingCheckout(
                                couponCode = state.value.couponCode,
                                stage = PendingDeliveryCheckoutStage.SDK_LAUNCH_READY,
                                confirmIdempotencyKey = idempotencyKey,
                                orderId = response.order.id,
                                sdkPayload = payload,
                            )
                        )
                        emitEffect(DeliveryEffect.LaunchPaymentSdk(payload.toString()))
                    } else {
                        completeCheckout(response.order)
                    }
                },
                onFailure = { error ->
                    updateState { it.copy(isCheckingOut = false) }
                    emitEffect(
                        DeliveryEffect.ShowError(
                            error.message?.asDeliveryUiText()
                                ?: DeliveryUiText.Resource(Res.string.delivery_error_confirm_order)
                        )
                    )
                }
            )
        }
    }

    private fun handlePaymentResult(result: DeliveryPaymentLaunchResult) {
        viewModelScope.launch {
            val pending = pendingDeliveryCheckoutStore.pendingCheckout.firstOrNull()
            val actualOrderId = pending?.orderId
            when (result) {
                is DeliveryPaymentLaunchResult.Success -> {
                    if (actualOrderId == null) {
                        emitEffect(
                            DeliveryEffect.ShowError(
                                DeliveryUiText.Resource(Res.string.delivery_checkout_missing_order_id)
                            )
                        )
                        return@launch
                    }
                    updateState { it.copy(checkoutPhase = DeliveryCheckoutPhase.VERIFYING_ORDER) }
                    pollOrderUntilTerminal(actualOrderId)
                }

                is DeliveryPaymentLaunchResult.Failure -> {
                    if (result.shouldPollOrderStatus) {
                        if (actualOrderId == null) {
                            emitEffect(
                                DeliveryEffect.ShowError(
                                    DeliveryUiText.Resource(Res.string.delivery_checkout_missing_order_id)
                                )
                            )
                            return@launch
                        }
                        updateState { it.copy(checkoutPhase = DeliveryCheckoutPhase.VERIFYING_ORDER) }
                        pollOrderUntilTerminal(actualOrderId)
                    } else {
                        updateState {
                            it.copy(
                                isCheckingOut = false,
                                pendingPaymentSdkPayloadJson = null,
                                checkoutPhase = DeliveryCheckoutPhase.REVIEW_READY,
                            )
                        }
                        state.value.checkoutQuote?.let { quote ->
                            persistPendingCheckout(
                                quote.toPendingCheckout(
                                    couponCode = state.value.couponCode,
                                    confirmIdempotencyKey = activeConfirmIdempotencyKey,
                                    orderId = actualOrderId,
                                    stage = PendingDeliveryCheckoutStage.REVIEW,
                                )
                            )
                        }
                        emitEffect(DeliveryEffect.ShowError(result.message.asDeliveryUiText()))
                    }
                }

                DeliveryPaymentLaunchResult.BackPressed -> {
                    updateState {
                        it.copy(
                            isCheckingOut = false,
                            pendingPaymentSdkPayloadJson = null,
                            checkoutPhase = DeliveryCheckoutPhase.REVIEW_READY,
                        )
                    }
                    state.value.checkoutQuote?.let { quote ->
                        persistPendingCheckout(
                            quote.toPendingCheckout(
                                couponCode = state.value.couponCode,
                                confirmIdempotencyKey = activeConfirmIdempotencyKey,
                                orderId = actualOrderId,
                                stage = PendingDeliveryCheckoutStage.REVIEW,
                            )
                        )
                    }
                    emitEffect(
                        DeliveryEffect.ShowToast(
                            DeliveryUiText.Resource(Res.string.delivery_checkout_payment_cancelled)
                        )
                    )
                }
            }
        }
    }

    /**
     * Polls the order service periodically to confirm payment success.
     * Stops immediately when a terminal status (success or fatal failure) is resolved.
     */
    private fun pollOrderUntilTerminal(orderId: String) {
        viewModelScope.launch {
            repeat(MAX_ORDER_STATUS_POLL_ATTEMPTS) {
                getDeliveryOrderDetailsUseCase(orderId).fold(
                    onSuccess = { order ->
                        when (order.resolveCheckoutState()) {
                            CheckoutOrderState.SUCCESS -> {
                                completeCheckout(order)
                                return@launch
                            }

                            CheckoutOrderState.RECOVERABLE_PENDING -> Unit

                            CheckoutOrderState.FAILURE -> {
                                activeConfirmIdempotencyKey = null
                                persistPendingCheckout(null)
                                updateState {
                                    it.copy(
                                        isCheckingOut = false,
                                        checkoutPhase = DeliveryCheckoutPhase.REVIEW_READY,
                                        pendingPaymentSdkPayloadJson = null,
                                    )
                                }
                                emitEffect(
                                    DeliveryEffect.ShowError(
                                        DeliveryUiText.Resource(Res.string.delivery_checkout_payment_failed)
                                    )
                                )
                                return@launch
                            }
                        }
                    },
                    onFailure = {
                        Unit
                    }
                )
                delay(ORDER_STATUS_POLL_INTERVAL_MS)
            }

            updateState {
                it.copy(
                    isCheckingOut = false,
                    checkoutPhase = DeliveryCheckoutPhase.REVIEW_READY,
                )
            }
            emitEffect(
                DeliveryEffect.ShowError(
                    DeliveryUiText.Resource(Res.string.delivery_checkout_timeout)
                )
            )
        }
    }

    private suspend fun completeCheckout(order: DeliveryOrderDto) {
        activeConfirmIdempotencyKey = null
        persistPendingCheckout(null)
        val id = order.id.orEmpty()
        updateState {
            it.copy(
                isCheckingOut = false,
                checkoutPhase = DeliveryCheckoutPhase.COMPLETED,
                cartItems = emptyMap(),
                pendingPaymentSdkPayloadJson = null,
                couponCode = null,
                couponDiscountInr = 0.0,
                couponType = null,
                hasManuallyRemovedCoupon = false,
            )
        }
        deliveryCheckoutTelemetry.finalOrderState(id, order.paymentStatus, order.status)
        emitEffect(DeliveryEffect.NavigateToOrderSummary(id))
    }

    private fun discardCheckout() {
        activeConfirmIdempotencyKey = null
        persistPendingCheckout(null)
        updateState {
            it.copy(
                checkoutQuote = null,
                checkoutPhase = DeliveryCheckoutPhase.IDLE,
                pendingPaymentSdkPayloadJson = null,
            )
        }
    }

    private fun persistPendingCheckout(checkout: PendingDeliveryCheckout?) {
        viewModelScope.launch {
            if (checkout != null) pendingDeliveryCheckoutStore.save(checkout) else pendingDeliveryCheckoutStore.clear()
        }
    }

    /**
     * Re-establishes a pending checkout state after process interruption.
     * Validates quote expiration timestamps before restoring the state.
     */
    private fun restorePendingCheckout() {
        viewModelScope.launch {
            pendingDeliveryCheckoutStore.pendingCheckout.collect { pending ->
                if (pending == null) return@collect

                val now = Clock.System.now().toEpochMilliseconds()
                if (pending.isExpired(now)) {
                    pendingDeliveryCheckoutStore.clear()
                    return@collect
                }

                val mintingCharge = pending.verifyQuote.mintingChargeInr.toDoubleOrNull().orZero()
                val payableCharge = pending.verifyQuote.payableChargeInr.toDoubleOrNull().orZero()
                val couponDiscount = (mintingCharge - payableCharge).coerceAtLeast(0.0)

                updateState {
                    it.copy(
                        checkoutQuote = pending.toUiQuote(),
                        cartItems = mapOf(pending.productId to 1),
                        selectedAddressId = pending.addressId,
                        couponCode = pending.couponCode,
                        couponDiscountInr = couponDiscount,
                        couponType = if (couponDiscount >= mintingCharge && mintingCharge > 0.0) {
                            TradeCouponType.FREE_DELIVERY
                        } else {
                            TradeCouponType.DELIVERY_DISCOUNT
                        },
                        checkoutPhase = when (pending.stage) {
                            PendingDeliveryCheckoutStage.REVIEW -> DeliveryCheckoutPhase.REVIEW_READY
                            PendingDeliveryCheckoutStage.CONFIRM_IN_FLIGHT -> DeliveryCheckoutPhase.CONFIRMING
                            PendingDeliveryCheckoutStage.SDK_LAUNCH_READY -> DeliveryCheckoutPhase.PAYMENT_LAUNCH_READY
                            PendingDeliveryCheckoutStage.SDK_OPENED -> DeliveryCheckoutPhase.PAYMENT_IN_PROGRESS
                            PendingDeliveryCheckoutStage.VERIFYING_ORDER -> DeliveryCheckoutPhase.VERIFYING_ORDER
                        },
                    )
                }

                if (pending.stage == PendingDeliveryCheckoutStage.VERIFYING_ORDER && pending.orderId != null) {
                    pollOrderUntilTerminal(pending.orderId)
                }
            }
        }
    }

    private fun applyCoupon(code: String) {
        if (code.isBlank()) return
        viewModelScope.launch {
            val coins = state.value.coins
            val cartItems = state.value.cartItems
            val subtotalMakingCharge = cartItems.mapNotNull { (id, qty) ->
                coins.find { it.id == id }?.let { it.makingCharge * qty }
            }.sum()
            val totalWeight = state.value.totalWeightGm

            val request = TradeCouponValidationRequest(
                orderType = TradeCouponOrderType.DELIVERY,
                code = code,
                deliveryFeeInr = subtotalMakingCharge,
                deliveryGrams = totalWeight,
            )
            updateState { it.copy(isCheckingOut = true) }
            when (val result = validateTradeCouponUseCase(request)) {
                is ApiResult.Success -> {
                    val validation = result.value
                    val discount = validation.promotionalDeliveryDiscount.toDoubleOrNull() ?: 0.0
                    val couponCode = validation.code?.takeIf { it.isNotBlank() } ?: code
                    
                    val couponType = state.value.availableCoupons.find { it.code.equals(couponCode, ignoreCase = true) }?.type
                        ?: if (discount >= subtotalMakingCharge && subtotalMakingCharge > 0.0) TradeCouponType.FREE_DELIVERY else TradeCouponType.DELIVERY_DISCOUNT

                    updateState {
                        it.copy(
                            isCheckingOut = false,
                            couponCode = couponCode,
                            couponDiscountInr = discount,
                            couponType = couponType,
                            hasManuallyRemovedCoupon = false,
                        )
                    }
                    emitEffect(
                        DeliveryEffect.ShowToast(
                            DeliveryUiText.Resource(
                                Res.string.delivery_catalog_coupon_applied,
                                listOf(couponCode)
                            )
                        )
                    )
                }
                is ApiResult.Failure -> {
                    updateState { it.copy(isCheckingOut = false) }
                    emitEffect(
                        DeliveryEffect.ShowError(
                            DeliveryUiText.Dynamic(result.error.message)
                        )
                    )
                }
            }
        }
    }

    private fun removeCoupon() {
        updateState {
            it.copy(
                couponCode = null,
                couponDiscountInr = 0.0,
                couponType = null,
                hasManuallyRemovedCoupon = true,
            )
        }
    }

    /**
     * Evaluates and automatically applies a free delivery coupon if criteria match.
     * Bypasses auto-apply if the user manually removed a coupon from their cart.
     */
    private fun autoApplyDeliveryCouponIfEligible() {
        val totalWeight = state.value.totalWeightGm
        val currentCoupon = state.value.couponCode
        val hasManuallyRemoved = state.value.hasManuallyRemovedCoupon
        val availableCoupons = state.value.availableCoupons

        if (!hasManuallyRemoved && currentCoupon == null && totalWeight > 0.0) {
            val autoCoupon = availableCoupons.firstOrNull {
                it.type == TradeCouponType.FREE_DELIVERY || it.type == TradeCouponType.DELIVERY_DISCOUNT
            }
            if (autoCoupon != null) {
                viewModelScope.launch {
                    val coins = state.value.coins
                    val cartItems = state.value.cartItems
                    val subtotalMakingCharge = cartItems.mapNotNull { (id, qty) ->
                        coins.find { it.id == id }?.let { it.makingCharge * qty }
                    }.sum()

                    val request = TradeCouponValidationRequest(
                        orderType = TradeCouponOrderType.DELIVERY,
                        code = autoCoupon.code,
                        deliveryFeeInr = subtotalMakingCharge,
                        deliveryGrams = totalWeight,
                    )
                    when (val result = validateTradeCouponUseCase(request)) {
                        is ApiResult.Success -> {
                            val validation = result.value
                            val discount = validation.promotionalDeliveryDiscount.toDoubleOrNull() ?: 0.0
                            val couponCode = validation.code ?: autoCoupon.code
                            val couponType = autoCoupon.type

                            updateState {
                                it.copy(
                                    couponCode = couponCode,
                                    couponDiscountInr = discount,
                                    couponType = couponType,
                                )
                            }
                        }
                        is ApiResult.Failure -> {
                            // Fail silently for auto-apply
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val MAX_PRODUCT_UNIT_ESTIMATE_INR = 95000.0
        const val MAX_ORDER_STATUS_POLL_ATTEMPTS = 15
        const val ORDER_STATUS_POLL_INTERVAL_MS = 2000L


        private fun generateIdempotencyKey(): String {
            val chars = ('a'..'f') + ('0'..'9')
            fun segment(length: Int) = (1..length).map { chars.random() }.joinToString("")
            return "${segment(8)}-${segment(4)}-${segment(4)}-${segment(4)}-${segment(12)}"
        }
    }
}

private fun Double?.orZero(): Double = this ?: 0.0

private fun String.asDeliveryUiText(): DeliveryUiText = DeliveryUiText.Dynamic(this)
