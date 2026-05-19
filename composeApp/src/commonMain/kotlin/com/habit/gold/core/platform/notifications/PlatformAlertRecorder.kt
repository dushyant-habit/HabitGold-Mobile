package com.habit.gold.core.platform.notifications

import com.habit.gold.core.storage.AppPreferencesStorage
import com.habit.gold.feature.alerts.data.local.AlertsStorage
import com.habit.gold.feature.alerts.data.local.StoredAlert
import kotlin.time.Clock

class PlatformAlertRecorder(
    private val alertsStorage: AlertsStorage,
    private val appPreferencesStorage: AppPreferencesStorage,
) {
    suspend fun record(title: String, description: String) {
        val trimmedTitle = title.trim().ifBlank { "HabitGold" }
        val trimmedDescription = description.trim()
        val currentAlerts = alertsStorage.readAlerts()
        val nextAlert = StoredAlert(
            id = "push-${Clock.System.now().toEpochMilliseconds()}",
            title = trimmedTitle,
            description = trimmedDescription,
            createdAt = Clock.System.now().toString(),
            isRead = false,
        )
        alertsStorage.writeAlerts((listOf(nextAlert) + currentAlerts).take(200))
        val currentPreferences = appPreferencesStorage.readPreferences()
        appPreferencesStorage.writePreferences(
            currentPreferences.copy(hasUnreadAlerts = true),
        )
    }
}
