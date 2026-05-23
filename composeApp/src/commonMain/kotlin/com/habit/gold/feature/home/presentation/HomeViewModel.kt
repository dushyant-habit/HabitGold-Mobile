package com.habit.gold.feature.home.presentation

import androidx.lifecycle.viewModelScope
import com.habit.gold.core.network.ApiResult
import com.habit.gold.core.presentation.mvi.MviViewModel
import com.habit.gold.core.storage.AppPreferencesStorage
import com.habit.gold.feature.home.domain.usecase.LoadHomeSummaryUseCase
import kotlinx.coroutines.launch

class HomeViewModel(
    private val loadHomeSummaryUseCase: LoadHomeSummaryUseCase,
    private val appPreferencesStorage: AppPreferencesStorage,
) : MviViewModel<HomeState, HomeIntent, HomeEffect>(HomeState()) {

    private var hasLoaded = false

    override fun onIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.Load -> {
                if (!hasLoaded) {
                    hasLoaded = true
                    restorePreferences()
                    loadSummary(mode = HomeRefreshMode.Initial)
                }
            }
            HomeIntent.Refresh -> loadSummary(mode = HomeRefreshMode.UserVisible)
            HomeIntent.BackgroundRefresh -> loadSummary(mode = HomeRefreshMode.Silent)
            HomeIntent.RestorePreferences -> restorePreferences()
            HomeIntent.ToggleBalanceVisibility -> toggleBalanceVisibility()
        }
    }

    /**
     * Restores Home-level persisted preferences so the dashboard matches Android behavior on launch.
     */
    private fun restorePreferences() {
        viewModelScope.launch {
            val preferences = appPreferencesStorage.readPreferences()
            updateState {
                it.copy(
                    isBalanceVisible = preferences.isBalanceVisible,
                    hasUnreadAlerts = preferences.hasUnreadAlerts,
                )
            }
        }
    }

    private fun loadSummary(mode: HomeRefreshMode) {
        viewModelScope.launch {
            updateState { current ->
                val shouldKeepContentVisible = current.summary != null &&
                    mode != HomeRefreshMode.Initial
                val shouldShowRefreshIndicator = mode == HomeRefreshMode.UserVisible && current.summary != null
                current.copy(
                    isLoading = !shouldKeepContentVisible,
                    isRefreshing = shouldShowRefreshIndicator,
                    errorMessage = null,
                )
            }

            when (val result = loadHomeSummaryUseCase()) {
                is ApiResult.Success -> {
                    updateState {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            summary = result.value,
                            errorMessage = null,
                        )
                    }
                }
                is ApiResult.Failure -> {
                    updateState {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = result.error.message,
                        )
                    }
                }
            }
        }
    }

    /**
     * Persists the balance-visibility toggle so the user keeps the same preference after relaunch.
     */
    private fun toggleBalanceVisibility() {
        viewModelScope.launch {
            val nextValue = !state.value.isBalanceVisible
            updateState { it.copy(isBalanceVisible = nextValue) }
            val current = appPreferencesStorage.readPreferences()
            appPreferencesStorage.writePreferences(
                current.copy(isBalanceVisible = nextValue),
            )
        }
    }
}

private enum class HomeRefreshMode {
    Initial,
    UserVisible,
    Silent,
}
