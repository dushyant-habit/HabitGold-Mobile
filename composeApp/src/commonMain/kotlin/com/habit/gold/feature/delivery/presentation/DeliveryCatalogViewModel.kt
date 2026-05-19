package com.habit.gold.feature.delivery.presentation

import androidx.lifecycle.viewModelScope
import com.habit.gold.core.presentation.mvi.MviViewModel
import com.habit.gold.core.session.SessionStore
import com.habit.gold.feature.delivery.data.DeliveryCheckoutTelemetry
import com.habit.gold.feature.delivery.data.PendingDeliveryCheckout
import com.habit.gold.feature.delivery.data.PendingDeliveryCheckoutStage
import com.habit.gold.feature.delivery.data.PendingDeliveryCheckoutStore
import com.habit.gold.feature.delivery.domain.model.CreateDeliveryQuoteDto
import com.habit.gold.feature.delivery.domain.model.ConfirmDeliveryOrderDto
import com.habit.gold.feature.delivery.domain.model.DeliveryCheckoutPhase
import com.habit.gold.feature.delivery.domain.model.DeliveryCheckoutQuote
import com.habit.gold.feature.delivery.domain.model.DeliveryOrderDto
import com.habit.gold.feature.delivery.domain.model.DeliveryQuoteResponseDto
import com.habit.gold.feature.delivery.domain.model.DeliveryVerifyQuoteDto
import com.habit.gold.feature.delivery.domain.model.PhysicalCoin
import com.habit.gold.feature.delivery.domain.usecase.ConfirmDeliveryOrderUseCase
import com.habit.gold.feature.delivery.domain.usecase.CreateDeliveryQuoteUseCase
import com.habit.gold.feature.delivery.domain.usecase.GetDeliveryProductsUseCase
import com.habit.gold.feature.delivery.domain.usecase.GetDeliveryOrderDetailsUseCase
import com.habit.gold.feature.trade.domain.usecase.GetSellAvailabilityUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.firstOrNull
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import com.habit.gold.core.network.fold

