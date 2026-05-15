package com.habit.gold.app

import com.habit.gold.core.navigation.AppRoute
import com.habit.gold.core.navigation.MainTab
import com.habit.gold.core.session.AppStartupDestination
import com.habit.gold.core.session.AppStartupState
import com.habit.gold.core.session.AuthSession
import kotlin.test.Test
import kotlin.test.assertEquals

class AppStartupCoordinatorTest {

    private val coordinator = AppStartupCoordinator()

    @Test
    fun `returns splash while startup is loading`() {
        assertEquals(AppRoute.Splash, coordinator.routeFor(AppStartupState.Loading))
    }

    @Test
    fun `routes login and basic info destinations through shared authentication shell`() {
        val loginRoute = coordinator.routeFor(
            AppStartupState.Ready(
                destination = AppStartupDestination.Login,
                session = AuthSession(),
            )
        )
        val basicInfoRoute = coordinator.routeFor(
            AppStartupState.Ready(
                destination = AppStartupDestination.BasicInfo,
                session = AuthSession(
                    accessToken = "access",
                    refreshToken = "refresh",
                    isLoggedIn = true,
                    isProfileComplete = false,
                ),
            )
        )

        assertEquals(AppRoute.Authentication, loginRoute)
        assertEquals(AppRoute.Authentication, basicInfoRoute)
    }

    @Test
    fun `preserves selected main tab for authenticated home route`() {
        val route = coordinator.routeFor(
            startupState = AppStartupState.Ready(
                destination = AppStartupDestination.Home,
                session = AuthSession(
                    accessToken = "access",
                    refreshToken = "refresh",
                    isLoggedIn = true,
                    isProfileComplete = true,
                ),
            ),
            currentMainTab = MainTab.History,
        )

        assertEquals(AppRoute.Main(tab = MainTab.History), route)
    }
}
