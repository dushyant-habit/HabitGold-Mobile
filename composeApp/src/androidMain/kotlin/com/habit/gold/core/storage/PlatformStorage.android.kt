package com.habit.gold.core.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

private const val SECURE_PREFS_NAME = "habitgold_secure_storage"
private const val APP_PREFS_NAME = "habitgold_app_storage"

private lateinit var applicationContext: Context

/**
 * Binds Android's application context before shared DI requests platform-backed storage.
 */
fun initializePlatformStorage(context: Context) {
    applicationContext = context.applicationContext
}

actual fun createPlatformSecureStorage(): SecureStorage {
    val context = platformContext()
    return AndroidSharedPreferencesStorage(
        sharedPreferences = createEncryptedSharedPreferences(context),
    )
}

actual fun createPlatformPreferencesStorage(): KeyValueStorage {
    return AndroidSharedPreferencesStorage(
        sharedPreferences = platformContext().getSharedPreferences(
            APP_PREFS_NAME,
            Context.MODE_PRIVATE,
        ),
    )
}

private fun platformContext(): Context {
    check(::applicationContext.isInitialized) {
        "Platform storage must be initialized before starting Koin."
    }
    return applicationContext
}

private fun createEncryptedSharedPreferences(
    context: Context,
): SharedPreferences {
    val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    return EncryptedSharedPreferences.create(
        context,
        SECURE_PREFS_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )
}

private class AndroidSharedPreferencesStorage(
    private val sharedPreferences: SharedPreferences,
) : SecureStorage, KeyValueStorage {

    override suspend fun read(key: String): String? = sharedPreferences.getString(key, null)

    override suspend fun write(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).commit()
    }

    override suspend fun delete(key: String) {
        sharedPreferences.edit().remove(key).commit()
    }

    override suspend fun clear() {
        sharedPreferences.edit().clear().commit()
    }
}