class DeliveryCatalogViewModel(
    private val getDeliveryProductsUseCase: GetDeliveryProductsUseCase,
    private val createDeliveryQuoteUseCase: CreateDeliveryQuoteUseCase,
    private val confirmDeliveryOrderUseCase: ConfirmDeliveryOrderUseCase,
    private val pendingDeliveryCheckoutStore: PendingDeliveryCheckoutStore,
    private val deliveryCheckoutTelemetry: DeliveryCheckoutTelemetry,
    private val getSellAvailabilityUseCase: GetSellAvailabilityUseCase,
    private val getDeliveryOrderDetailsUseCase: GetDeliveryOrderDetailsUseCase,
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
            is DeliveryIntent.RefreshGoldBalance -> refreshGoldBalance()
            is DeliveryIntent.PrepareQuote -> prepareQuote(intent.couponCode)
            is DeliveryIntent.ConfirmOrder -> confirmOrder()
            is DeliveryIntent.HandlePaymentResult -> handlePaymentResult(intent.status, intent.payload)
            is DeliveryIntent.DiscardCheckout -> discardCheckout()
            is DeliveryIntent.ClearError -> { /* no-op */ }
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
                    updateState { it.copy(isLoadingProducts = false, coins = applyDeliveryProductFilter(products)) }
                },
                onFailure = { error ->
                    updateState { it.copy(isLoadingProducts = false) }
                    emitEffect(DeliveryEffect.ShowError(error.message ?: "Failed to load products"))
                }
            )
        }
    }

    private fun applyDeliveryProductFilter(products: List<PhysicalCoin>): List<PhysicalCoin> {
        val liveBuyPrice = state.value.liveBuyPricePerGram
        val maxEstimate = MAX_PRODUCT_UNIT_ESTIMATE_INR
        return products.filter {
            it.estimatedUnitSubtotalInr(liveBuyPrice) <= maxEstimate
        }
    }

    private fun updateQuantity(coinId: String, delta: Int) {
        updateState { currentState ->
            val currentQty = currentState.cartItems[coinId] ?: 0
            val newQty = (currentQty + delta).coerceIn(0, 1) // MAX_COINS_PER_ORDER = 1
            
            // If another coin is already in cart, remove it (MAX_COINS_PER_ORDER = 1 across all items)
            val newCart = if (newQty > 0) {
                mapOf(coinId to newQty)
            } else {
                emptyMap()
            }
            
            currentState.copy(cartItems = newCart)
        }
        autoApplyDelivery5IfEligible()
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
                            redeemableGoldGrams = availability.sellableGoldBalanceGrams
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

    private fun prepareQuote(couponCode: String?) {
        viewModelScope.launch {
            val addressId = state.value.selectedAddressId
            val cartItem = state.value.cartItems.entries.firstOrNull()
            if (addressId == null || cartItem == null) {
                emitEffect(DeliveryEffect.ShowError("Select an address and a coin to continue."))
                return@launch
            }
            
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
            
            updateState { it.copy(isCheckingOut = true, checkoutPhase = DeliveryCheckoutPhase.CONFIRMING) }
            
            createDeliveryQuoteUseCase(
                CreateDeliveryQuoteDto(
                    addressId = addressId,
                    productId = cartItem.key,
                    couponCode = couponCode ?: state.value.couponCode
                )
            ).fold(
                onSuccess = { response ->
                    activeConfirmIdempotencyKey = null
                    val quote = response.toUiQuote(cartItem.key, addressId)
                    updateState {
                        it.copy(
                            isCheckingOut = false,
                            checkoutQuote = quote,
                            checkoutPhase = DeliveryCheckoutPhase.REVIEW_READY
                        )
                    }
                    persistPendingCheckout(quote.toPendingCheckout(stage = PendingDeliveryCheckoutStage.REVIEW))
                    deliveryCheckoutTelemetry.quoteCreated(quote.quoteId, cartItem.key, addressId)
                    emitEffect(DeliveryEffect.NavigateToCheckout)
                },
                onFailure = { error ->
                    updateState { it.copy(isCheckingOut = false, checkoutPhase = DeliveryCheckoutPhase.IDLE) }
                    emitEffect(DeliveryEffect.ShowError(error.message ?: "Failed to create quote"))
                }
            )
        }
    }

    private fun confirmOrder() {
        // Re-entry guard: prevent multiple concurrent confirm calls
        val currentState = state.value
        if (currentState.isCheckingOut) return
        val phase = currentState.checkoutPhase
        if (phase == DeliveryCheckoutPhase.PAYMENT_LAUNCH_READY ||
            phase == DeliveryCheckoutPhase.PAYMENT_IN_PROGRESS ||
            phase == DeliveryCheckoutPhase.VERIFYING_ORDER
        ) return

        val quote = currentState.checkoutQuote
        if (quote == null) {
            viewModelScope.launch { emitEffect(DeliveryEffect.ShowError("Checkout quote unavailable.")) }
            return
        }

        val now = Clock.System.now().toEpochMilliseconds()
        if (quote.isExpired(now)) {
            viewModelScope.launch { emitEffect(DeliveryEffect.ShowError("Quote expired. Please refresh the quote.")) }
            return
        }

        val idempotencyKey = activeConfirmIdempotencyKey ?: generateIdempotencyKey().also {
            activeConfirmIdempotencyKey = it
        }

        updateState { it.copy(isCheckingOut = true) }
        viewModelScope.launch {
            confirmDeliveryOrderUseCase(
                idempotencyKey = idempotencyKey,
                body = ConfirmDeliveryOrderDto(quoteId = quote.quoteId)
            ).fold(
                onSuccess = { response ->
                    deliveryCheckoutTelemetry.confirmStarted(quote.quoteId, idempotencyKey)
                    val payload = response.sdkPayload
                    if (payload != null) {
                        updateState {
                            it.copy(
                                isCheckingOut = false,
                                pendingPaymentSdkPayloadJson = payload.toString(),
                                checkoutPhase = DeliveryCheckoutPhase.PAYMENT_LAUNCH_READY
                            )
                        }
                        persistPendingCheckout(quote.toPendingCheckout(stage = PendingDeliveryCheckoutStage.SDK_LAUNCH_READY, confirmIdempotencyKey = idempotencyKey, orderId = response.order.id, sdkPayload = payload))
                        emitEffect(DeliveryEffect.LaunchPaymentSdk(payload.toString()))
                    } else {
                        // Success without payment SDK
                        completeCheckout(response.order)
                    }
                },
                onFailure = { error ->
                    updateState { it.copy(isCheckingOut = false) }
                    emitEffect(DeliveryEffect.ShowError(error.message ?: "Failed to confirm order"))
                }
            )
        }
    }

    private fun handlePaymentResult(status: String, payload: String?) {
        val orderId = state.value.checkoutQuote?.quoteId // Note: In Android it tracked lastPlacedOrderId, we need an orderId. Let's get it from pending store
        
        viewModelScope.launch {
            val pending = pendingDeliveryCheckoutStore.pendingCheckout.firstOrNull()
            val actualOrderId = pending?.orderId
            if (actualOrderId == null) {
                emitEffect(DeliveryEffect.ShowError("Missing order ID for payment verification"))
                return@launch
            }
            
            updateState { it.copy(checkoutPhase = DeliveryCheckoutPhase.VERIFYING_ORDER) }
            pollOrderUntilTerminal(actualOrderId)
        }
    }

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
                            CheckoutOrderState.RECOVERABLE_PENDING -> {
                                // Keep polling
                            }
                            CheckoutOrderState.FAILURE -> {
                                activeConfirmIdempotencyKey = null
                                persistPendingCheckout(null)
                                updateState {
                                    it.copy(
                                        checkoutPhase = DeliveryCheckoutPhase.REVIEW_READY
                                    )
                                }
                                emitEffect(DeliveryEffect.ShowError("Payment failed. Please try again."))
                                return@launch
                            }
                        }
                    },
                    onFailure = {
                        // Transient error, ignore
                    }
                )
                delay(ORDER_STATUS_POLL_INTERVAL_MS)
            }

            updateState { it.copy(checkoutPhase = DeliveryCheckoutPhase.REVIEW_READY) }
            emitEffect(DeliveryEffect.ShowError("Payment verification timed out. Please check tracking."))
        }
    }

    private suspend fun completeCheckout(order: DeliveryOrderDto) {
        activeConfirmIdempotencyKey = null
        persistPendingCheckout(null)
        val id = order.id ?: ""
        updateState {
            it.copy(
                isCheckingOut = false,
                checkoutPhase = DeliveryCheckoutPhase.COMPLETED,
                cartItems = emptyMap(),
                checkoutQuote = null
            )
        }
        deliveryCheckoutTelemetry.finalOrderState(id, order.paymentStatus, order.status)
        emitEffect(DeliveryEffect.NavigateToOrderSummary(id))
    }

    private fun discardCheckout() {
        activeConfirmIdempotencyKey = null
        persistPendingCheckout(null)
        updateState { it.copy(checkoutQuote = null, checkoutPhase = DeliveryCheckoutPhase.IDLE) }
    }

    private fun persistPendingCheckout(checkout: PendingDeliveryCheckout?) {
        viewModelScope.launch {
            if (checkout != null) pendingDeliveryCheckoutStore.save(checkout)
            else pendingDeliveryCheckoutStore.clear()
        }
    }

    private fun restorePendingCheckout() {
        viewModelScope.launch {
            pendingDeliveryCheckoutStore.pendingCheckout.collect { pending ->
                if (pending != null) {
                    val now = Clock.System.now().toEpochMilliseconds()
                    if (!pending.isExpired(now)) {
                        updateState {
                            it.copy(
                                checkoutQuote = pending.toUiQuote(),
                                cartItems = mapOf(pending.productId to 1), // Only 1 coin allowed
                                selectedAddressId = pending.addressId,
                                checkoutPhase = when (pending.stage) {
                                    PendingDeliveryCheckoutStage.REVIEW -> DeliveryCheckoutPhase.REVIEW_READY
                                    PendingDeliveryCheckoutStage.CONFIRM_IN_FLIGHT -> DeliveryCheckoutPhase.CONFIRMING
                                    PendingDeliveryCheckoutStage.SDK_LAUNCH_READY -> DeliveryCheckoutPhase.PAYMENT_LAUNCH_READY
                                    PendingDeliveryCheckoutStage.SDK_OPENED -> DeliveryCheckoutPhase.PAYMENT_IN_PROGRESS
                                    PendingDeliveryCheckoutStage.VERIFYING_ORDER -> DeliveryCheckoutPhase.VERIFYING_ORDER
                                }
                            )
                        }
                        if (pending.stage == PendingDeliveryCheckoutStage.VERIFYING_ORDER && pending.orderId != null) {
                            pollOrderUntilTerminal(pending.orderId)
                        }
                    } else {
                        pendingDeliveryCheckoutStore.clear()
                    }
                }
            }
        }
    }



    private fun applyCoupon(code: String) {
        if (code.equals(DELIVERY5_COUPON_CODE, ignoreCase = true)) {
            val totalWeight = state.value.totalWeightGm
            if (totalWeight >= DELIVERY5_MIN_WEIGHT_GM) {
                updateState { it.copy(couponCode = DELIVERY5_COUPON_CODE, couponDiscountInr = DELIVERY5_DISCOUNT_INR) }
                viewModelScope.launch { emitEffect(DeliveryEffect.ShowToast("Coupon $DELIVERY5_COUPON_CODE applied! Free delivery on 5g+ coins.")) }
            } else {
                viewModelScope.launch { emitEffect(DeliveryEffect.ShowError("Coupon $DELIVERY5_COUPON_CODE is only valid for coins 5g and above.")) }
            }
        } else {
            // Generic coupon - for now just reject unknown codes
            viewModelScope.launch { emitEffect(DeliveryEffect.ShowError("Invalid coupon code.")) }
        }
    }

    private fun removeCoupon() {
        updateState { it.copy(couponCode = null, couponDiscountInr = 0.0) }
    }

    /**
     * Checks if DELIVERY5 coupon should be auto-applied for the current cart.
     * Called after cart changes (quantity update / product selection).
     */
    private fun autoApplyDelivery5IfEligible() {
        val totalWeight = state.value.totalWeightGm
        val current = state.value.couponCode
        if (totalWeight >= DELIVERY5_MIN_WEIGHT_GM && current == null) {
            updateState { it.copy(couponCode = DELIVERY5_COUPON_CODE, couponDiscountInr = DELIVERY5_DISCOUNT_INR) }
        } else if (totalWeight < DELIVERY5_MIN_WEIGHT_GM && current.equals(DELIVERY5_COUPON_CODE, ignoreCase = true)) {
            // Remove auto-applied coupon if weight dropped below threshold
            updateState { it.copy(couponCode = null, couponDiscountInr = 0.0) }
        }
    }

    companion object {
        const val MAX_PRODUCT_UNIT_ESTIMATE_INR = 95000.0
        const val MAX_ORDER_STATUS_POLL_ATTEMPTS = 15
        const val ORDER_STATUS_POLL_INTERVAL_MS = 2000L
        const val DELIVERY5_COUPON_CODE = "DELIVERY5"
        const val DELIVERY5_MIN_WEIGHT_GM = 5.0
        const val DELIVERY5_DISCOUNT_INR = 100.0 // Free delivery (full minting charge waiver) for 5g+ coins

        private fun generateIdempotencyKey(): String {
            val chars = ('a'..'f') + ('0'..'9')
            fun seg(n: Int) = (1..n).map { chars.random() }.joinToString("")
            return "${seg(8)}-${seg(4)}-${seg(4)}-${seg(4)}-${seg(12)}"
        }
    }
}

