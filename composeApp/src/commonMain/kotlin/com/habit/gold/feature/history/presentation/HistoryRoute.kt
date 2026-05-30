package com.habit.gold.feature.history.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import com.habit.gold.feature.trade.domain.usecase.GetTradeTransactionsUseCase

data class HistoryRouteDependencies(
    val getTradeTransactionsUseCase: GetTradeTransactionsUseCase,
)

@Composable
fun HistoryRoute(
    dependencies: HistoryRouteDependencies,
    sessionResetKey: String,
    onOpenTransactionDetails: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val historyViewModel = viewModel(key = "history:$sessionResetKey") {
        HistoryViewModel(
            getTradeTransactionsUseCase = dependencies.getTradeTransactionsUseCase,
        )
    }
    val state by historyViewModel.state.collectAsStateWithLifecycle()

    HistoryScreen(
        state = state,
        onIntent = historyViewModel::onIntent,
        onTransactionClick = { transaction ->
            onOpenTransactionDetails(transaction.id)
        },
        modifier = modifier,
    )
}
