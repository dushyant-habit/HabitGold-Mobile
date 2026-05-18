package com.habit.gold.feature.profile.presentation

import android.content.Context
import android.content.ContextWrapper
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

@Composable
internal actual fun rememberProfileBiometricAuthenticator(): ProfileBiometricAuthenticator {
    val context = LocalContext.current
    return remember(context) { AndroidProfileBiometricAuthenticator(context) }
}

private class AndroidProfileBiometricAuthenticator(
    private val context: Context,
) : ProfileBiometricAuthenticator {

    override val label: String = "Fingerprint"

    override suspend fun authenticate(
        promptTitle: String,
        promptSubtitle: String,
        cancelLabel: String,
    ): ProfileBiometricAuthResult = suspendCancellableCoroutine { continuation ->
        val biometricManager = BiometricManager.from(context)
        val canAuthenticate = biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.BIOMETRIC_WEAK,
        )
        if (canAuthenticate != BiometricManager.BIOMETRIC_SUCCESS) {
            continuation.resume(
                ProfileBiometricAuthResult.Unavailable("No fingerprint biometric enabled on your device."),
            )
            return@suspendCancellableCoroutine
        }

        val activity = context.findFragmentActivity()
        if (activity == null) {
            continuation.resume(
                ProfileBiometricAuthResult.Error("Unable to start biometric authentication right now."),
            )
            return@suspendCancellableCoroutine
        }

        val prompt = BiometricPrompt(
            activity,
            ContextCompat.getMainExecutor(context),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    if (continuation.isActive) {
                        continuation.resume(ProfileBiometricAuthResult.Success)
                    }
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    if (continuation.isActive) {
                        continuation.resume(ProfileBiometricAuthResult.Error(errString.toString()))
                    }
                }

                override fun onAuthenticationFailed() {
                    // The platform keeps the prompt open, so we intentionally wait for the
                    // final success/error callback instead of surfacing a premature failure.
                }
            },
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(promptTitle)
            .setSubtitle(promptSubtitle)
            .setNegativeButtonText(cancelLabel)
            .build()

        prompt.authenticate(promptInfo)
    }
}

private tailrec fun Context.findFragmentActivity(): FragmentActivity? {
    return when (this) {
        is FragmentActivity -> this
        is ContextWrapper -> baseContext.findFragmentActivity()
        else -> null
    }
}
