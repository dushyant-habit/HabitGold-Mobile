package com.habit.gold.feature.alerts.data.repository

import com.habit.gold.core.storage.AppPreferencesStorage
import com.habit.gold.feature.alerts.data.local.AlertsStorage
import com.habit.gold.feature.alerts.domain.model.AlertItem
import com.habit.gold.feature.alerts.domain.repository.AlertsRepository

class AlertsRepositoryImpl(
    private val alertsStorage: AlertsStorage,
    private val appPreferencesStorage: AppPreferencesStorage,
) : AlertsRepository {
    override suspend fun getAlerts(): List<AlertItem> {
        return alertsStorage.readAlerts()
            .sortedByDescending { it.createdAt }
            .map { alert ->
                AlertItem(
                    id = alert.id,
                    title = alert.title,
                    description = alert.description,
                    createdAt = alert.createdAt,
                    isRead = alert.isRead,
                )
            }
    }

    override suspend fun markAllAlertsRead() {
        val alerts = alertsStorage.readAlerts()
        if (alerts.none { !it.isRead }) {
            val currentPreferences = appPreferencesStorage.readPreferences()
            if (currentPreferences.hasUnreadAlerts) {
                appPreferencesStorage.writePreferences(
                    currentPreferences.copy(hasUnreadAlerts = false),
                )
            }
            return
        }

        alertsStorage.writeAlerts(
            alerts.map { it.copy(isRead = true) },
        )
        val currentPreferences = appPreferencesStorage.readPreferences()
        appPreferencesStorage.writePreferences(
            currentPreferences.copy(hasUnreadAlerts = false),
        )
    }
}
