package com.habit.gold.feature.auth.presentation

import androidx.lifecycle.viewModelScope
import com.habit.gold.PlatformInfo
import com.habit.gold.core.config.AppConfig
import com.habit.gold.core.localization.AppStrings
import com.habit.gold.core.network.ApiResult
import com.habit.gold.core.presentation.mvi.MviViewModel
import com.habit.gold.core.session.AuthSession
import com.habit.gold.core.session.SessionStore
import com.habit.gold.feature.auth.domain.AuthRepository
import com.habit.gold.feature.auth.domain.AuthValidators
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AuthFlowViewModel(
    appConfig: AppConfig,
    platformInfo: PlatformInfo,
    private val appStrings: AppStrings,
    private val authRepository: AuthRepository,
    private val sessionStore: SessionStore,
) : MviViewModel<AuthFlowUiState, AuthIntent, AuthEffect>(
    initialState = AuthFlowUiState(
        appName = appConfig.appName,
        platformLabel = "${platformInfo.name} ${platformInfo.version}",
        environmentLabel = appConfig.environment.name,
    )
) {
    val uiState: StateFlow<AuthFlowUiState> = state

    private var resendTimerJob: Job? = null

    init {
        viewModelScope.launch {
            sessionStore.state.collectLatest(::syncFromSession)
        }
    }

    override fun onIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.UpdatePhoneNumber -> onPhoneChanged(intent.rawValue)
            is AuthIntent.UpdateOtp -> onOtpChanged(intent.rawValue)
            is AuthIntent.UpdateName -> onNameChanged(intent.rawValue)
            is AuthIntent.UpdateEmail -> onEmailChanged(intent.rawValue)
            is AuthIntent.UpdatePinCode -> onPinCodeChanged(intent.rawValue)
            AuthIntent.RequestOtp -> requestOtp()
            AuthIntent.VerifyOtp -> verifyOtp()
            AuthIntent.ResendOtp -> resendOtp()
            AuthIntent.ReturnToLogin -> returnToLogin()
            AuthIntent.SubmitBasicInfo -> submitBasicInfo()
        }
    }

    private fun onPhoneChanged(rawValue: String) {
        updateState {
            it.copy(
                phoneNumber = AuthValidators.normalizePhone(rawValue),
                errorMessage = null,
            )
        }
    }

    private fun onOtpChanged(rawValue: String) {
        updateState {
            it.copy(
                otpCode = AuthValidators.normalizeOtp(rawValue),
                errorMessage = null,
            )
        }
    }

    private fun onNameChanged(rawValue: String) {
        updateState { it.copy(name = rawValue, errorMessage = null) }
    }

    private fun onEmailChanged(rawValue: String) {
        updateState { it.copy(email = rawValue, errorMessage = null) }
    }

    private fun onPinCodeChanged(rawValue: String) {
        updateState {
            it.copy(
                pinCode = AuthValidators.normalizePinCode(rawValue),
                errorMessage = null,
            )
        }
    }

    private fun requestOtp() {
        val phoneNumber = state.value.phoneNumber
        if (!AuthValidators.isPhoneValid(phoneNumber)) {
            showError(appStrings.authInvalidPhoneError)
            return
        }

        viewModelScope.launch {
            setLoading(true)
            when (val result = authRepository.requestOtp(phoneNumber)) {
                is ApiResult.Success -> {
                    updateState {
                        it.copy(
                            screen = AuthStep.Otp,
                            otpCode = "",
                            otpRefId = result.value.refId,
                            isLoading = false,
                            errorMessage = null,
                            resendSecondsRemaining = 30,
                        )
                    }
                    startResendCountdown()
                }
                is ApiResult.Failure -> {
                    showError(result.error.message)
                }
            }
        }
    }

    private fun verifyOtp() {
        val currentState = state.value
        if (!AuthValidators.isOtpValid(currentState.otpCode)) {
            showError(appStrings.authInvalidOtpError)
            return
        }

        viewModelScope.launch {
            setLoading(true)
            when (val result = authRepository.verifyOtp(currentState.phoneNumber, currentState.otpCode)) {
                is ApiResult.Success -> {
                    resendTimerJob?.cancel()
                    updateState {
                        it.copy(
                            screen = if (result.value.requiresBasicInfo) AuthStep.BasicInfo else AuthStep.Handoff,
                            user = result.value.user,
                            name = result.value.user.name,
                            email = result.value.user.email,
                            pinCode = result.value.user.pinCode,
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
        if (!state.value.canResendOtp) return
        requestOtp()
    }

    private fun returnToLogin() {
        resendTimerJob?.cancel()
        updateState {
            it.copy(
                screen = AuthStep.Login,
                otpCode = "",
                otpRefId = "",
                errorMessage = null,
                resendSecondsRemaining = 0,
                isLoading = false,
            )
        }
    }

    private fun submitBasicInfo() {
        val currentState = state.value
        val trimmedName = currentState.name.trim()
        val trimmedEmail = currentState.email.trim()
        val trimmedPinCode = currentState.pinCode.trim()

        when {
            trimmedName.isBlank() -> {
                showError(appStrings.authBlankNameError)
                return
            }
            !AuthValidators.isEmailValid(trimmedEmail) -> {
                showError(appStrings.authInvalidEmailError)
                return
            }
            !AuthValidators.isPinCodeValid(trimmedPinCode) -> {
                showError(appStrings.authInvalidPinCodeError)
                return
            }
        }

        viewModelScope.launch {
            setLoading(true)
            when (val result = authRepository.submitBasicInfo(trimmedName, trimmedEmail, trimmedPinCode)) {
                is ApiResult.Success -> {
                    updateState {
                        it.copy(
                            screen = AuthStep.Handoff,
                            user = result.value,
                            name = result.value.name,
                            email = result.value.email,
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
     * Keeps resend timing fully inside shared auth state so Android and iOS behave identically.
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
                    name = "",
                    email = "",
                    pinCode = "",
                    user = null,
                    isLoading = false,
                    errorMessage = null,
                    resendSecondsRemaining = 0,
                )
            }
            return
        }

        updateState {
            it.copy(
                screen = if (session.isProfileComplete) AuthStep.Handoff else AuthStep.BasicInfo,
                phoneNumber = session.user?.phoneNumber.orEmpty(),
                name = session.user?.name.orEmpty(),
                email = session.user?.email.orEmpty(),
                pinCode = session.user?.pinCode.orEmpty(),
                user = session.user,
                isLoading = false,
                errorMessage = null,
            )
        }
    }
}
