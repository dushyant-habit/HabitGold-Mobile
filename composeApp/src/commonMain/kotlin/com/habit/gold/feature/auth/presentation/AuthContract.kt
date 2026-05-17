package com.habit.gold.feature.auth.presentation

import com.habit.gold.core.presentation.mvi.MviEffect
import com.habit.gold.core.presentation.mvi.MviIntent
import com.habit.gold.core.presentation.mvi.MviState
import com.habit.gold.feature.auth.domain.AuthenticatedUser

enum class AuthStep {
    Login,
    Otp,
    BasicDetails,
    Handoff,
}

data class AuthFlowUiState(
    val appName: String = "",
    val platformLabel: String = "",
    val environmentLabel: String = "",
    val screen: AuthStep = AuthStep.Login,
    val phoneNumber: String = "",
    val otpCode: String = "",
    val otpRefId: String = "",
    val legalName: String = "",
    val referralCode: String = "",
    val pinCode: String = "",
    val isPinCodeRequired: Boolean = true,
    val user: AuthenticatedUser? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val resendAttempt: Int = 0,
    val resendSecondsRemaining: Int = 0,
) : MviState {
    val canResendOtp: Boolean = resendSecondsRemaining == 0 && !isLoading
}

sealed interface AuthIntent : MviIntent {
    data class UpdatePhoneNumber(val rawValue: String) : AuthIntent
    data class UpdateOtp(val rawValue: String) : AuthIntent
    data class UpdateLegalName(val rawValue: String) : AuthIntent
    data class UpdateReferralCode(val rawValue: String) : AuthIntent
    data class UpdatePinCode(val rawValue: String) : AuthIntent
    data object RequestOtp : AuthIntent
    data object VerifyOtp : AuthIntent
    data object ResendOtp : AuthIntent
    data object ReturnToLogin : AuthIntent
    data object ReturnToOtp : AuthIntent
    data object SubmitBasicDetails : AuthIntent
}

sealed interface AuthEffect : MviEffect
