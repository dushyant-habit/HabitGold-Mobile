package com.habit.gold.feature.history.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.habit.gold.core.presentation.PlatformBackHandler
import com.habit.gold.feature.trade.domain.usecase.GetTradeInvoiceUseCase
import com.habit.gold.feature.trade.domain.usecase.GetTradeTransactionsUseCase
import com.habit.gold.feature.trade.presentation.TradeInvoiceViewerScreen
import com.habit.gold.feature.trade.presentation.TradeTransactionDetailsScreen

data class HistoryRouteDependencies(
    val getTradeTransactionsUseCase: GetTradeTransactionsUseCase,
    val getTradeInvoiceUseCase: GetTradeInvoiceUseCase,
)

private sealed interface HistoryDestination {
    data object List : HistoryDestination
    data class TransactionDetails(val transactionId: String) : HistoryDestination
    data class InvoiceViewer(
        val invoiceUrl: String,
        val transactionId: String,
    ) : HistoryDestination
}

@Composable
fun HistoryRoute(
    dependencies: HistoryRouteDependencies,
    modifier: Modifier = Modifier,
) {
    val historyViewModel = viewModel {
        HistoryViewModel(
            getTradeTransactionsUseCase = dependencies.getTradeTransactionsUseCase,
        )
    }
    val state by historyViewModel.state.collectAsStateWithLifecycle()
    var destination by remember { mutableStateOf<HistoryDestination>(HistoryDestination.List) }

    PlatformBackHandler(
        enabled = destination !is HistoryDestination.List,
        onBack = { destination = HistoryDestination.List },
    )

    when (val activeDestination = destination) {
        HistoryDestination.List -> HistoryScreen(
            state = state,
            onIntent = historyViewModel::onIntent,
            onTransactionClick = { transaction ->
                destination = HistoryDestination.TransactionDetails(transaction.id)
            },
            modifier = modifier,
        )

        is HistoryDestination.TransactionDetails -> TradeTransactionDetailsScreen(
            transactionId = activeDestination.transactionId,
            getTradeTransactionsUseCase = dependencies.getTradeTransactionsUseCase,
            getTradeInvoiceUseCase = dependencies.getTradeInvoiceUseCase,
            onBackClick = { destination = HistoryDestination.List },
            onOpenInvoice = { invoiceUrl ->
                destination = HistoryDestination.InvoiceViewer(
                    invoiceUrl = invoiceUrl,
                    transactionId = activeDestination.transactionId,
                )
            },
            modifier = modifier,
        )

        is HistoryDestination.InvoiceViewer -> TradeInvoiceViewerScreen(
            invoiceUrl = activeDestination.invoiceUrl,
            onBackClick = {
                destination = HistoryDestination.TransactionDetails(activeDestination.transactionId)
            },
            modifier = modifier,
        )
    }
}
