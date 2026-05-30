package com.habit.gold.feature.savings.presentation

import com.habit.gold.feature.savings.domain.model.SavingsMandate
import com.habit.gold.feature.trade.domain.model.TradeAvailableCoupon
import com.habit.gold.feature.trade.domain.model.TradeCouponValidation
import com.habit.gold.feature.trade.domain.model.TradePaymentLaunchRequest

internal enum class SavingsFrequency(
    val routeValue: String,
    val apiValue: String,
    val defaultAmount: Int,
    val minAmount: Int,
    val maxAmount: Int,
    val quickAmounts: List<SavingsQuickAmount>,
) {
    Daily(
        routeValue = "Daily",
        apiValue = "DAILY",
        defaultAmount = 100,
        minAmount = 10,
        maxAmount = 5000,
        quickAmounts = listOf(
            SavingsQuickAmount(50),
            SavingsQuickAmount(200, "Popular"),
            SavingsQuickAmount(500),
            SavingsQuickAmount(1000),
        ),
    ),
    Weekly(
        routeValue = "Weekly",
        apiValue = "WEEKLY",
        defaultAmount = 1000,
        minAmount = 50,
        maxAmount = 15000,
        quickAmounts = listOf(
            SavingsQuickAmount(500),
            SavingsQuickAmount(1000, "Popular"),
            SavingsQuickAmount(2000),
            SavingsQuickAmount(3000),
        ),
    ),
    Monthly(
        routeValue = "Monthly",
        apiValue = "MONTHLY",
        defaultAmount = 5000,
        minAmount = 50,
        maxAmount = 15000,
        quickAmounts = listOf(
            SavingsQuickAmount(1000),
            SavingsQuickAmount(5000, "Popular"),
            SavingsQuickAmount(10000),
            SavingsQuickAmount(15000),
        ),
    ),
    ;

    companion object {
        fun fromRouteValue(raw: String): SavingsFrequency {
            return entries.firstOrNull { it.routeValue.equals(raw.trim(), ignoreCase = true) } ?: Daily
        }
    }
}

internal val SavingsWeeklyExecutionDays: List<Pair<Int, String>> = listOf(
    1 to "Mon",
    2 to "Tue",
    3 to "Wed",
    4 to "Thu",
    5 to "Fri",
    6 to "Sat",
    7 to "Sun",
)

internal data class SavingsQuickAmount(
    val amount: Int,
    val tag: String? = null,
)

internal sealed interface SavingsSetupPhase {
    data object Form : SavingsSetupPhase
    data class Polling(val mandateId: String, val attempt: Int) : SavingsSetupPhase
    data class Processing(val mandateId: String) : SavingsSetupPhase
    data class Success(val mandateId: String) : SavingsSetupPhase
    data class Failure(val message: String) : SavingsSetupPhase
}

internal data class SavingsSetupUiState(
    val frequency: SavingsFrequency = SavingsFrequency.Daily,
    val amountText: String = "",
    val selectedExecutionDay: Int? = null,
    val mandateId: String? = null,
    val existingMandate: SavingsMandate? = null,
    val currentAmount: Int? = null,
    val isUpgradeFlow: Boolean = false,
    val isPausedMandate: Boolean = false,
    val initialStatus: String? = null,
    val availableCoupons: List<TradeAvailableCoupon> = emptyList(),
    val couponDraft: String = "",
    val appliedCoupon: TradeCouponValidation? = null,
    val isSubmitting: Boolean = false,
    val inlineErrorMessage: String? = null,
    val pendingMandateId: String? = null,
    val phase: SavingsSetupPhase = SavingsSetupPhase.Form,
) {
    val amountValue: Int?
        get() = amountText.toIntOrNull()

    val canEditAmount: Boolean
        get() = phase == SavingsSetupPhase.Form && !isSubmitting
}

internal sealed interface SavingsSetupIntent {
    data class Initialize(val destination: SavingsDestination.Setup) : SavingsSetupIntent
    data class ChangeAmount(val rawValue: String) : SavingsSetupIntent
    data class SelectExecutionDay(val day: Int) : SavingsSetupIntent
    data class ChangeCouponDraft(val rawValue: String) : SavingsSetupIntent
    data class ApplyCoupon(val code: String) : SavingsSetupIntent
    data object ClearAppliedCoupon : SavingsSetupIntent
    data object Submit : SavingsSetupIntent
    data class HandlePaymentResult(val result: com.habit.gold.feature.trade.domain.model.TradePaymentLaunchResult) : SavingsSetupIntent
    data object RetryPolling : SavingsSetupIntent
    data object ResetToForm : SavingsSetupIntent
}

internal sealed interface SavingsSetupEffect {
    data class LaunchPayment(val request: TradePaymentLaunchRequest.Juspay) : SavingsSetupEffect
}

internal fun SavingsFrequency.defaultExecutionDay(): Int? {
    return when (this) {
        SavingsFrequency.Daily -> null
        SavingsFrequency.Weekly -> 1
        SavingsFrequency.Monthly -> 1
    }
}

internal fun SavingsFrequency.displayName(): String = routeValue

internal fun recommendedUpgradeAmount(
    currentAmount: Int,
    quickAmounts: List<SavingsQuickAmount>,
    maxAmount: Int,
): Int {
    return quickAmounts.firstOrNull { it.amount > currentAmount }?.amount
        ?: (currentAmount + quickAmounts.firstOrNull()?.amount.orEmpty())
            .coerceAtMost(maxAmount)
            .coerceAtLeast(currentAmount + 1)
}

private fun Int?.orEmpty(): Int = this ?: 0
