package com.habit.gold.app

import com.habit.gold.core.navigation.AppRoute
import com.habit.gold.core.navigation.MainTab
import com.habit.gold.core.session.AppStartupDestination
import com.habit.gold.core.session.AppStartupState

class AppStartupCoordinator {

    fun routeFor(
        startupState: AppStartupState,
        currentMainTab: MainTab = MainTab.Home,
    ): AppRoute {
        return when (startupState) {
            AppStartupState.Loading -> AppRoute.Splash
            is AppStartupState.Ready -> when (startupState.destination) {
                AppStartupDestination.Login,
                AppStartupDestination.BasicInfo,
                -> AppRoute.Authentication

                AppStartupDestination.Home -> AppRoute.Main(tab = currentMainTab)
            }
        }
    }
}
