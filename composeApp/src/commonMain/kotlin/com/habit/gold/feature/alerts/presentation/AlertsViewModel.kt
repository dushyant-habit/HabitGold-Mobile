package com.habit.gold.feature.alerts.presentation

import androidx.lifecycle.viewModelScope
import com.habit.gold.core.presentation.mvi.MviViewModel
import com.habit.gold.feature.alerts.domain.model.AlertItem
import com.habit.gold.feature.alerts.domain.usecase.GetAlertsUseCase
import com.habit.gold.feature.alerts.domain.usecase.MarkAllAlertsReadUseCase
import kotlinx.coroutines.launch

class AlertsViewModel(
    private val getAlertsUseCase: GetAlertsUseCase,
    private val markAllAlertsReadUseCase: MarkAllAlertsReadUseCase,
) : MviViewModel<AlertsState, AlertsIntent, AlertsEffect>(AlertsState()) {

    private var hasLoaded = false

    override fun onIntent(intent: AlertsIntent) {
        when (intent) {
            AlertsIntent.Load -> {
                if (!hasLoaded) {
                    hasLoaded = true
                    loadAlerts()
                }
            }
        }
    }

    private fun loadAlerts() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            val alerts = getAlertsUseCase()
            markAllAlertsReadUseCase()
            updateState {
                it.copy(
                    isLoading = false,
                    alerts = alerts.map(::toUiModel),
                )
            }
        }
    }

    private fun toUiModel(alert: AlertItem): AlertsUiModel {
        return AlertsUiModel(
            id = alert.id,
            title = alert.title,
            description = alert.description,
            createdAt = alert.createdAt,
            isRead = alert.isRead,
        )
    }
}