private fun DeliveryQuoteResponseDto.toUiQuote(productId: String, addressId: String): DeliveryCheckoutQuote =
    DeliveryCheckoutQuote(
        quoteId = quoteId,
        productId = productId,
        addressId = addressId,
        goldWeightGrams = verifyQuote.goldWeightGrams.toDoubleOrNull() ?: 0.0,
        mintingChargeInr = verifyQuote.mintingChargeInr.toDoubleOrNull() ?: 0.0,
        payableChargeInr = verifyQuote.payableChargeInr.toDoubleOrNull() ?: 0.0,
        estimatedDispatchDays = verifyQuote.estimatedDispatchDays,
        verifyExpiresAt = verifyQuote.verifyExpiresAt
    )

private fun DeliveryCheckoutQuote.toPendingCheckout(
    couponCode: String? = null,
    confirmIdempotencyKey: String? = null,
    orderId: String? = null,
    sdkPayload: JsonObject? = null,
    stage: PendingDeliveryCheckoutStage
): PendingDeliveryCheckout =
    PendingDeliveryCheckout(
        quoteId = quoteId,
        productId = productId.orEmpty(),
        addressId = addressId.orEmpty(),
        couponCode = couponCode,
        verifyQuote = DeliveryVerifyQuoteDto(
            mintingChargeInr = mintingChargeInr.toString(),
            payableChargeInr = payableChargeInr.toString(),
            goldWeightGrams = goldWeightGrams.toString(),
            verifyExpiresAt = verifyExpiresAt.orEmpty(),
            estimatedDispatchDays = estimatedDispatchDays
        ),
        confirmIdempotencyKey = confirmIdempotencyKey,
        orderId = orderId,
        sdkPayload = sdkPayload,
        stage = stage
    )

