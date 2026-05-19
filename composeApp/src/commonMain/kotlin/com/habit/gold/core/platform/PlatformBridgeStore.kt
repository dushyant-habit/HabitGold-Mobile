package com.habit.gold.core.platform

import com.habit.gold.core.storage.KeyValueStorage

private const val KEY_PENDING_REFERRAL_CODE = "platform.pending_referral_code"
private const val KEY_INSTALL_REFERRER_CAPTURED = "platform.install_referrer_captured"
private const val KEY_CURRENT_DEVICE_TOKEN = "platform.current_device_token"
private const val KEY_LAST_REGISTERED_DEVICE_TOKEN = "platform.last_registered_device_token"

class PlatformBridgeStore(
    private val storage: KeyValueStorage,
) {
    suspend fun readPendingReferralCode(): String? {
        return storage.read(KEY_PENDING_REFERRAL_CODE)?.trim()?.takeIf { it.isNotEmpty() }
    }

    suspend fun writePendingReferralCode(referralCode: String?) {
        val value = referralCode?.trim()?.takeIf { it.isNotEmpty() }
        if (value == null) {
            storage.delete(KEY_PENDING_REFERRAL_CODE)
        } else {
            storage.write(KEY_PENDING_REFERRAL_CODE, value)
        }
    }

    suspend fun readInstallReferrerCaptured(): Boolean {
        return storage.read(KEY_INSTALL_REFERRER_CAPTURED).toBoolean()
    }

    suspend fun writeInstallReferrerCaptured(value: Boolean) {
        storage.write(KEY_INSTALL_REFERRER_CAPTURED, value.toString())
    }

    suspend fun readCurrentDeviceToken(): String? {
        return storage.read(KEY_CURRENT_DEVICE_TOKEN)?.trim()?.takeIf { it.isNotEmpty() }
    }

    suspend fun writeCurrentDeviceToken(token: String?) {
        val value = token?.trim()?.takeIf { it.isNotEmpty() }
        if (value == null) {
            storage.delete(KEY_CURRENT_DEVICE_TOKEN)
        } else {
            storage.write(KEY_CURRENT_DEVICE_TOKEN, value)
        }
    }

    suspend fun readLastRegisteredDeviceToken(): String? {
        return storage.read(KEY_LAST_REGISTERED_DEVICE_TOKEN)?.trim()?.takeIf { it.isNotEmpty() }
    }

    suspend fun writeLastRegisteredDeviceToken(token: String?) {
        val value = token?.trim()?.takeIf { it.isNotEmpty() }
        if (value == null) {
            storage.delete(KEY_LAST_REGISTERED_DEVICE_TOKEN)
        } else {
            storage.write(KEY_LAST_REGISTERED_DEVICE_TOKEN, value)
        }
    }
}
