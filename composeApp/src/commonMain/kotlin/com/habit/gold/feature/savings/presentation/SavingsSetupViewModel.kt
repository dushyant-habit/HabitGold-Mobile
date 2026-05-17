package com.habit.gold.feature.savings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habit.gold.core.network.ApiResult
import com.habit.gold.feature.savings.domain.model.SavingsCreateMandateRequest
import com.habit.gold.feature.savings.domain.model.SavingsMandate
import com.habit.gold.feature.savings.domain.usecase.CreateSavingsMandateSessionUseCase
import com.habit.gold.feature.savings.domain.usecase.GetSavingsMandateUseCase
import com.habit.gold.feature.savings.domain.usecase.UpdateSavingsMandateSessionUseCase
import com.habit.gold.feature.trade.domain.model.TradeCouponOrderType
import com.habit.gold.feature.trade.domain.model.TradeCouponValidationRequest
import com.habit.gold.feature.trade.domain.model.TradePaymentContext
import com.habit.gold.feature.trade.domain.model.TradePaymentLaunchRequest
import com.habit.gold.feature.trade.domain.model.TradePaymentLaunchResult
import com.habit.gold.feature.trade.domain.usecase.GetTradeAvailableCouponsUseCase
import com.habit.gold.feature.trade.domain.usecase.ValidateTradeCouponUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