private fun PendingDeliveryCheckout.toUiQuote(): DeliveryCheckoutQuote =
    DeliveryCheckoutQuote(
        quoteId = quoteId,
        productId = productId,
        addressId = addressId,
        goldWeightGrams = verifyQuote.goldWeightGrams.toDoubleOrNull() ?: 0.0,
        mintingChargeInr = verifyQuote.mintingChargeInr.toDoubleOrNull() ?: 0.0,
        payableChargeInr = verifyQuote.payableChargeInr.toDoubleOrNull() ?: 0.0,
        estimatedDispatchDays = verifyQuote.estimatedDispatchDays,
        verifyExpiresAt = verifyQuote.verifyExpiresAt
    )

private fun String.parseIsoInstantToMillis(): Long? =
    runCatching { Instant.parse(this).toEpochMilliseconds() }.getOrNull()

private fun DeliveryCheckoutQuote.isExpired(nowMillis: Long): Boolean {
    val expiresAt = verifyExpiresAt?.parseIsoInstantToMillis() ?: return false
    return expiresAt <= nowMillis
}

private fun PendingDeliveryCheckout.isExpired(nowMillis: Long): Boolean {
    val expiresAt = verifyQuote.verifyExpiresAt.parseIsoInstantToMillis() ?: return false
    return expiresAt <= nowMillis
}

