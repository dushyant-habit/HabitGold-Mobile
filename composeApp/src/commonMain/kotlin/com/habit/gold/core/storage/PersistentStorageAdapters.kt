package com.habit.gold.core.storage

import com.habit.gold.feature.auth.domain.AuthenticatedUser
import kotlinx.serialization.json.Json

private const val USER_PROFILE_KEY = "session.user_profile"
private const val SESSION_METADATA_KEY = "session.metadata"
private const val APP_PREFERENCES_KEY = "app.preferences"

private val appStorageJson = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}

class JsonUserProfileStorage(
    private val storage: KeyValueStorage,
    private val json: Json = appStorageJson,
) : UserProfileStorage {
    override suspend fun readUser(): AuthenticatedUser? {
        val raw = storage.read(USER_PROFILE_KEY) ?: return null
        return runCatching {
            json.decodeFromString<AuthenticatedUser>(raw)
        }.getOrNull()
    }

    override suspend fun writeUser(user: AuthenticatedUser) {
        storage.write(
            key = USER_PROFILE_KEY,
            value = json.encodeToString(user),
        )
    }

    override suspend fun clearUser() {
        storage.delete(USER_PROFILE_KEY)
    }
}

class JsonSessionMetadataStorage(
    private val storage: KeyValueStorage,
    private val json: Json = appStorageJson,
) : SessionMetadataStorage {
    override suspend fun readMetadata(): SessionMetadata? {
        val raw = storage.read(SESSION_METADATA_KEY) ?: return null
        return runCatching {
            json.decodeFromString<SessionMetadata>(raw)
        }.getOrNull()
    }

    override suspend fun writeMetadata(metadata: SessionMetadata) {
        storage.write(
            key = SESSION_METADATA_KEY,
            value = json.encodeToString(metadata),
        )
    }

    override suspend fun clearMetadata() {
        storage.delete(SESSION_METADATA_KEY)
    }
}

class JsonAppPreferencesStorage(
    private val storage: KeyValueStorage,
    private val json: Json = appStorageJson,
) : AppPreferencesStorage {
    override suspend fun readPreferences(): AppPreferences {
        val raw = storage.read(APP_PREFERENCES_KEY) ?: return AppPreferences()
        return runCatching {
            json.decodeFromString<AppPreferences>(raw)
        }.getOrElse { AppPreferences() }
    }

    override suspend fun writePreferences(preferences: AppPreferences) {
        storage.write(
            key = APP_PREFERENCES_KEY,
            value = json.encodeToString(preferences),
        )
    }

    override suspend fun clearPreferences() {
        storage.delete(APP_PREFERENCES_KEY)
    }
}
