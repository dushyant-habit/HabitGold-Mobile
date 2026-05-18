package com.habit.gold.feature.profile.presentation

import androidx.lifecycle.viewModelScope
import com.habit.gold.core.network.ApiResult
import com.habit.gold.core.presentation.mvi.MviViewModel
import com.habit.gold.feature.profile.domain.model.ProfileSummary
import com.habit.gold.feature.profile.domain.usecase.GetProfileSummaryUseCase
import com.habit.gold.feature.profile.domain.usecase.LogoutProfileUseCase
import com.habit.gold.feature.profile.domain.usecase.RequestDeleteAccountUseCase
import kotlinx.coroutines.launch

class ProfileViewModel(
    initialSummary: ProfileSummary?,
    private val getProfileSummaryUseCase: GetProfileSummaryUseCase,
    private val logoutProfileUseCase: LogoutProfileUseCase,
    private val requestDeleteAccountUseCase: RequestDeleteAccountUseCase,
) : MviViewModel<ProfileState, ProfileIntent, Nothing>(
    ProfileState(
        isLoading = initialSummary == null,
        summary = initialSummary,
    )
) {

    override fun onIntent(intent: ProfileIntent) {
        when (intent) {
            ProfileIntent.Load -> load(forceRefresh = false)
            ProfileIntent.Refresh -> load(forceRefresh = true)
            ProfileIntent.Logout -> logout()
            ProfileIntent.DeleteAccount -> requestDeleteAccount()
            ProfileIntent.ClearError -> updateState { it.copy(errorMessage = null) }
        }
    }

    private fun load(forceRefresh: Boolean) {
        val currentSummary = state.value.summary
        if (!forceRefresh && currentSummary != null) {
            updateState {
                it.copy(isLoading = false, isRefreshing = true, errorMessage = null)
            }
        } else {
            updateState {
                it.copy(
                    isLoading = true,
                    isRefreshing = false,
                    errorMessage = null,
                )
            }
        }

        viewModelScope.launch {
            when (val result = getProfileSummaryUseCase()) {
                is ApiResult.Success -> updateState {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        summary = result.value,
                        errorMessage = null,
                    )
                }
                is ApiResult.Failure -> updateState {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = result.error.message,
                    )
                }
            }
        }
    }

    private fun logout() {
        if (state.value.isLogoutInFlight) return
        updateState { it.copy(isLogoutInFlight = true, errorMessage = null) }
        viewModelScope.launch {
            when (val result = logoutProfileUseCase()) {
                is ApiResult.Success -> updateState { it.copy(isLogoutInFlight = false) }
                is ApiResult.Failure -> updateState {
                    it.copy(isLogoutInFlight = false, errorMessage = result.error.message)
                }
            }
        }
    }

    private fun requestDeleteAccount() {
        if (state.value.isDeleteInFlight) return
        updateState { it.copy(isDeleteInFlight = true, errorMessage = null) }
        viewModelScope.launch {
            when (val result = requestDeleteAccountUseCase()) {
                is ApiResult.Success -> updateState { it.copy(isDeleteInFlight = false) }
                is ApiResult.Failure -> updateState {
                    it.copy(isDeleteInFlight = false, errorMessage = result.error.message)
                }
            }
        }
    }
}

