package com.habit.gold.feature.home.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.habit.gold.core.navigation.MainTab
import com.habit.gold.core.presentation.PlatformBackHandler
import com.habit.gold.core.storage.AppPreferencesStorage
import com.habit.gold.core.session.AuthSession
import com.habit.gold.feature.alerts.presentation.AlertsRoute
import com.habit.gold.feature.alerts.presentation.AlertsRouteDependencies
import com.habit.gold.feature.home.domain.model.HomeSipMandate
import com.habit.gold.feature.home.domain.usecase.GetHomePriceHistoryUseCase
import com.habit.gold.feature.home.domain.usecase.LoadHomeSummaryUseCase
import com.habit.gold.feature.profile.presentation.ProfileDestination
import com.habit.gold.feature.profile.presentation.ProfileRoute
import com.habit.gold.feature.profile.presentation.ProfileRouteDependencies
import com.habit.gold.feature.savings.presentation.SavingsDestination
import com.habit.gold.feature.savings.presentation.SavingsRoute
import com.habit.gold.feature.savings.presentation.SavingsRouteDependencies
import com.habit.gold.feature.trade.presentation.TradeDestination
import com.habit.gold.feature.trade.presentation.TradeRoute
import com.habit.gold.feature.trade.presentation.TradeRouteDependencies
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

data class HomeRouteDependencies(
    val loadHomeSummaryUseCase: LoadHomeSummaryUseCase,
    val appPreferencesStorage: AppPreferencesStorage,
    val getHomePriceHistoryUseCase: GetHomePriceHistoryUseCase,
)

