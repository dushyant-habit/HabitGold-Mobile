package com.habit.gold.feature.profile.presentation

import com.habit.gold.core.presentation.mvi.MviIntent
import com.habit.gold.core.presentation.mvi.MviState
import com.habit.gold.feature.profile.domain.model.ProfileSummary

data class ProfileState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isLogoutInFlight: Boolean = false,
    val isDeleteInFlight: Boolean = false,
    val summary: ProfileSummary? = null,
    val errorMessage: String? = null,
) : MviState

sealed interface ProfileIntent : MviIntent {
    data object Load : ProfileIntent
    data object Refresh : ProfileIntent
    data object Logout : ProfileIntent
    data object DeleteAccount : ProfileIntent
    data object ClearError : ProfileIntent
}

