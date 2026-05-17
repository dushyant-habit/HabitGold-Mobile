package com.habit.gold.core.storage

import kotlinx.serialization.Serializable
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Serializable
data class AppPreferences(
    val hasUnreadAlerts: Boolean = false,
    val isBalanceVisible: Boolean = true,
)

interface AppPreferencesStorage {
    suspend fun readPreferences(): AppPreferences
    suspend fun writePreferences(preferences: AppPreferences)
    suspend fun clearPreferences()
}

class InMemoryAppPreferencesStorage : AppPreferencesStorage {
    private val mutex = Mutex()
    private var preferences = AppPreferences()

    override suspend fun readPreferences(): AppPreferences = mutex.withLock {
        preferences
    }

    override suspend fun writePreferences(preferences: AppPreferences) {
        mutex.withLock {
            this.preferences = preferences
        }
    }

    override suspend fun clearPreferences() {
        mutex.withLock {
            preferences = AppPreferences()
        }
    }
}
