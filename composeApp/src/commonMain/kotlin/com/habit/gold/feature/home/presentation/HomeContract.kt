package com.habit.gold.feature.home.presentation

import com.habit.gold.core.presentation.mvi.MviEffect
import com.habit.gold.core.presentation.mvi.MviIntent
import com.habit.gold.core.presentation.mvi.MviState
import com.habit.gold.feature.home.domain.model.HomeSummary

data class HomeState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isBalanceVisible: Boolean = true,
    val hasUnreadAlerts: Boolean = false,
    val summary: HomeSummary? = null,
    val errorMessage: String? = null,
) : MviState

sealed interface HomeIntent : MviIntent {
    data object Load : HomeIntent
    data object Refresh : HomeIntent
    data object RestorePreferences : HomeIntent
    data object ToggleBalanceVisibility : HomeIntent
}

sealed interface HomeEffect : MviEffect
