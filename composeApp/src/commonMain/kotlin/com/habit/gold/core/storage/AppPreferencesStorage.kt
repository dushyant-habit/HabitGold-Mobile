package com.habit.gold.core.storage

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class AppPreferences(
    val hasUnreadAlerts: Boolean = false,
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
