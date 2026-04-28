package com.habit.gold.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habit.gold.PlatformInfo
import com.habit.gold.core.config.AppConfig
import com.habit.gold.core.config.BootstrapInfo
import com.habit.gold.core.network.BootstrapRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = true,
    val bootstrapInfo: BootstrapInfo? = null,
)

class HomeViewModel(
    private val appConfig: AppConfig,
    private val platformInfo: PlatformInfo,
    private val bootstrapRepository: BootstrapRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            val bootstrapInfo = bootstrapRepository.loadBootstrapInfo()
            _uiState.value = HomeUiState(
                isLoading = false,
                bootstrapInfo = bootstrapInfo.copy(
                    appConfig = appConfig,
                    platformInfo = platformInfo,
                ),
            )
        }
    }
}