private enum class CheckoutOrderState {
    SUCCESS,
    FAILURE,
    RECOVERABLE_PENDING
}

private fun DeliveryOrderDto.resolveCheckoutState(): CheckoutOrderState {
    val payment = paymentStatus?.trim()?.uppercase()
    val status = status?.trim()?.uppercase()
    return when {
        payment == "SUCCESS" -> CheckoutOrderState.SUCCESS
        payment == "FAILED" || payment == "FAILURE" || payment == "CANCELLED" -> CheckoutOrderState.FAILURE
        payment == "PAYMENT_SESSION_PENDING" -> CheckoutOrderState.RECOVERABLE_PENDING
        confirmedAt != null && payment.isNullOrBlank() -> CheckoutOrderState.SUCCESS
        status == "CONFIRMED" || status == "DISPATCHED" || status == "IN_TRANSIT" || status == "DELIVERED" -> CheckoutOrderState.SUCCESS
        else -> CheckoutOrderState.RECOVERABLE_PENDING
    }
}
private fun parseDeliveryProducts(payload: JsonElement): List<PhysicalCoin> {
    val array: JsonArray? = when (payload) {
        is JsonArray -> payload
        is JsonObject -> payload["data"] as? JsonArray
        else -> null
    }

    val list = array
        ?.mapNotNull { it as? JsonObject }
        ?.mapNotNull { obj ->
            val id = obj.string("productId")
                ?: obj.string("id")
                ?: obj.string("safeGoldProductId")
                ?: return@mapNotNull null
            val sku = obj.string("sku").orEmpty()
            val productName = obj.string("productName").orEmpty()
            val weight = obj.double("weight")
                ?: obj.double("weightGrams")
                ?: obj.double("productWeightGrams")
                ?: obj.double("weightGm")
                ?: return@mapNotNull null
            val making = obj.double("makingCharge") ?: 0.0
            val image = obj.string("image").orEmpty()
            val stamp = obj.string("metalStamp").orEmpty()
            val dispatchDays = obj.optInt("estimatedDispatchDays")
            PhysicalCoin(
                id = id,
                sku = sku,
                productName = productName.ifBlank { "${weight}g product" },
                weightGm = weight,
                makingCharge = making,
                imageUrl = image,
                metalStamp = stamp,
                estimatedDispatchDays = dispatchDays
            )
        }
        ?: emptyList()

    return list.sortedWith(compareBy({ it.weightGm }, { it.productName }))
}

