package com.habit.gold.feature.auth.presentation

import androidx.lifecycle.viewModelScope
import com.habit.gold.PlatformInfo
import com.habit.gold.core.config.AppConfig
import com.habit.gold.core.localization.AppStrings
import com.habit.gold.core.network.ApiResult
import com.habit.gold.core.presentation.mvi.MviViewModel
import com.habit.gold.core.session.AuthSession
import com.habit.gold.core.session.SessionStore
import com.habit.gold.feature.auth.domain.AuthValidators
import com.habit.gold.feature.auth.domain.usecase.RequestOtpUseCase
import com.habit.gold.feature.auth.domain.usecase.SubmitBasicDetailsUseCase
import com.habit.gold.feature.auth.domain.usecase.VerifyOtpUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AuthFlowViewModel(
    appConfig: AppConfig,
    platformInfo: PlatformInfo,
    private val appStrings: AppStrings,
    private val requestOtpUseCase: RequestOtpUseCase,
    private val verifyOtpUseCase: VerifyOtpUseCase,
    private val submitBasicDetailsUseCase: SubmitBasicDetailsUseCase,
    private val sessionStore: SessionStore,
) : MviViewModel<AuthFlowUiState, AuthIntent, AuthEffect>(
    initialState = AuthFlowUiState(
        appName = appConfig.appName,
        platformLabel = "${platformInfo.name} ${platformInfo.version}",
        environmentLabel = appConfig.environment.name,
    )
) {
    val uiState: StateFlow<AuthFlowUiState> = state

    private val resendIntervals = listOf(30, 60, 120, 300)
    private var resendTimerJob: Job? = null

    init {
        viewModelScope.launch {
            sessionStore.state.collectLatest(::syncFromSession)
        }
    }

    override fun onIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.UpdatePhoneNumber -> updatePhoneNumber(intent.rawValue)
            is AuthIntent.UpdateOtp -> updateOtp(intent.rawValue)
            is AuthIntent.UpdateLegalName -> updateLegalName(intent.rawValue)
            is AuthIntent.UpdateReferralCode -> updateReferralCode(intent.rawValue)
            is AuthIntent.UpdatePinCode -> updatePinCode(intent.rawValue)
            AuthIntent.RequestOtp -> requestOtp()
            AuthIntent.VerifyOtp -> verifyOtp()
            AuthIntent.ResendOtp -> resendOtp()
            AuthIntent.ReturnToLogin -> returnToLogin()
            AuthIntent.ReturnToOtp -> returnToOtp()
            AuthIntent.SubmitBasicDetails -> submitBasicDetails()
        }
    }

    private fun updatePhoneNumber(rawValue: String) {
        updateState {
            it.copy(
                phoneNumber = AuthValidators.normalizePhone(rawValue),
                errorMessage = null,
            )
        }
    }

    private fun updateOtp(rawValue: String) {
        updateState {
            it.copy(
                otpCode = AuthValidators.normalizeOtp(rawValue),
                errorMessage = null,
            )
        }
    }

    private fun updateLegalName(rawValue: String) {
        updateState {
            it.copy(
                legalName = AuthValidators.normalizeLegalName(rawValue),
                errorMessage = null,
            )
        }
    }

    private fun updateReferralCode(rawValue: String) {
        updateState {
            it.copy(
                referralCode = AuthValidators.normalizeReferralCode(rawValue),
                errorMessage = null,
            )
        }
    }

    private fun updatePinCode(rawValue: String) {
        updateState {
            it.copy(
                pinCode = AuthValidators.normalizePinCode(rawValue),
                errorMessage = null,
            )
        }
    }

    /**
     * Starts the OTP journey from the shared validation rules so both Android and iOS gate on the same input.
     */
    private fun requestOtp() {
        val phoneNumber = state.value.phoneNumber
        if (!AuthValidators.isPhoneValid(phoneNumber)) {
            showError(appStrings.authInvalidPhoneError)
            return
        }

        viewModelScope.launch {
            setLoading(true)
            when (val result = requestOtpUseCase(phoneNumber)) {
                is ApiResult.Success -> {
                    updateState {
                        it.copy(
                            screen = AuthStep.Otp,
                            otpCode = "",
                            otpRefId = result.value.refId,
                            isLoading = false,
                            errorMessage = null,
                            resendAttempt = 0,
                            resendSecondsRemaining = resendIntervals.first(),
                        )
                    }
                    startResendCountdown()
                }
                is ApiResult.Failure -> showError(result.error.message)
            }
        }
    }

    /**
     * Verifies OTP and respects the backend onboarding flags so shared auth stays aligned with production Android behavior.
     */
    private fun verifyOtp() {
        val currentState = state.value
        if (!AuthValidators.isOtpValid(currentState.otpCode)) {
            showError(appStrings.authInvalidOtpError)
            return
        }

        viewModelScope.launch {
            setLoading(true)
            when (val result = verifyOtpUseCase(currentState.phoneNumber, currentState.otpCode)) {
                is ApiResult.Success -> {
                    resendTimerJob?.cancel()
                    updateState {
                        it.copy(
                            screen = if (result.value.requiresBasicDetails) AuthStep.BasicDetails else AuthStep.Handoff,
                            user = result.value.user,
                            legalName = result.value.user.name,
                            pinCode = result.value.user.pinCode,
                            isPinCodeRequired = result.value.isPinCodeRequired,
                            isLoading = false,
                            errorMessage = null,
                            resendSecondsRemaining = 0,
                        )
                    }
                }
                is ApiResult.Failure -> showError(result.error.message)
            }
        }
    }

    private fun resendOtp() {
        val currentState = state.value
        if (!currentState.canResendOtp) return

        viewModelScope.launch {
            setLoading(true)
            when (val result = requestOtpUseCase(currentState.phoneNumber)) {
                is ApiResult.Success -> {
                    val nextAttempt = (currentState.resendAttempt + 1).coerceAtMost(resendIntervals.lastIndex)
                    updateState {
                        it.copy(
                            screen = AuthStep.Otp,
                            otpCode = "",
                            otpRefId = result.value.refId,
                            isLoading = false,
                            errorMessage = null,
                            resendAttempt = nextAttempt,
                            resendSecondsRemaining = resendIntervals[nextAttempt],
                        )
                    }
                    startResendCountdown()
                }
                is ApiResult.Failure -> showError(result.error.message)
            }
        }
    }

    private fun returnToLogin() {
        resendTimerJob?.cancel()
        updateState {
            it.copy(
                screen = AuthStep.Login,
                otpCode = "",
                otpRefId = "",
                legalName = "",
                referralCode = "",
                pinCode = "",
                isPinCodeRequired = true,
                user = null,
                isLoading = false,
                errorMessage = null,
                resendAttempt = 0,
                resendSecondsRemaining = 0,
            )
        }
    }

    private fun returnToOtp() {
        updateState {
            it.copy(
                screen = AuthStep.Otp,
                otpCode = "",
                isLoading = false,
                errorMessage = null,
            )
        }
    }

    /**
     * Completes onboarding using the exact shared contract that updates profile first and then applies the referral code.
     */
    private fun submitBasicDetails() {
        val currentState = state.value
        val trimmedLegalName = currentState.legalName.trim()
        val trimmedPinCode = currentState.pinCode.trim()
        val normalizedReferralCode = currentState.referralCode.trim().ifBlank { null }

        when {
            !AuthValidators.isLegalNameValid(trimmedLegalName) -> {
                showError(appStrings.authInvalidLegalNameError)
                return
            }
            currentState.isPinCodeRequired && !AuthValidators.isPinCodeValid(trimmedPinCode) -> {
                showError(appStrings.authInvalidPinCodeError)
                return
            }
        }

        viewModelScope.launch {
            setLoading(true)
            when (
                val result = submitBasicDetailsUseCase(
                    legalName = trimmedLegalName,
                    pinCode = trimmedPinCode.takeIf { currentState.isPinCodeRequired },
                    referralCode = normalizedReferralCode,
                )
            ) {
                is ApiResult.Success -> {
                    updateState {
                        it.copy(
                            screen = AuthStep.Handoff,
                            user = result.value,
                            legalName = result.value.name,
                            pinCode = result.value.pinCode,
                            isLoading = false,
                            errorMessage = null,
                        )
                    }
                }
                is ApiResult.Failure -> showError(result.error.message)
            }
        }
    }

    /**
     * Keeps resend timing fully inside shared auth state so Android and iOS stay in sync across navigation and restores.
     */
    private fun startResendCountdown() {
        resendTimerJob?.cancel()
        resendTimerJob = viewModelScope.launch {
            while (state.value.resendSecondsRemaining > 0) {
                delay(1_000)
                updateState { currentState ->
                    currentState.copy(
                        resendSecondsRemaining = (currentState.resendSecondsRemaining - 1).coerceAtLeast(0),
                    )
                }
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        updateState { it.copy(isLoading = isLoading, errorMessage = null) }
    }

    private fun showError(message: String) {
        updateState { it.copy(isLoading = false, errorMessage = message) }
    }

    private fun syncFromSession(session: AuthSession) {
        if (!session.isLoggedIn) {
            resendTimerJob?.cancel()
            updateState {
                it.copy(
                    screen = AuthStep.Login,
                    otpCode = "",
                    otpRefId = "",
                    legalName = "",
                    referralCode = "",
                    pinCode = "",
                    isPinCodeRequired = true,
                    user = null,
                    isLoading = false,
                    errorMessage = null,
                    resendAttempt = 0,
                    resendSecondsRemaining = 0,
                )
            }
            return
        }

        updateState {
            it.copy(
                screen = if (session.isProfileComplete) AuthStep.Handoff else AuthStep.BasicDetails,
                phoneNumber = session.user?.phoneNumber.orEmpty(),
                legalName = session.user?.name.orEmpty(),
                pinCode = session.user?.pinCode.orEmpty(),
                isPinCodeRequired = session.isPinCodeRequired,
                user = session.user,
                isLoading = false,
                errorMessage = null,
            )
        }
    }
}
