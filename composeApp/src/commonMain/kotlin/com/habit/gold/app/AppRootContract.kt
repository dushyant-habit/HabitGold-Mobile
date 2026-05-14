package com.habit.gold.app

import com.habit.gold.core.navigation.AppRoute
import com.habit.gold.core.presentation.mvi.MviEffect
import com.habit.gold.core.presentation.mvi.MviIntent
import com.habit.gold.core.presentation.mvi.MviState
import com.habit.gold.core.session.AppStartupState
import com.habit.gold.core.session.AuthSession
import com.habit.gold.core.navigation.MainTab

data class AppRootState(
    val startupState: AppStartupState = AppStartupState.Loading,
    val currentRoute: AppRoute = AppRoute.Splash,
    val session: AuthSession = AuthSession(),
) : MviState

sealed interface AppRootIntent : MviIntent {
    data class SelectMainTab(
        val tab: MainTab,
    ) : AppRootIntent

    data object Logout : AppRootIntent
}

sealed interface AppRootEffect : MviEffect
