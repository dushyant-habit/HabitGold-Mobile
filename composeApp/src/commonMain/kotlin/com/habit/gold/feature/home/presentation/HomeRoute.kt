package com.habit.gold.feature.home.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.habit.gold.core.navigation.MainTab
import com.habit.gold.core.session.AuthSession
import com.habit.gold.feature.home.domain.usecase.GetHomePriceHistoryUseCase
import com.habit.gold.feature.home.domain.usecase.LoadHomeSummaryUseCase
import org.koin.core.Koin

@Composable
fun HomeRoute(
    appKoin: Koin,
    session: AuthSession,
    onSelectTab: (MainTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val homeViewModel = viewModel {
        HomeViewModel(
            loadHomeSummaryUseCase = appKoin.get<LoadHomeSummaryUseCase>(),
        )
    }
    val uiState = homeViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(homeViewModel) {
        homeViewModel.onIntent(HomeIntent.Load)
    }

    HomeScreen(
        session = session,
        uiState = uiState.value,
        onRefresh = { homeViewModel.onIntent(HomeIntent.Refresh) },
        getHomePriceHistoryUseCase = appKoin.get<GetHomePriceHistoryUseCase>(),
        onOpenHistory = { onSelectTab(MainTab.History) },
        modifier = modifier,
    )
}
