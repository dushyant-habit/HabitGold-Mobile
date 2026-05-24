package com.habit.gold.feature.profile.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSError
import platform.LocalAuthentication.LAContext
import platform.LocalAuthentication.LABiometryTypeFaceID
import platform.LocalAuthentication.LABiometryTypeTouchID
import platform.LocalAuthentication.LAPolicyDeviceOwnerAuthenticationWithBiometrics
import kotlin.coroutines.resume

@Composable
internal actual fun rememberProfileBiometricAuthenticator(): ProfileBiometricAuthenticator {
    return remember { IosProfileBiometricAuthenticator() }
}

private class IosProfileBiometricAuthenticator : ProfileBiometricAuthenticator {

    override val label: String
        get() = labelFor(createContext())

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun authenticate(
        promptTitle: String,
        promptSubtitle: String,
        cancelLabel: String,
    ): ProfileBiometricAuthResult = suspendCancellableCoroutine { continuation ->
        val context = createContext()
        memScoped {
            val errorPointer = alloc<ObjCObjectVar<NSError?>>()
            if (!context.canEvaluatePolicy(LAPolicyDeviceOwnerAuthenticationWithBiometrics, errorPointer.ptr)) {
                val unavailableMessage = when (labelFor(context)) {
                    "Face ID" -> "Face ID is not available on this device."
                    "Touch ID" -> "Touch ID is not available on this device."
                    else -> "Biometric authentication is not available on this device."
                }
                continuation.resume(ProfileBiometricAuthResult.Unavailable(unavailableMessage))
                return@memScoped
            }
        }

        context.evaluatePolicy(
            policy = LAPolicyDeviceOwnerAuthenticationWithBiometrics,
            localizedReason = promptSubtitle,
        ) { success, error ->
            if (!continuation.isActive) return@evaluatePolicy
            if (success) {
                continuation.resume(ProfileBiometricAuthResult.Success)
            } else {
                continuation.resume(
                    ProfileBiometricAuthResult.Error(
                        error?.localizedDescription ?: "${labelFor(context)} authentication was cancelled.",
                    ),
                )
            }
        }
    }
}

private fun createContext(): LAContext {
    return LAContext().apply {
        localizedFallbackTitle = ""
    }
}

private fun labelFor(context: LAContext): String {
    return when (context.biometryType) {
        LABiometryTypeFaceID -> "Face ID"
        LABiometryTypeTouchID -> "Touch ID"
        else -> "Biometric"
    }
}
