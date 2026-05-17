package com.habit.gold.feature.auth.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.habit.gold.feature.auth.presentation.components.AuthBasicDetailsScreen
import com.habit.gold.feature.auth.presentation.components.AuthHandoffScreen
import com.habit.gold.feature.auth.presentation.components.AuthLoginScreen
import com.habit.gold.feature.auth.presentation.components.AuthOtpScreen

@Composable
fun AuthFlowScreen(
    uiState: AuthFlowUiState,
    onIntent: (AuthIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        when (uiState.screen) {
            AuthStep.Login -> AuthLoginScreen(
                uiState = uiState,
                onPhoneChanged = { onIntent(AuthIntent.UpdatePhoneNumber(it)) },
                onRequestOtp = { onIntent(AuthIntent.RequestOtp) },
            )
            AuthStep.Otp -> AuthOtpScreen(
                uiState = uiState,
                onOtpChanged = { onIntent(AuthIntent.UpdateOtp(it)) },
                onVerifyOtp = { onIntent(AuthIntent.VerifyOtp) },
                onBackToLogin = { onIntent(AuthIntent.ReturnToLogin) },
                onResendOtp = { onIntent(AuthIntent.ResendOtp) },
            )
            AuthStep.BasicDetails -> AuthBasicDetailsScreen(
                uiState = uiState,
                onBackToOtp = { onIntent(AuthIntent.ReturnToOtp) },
                onLegalNameChanged = { onIntent(AuthIntent.UpdateLegalName(it)) },
                onReferralCodeChanged = { onIntent(AuthIntent.UpdateReferralCode(it)) },
                onPinCodeChanged = { onIntent(AuthIntent.UpdatePinCode(it)) },
                onSubmitBasicDetails = { onIntent(AuthIntent.SubmitBasicDetails) },
            )
            AuthStep.Handoff -> AuthHandoffScreen(
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
