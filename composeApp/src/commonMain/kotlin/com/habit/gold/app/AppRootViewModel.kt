package com.habit.gold.app

import androidx.lifecycle.viewModelScope
import com.habit.gold.core.navigation.AppRoute
import com.habit.gold.core.navigation.MainTab
import com.habit.gold.core.presentation.mvi.MviViewModel
import com.habit.gold.core.session.AppStartupState
import com.habit.gold.core.session.AuthSession
import com.habit.gold.core.session.SessionStore
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

class AppRootViewModel(
    private val sessionStore: SessionStore,
    private val startupCoordinator: AppStartupCoordinator,
) : MviViewModel<AppRootState, AppRootIntent, AppRootEffect>(AppRootState()) {

    private var hasStarted = false

    fun start() {
        if (hasStarted) return
        hasStarted = true

        viewModelScope.launch {
            syncFromSession(sessionStore.restore())
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