@Composable
fun HomeRoute(
    dependencies: HomeRouteDependencies,
    alertsDependencies: AlertsRouteDependencies,
    profileDependencies: ProfileRouteDependencies,
    savingsDependencies: SavingsRouteDependencies,
    tradeDependencies: TradeRouteDependencies,
    session: AuthSession,
    onSelectTab: (MainTab) -> Unit,
    onBottomBarVisibilityChange: (Boolean) -> Unit,
    onDashboardVisibilityChange: (Boolean) -> Unit = {},
    onBiometricStateChanged: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val homeViewModel = viewModel {
        HomeViewModel(
            loadHomeSummaryUseCase = dependencies.loadHomeSummaryUseCase,
            appPreferencesStorage = dependencies.appPreferencesStorage,
        )
    }
    val uiState = homeViewModel.state.collectAsStateWithLifecycle()
    var destination by remember { mutableStateOf<HomeDestination>(HomeDestination.Dashboard) }

    LaunchedEffect(homeViewModel) {
        homeViewModel.onIntent(HomeIntent.Load)
    }

    LaunchedEffect(destination) {
        onBottomBarVisibilityChange(destination is HomeDestination.Dashboard)
        onDashboardVisibilityChange(destination is HomeDestination.Dashboard)
        if (destination is HomeDestination.Dashboard) {
            homeViewModel.onIntent(HomeIntent.RestorePreferences)
        }
    }

    PlatformBackHandler(
        enabled = destination !is HomeDestination.Dashboard && destination !is HomeDestination.Trade,
        onBack = { destination = HomeDestination.Dashboard },
    )

    when (val activeDestination = destination) {
        HomeDestination.Dashboard -> HomeScreen(
            session = session,
            uiState = uiState.value,
            onRefresh = { homeViewModel.onIntent(HomeIntent.Refresh) },
            getHomePriceHistoryUseCase = dependencies.getHomePriceHistoryUseCase,
            onOpenProfile = {
                destination = HomeDestination.Profile(
                    destination = ProfileDestination.Hub,
                    returnDestination = HomeDestination.Dashboard,
                )
            },
            onOpenAlerts = { destination = HomeDestination.Alerts },
            onOpenBuyGold = { destination = HomeDestination.Trade(TradeDestination.Buy()) },
            onOpenSellGold = { destination = HomeDestination.Trade(TradeDestination.WithdrawalMode) },
            onOpenGoldValueDetails = {
                destination = HomeDestination.GoldValueDetails(uiState.value.summary?.dashboard)
            },
            onToggleBalanceVisibility = { homeViewModel.onIntent(HomeIntent.ToggleBalanceVisibility) },
            onOpenSavingsDetails = {
                destination = HomeDestination.Savings(
                    destination = SavingsDestination.Manage,
                    returnDestination = HomeDestination.Dashboard,
                )
            },
            onOpenSavingsSetup = { frequency, mandate ->
                destination = HomeDestination.Savings(
                    destination = SavingsDestination.Setup(
                        frequency = frequency,
                        initialAmount = mandate?.let(::homeSavingsRouteAmount),
                        mandateId = mandate?.id,
                        initialExecutionDay = mandate?.let(::homeSavingsRouteExecutionDay),
                        initialStatus = mandate?.status,
                    ),
                    returnDestination = HomeDestination.Dashboard,
                )
            },
            onOpenTransaction = { item -> destination = HomeDestination.TransactionDetails(item) },
            onOpenSupport = {
                destination = HomeDestination.Profile(
                    destination = ProfileDestination.HelpCenter(),
                    returnDestination = HomeDestination.Dashboard,
                )
            },
            modifier = modifier,
        )
        HomeDestination.Alerts -> AlertsRoute(
            dependencies = alertsDependencies,
            onBackClick = { destination = HomeDestination.Dashboard },
            modifier = modifier,
        )
        is HomeDestination.GoldValueDetails -> HomeGoldValueDetailsScreen(
            dashboard = activeDestination.dashboard,
            onBackClick = { destination = HomeDestination.Dashboard },
            onBuyGoldClick = { destination = HomeDestination.Trade(TradeDestination.Buy()) },
            onSellGoldClick = { destination = HomeDestination.Trade(TradeDestination.WithdrawalMode) },
        )
        is HomeDestination.Profile -> ProfileRoute(
            dependencies = profileDependencies,
            destination = activeDestination.destination,
            session = session,
            onBackToHome = { destination = activeDestination.returnDestination },
            onNavigate = { nextProfileDestination ->
                destination = HomeDestination.Profile(
                    destination = nextProfileDestination,
                    returnDestination = activeDestination.returnDestination,
                )
            },
            onOpenAutopay = {
                destination = HomeDestination.Savings(
                    destination = SavingsDestination.Manage,
                    returnDestination = HomeDestination.Profile(
                        destination = ProfileDestination.Hub,
                        returnDestination = activeDestination.returnDestination,
                    ),
                )
            },
            onBiometricStateChanged = onBiometricStateChanged,
            modifier = modifier,
        )
        is HomeDestination.TransactionDetails -> HomeTransactionDetailsScreen(
            transactionPreview = activeDestination.item,
            onBackClick = { destination = HomeDestination.Dashboard },
        )
        is HomeDestination.Savings -> SavingsRoute(
            dependencies = savingsDependencies,
            destination = activeDestination.destination,
            onBackToHome = { destination = activeDestination.returnDestination },
            onOpenHelp = {
                destination = HomeDestination.Profile(
                    destination = ProfileDestination.HelpCenter(),
                    returnDestination = activeDestination,
                )
            },
            modifier = modifier,
        )
        is HomeDestination.Trade -> TradeRoute(
            dependencies = tradeDependencies,
            destination = activeDestination.destination,
            onBackToHome = { destination = HomeDestination.Dashboard },
            onNavigate = { nextTradeDestination ->
                destination = HomeDestination.Trade(nextTradeDestination)
            },
            onOpenHelp = {
                destination = HomeDestination.Profile(
                    destination = ProfileDestination.HelpCenter(),
                    returnDestination = activeDestination,
                )
            },
            modifier = modifier,
        )
        is HomeDestination.Deferred -> HomeDeferredRouteScreen(
            target = activeDestination.target,
            onBackClick = { destination = HomeDestination.Dashboard },
            onOpenHistoryTab = {
                destination = HomeDestination.Dashboard
                onSelectTab(MainTab.History)
            },
        )
    }
}

private fun homeSavingsRouteAmount(mandate: HomeSipMandate): String? {
    return mandate.billing?.currentAmount
        ?: mandate.billingCurrentAmount
        ?: mandate.billing?.nextExecutionAmount
        ?: mandate.billingNextExecutionAmount
        ?: mandate.amount
}

private fun homeSavingsRouteExecutionDay(mandate: HomeSipMandate): Int? {
    val rawDate = mandate.nextExecutionDate?.takeIf { it.isNotBlank() } ?: return null
    val parsedDate = runCatching { LocalDate.parse(rawDate.take(10)) }.getOrNull() ?: return null
    return when (mandate.frequency.trim().uppercase()) {
        "WEEKLY" -> parsedDate.dayOfWeek.homeIsoDayNumber()
        "MONTHLY" -> parsedDate.day
        else -> null
    }
}

private fun DayOfWeek.homeIsoDayNumber(): Int {
    return when (this) {
        DayOfWeek.MONDAY -> 1
        DayOfWeek.TUESDAY -> 2
        DayOfWeek.WEDNESDAY -> 3
        DayOfWeek.THURSDAY -> 4
        DayOfWeek.FRIDAY -> 5
        DayOfWeek.SATURDAY -> 6
        DayOfWeek.SUNDAY -> 7
    }
}
