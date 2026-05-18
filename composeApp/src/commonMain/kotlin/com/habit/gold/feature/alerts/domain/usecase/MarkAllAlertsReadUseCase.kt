package com.habit.gold.feature.alerts.domain.usecase

import com.habit.gold.feature.alerts.domain.repository.AlertsRepository

class MarkAllAlertsReadUseCase(
    private val repository: AlertsRepository,
) {
    suspend operator fun invoke() = repository.markAllAlertsRead()
}
