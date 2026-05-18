package com.habit.gold.feature.trade.presentation.buy

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.habit.gold.core.presentation.PlatformBackHandler
import com.habit.gold.feature.trade.presentation.TradeDestination
import com.habit.gold.feature.trade.presentation.TradeRouteDependencies

@Composable
internal fun BuyTradeRouteController(
    dependencies: TradeRouteDependencies,
    destination: TradeDestination.Buy,
    onBackToHome: () -> Unit,
    onNavigate: (TradeDestination) -> Unit,
    onOpenHelp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val buyTradeViewModel = viewModel {
        BuyTradeViewModel(
            createBuyOrderUseCase = dependencies.createBuyOrderUseCase,
            getTradeAvailableCouponsUseCase = dependencies.getTradeAvailableCouponsUseCase,
            validateTradeCouponUseCase = dependencies.validateTradeCouponUseCase,
            pollTradeStatusUseCase = dependencies.pollTradeStatusUseCase,
        )
    }
    val livePriceState = dependencies.livePriceStore.state.collectAsStateWithLifecycle()
    val state = buyTradeViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(buyTradeViewModel) {
        buyTradeViewModel.effects.collect { effect ->
            when (effect) {
                is BuyTradeEffect.LaunchPayment -> {
                    val result = dependencies.paymentLauncher.launch(effect.request)
                    buyTradeViewModel.onIntent(BuyTradeIntent.HandlePaymentResult(result))
                }
                is BuyTradeEffect.ShowMessage -> Unit
            }
        }
    }

    LaunchedEffect(destination) {
        buyTradeViewModel.onIntent(BuyTradeIntent.Load)
        buyTradeViewModel.onIntent(
            BuyTradeIntent.ChangeEntryMode(
                if (destination.oneTimeUseGrams) {
                    BuyTradeEntryMode.Grams
                } else {
                    BuyTradeEntryMode.Rupees
                },
            ),
        )
    }

    PlatformBackHandler(
        enabled = state.value.step != BuyTradeStep.Processing,
        onBack = onBackToHome,
    )

    val exitBuyToDashboard = {
        buyTradeViewModel.onIntent(BuyTradeIntent.ResetToEntry)
        onBackToHome()
    }

    BuyTradeScreen(
        state = state.value,
        livePriceState = livePriceState.value,
        onBackClick = onBackToHome,
        onGoToDashboard = exitBuyToDashboard,
        onHelpClick = onOpenHelp,
        getTradeInvoiceUseCase = dependencies.getTradeInvoiceUseCase,
        onOpenInvoice = { invoiceUrl ->
            onNavigate(
                TradeDestination.InvoiceViewer(
                    invoiceUrl = invoiceUrl,
                    returnDestination = TradeDestination.Buy(),
                ),
            )
        },
        initialAmount = destination.amount,
        initialOneTimeUseGrams = destination.oneTimeUseGrams,
        onIntent = buyTradeViewModel::onIntent,
        modifier = modifier,
    )
}