internal fun JsonObject.string(key: String): String? = (this[key] as? JsonPrimitive)?.content

private fun JsonObject.double(key: String): Double? {
    val p = this[key] as? JsonPrimitive ?: return null
    return p.content.toDoubleOrNull()
}

private fun JsonObject.optInt(key: String): Int? {
    val p = this[key] as? JsonPrimitive ?: return null
    return p.content.toIntOrNull()
}

// ── API response JSON helpers (mirrored from legacy ApiResponseExt.kt) ──────

/**
 * Many authenticated v1 responses wrap payloads as `{ "data": ... }`.
 * Returns the `data` object when present, otherwise `this`.
 */
internal fun JsonObject.dataPayloadOrSelf(): JsonObject =
    (this["data"] as? JsonObject) ?: this

/**
 * Extract the created address ID from the API response, handling both
 * `{ id: ... }` and `{ data: { id: ... } }` shapes.
 */
internal fun JsonObject.extractAddressId(): String? {
    fun pick(o: JsonObject): String? =
        (o["id"] as? JsonPrimitive)?.content?.takeIf { it.isNotBlank() }
            ?: (o["addressId"] as? JsonPrimitive)?.content?.takeIf { it.isNotBlank() }
    val root = dataPayloadOrSelf()
    return pick(root) ?: pick(this)
}

/**
 * Returns `true` when the delivery-pincode validation response indicates
 * that the pincode is serviceable. Handles boolean and string representations.
 */
internal fun JsonObject.pincodeAccepted(): Boolean {
    val serviceable = this["serviceable"]
    if (serviceable is JsonPrimitive) {
        if (serviceable.isString) return serviceable.content.equals("true", ignoreCase = true)
        return serviceable.content.toBooleanStrictOrNull() ?: false
    }
    val status = (this["status"] as? JsonPrimitive)?.content
    return status?.equals("SERVICEABLE", ignoreCase = true) == true
        || status?.equals("SUCCESS", ignoreCase = true) == true
}
