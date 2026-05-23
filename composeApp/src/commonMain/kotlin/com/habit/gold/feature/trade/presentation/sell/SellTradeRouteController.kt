package com.habit.gold.feature.trade.presentation.sell

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.habit.gold.core.presentation.PlatformBackHandler
import com.habit.gold.feature.trade.presentation.TradeDestination
import com.habit.gold.feature.trade.presentation.TradeRouteDependencies

@Composable
internal fun SellTradeRouteController(
    dependencies: TradeRouteDependencies,
    destination: TradeDestination,
    onNavigate: (TradeDestination) -> Unit,
    onBackToHome: () -> Unit,
    onTradeMutation: () -> Unit,
    onOpenHelp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sellTradeViewModel = viewModel {
        SellTradeViewModel(
            getSellAvailabilityUseCase = dependencies.getSellAvailabilityUseCase,
            getTradeUserVpasUseCase = dependencies.getTradeUserVpasUseCase,
            createSellOrderUseCase = dependencies.createSellOrderUseCase,
            executeSellOrderUseCase = dependencies.executeSellOrderUseCase,
            pollTradeStatusUseCase = dependencies.pollTradeStatusUseCase,
            getTradeStatusUseCase = dependencies.getTradeStatusUseCase,
        )
    }
    val livePriceState = dependencies.livePriceStore.state.collectAsStateWithLifecycle()
    val state = sellTradeViewModel.state.collectAsStateWithLifecycle()
    val lastReportedMutationOrderId = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(destination) {
        when (destination) {
            TradeDestination.Sell -> sellTradeViewModel.onIntent(SellTradeIntent.Load)
            is TradeDestination.SellPayout -> {
                sellTradeViewModel.onIntent(SellTradeIntent.Load)
                sellTradeViewModel.onIntent(SellTradeIntent.StartPolling(destination.orderId))
            }
            else -> Unit
        }
    }

    LaunchedEffect(state.value.createdOrder?.orderId, state.value.pollingSnapshot?.orderId, state.value.step) {
        val orderId = state.value.createdOrder?.orderId ?: state.value.pollingSnapshot?.orderId
        val isTerminalStep = state.value.step == SellTradeStep.Success ||
            state.value.step == SellTradeStep.Failure ||
            state.value.step == SellTradeStep.Pending
        if (orderId != null && isTerminalStep && lastReportedMutationOrderId.value != orderId) {
            lastReportedMutationOrderId.value = orderId
            onTradeMutation()
        }
    }

    PlatformBackHandler(
        enabled = true,
        onBack = {
            when (destination) {
                TradeDestination.Sell -> onNavigate(TradeDestination.WithdrawalMode)
                is TradeDestination.SellPayout -> onNavigate(TradeDestination.Sell)
                else -> onBackToHome()
            }
        },
    )

    val exitSellToDashboard = {
        sellTradeViewModel.onIntent(SellTradeIntent.BackToEntry)
        onBackToHome()
    }

    SellTradeScreen(
        state = state.value,
        livePriceState = livePriceState.value,
        onBackClick = {
            when (destination) {
                TradeDestination.Sell -> onNavigate(TradeDestination.WithdrawalMode)
                is TradeDestination.SellPayout -> onNavigate(TradeDestination.Sell)
                else -> onBackToHome()
            }
        },
        onHelpClick = onOpenHelp,
        onGoToDashboard = exitSellToDashboard,
        getTradeInvoiceUseCase = dependencies.getTradeInvoiceUseCase,
        onOpenInvoice = { invoiceUrl ->
            onNavigate(
                TradeDestination.InvoiceViewer(
                    invoiceUrl = invoiceUrl,
                    returnDestination = TradeDestination.Sell,
                ),
            )
        },
        onIntent = sellTradeViewModel::onIntent,
        modifier = modifier,
    )
}
