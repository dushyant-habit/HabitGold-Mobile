package com.habit.gold.feature.savings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habit.gold.core.network.ApiResult
import com.habit.gold.feature.savings.domain.model.SavingsMandate
import com.habit.gold.feature.savings.domain.usecase.CancelSavingsMandateUseCase
import com.habit.gold.feature.savings.domain.usecase.GetSavingsMandatesUseCase
import com.habit.gold.feature.savings.domain.usecase.PauseSavingsMandateUseCase
import com.habit.gold.feature.savings.domain.usecase.ResumeSavingsMandateUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal enum class SavingsStatusFilter {
    Success,
    Paused,
    Cancelled,
    Failed,
}

internal data class SavingsUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val mandates: List<SavingsMandate> = emptyList(),
    val selectedFilter: SavingsStatusFilter? = null,
    val expandedMandateIds: Set<String> = emptySet(),
    val actionInFlightMandateId: String? = null,
    val actionMessage: SavingsActionMessage? = null,
    val actionErrorMessage: String? = null,
) {
    val visibleMandates: List<SavingsMandate>
        get() {
            val filtered = mandates.filter { mandate ->
                when (selectedFilter) {
                    null -> mandate.statusBucket() == SavingsStatusBucket.Success || mandate.statusBucket() == SavingsStatusBucket.Paused
                    SavingsStatusFilter.Success -> mandate.statusBucket() == SavingsStatusBucket.Success
                    SavingsStatusFilter.Paused -> mandate.statusBucket() == SavingsStatusBucket.Paused
                    SavingsStatusFilter.Cancelled -> mandate.statusBucket() == SavingsStatusBucket.Cancelled
                    SavingsStatusFilter.Failed -> mandate.statusBucket() == SavingsStatusBucket.Failed || mandate.statusBucket() == SavingsStatusBucket.Pending
                }
            }
            return filtered.sortedByDescending { it.createdAt.orEmpty() }
        }
}

internal enum class SavingsActionMessage {
    Paused,
    Resumed,
    Cancelled,
}

internal sealed interface SavingsIntent {
    data object Refresh : SavingsIntent
    data class SelectFilter(val filter: SavingsStatusFilter) : SavingsIntent
    data object ClearFilter : SavingsIntent
    data class ToggleExpanded(val mandateId: String) : SavingsIntent
    data class PauseMandate(val mandateId: String) : SavingsIntent
    data class ResumeMandate(val mandateId: String) : SavingsIntent
    data class CancelMandate(val mandateId: String) : SavingsIntent
    data object ConsumeActionMessage : SavingsIntent
}

internal class SavingsViewModel internal constructor(
    private val getSavingsMandatesUseCase: GetSavingsMandatesUseCase,
    private val pauseSavingsMandateUseCase: PauseSavingsMandateUseCase,
    private val resumeSavingsMandateUseCase: ResumeSavingsMandateUseCase,
    private val cancelSavingsMandateUseCase: CancelSavingsMandateUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(SavingsUiState())
    internal val state: StateFlow<SavingsUiState> = _state.asStateFlow()

    init {
        loadMandates()
    }

    internal fun onIntent(intent: SavingsIntent) {
        when (intent) {
            SavingsIntent.Refresh -> loadMandates()
            SavingsIntent.ClearFilter -> _state.value = _state.value.copy(selectedFilter = null)
            SavingsIntent.ConsumeActionMessage -> _state.value = _state.value.copy(actionMessage = null, actionErrorMessage = null)
            is SavingsIntent.SelectFilter -> _state.value = _state.value.copy(selectedFilter = intent.filter)
            is SavingsIntent.ToggleExpanded -> toggleExpanded(intent.mandateId)
            is SavingsIntent.PauseMandate -> runMandateAction(
                mandateId = intent.mandateId,
                successMessage = SavingsActionMessage.Paused,
                action = { pauseSavingsMandateUseCase(intent.mandateId) },
            )
            is SavingsIntent.ResumeMandate -> runMandateAction(
                mandateId = intent.mandateId,
                successMessage = SavingsActionMessage.Resumed,
                action = { resumeSavingsMandateUseCase(intent.mandateId) },
            )
            is SavingsIntent.CancelMandate -> runMandateAction(
                mandateId = intent.mandateId,
                successMessage = SavingsActionMessage.Cancelled,
                action = { cancelSavingsMandateUseCase(intent.mandateId) },
            )
        }
    }

    private fun loadMandates() {
        viewModelScope.launch {
            val previousMandates = _state.value.mandates
            _state.value = _state.value.copy(
                isLoading = true,
                errorMessage = null,
                mandates = previousMandates,
                actionErrorMessage = null,
            )
            when (val result = getSavingsMandatesUseCase()) {
                is ApiResult.Failure -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = result.error.message,
                    )
                }
                is ApiResult.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = null,
                        mandates = result.value,
                    )
                }
            }
        }
    }

    private fun runMandateAction(
        mandateId: String,
        successMessage: SavingsActionMessage,
        action: suspend () -> ApiResult<Unit>,
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                actionInFlightMandateId = mandateId,
                actionMessage = null,
                actionErrorMessage = null,
            )
            when (val result = action()) {
                is ApiResult.Failure -> {
                    _state.value = _state.value.copy(
                        actionInFlightMandateId = null,
                        actionErrorMessage = result.error.message,
                    )
                }
                is ApiResult.Success -> {
                    _state.value = _state.value.copy(
                        actionInFlightMandateId = null,
                        actionMessage = successMessage,
                        actionErrorMessage = null,
                    )
                    loadMandates()
                }
            }
        }
    }

    private fun toggleExpanded(mandateId: String) {
        val current = _state.value.expandedMandateIds.toMutableSet()
        if (!current.add(mandateId)) {
            current.remove(mandateId)
        }
        _state.value = _state.value.copy(expandedMandateIds = current)
    }
}

internal enum class SavingsStatusBucket {
    Success,
    Paused,
    Cancelled,
    Failed,
    Pending,
    Unknown,
}

internal fun SavingsMandate.statusBucket(): SavingsStatusBucket {
    return when (status.trim().uppercase()) {
        "ACTIVE", "REGISTERED", "SUCCESS", "COMPLETED" -> SavingsStatusBucket.Success
        "PAUSED" -> SavingsStatusBucket.Paused
        "CANCELLED", "CANCELED" -> SavingsStatusBucket.Cancelled
        "FAILED_REGISTRATION", "FAILED", "FAILURE", "REJECTED", "EXPIRED" -> SavingsStatusBucket.Failed
        "PENDING_REGISTRATION", "PENDING", "PROCESSING", "INITIATED", "CREATED" -> SavingsStatusBucket.Pending
        else -> SavingsStatusBucket.Unknown
    }
}
