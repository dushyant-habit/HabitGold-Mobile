package com.habit.gold.feature.alerts.domain.repository

import com.habit.gold.feature.alerts.domain.model.AlertItem

interface AlertsRepository {
    suspend fun getAlerts(): List<AlertItem>
    suspend fun markAllAlertsRead()
}
