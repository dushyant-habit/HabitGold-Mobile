package com.habit.gold.feature.alerts.presentation

import com.habit.gold.core.presentation.mvi.MviEffect
import com.habit.gold.core.presentation.mvi.MviIntent
import com.habit.gold.core.presentation.mvi.MviState

data class AlertsState(
    val isLoading: Boolean = true,
    val alerts: List<AlertsUiModel> = emptyList(),
) : MviState

data class AlertsUiModel(
    val id: String,
    val title: String,
    val description: String,
    val createdAt: String,
    val isRead: Boolean,
)

sealed interface AlertsIntent : MviIntent {
    data object Load : AlertsIntent
}

sealed interface AlertsEffect : MviEffect
