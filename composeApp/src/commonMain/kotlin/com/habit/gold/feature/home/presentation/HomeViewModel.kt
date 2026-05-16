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
                    loadSummary(isUserRefresh = false)
                }
            }
            HomeIntent.Refresh -> loadSummary(isUserRefresh = true)
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
                it.copy(isBalanceVisible = preferences.isBalanceVisible)
            }
        }
    }

    private fun loadSummary(isUserRefresh: Boolean) {
        viewModelScope.launch {
            updateState { current ->
                val shouldKeepContentVisible = isUserRefresh && current.summary != null
                current.copy(
                    isLoading = !shouldKeepContentVisible,
                    isRefreshing = shouldKeepContentVisible,
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
