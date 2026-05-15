package com.habit.gold.core.storage

import platform.Foundation.NSUserDefaults

private const val SECURE_SUITE_NAME = "com.habit.gold.secure"
private const val APP_SUITE_NAME = "com.habit.gold.app"

actual fun createPlatformSecureStorage(): SecureStorage {
    return AppleUserDefaultsStorage(
        userDefaults = NSUserDefaults(suiteName = SECURE_SUITE_NAME),
    )
}

actual fun createPlatformPreferencesStorage(): KeyValueStorage {
    return AppleUserDefaultsStorage(
        userDefaults = NSUserDefaults(suiteName = APP_SUITE_NAME),
    )
}

private class AppleUserDefaultsStorage(
    private val userDefaults: NSUserDefaults,
) : SecureStorage, KeyValueStorage {

    override suspend fun read(key: String): String? = userDefaults.stringForKey(key)

    override suspend fun write(key: String, value: String) {
        userDefaults.setObject(value, forKey = key)
    }

    override suspend fun delete(key: String) {
        userDefaults.removeObjectForKey(key)
    }

    override suspend fun clear() {
        val keys = userDefaults.dictionaryRepresentation().keys
        for (key in keys) {
            val stringKey = key as? String ?: continue
            userDefaults.removeObjectForKey(stringKey)
        }
    }
}
