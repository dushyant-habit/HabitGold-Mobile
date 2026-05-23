package com.habit.gold.app

import androidx.lifecycle.viewModelScope
import com.habit.gold.core.navigation.AppRoute
import com.habit.gold.core.navigation.MainTab
import com.habit.gold.core.platform.notifications.DeviceTokenSyncManager
import com.habit.gold.core.presentation.mvi.MviViewModel
import com.habit.gold.core.session.AppStartupState
import com.habit.gold.core.session.AuthSession
import com.habit.gold.core.session.SessionStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

class AppRootViewModel(
    private val sessionStore: SessionStore,
    private val startupCoordinator: AppStartupCoordinator,
    private val deviceTokenSyncManager: DeviceTokenSyncManager,
) : MviViewModel<AppRootState, AppRootIntent, AppRootEffect>(AppRootState()) {

    private var hasStarted = false

    /**
     * Restores persisted session state exactly once so the shared app shell can route consistently.
     */
    fun start() {
        if (hasStarted) return
        hasStarted = true

        viewModelScope.launch {
            val session = sessionStore.restore()
            // Keep a short branded splash while startup routing settles.
            delay(1200)
            syncFromSession(session)
            sessionStore.state
                .drop(1)
                .collect(::syncFromSession)
        }
    }

    override fun onIntent(intent: AppRootIntent) {
        when (intent) {
            is AppRootIntent.SelectMainTab -> selectMainTab(intent.tab)
            AppRootIntent.Logout -> logout()
        }
    }

    private fun selectMainTab(tab: MainTab) {
        val currentState = state.value
        val currentRoute = currentState.currentRoute as? AppRoute.Main ?: return
        if (currentRoute.tab == tab) return

        updateState {
            it.copy(currentRoute = AppRoute.Main(tab = tab))
        }
    }

    private fun logout() {
        viewModelScope.launch {
            deviceTokenSyncManager.unregisterCurrentTokenBeforeLogout()
            sessionStore.clear()
        }
    }

    private fun syncFromSession(session: AuthSession) {
        val startupState = AppStartupState.Ready(
            destination = session.startupDestination,
            session = session,
        )
        val currentMainTab = (state.value.currentRoute as? AppRoute.Main)?.tab ?: MainTab.Home

        updateState {
            it.copy(
                startupState = startupState,
                currentRoute = startupCoordinator.routeFor(startupState, currentMainTab),
                session = session,
            )
        }
    }
}
