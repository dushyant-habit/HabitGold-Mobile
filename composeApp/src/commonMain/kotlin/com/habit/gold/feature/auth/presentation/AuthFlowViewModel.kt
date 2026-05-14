package com.habit.gold.feature.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habit.gold.PlatformInfo
import com.habit.gold.core.config.AppConfig
import com.habit.gold.core.network.ApiResult
import com.habit.gold.core.session.SessionStore
import com.habit.gold.feature.auth.domain.AuthRepository
import com.habit.gold.feature.auth.domain.AuthValidators
import com.habit.gold.feature.auth.domain.AuthenticatedUser
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AuthStep {
    Login,
    Otp,
    BasicInfo,
}

data class AuthFlowUiState(
    val appName: String = "",
    val platformLabel: String = "",
    val environmentLabel: String = "",
    val screen: AuthStep = AuthStep.Login,
    val phoneNumber: String = "",
    val otpCode: String = "",
    val otpRefId: String = "",
    val name: String = "",
    val email: String = "",
    val pinCode: String = "",
    val user: AuthenticatedUser? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val resendSecondsRemaining: Int = 0,
) {
    val canResendOtp: Boolean = resendSecondsRemaining == 0 && !isLoading
}

class AuthFlowViewModel(
    appConfig: AppConfig,
    platformInfo: PlatformInfo,
    private val authRepository: AuthRepository,
    private val sessionStore: SessionStore,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AuthFlowUiState(
            appName = appConfig.appName,
            platformLabel = "${platformInfo.name} ${platformInfo.version}",
            environmentLabel = appConfig.environment.name,
        )
    )
    val uiState: StateFlow<AuthFlowUiState> = _uiState.asStateFlow()

    private var resendTimerJob: Job? = null

    init {
        viewModelScope.launch {
            sessionStore.state.collectLatest(::syncFromSession)
        }
    }

    fun onPhoneChanged(rawValue: String) {
        _uiState.update {
            it.copy(
                phoneNumber = AuthValidators.normalizePhone(rawValue),
                errorMessage = null,
            )
        }
    }

    fun onOtpChanged(rawValue: String) {
        _uiState.update {
            it.copy(
                otpCode = AuthValidators.normalizeOtp(rawValue),
                errorMessage = null,
            )
        }
    }

    fun onNameChanged(rawValue: String) {
        _uiState.update { it.copy(name = rawValue, errorMessage = null) }
    }

    fun onEmailChanged(rawValue: String) {
        _uiState.update { it.copy(email = rawValue, errorMessage = null) }
    }

    fun onPinCodeChanged(rawValue: String) {
        _uiState.update {
            it.copy(
                pinCode = AuthValidators.normalizePinCode(rawValue),
                errorMessage = null,
            )
        }
    }

    fun requestOtp() {
        val phoneNumber = uiState.value.phoneNumber
        if (!AuthValidators.isPhoneValid(phoneNumber)) {
            showError("Enter a valid 10-digit mobile number.")
            return
        }

        viewModelScope.launch {
            setLoading(true)
            when (val result = authRepository.requestOtp(phoneNumber)) {
                is ApiResult.Success -> {
                    _uiState.update {
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

    fun verifyOtp() {
        val state = uiState.value
        if (!AuthValidators.isOtpValid(state.otpCode)) {
            showError("Enter the 6-digit OTP sent to your mobile number.")
            return
        }

        viewModelScope.launch {
            setLoading(true)
            when (val result = authRepository.verifyOtp(state.phoneNumber, state.otpCode)) {
                is ApiResult.Success -> {
                    resendTimerJob?.cancel()
                    val user = result.value.user
                    _uiState.update {
                        it.copy(
                            screen = if (result.value.requiresBasicInfo) AuthStep.BasicInfo else AuthStep.Otp,
                            user = user,
                            name = user.name,
                            email = user.email,
                            pinCode = user.pinCode,
                            otpCode = "",
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

    fun resendOtp() {
        if (!uiState.value.canResendOtp) return
        requestOtp()
    }

    fun returnToLogin() {
        resendTimerJob?.cancel()
        _uiState.update {
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

    fun submitBasicInfo() {
        val state = uiState.value
        val trimmedName = state.name.trim()
        val trimmedEmail = state.email.trim()
        val trimmedPinCode = state.pinCode.trim()

        when {
            trimmedName.isBlank() -> {
                showError("Enter your full name.")
                return
            }
            !AuthValidators.isEmailValid(trimmedEmail) -> {
                showError("Enter a valid email address.")
                return
            }
            !AuthValidators.isPinCodeValid(trimmedPinCode) -> {
                showError("Enter a valid 6-digit pin code.")
                return
            }
        }

        viewModelScope.launch {
            setLoading(true)
            when (val result = authRepository.submitBasicInfo(trimmedName, trimmedEmail, trimmedPinCode)) {
                is ApiResult.Success -> {
                    _uiState.update {
                        it.copy(
                            screen = AuthStep.BasicInfo,
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

    private fun startResendCountdown() {
        resendTimerJob?.cancel()
        resendTimerJob = viewModelScope.launch {
            while (uiState.value.resendSecondsRemaining > 0) {
                delay(1_000)
                _uiState.update { state ->
                    state.copy(
                        resendSecondsRemaining = (state.resendSecondsRemaining - 1).coerceAtLeast(0),
                    )
                }
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        _uiState.update { it.copy(isLoading = isLoading, errorMessage = null) }
    }

    private fun showError(message: String) {
        _uiState.update { it.copy(isLoading = false, errorMessage = message) }
    }

    private fun syncFromSession(session: com.habit.gold.core.session.AuthSession) {
        if (!session.isLoggedIn) {
            resendTimerJob?.cancel()
            _uiState.update {
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

        _uiState.update {
            it.copy(
                screen = if (session.isProfileComplete) AuthStep.Login else AuthStep.BasicInfo,
                phoneNumber = session.user?.phoneNumber.orEmpty(),
                name = session.user?.name.orEmpty(),
                email = session.user?.email.orEmpty(),
                pinCode = session.user?.pinCode.orEmpty(),
                user = session.user,
                errorMessage = null,
            )
        }
    }
}
