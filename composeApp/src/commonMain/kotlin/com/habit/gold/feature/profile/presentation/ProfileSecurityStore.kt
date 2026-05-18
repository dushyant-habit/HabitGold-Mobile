package com.habit.gold.feature.profile.presentation

import com.habit.gold.core.storage.SecureStorage

internal data class ProfileSecuritySettings(
    val biometricEnabled: Boolean,
)

internal class ProfileSecurityStore(
    private val secureStorage: SecureStorage,
) {
    suspend fun read(): ProfileSecuritySettings {
        return ProfileSecuritySettings(
            biometricEnabled = secureStorage.read(KEY_BIOMETRIC_ENABLED) == "true",
        )
    }

    suspend fun save(biometricEnabled: Boolean) {
        secureStorage.write(KEY_BIOMETRIC_ENABLED, biometricEnabled.toString())
    }

    suspend fun setBiometricEnabled(enabled: Boolean) {
        secureStorage.write(KEY_BIOMETRIC_ENABLED, enabled.toString())
    }

    suspend fun clear() {
        secureStorage.delete(KEY_BIOMETRIC_ENABLED)
    }

    private companion object {
        const val KEY_BIOMETRIC_ENABLED = "profile.security.biometric_enabled"
    }
}
