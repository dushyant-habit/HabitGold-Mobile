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
import com.habit.gold.core.storage.AppPreferencesStorage
import com.habit.gold.core.session.AuthSession
import com.habit.gold.feature.home.domain.usecase.GetHomePriceHistoryUseCase
import com.habit.gold.feature.home.domain.usecase.LoadHomeSummaryUseCase

data class HomeRouteDependencies(
    val loadHomeSummaryUseCase: LoadHomeSummaryUseCase,
    val appPreferencesStorage: AppPreferencesStorage,
    val getHomePriceHistoryUseCase: GetHomePriceHistoryUseCase,
)

@Composable
fun HomeRoute(
    dependencies: HomeRouteDependencies,
    session: AuthSession,
    onSelectTab: (MainTab) -> Unit,
    onBottomBarVisibilityChange: (Boolean) -> Unit,
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
    }

    when (val activeDestination = destination) {
        HomeDestination.Dashboard -> HomeScreen(
            session = session,
            uiState = uiState.value,
            onRefresh = { homeViewModel.onIntent(HomeIntent.Refresh) },
            getHomePriceHistoryUseCase = dependencies.getHomePriceHistoryUseCase,
            onOpenProfile = { destination = HomeDestination.Deferred(HomeDeferredTarget.Profile) },
            onOpenAlerts = { destination = HomeDestination.Deferred(HomeDeferredTarget.Alerts) },
            onOpenBuyGold = { destination = HomeDestination.Deferred(HomeDeferredTarget.BuyGold) },
            onOpenSellGold = { destination = HomeDestination.Deferred(HomeDeferredTarget.SellGold) },
            onOpenGoldValueDetails = {
                destination = HomeDestination.GoldValueDetails(uiState.value.summary?.dashboard)
            },
            onToggleBalanceVisibility = { homeViewModel.onIntent(HomeIntent.ToggleBalanceVisibility) },
            onOpenSavingsDetails = { destination = HomeDestination.Deferred(HomeDeferredTarget.Savings) },
            onOpenTransaction = { item -> destination = HomeDestination.TransactionDetails(item) },
            onOpenSupport = { destination = HomeDestination.HelpCenter },
            modifier = modifier,
        )
        is HomeDestination.GoldValueDetails -> HomeGoldValueDetailsScreen(
            dashboard = activeDestination.dashboard,
            onBackClick = { destination = HomeDestination.Dashboard },
            onBuyGoldClick = { destination = HomeDestination.Deferred(HomeDeferredTarget.BuyGold) },
            onSellGoldClick = { destination = HomeDestination.Deferred(HomeDeferredTarget.SellGold) },
        )
        HomeDestination.HelpCenter -> HomeHelpCenterScreen(
            onBackClick = { destination = HomeDestination.Dashboard },
        )
        is HomeDestination.TransactionDetails -> HomeTransactionDetailsScreen(
            transactionPreview = activeDestination.item,
            onBackClick = { destination = HomeDestination.Dashboard },
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
