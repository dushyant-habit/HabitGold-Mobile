package com.habit.gold.feature.trade.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.habit.gold.core.presentation.PlatformBackHandler
import com.habit.gold.feature.trade.domain.TradePaymentLauncher
import com.habit.gold.feature.trade.domain.usecase.CreateBuyOrderUseCase
import com.habit.gold.feature.trade.domain.usecase.CreateSellOrderUseCase
import com.habit.gold.feature.trade.domain.usecase.ExecuteSellOrderUseCase
import com.habit.gold.feature.trade.domain.usecase.GetSellAvailabilityUseCase
import com.habit.gold.feature.trade.domain.usecase.GetTradeAvailableCouponsUseCase
import com.habit.gold.feature.trade.domain.usecase.GetTradeInvoiceUseCase
import com.habit.gold.feature.trade.domain.usecase.GetTradeStatusUseCase
import com.habit.gold.feature.trade.domain.usecase.GetTradeTransactionsUseCase
import com.habit.gold.feature.trade.domain.usecase.GetTradeUserVpasUseCase
import com.habit.gold.feature.trade.domain.usecase.PollTradeStatusUseCase
import com.habit.gold.feature.trade.domain.TradeLivePriceStore
import com.habit.gold.feature.trade.domain.usecase.ValidateTradeCouponUseCase
import com.habit.gold.feature.trade.presentation.buy.BuyTradeRouteController
import com.habit.gold.feature.trade.presentation.sell.SellTradeRouteController
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.trade_route_get_coin_message
import habitgoldmobile.composeapp.generated.resources.trade_route_get_coin_title
import habitgoldmobile.composeapp.generated.resources.trade_route_help_message
import habitgoldmobile.composeapp.generated.resources.trade_route_help_title
import habitgoldmobile.composeapp.generated.resources.trade_route_vpa_message
import habitgoldmobile.composeapp.generated.resources.trade_route_vpa_title
import org.jetbrains.compose.resources.stringResource

data class TradeRouteDependencies(
    val livePriceStore: TradeLivePriceStore,
    val createBuyOrderUseCase: CreateBuyOrderUseCase,
    val getTradeAvailableCouponsUseCase: GetTradeAvailableCouponsUseCase,
    val validateTradeCouponUseCase: ValidateTradeCouponUseCase,
    val pollTradeStatusUseCase: PollTradeStatusUseCase,
    val paymentLauncher: TradePaymentLauncher,
    val createSellOrderUseCase: CreateSellOrderUseCase,
    val executeSellOrderUseCase: ExecuteSellOrderUseCase,
    val getSellAvailabilityUseCase: GetSellAvailabilityUseCase,
    val getTradeUserVpasUseCase: GetTradeUserVpasUseCase,
    val getTradeStatusUseCase: GetTradeStatusUseCase,
    val getTradeTransactionsUseCase: GetTradeTransactionsUseCase,
    val getTradeInvoiceUseCase: GetTradeInvoiceUseCase,
)

@Composable
fun TradeRoute(
    dependencies: TradeRouteDependencies,
    destination: TradeDestination,
    onBackToHome: () -> Unit,
    onNavigate: (TradeDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (destination !is TradeDestination.Buy && destination != TradeDestination.Sell && destination !is TradeDestination.SellPayout) {
        PlatformBackHandler(
            enabled = true,
            onBack = {
                when (destination) {
                    TradeDestination.WithdrawalMode -> onBackToHome()
                    TradeDestination.GetCoinCatalog -> onNavigate(TradeDestination.WithdrawalMode)
                    is TradeDestination.TransactionDetails -> onBackToHome()
                    is TradeDestination.InvoiceViewer -> {
                        destination.returnDestination?.let(onNavigate) ?: onBackToHome()
                    }
                    TradeDestination.VpaList -> onNavigate(TradeDestination.Sell)
                    TradeDestination.HelpCenter -> onNavigate(TradeDestination.Buy())
                }
            },
        )
    }

    when (destination) {
        TradeDestination.WithdrawalMode -> WithdrawalModeScreen(
            onBackClick = onBackToHome,
            onNavigateToCoinMode = { onNavigate(TradeDestination.GetCoinCatalog) },
            onNavigateToCashMode = { onNavigate(TradeDestination.Sell) },
            modifier = modifier,
        )
        is TradeDestination.Buy -> BuyTradeRouteController(
            dependencies = dependencies,
            destination = destination,
            onBackToHome = onBackToHome,
            onNavigate = onNavigate,
            modifier = modifier,
        )
        TradeDestination.Sell,
        is TradeDestination.SellPayout -> SellTradeRouteController(
            dependencies = dependencies,
            destination = destination,
            onNavigate = onNavigate,
            onBackToHome = onBackToHome,
            modifier = modifier,
        )
        TradeDestination.GetCoinCatalog -> TradeDeferredScreen(
            title = stringResource(Res.string.trade_route_get_coin_title),
            message = stringResource(Res.string.trade_route_get_coin_message),
            onBackClick = { onNavigate(TradeDestination.WithdrawalMode) },
            modifier = modifier,
        )
        is TradeDestination.TransactionDetails -> TradeTransactionDetailsScreen(
            transactionId = destination.transactionId,
            getTradeTransactionsUseCase = dependencies.getTradeTransactionsUseCase,
            getTradeInvoiceUseCase = dependencies.getTradeInvoiceUseCase,
            onBackClick = onBackToHome,
            onOpenInvoice = { invoiceUrl ->
                onNavigate(
                    TradeDestination.InvoiceViewer(
                        invoiceUrl = invoiceUrl,
                        returnDestination = TradeDestination.TransactionDetails(destination.transactionId),
                    ),
                )
            },
            modifier = modifier,
        )
        is TradeDestination.InvoiceViewer -> TradeInvoiceViewerScreen(
            invoiceUrl = destination.invoiceUrl,
            onBackClick = {
                destination.returnDestination?.let(onNavigate) ?: onBackToHome()
            },
            modifier = modifier,
        )
        TradeDestination.VpaList -> TradeDeferredScreen(
            title = stringResource(Res.string.trade_route_vpa_title),
            message = stringResource(Res.string.trade_route_vpa_message),
            onBackClick = { onNavigate(TradeDestination.Sell) },
            modifier = modifier,
        )
        TradeDestination.HelpCenter -> TradeDeferredScreen(
            title = stringResource(Res.string.trade_route_help_title),
            message = stringResource(Res.string.trade_route_help_message),
            onBackClick = { onNavigate(TradeDestination.Buy()) },
            modifier = modifier,
        )
    }
}