internal class SavingsSetupViewModel(
    private val createSavingsMandateSessionUseCase: CreateSavingsMandateSessionUseCase,
    private val updateSavingsMandateSessionUseCase: UpdateSavingsMandateSessionUseCase,
    private val getSavingsMandateUseCase: GetSavingsMandateUseCase,
    private val getTradeAvailableCouponsUseCase: GetTradeAvailableCouponsUseCase? = null,
    private val validateTradeCouponUseCase: ValidateTradeCouponUseCase? = null,
) : ViewModel() {
    private val _state = MutableStateFlow(SavingsSetupUiState())
    internal val state: StateFlow<SavingsSetupUiState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<SavingsSetupEffect>()
    internal val effects: SharedFlow<SavingsSetupEffect> = _effects.asSharedFlow()

    private var initializedDestination: SavingsDestination.Setup? = null

    internal fun onIntent(intent: SavingsSetupIntent) {
        when (intent) {
            is SavingsSetupIntent.Initialize -> initialize(intent.destination)
            is SavingsSetupIntent.ChangeAmount -> updateAmount(intent.rawValue)
            is SavingsSetupIntent.SelectExecutionDay -> updateExecutionDay(intent.day)
            is SavingsSetupIntent.ChangeCouponDraft -> updateCouponDraft(intent.rawValue)
            is SavingsSetupIntent.ApplyCoupon -> applyCoupon(intent.code)
            SavingsSetupIntent.ClearAppliedCoupon -> clearAppliedCoupon()
            SavingsSetupIntent.Submit -> submit()
            is SavingsSetupIntent.HandlePaymentResult -> handlePaymentResult(intent.result)
            SavingsSetupIntent.RetryPolling -> retryPolling()
            SavingsSetupIntent.ResetToForm -> resetToForm()
        }
    }

    private fun initialize(destination: SavingsDestination.Setup) {
        if (initializedDestination == destination) return
        initializedDestination = destination

        val frequency = SavingsFrequency.fromRouteValue(destination.frequency)
        val seededAmount = destination.initialAmount?.filter(Char::isDigit).orEmpty()
        val initialAmount = seededAmount.takeIf { it.isNotBlank() } ?: frequency.defaultAmount.toString()
        val executionDay = destination.initialExecutionDay ?: frequency.defaultExecutionDay()
        val initialStatusBucket = destination.initialStatus?.let { status ->
            SavingsMandate(
                id = destination.mandateId.orEmpty(),
                userId = null,
                name = "",
                amount = initialAmount,
                frequency = frequency.apiValue,
                startDate = "",
                status = status,
                juspayMandateId = null,
                promoCode = null,
                nextExecutionDate = null,
                billingCurrentAmount = initialAmount,
                billingNextExecutionAmount = initialAmount,
                billingLastEventName = null,
                billingLastEventAt = null,
                consecutiveFailures = 0,
                createdAt = null,
                updatedAt = null,
                billing = null,
            ).statusBucket()
        }
        val isPaused = initialStatusBucket == SavingsStatusBucket.Paused
        val initialAmountValue = initialAmount.toIntOrNull()
        val displayAmount = when {
            destination.mandateId == null -> initialAmount
            isPaused -> initialAmount
            initialAmountValue != null -> recommendedUpgradeAmount(
                currentAmount = initialAmountValue,
                quickAmounts = frequency.quickAmounts,
                maxAmount = frequency.maxAmount,
            ).toString()
            else -> initialAmount
        }

        _state.value = SavingsSetupUiState(
            frequency = frequency,
            amountText = displayAmount,
            selectedExecutionDay = executionDay,
            mandateId = destination.mandateId,
            currentAmount = initialAmountValue,
            isUpgradeFlow = destination.mandateId != null,
            isPausedMandate = isPaused,
            initialStatus = destination.initialStatus,
        )
        preloadCoupons()
    }

    private fun updateAmount(rawValue: String) {
        if (!_state.value.canEditAmount) return
        _state.value = _state.value.copy(
            amountText = rawValue.filter(Char::isDigit).take(5),
            inlineErrorMessage = null,
        )
    }

    private fun updateExecutionDay(day: Int) {
        if (!_state.value.canEditAmount) return
        _state.value = _state.value.copy(
            selectedExecutionDay = day,
            inlineErrorMessage = null,
        )
    }

    private fun updateCouponDraft(rawValue: String) {
        _state.value = _state.value.copy(
            couponDraft = rawValue.uppercase().filter { it.isLetterOrDigit() || it == '_' }.take(SIP_MAX_COUPON_LENGTH),
            inlineErrorMessage = null,
        )
    }

    private fun clearAppliedCoupon() {
        _state.value = _state.value.copy(
            appliedCoupon = null,
            inlineErrorMessage = null,
        )
    }

    private fun preloadCoupons() {
        val couponUseCase = getTradeAvailableCouponsUseCase ?: return
        viewModelScope.launch {
            val amount = _state.value.amountValue?.toDouble()
            when (val result = couponUseCase(orderType = TradeCouponOrderType.SIP, amount = amount)) {
                is ApiResult.Success -> _state.value = _state.value.copy(availableCoupons = result.value)
                is ApiResult.Failure -> Unit
            }
        }
    }

    private fun applyCoupon(code: String) {
        val couponUseCase = validateTradeCouponUseCase ?: return
        val current = _state.value
        val amount = current.amountValue?.toDouble()
        if (code.isBlank() || amount == null || amount <= 0.0) return
        viewModelScope.launch {
            when (
                val result = couponUseCase(
                    TradeCouponValidationRequest(
                        orderType = TradeCouponOrderType.SIP,
                        code = code,
                        amount = amount,
                    )
                )
            ) {
                is ApiResult.Success -> {
                    _state.value = _state.value.copy(
                        appliedCoupon = result.value,
                        couponDraft = "",
                        inlineErrorMessage = null,
                    )
                }

                is ApiResult.Failure -> {
                    _state.value = _state.value.copy(inlineErrorMessage = result.error.message)
                }
            }
        }
    }

    private fun submit() {
        val current = _state.value
        val amount = current.amountValue
        val validationError = validate(current, amount)
        if (validationError != null) {
            _state.value = current.copy(inlineErrorMessage = validationError)
            return
        }

        viewModelScope.launch {
            _state.value = current.copy(
                isSubmitting = true,
                inlineErrorMessage = null,
            )
            val request = SavingsCreateMandateRequest(
                amount = amount!!,
                frequency = current.frequency.apiValue,
                name = "${current.frequency.displayName()} Gold Savings",
                goalType = "SAVINGS",
                executionDay = current.selectedExecutionDay,
                promoCode = current.appliedCoupon?.code,
            )
            val result = if (current.mandateId == null) {
                createSavingsMandateSessionUseCase(request)
            } else {
                updateSavingsMandateSessionUseCase(current.mandateId, request)
            }

            when (result) {
                is ApiResult.Failure -> {
                    _state.value = _state.value.copy(
                        isSubmitting = false,
                        inlineErrorMessage = result.error.message,
                    )
                }

                is ApiResult.Success -> {
                    val payloadJson = result.value.sdkPayloadJson?.takeIf { it.isNotBlank() }
                    if (payloadJson.isNullOrBlank()) {
                        _state.value = _state.value.copy(
                            isSubmitting = false,
                            inlineErrorMessage = "Unable to continue savings setup right now.",
                        )
                        return@launch
                    }
                    _state.value = _state.value.copy(
                        isSubmitting = false,
                        pendingMandateId = result.value.mandateId,
                    )
                    _effects.emit(
                        SavingsSetupEffect.LaunchPayment(
                            TradePaymentLaunchRequest.Juspay(
                                payloadJson = payloadJson,
                                context = if (current.mandateId == null) {
                                    TradePaymentContext.BuySipSetup
                                } else {
                                    TradePaymentContext.BuySipUpgrade
                                },
                            ),
                        )
                    )
                }
            }
        }
    }

    private fun handlePaymentResult(result: TradePaymentLaunchResult) {
        when (result) {
            TradePaymentLaunchResult.BackPressed -> resetToForm()
            is TradePaymentLaunchResult.Success -> {
                val pendingMandateId = _state.value.pendingMandateId
                if (pendingMandateId.isNullOrBlank()) {
                    _state.value = _state.value.copy(
                        phase = SavingsSetupPhase.Failure("Unable to verify your savings plan right now."),
                    )
                    return
                }
                pollMandateStatus(pendingMandateId)
            }

            is TradePaymentLaunchResult.Failure -> {
                val pendingMandateId = _state.value.pendingMandateId
                if (pendingMandateId.isNullOrBlank()) {
                    _state.value = _state.value.copy(
                        inlineErrorMessage = result.message,
                        pendingMandateId = null,
                        phase = SavingsSetupPhase.Form,
                    )
                    return
                }
                pollMandateStatus(pendingMandateId)
            }
        }
    }

    private fun retryPolling() {
        val pendingMandateId = _state.value.pendingMandateId
        if (pendingMandateId.isNullOrBlank()) {
            resetToForm()
            return
        }
        pollMandateStatus(pendingMandateId)
    }

    private fun pollMandateStatus(mandateId: String) {
        viewModelScope.launch {
            var latestStatusBucket: SavingsStatusBucket? = null
            repeat(SIP_MAX_POLLS) { index ->
                val attempt = index + 1
                _state.value = _state.value.copy(
                    phase = SavingsSetupPhase.Polling(mandateId, attempt),
                    inlineErrorMessage = null,
                )
                when (val result = getSavingsMandateUseCase(mandateId)) {
                    is ApiResult.Success -> {
                        latestStatusBucket = result.value.statusBucket()
                        when (latestStatusBucket) {
                            SavingsStatusBucket.Success -> {
                                _state.value = _state.value.copy(
                                    existingMandate = result.value,
                                    pendingMandateId = null,
                                    phase = SavingsSetupPhase.Success(mandateId),
                                )
                                return@launch
                            }

                            SavingsStatusBucket.Failed,
                            SavingsStatusBucket.Cancelled -> {
                                _state.value = _state.value.copy(
                                    pendingMandateId = null,
                                    phase = SavingsSetupPhase.Failure("Savings registration failed. Please try again."),
                                )
                                return@launch
                            }

                            else -> Unit
                        }
                    }

                    is ApiResult.Failure -> Unit
                }
                if (attempt < SIP_MAX_POLLS) {
                    delay(SIP_POLL_INTERVAL_MS)
                }
            }

            _state.value = _state.value.copy(
                phase = if (latestStatusBucket == null || latestStatusBucket == SavingsStatusBucket.Pending || latestStatusBucket == SavingsStatusBucket.Unknown) {
                    SavingsSetupPhase.Processing(mandateId)
                } else {
                    SavingsSetupPhase.Failure("Unable to confirm savings registration status.")
                },
            )
        }
    }

    private fun resetToForm() {
        _state.value = _state.value.copy(
            isSubmitting = false,
            inlineErrorMessage = null,
            pendingMandateId = null,
            phase = SavingsSetupPhase.Form,
        )
    }

    private fun validate(
        state: SavingsSetupUiState,
        amount: Int?,
    ): String? {
        if (amount == null) {
            return "Enter a valid savings amount."
        }
        if (amount < state.frequency.minAmount || amount > state.frequency.maxAmount) {
            return "Savings amount must be between ₹${state.frequency.minAmount} and ₹${state.frequency.maxAmount}."
        }
        if (state.isUpgradeFlow && !state.isPausedMandate) {
            val currentAmount = state.currentAmount
            if (currentAmount != null && amount <= currentAmount) {
                return "Choose an amount greater than your current savings amount to upgrade."
            }
        }
        return when (state.frequency) {
            SavingsFrequency.Daily -> null
            SavingsFrequency.Weekly -> {
                if (state.selectedExecutionDay in 1..7) null else "Select the weekday for your savings plan."
            }

            SavingsFrequency.Monthly -> {
                if (state.selectedExecutionDay in 1..31) null else "Select the monthly execution date."
            }
        }
    }

    private fun SavingsMandate.currentAmountValue(): Int? {
        return billing?.currentAmount
            ?.filter(Char::isDigit)
            ?.toIntOrNull()
            ?: billingCurrentAmount?.filter(Char::isDigit)?.toIntOrNull()
            ?: amount.filter(Char::isDigit).toIntOrNull()
    }

    private fun SavingsMandate.defaultExecutionDay(frequency: SavingsFrequency): Int? {
        val parsedDate = nextExecutionDate
            ?.take(10)
            ?.let { rawDate -> runCatching { LocalDate.parse(rawDate) }.getOrNull() }
        return when (frequency) {
            SavingsFrequency.Daily -> null
            SavingsFrequency.Weekly -> parsedDate?.dayOfWeek?.savingsIsoDayNumber() ?: frequency.defaultExecutionDay()
            SavingsFrequency.Monthly -> parsedDate?.day ?: frequency.defaultExecutionDay()
        }
    }

    private fun DayOfWeek.savingsIsoDayNumber(): Int {
        return when (this) {
            DayOfWeek.MONDAY -> 1
            DayOfWeek.TUESDAY -> 2
            DayOfWeek.WEDNESDAY -> 3
            DayOfWeek.THURSDAY -> 4
            DayOfWeek.FRIDAY -> 5
            DayOfWeek.SATURDAY -> 6
            DayOfWeek.SUNDAY -> 7
        }
    }

    private companion object {
        const val SIP_POLL_INTERVAL_MS = 5_000L
        const val SIP_MAX_POLLS = 6
        const val SIP_MAX_COUPON_LENGTH = 20
    }
}
