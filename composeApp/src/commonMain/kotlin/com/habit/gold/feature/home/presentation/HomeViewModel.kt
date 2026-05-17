package com.habit.gold.feature.home.presentation

import androidx.lifecycle.viewModelScope
import com.habit.gold.core.network.ApiResult
import com.habit.gold.core.presentation.mvi.MviViewModel
import com.habit.gold.feature.home.domain.usecase.LoadHomeSummaryUseCase
import kotlinx.coroutines.launch

class HomeViewModel(
    private val loadHomeSummaryUseCase: LoadHomeSummaryUseCase,
) : MviViewModel<HomeState, HomeIntent, HomeEffect>(HomeState()) {

    private var hasLoaded = false

    override fun onIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.Load -> {
                if (!hasLoaded) {
                    hasLoaded = true
                    loadSummary()
                }
            }
            HomeIntent.Refresh -> loadSummary()
        }
    }

    private fun loadSummary() {
        viewModelScope.launch {
            updateState { current ->
                current.copy(
                    isLoading = true,
                    errorMessage = null,
                )
            }

            when (val result = loadHomeSummaryUseCase()) {
                is ApiResult.Success -> {
                    updateState {
                        it.copy(
                            isLoading = false,
                            summary = result.value,
                            errorMessage = null,
                        )
                    }
                }
                is ApiResult.Failure -> {
                    updateState {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.error.message,
                        )
                    }
                }
            }
        }
    }
}
