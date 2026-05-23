package com.habit.gold.feature.trade.presentation.buy

import com.habit.gold.core.presentation.mvi.MviEffect
import com.habit.gold.core.presentation.mvi.MviIntent
import com.habit.gold.core.presentation.mvi.MviState
import com.habit.gold.feature.trade.domain.model.TradeAvailableCoupon
import com.habit.gold.feature.trade.domain.model.TradeBuyOrder
import com.habit.gold.feature.trade.domain.model.TradeCouponValidation
import com.habit.gold.feature.trade.domain.model.TradePaymentLaunchResult
import com.habit.gold.feature.trade.domain.model.TradePaymentLaunchRequest
import com.habit.gold.feature.trade.domain.model.TradePollingSnapshot

enum class BuyTradeTab {
    OneTime,
    Sip,
}

enum class BuyTradeEntryMode {
    Rupees,
    Grams,
}

sealed interface BuyTradeStep {
    data object Entry : BuyTradeStep
    data object Processing : BuyTradeStep
    data object Success : BuyTradeStep
    data object Failure : BuyTradeStep
    data object Pending : BuyTradeStep
}

data class BuyTradeState(
    val isLoading: Boolean = false,
    val step: BuyTradeStep = BuyTradeStep.Entry,
    val activeTab: BuyTradeTab = BuyTradeTab.OneTime,
    val entryMode: BuyTradeEntryMode = BuyTradeEntryMode.Rupees,
    val order: TradeBuyOrder? = null,
    val currentOrderId: String? = null,
    val pendingPaymentRequest: TradePaymentLaunchRequest? = null,
    val pollingSnapshot: TradePollingSnapshot? = null,
    val availableCoupons: List<TradeAvailableCoupon> = emptyList(),
    val appliedCouponCode: String? = null,
    val appliedCouponValidatedAmount: Double? = null,
    val appliedCouponValidatedGrams: Double? = null,
    val appliedCoupon: TradeCouponValidation? = null,
    val errorMessage: String? = null,
) : MviState

sealed interface BuyTradeIntent : MviIntent {
    data object Load : BuyTradeIntent
    data object RefreshPrice : BuyTradeIntent
    data class ChangeTab(val tab: BuyTradeTab) : BuyTradeIntent
    data class ChangeEntryMode(val mode: BuyTradeEntryMode) : BuyTradeIntent
    data class SubmitOneTimeOrder(
        val amount: Double?,
        val grams: Double?,
        val buyRateId: String,
        val couponCode: String? = null,
        val couponValidationAmount: Double? = amount,
        val couponValidationGrams: Double? = grams,
        val useRewardsInr: Double? = null,
    ) : BuyTradeIntent
    data class HandlePaymentResult(val result: TradePaymentLaunchResult) : BuyTradeIntent
    data class ApplyCoupon(
        val code: String,
        val amount: Double?,
        val grams: Double?,
        val silent: Boolean = false,
    ) : BuyTradeIntent
    data object ResetToEntry : BuyTradeIntent
    data object ClearAppliedCoupon : BuyTradeIntent
    data class StartPolling(val orderId: String) : BuyTradeIntent
}

sealed interface BuyTradeEffect : MviEffect {
    data class LaunchPayment(val request: TradePaymentLaunchRequest) : BuyTradeEffect
    data class ShowMessage(val message: String) : BuyTradeEffect
    data object RefreshLivePrice : BuyTradeEffect
}
