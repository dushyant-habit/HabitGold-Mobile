package com.habit.gold.feature.trade.presentation.buy

import androidx.lifecycle.viewModelScope
import com.habit.gold.core.network.ApiResult
import com.habit.gold.core.presentation.mvi.MviViewModel
import com.habit.gold.feature.trade.domain.model.TradeCouponOrderType
import com.habit.gold.feature.trade.domain.model.TradePaymentContext
import com.habit.gold.feature.trade.domain.model.TradePaymentLaunchRequest
import com.habit.gold.feature.trade.domain.model.TradePaymentLaunchResult
import com.habit.gold.feature.trade.domain.model.TradePollingOutcome
import com.habit.gold.feature.trade.domain.model.TradePollingPolicies
import com.habit.gold.feature.trade.domain.usecase.CreateBuyOrderUseCase
import com.habit.gold.feature.trade.domain.usecase.GetTradeAvailableCouponsUseCase
import com.habit.gold.feature.trade.domain.usecase.PollTradeStatusUseCase
import com.habit.gold.feature.trade.domain.usecase.ValidateTradeCouponUseCase
import kotlinx.coroutines.launch

class BuyTradeViewModel(
    private val createBuyOrderUseCase: CreateBuyOrderUseCase,
    private val getTradeAvailableCouponsUseCase: GetTradeAvailableCouponsUseCase,
    private val validateTradeCouponUseCase: ValidateTradeCouponUseCase,
    private val pollTradeStatusUseCase: PollTradeStatusUseCase,
) : MviViewModel<BuyTradeState, BuyTradeIntent, BuyTradeEffect>(BuyTradeState()) {

    private var hasLoaded = false

    override fun onIntent(intent: BuyTradeIntent) {
        when (intent) {
            BuyTradeIntent.Load -> {
                if (!hasLoaded) {
                    hasLoaded = true
                    preloadCoupons()
                }
            }
            BuyTradeIntent.ResetToEntry -> resetToEntry()
            is BuyTradeIntent.ChangeEntryMode -> updateState { it.copy(entryMode = intent.mode) }
            is BuyTradeIntent.ChangeTab -> updateState { it.copy(activeTab = intent.tab) }
            BuyTradeIntent.RefreshPrice -> updateState { it.copy(errorMessage = null) }
            BuyTradeIntent.ClearAppliedCoupon -> updateState {
                it.copy(
                    appliedCouponCode = null,
                    appliedCouponValidatedAmount = null,
                    appliedCouponValidatedGrams = null,
                    appliedCoupon = null,
                    errorMessage = null,
                )
            }
            is BuyTradeIntent.SubmitOneTimeOrder -> submitOneTimeOrder(intent)
            is BuyTradeIntent.HandlePaymentResult -> handlePaymentResult(intent.result)
            is BuyTradeIntent.StartPolling -> startPolling(intent.orderId)
            is BuyTradeIntent.ApplyCoupon -> applyCoupon(
                code = intent.code,
                amount = intent.amount,
                grams = intent.grams,
                silent = intent.silent,
            )
        }
    }

    private fun preloadCoupons() {
        viewModelScope.launch {
            when (val result = getTradeAvailableCouponsUseCase(orderType = TradeCouponOrderType.BUY)) {
                is ApiResult.Success -> updateState { it.copy(availableCoupons = result.value) }
                is ApiResult.Failure -> updateState { it.copy(errorMessage = result.error.message) }
            }
        }
    }

    private fun submitOneTimeOrder(intent: BuyTradeIntent.SubmitOneTimeOrder) {
        viewModelScope.launch {
            updateState {
                it.copy(
                    isLoading = true,
                    step = BuyTradeStep.Entry,
                    currentOrderId = null,
                    pollingSnapshot = null,
                    errorMessage = null,
                )
            }

            val validatedCouponCode = intent.couponCode?.takeIf { it.isNotBlank() }?.let { couponCode ->
                val validationRequest = com.habit.gold.feature.trade.domain.model.TradeCouponValidationRequest(
                    orderType = TradeCouponOrderType.BUY,
                    code = couponCode,
                    amount = intent.couponValidationAmount,
                    grams = intent.couponValidationGrams,
                )
                when (val validationResult = validateTradeCouponUseCase(validationRequest)) {
                    is ApiResult.Success -> {
                        updateState {
                            it.copy(
                                appliedCouponCode = validationResult.value.code?.takeIf(String::isNotBlank) ?: couponCode,
                                appliedCouponValidatedAmount = intent.couponValidationAmount,
                                appliedCouponValidatedGrams = intent.couponValidationGrams,
                                appliedCoupon = validationResult.value,
                            )
                        }
                        validationResult.value.code?.takeIf { it.isNotBlank() } ?: couponCode
                    }
                    is ApiResult.Failure -> {
                        updateState {
                            it.copy(
                                isLoading = false,
                                step = BuyTradeStep.Entry,
                                errorMessage = validationResult.error.message,
                            )
                        }
                        return@launch
                    }
                }
            }

            when (
                val result = createBuyOrderUseCase(
                    com.habit.gold.feature.trade.domain.model.TradeBuyOrderRequest(
                        amount = intent.amount,
                        grams = intent.grams,
                        buyRateId = intent.buyRateId,
                        couponCode = validatedCouponCode,
                        useRewardsInr = intent.useRewardsInr,
                    )
                )
            ) {
                is ApiResult.Success -> {
                    val paymentRequest = result.value.sdkPayloadJson?.takeIf { it.isNotBlank() }?.let { payloadJson ->
                        TradePaymentLaunchRequest.Juspay(
                            payloadJson = payloadJson,
                            context = TradePaymentContext.BuyOneTime,
                        )
                    }
                    updateState {
                        it.copy(
                            isLoading = false,
                            order = result.value,
                            currentOrderId = result.value.orderId,
                            pendingPaymentRequest = paymentRequest,
                            pollingSnapshot = null,
                            errorMessage = null,
                        )
                    }
                    if (paymentRequest != null) {
                        emitEffect(BuyTradeEffect.LaunchPayment(paymentRequest))
                    } else {
                        startPolling(result.value.orderId)
                    }
                }
                is ApiResult.Failure -> {
                    if (isLiveRateExpiredMessage(result.error.message)) {
                        updateState {
                            it.copy(
                                isLoading = false,
                                step = BuyTradeStep.Entry,
                                errorMessage = null,
                            )
                        }
                        emitEffect(BuyTradeEffect.RefreshLivePrice)
                    } else {
                        updateState {
                            it.copy(
                                isLoading = false,
                                step = BuyTradeStep.Entry,
                                errorMessage = result.error.message,
                            )
                        }
                    }
                }
            }
        }
    }

    private fun startPolling(orderId: String) {
        viewModelScope.launch {
            updateState {
                it.copy(
                    isLoading = true,
                    step = BuyTradeStep.Processing,
                    currentOrderId = orderId,
                    pendingPaymentRequest = null,
                    errorMessage = null,
                )
            }
            when (val result = pollTradeStatusUseCase(orderId, TradePollingPolicies.buy())) {
                is ApiResult.Success -> {
                    val nextError = when (val outcome = result.value) {
                        is TradePollingOutcome.Failure -> outcome.message
                        else -> null
                    }
                    val nextSnapshot = when (val outcome = result.value) {
                        is TradePollingOutcome.Success -> outcome.snapshot
                        is TradePollingOutcome.Failure -> outcome.snapshot
                        is TradePollingOutcome.Processing -> outcome.snapshot
                        is TradePollingOutcome.Pending -> outcome.snapshot
                    }
                    val nextStep = when (result.value) {
                        is TradePollingOutcome.Success -> BuyTradeStep.Success
                        is TradePollingOutcome.Failure -> BuyTradeStep.Failure
                        is TradePollingOutcome.Processing -> BuyTradeStep.Pending
                        is TradePollingOutcome.Pending -> BuyTradeStep.Pending
                    }
                    updateState {
                        it.copy(
                            isLoading = false,
                            step = nextStep,
                            pollingSnapshot = nextSnapshot,
                            errorMessage = nextError,
                        )
                    }
                }
                is ApiResult.Failure -> updateState {
                    it.copy(
                        isLoading = false,
                        step = BuyTradeStep.Failure,
                        errorMessage = result.error.message,
                    )
                }
            }
        }
    }

    private fun handlePaymentResult(result: TradePaymentLaunchResult) {
        when (result) {
            is TradePaymentLaunchResult.Success -> {
                val currentOrderId = state.value.currentOrderId ?: state.value.order?.orderId
                updateState {
                    it.copy(
                        pendingPaymentRequest = null,
                        step = BuyTradeStep.Processing,
                        errorMessage = null,
                    )
                }
                currentOrderId?.let(::startPolling)
            }
            is TradePaymentLaunchResult.Failure -> {
                val currentOrderId = state.value.currentOrderId ?: state.value.order?.orderId
                if (result.shouldPollOrderStatus && !currentOrderId.isNullOrBlank()) {
                    updateState {
                        it.copy(
                            pendingPaymentRequest = null,
                            step = BuyTradeStep.Processing,
                            errorMessage = null,
                        )
                    }
                    startPolling(currentOrderId)
                } else {
                    updateState {
                        it.copy(
                            pendingPaymentRequest = null,
                            step = BuyTradeStep.Entry,
                            errorMessage = result.message,
                        )
                    }
                }
            }
            TradePaymentLaunchResult.BackPressed -> {
                updateState {
                    it.copy(
                        pendingPaymentRequest = null,
                        step = BuyTradeStep.Entry,
                        errorMessage = null,
                    )
                }
            }
        }
    }

    private fun resetToEntry() {
        updateState {
            it.copy(
                isLoading = false,
                step = BuyTradeStep.Entry,
                order = null,
                currentOrderId = null,
                pendingPaymentRequest = null,
                pollingSnapshot = null,
                errorMessage = null,
            )
        }
    }

    private fun applyCoupon(
        code: String,
        amount: Double?,
        grams: Double?,
        silent: Boolean,
    ) {
        if (code.isBlank()) return
        viewModelScope.launch {
            val request = com.habit.gold.feature.trade.domain.model.TradeCouponValidationRequest(
                orderType = TradeCouponOrderType.BUY,
                code = code,
                amount = amount,
                grams = grams,
            )
            when (val result = validateTradeCouponUseCase(request)) {
                is ApiResult.Success -> updateState {
                    it.copy(
                        appliedCouponCode = result.value.code?.takeIf(String::isNotBlank) ?: code,
                        appliedCouponValidatedAmount = amount,
                        appliedCouponValidatedGrams = grams,
                        appliedCoupon = result.value,
                        errorMessage = null,
                    )
                }
                is ApiResult.Failure -> updateState {
                    if (silent) {
                        it.copy(
                            appliedCouponCode = code,
                            appliedCouponValidatedAmount = null,
                            appliedCouponValidatedGrams = null,
                            errorMessage = null,
                        )
                    } else {
                        it.copy(errorMessage = result.error.message)
                    }
                }
            }
        }
    }

    private fun isLiveRateExpiredMessage(message: String): Boolean {
        val normalized = message.lowercase()
        return normalized.contains("selected gold rate has expired") ||
            normalized.contains("gold rate has expired") ||
            normalized.contains("gold rate has changed") ||
            normalized.contains("rate has expired") ||
            normalized.contains("rate has changed")
    }
}
