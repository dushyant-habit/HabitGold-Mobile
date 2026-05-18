package com.habit.gold.feature.alerts.domain.usecase

import com.habit.gold.feature.alerts.domain.model.AlertItem
import com.habit.gold.feature.alerts.domain.repository.AlertsRepository

class GetAlertsUseCase(
    private val repository: AlertsRepository,
) {
    suspend operator fun invoke(): List<AlertItem> = repository.getAlerts()
}
