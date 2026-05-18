package com.habit.gold.feature.profile.presentation

import androidx.compose.runtime.Composable

internal sealed interface ProfileBiometricAuthResult {
    data object Success : ProfileBiometricAuthResult
    data class Unavailable(val message: String) : ProfileBiometricAuthResult
    data class Error(val message: String) : ProfileBiometricAuthResult
}

internal interface ProfileBiometricAuthenticator {
    val label: String

    suspend fun authenticate(
        promptTitle: String,
        promptSubtitle: String,
        cancelLabel: String,
    ): ProfileBiometricAuthResult
}

@Composable
internal expect fun rememberProfileBiometricAuthenticator(): ProfileBiometricAuthenticator
