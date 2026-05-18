package com.habit.gold.feature.trade.presentation.sell

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
