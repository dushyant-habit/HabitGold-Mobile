package com.habit.gold.feature.alerts.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.habit.gold.feature.alerts.domain.usecase.GetAlertsUseCase
import com.habit.gold.feature.alerts.domain.usecase.MarkAllAlertsReadUseCase

data class AlertsRouteDependencies(
    val getAlertsUseCase: GetAlertsUseCase,
    val markAllAlertsReadUseCase: MarkAllAlertsReadUseCase,
)

@Composable
fun AlertsRoute(
    dependencies: AlertsRouteDependencies,
    sessionResetKey: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = viewModel(key = "alerts:$sessionResetKey") {
        AlertsViewModel(
            getAlertsUseCase = dependencies.getAlertsUseCase,
            markAllAlertsReadUseCase = dependencies.markAllAlertsReadUseCase,
        )
    }
    val state = viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.onIntent(AlertsIntent.Load)
    }

    AlertsScreen(
        state = state.value,
        onBackClick = onBackClick,
        modifier = modifier,
    )
}
