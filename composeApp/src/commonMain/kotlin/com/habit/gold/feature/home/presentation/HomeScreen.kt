package com.habit.gold.feature.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.habit.gold.core.designsystem.HabitGoldPullToRefreshIndicator
import com.habit.gold.core.session.AuthSession
import com.habit.gold.feature.home.domain.model.HomeSipMandate
import com.habit.gold.feature.home.domain.usecase.GetHomePriceHistoryUseCase
import com.habit.gold.feature.home.presentation.components.GoldSavingsPlansCard
import com.habit.gold.feature.home.presentation.components.HomeBalanceCard
import com.habit.gold.feature.home.presentation.components.HomeBottomSheetHost
import com.habit.gold.feature.home.presentation.components.HomeBottomSheetState
import com.habit.gold.feature.home.presentation.components.HomeErrorCard
import com.habit.gold.feature.home.presentation.components.HomeForceUpdateCard
import com.habit.gold.feature.home.presentation.components.HomeLoadingCard
import com.habit.gold.feature.home.presentation.components.HomeSipMandatesSection
import com.habit.gold.feature.home.presentation.components.HomeSupportFooter
import com.habit.gold.feature.home.presentation.components.HomeTopBar
import com.habit.gold.feature.home.presentation.components.HomeTrustHighlightsSection
import com.habit.gold.feature.home.presentation.components.HomeWhyHabitGoldSection
import com.habit.gold.feature.home.presentation.components.HomeZeroBalanceSection
import com.habit.gold.feature.home.presentation.components.RecentActivitySection

private val HomeBackground = Color(0xFFF8F8FB)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    session: AuthSession,
    uiState: HomeState,
    getHomePriceHistoryUseCase: GetHomePriceHistoryUseCase,
    onRefresh: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenAlerts: () -> Unit,
    onOpenBuyGold: () -> Unit,
    onOpenSellGold: () -> Unit,
    onOpenGoldValueDetails: () -> Unit,
    onToggleBalanceVisibility: () -> Unit,
    onOpenSavingsDetails: () -> Unit,
    onOpenSavingsSetup: (String, HomeSipMandate?) -> Unit,
    onOpenTransaction: (com.habit.gold.feature.home.domain.model.HomeRecentTransactionPreview) -> Unit,
    onOpenAllTransactions: () -> Unit,
    onOpenSupport: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val summary = uiState.summary
    val dashboard = summary?.dashboard
    val listState = rememberLazyListState()
    val pullToRefreshState = rememberPullToRefreshState()
    var isBalanceExpanded by rememberSaveable { mutableStateOf(false) }
    var activeSheet by remember { mutableStateOf<HomeBottomSheetState?>(null) }

    LaunchedEffect(dashboard?.liveBuyPricePerGram) {
        val liveBuyPrice = dashboard?.liveBuyPricePerGram ?: 0.0
        if (liveBuyPrice > 0.0) {
            getHomePriceHistoryUseCase("1Y", liveBuyPrice)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(HomeBackground),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            HomeTopBar(
                user = session.user,
                liveRate = dashboard?.liveBuyPricePerGram ?: 0.0,
                hasUnreadAlerts = uiState.hasUnreadAlerts,
                onProfileClick = onOpenProfile,
                onAlertsClick = onOpenAlerts,
                onOpenGoldPrice = { activeSheet = HomeBottomSheetState.GoldPrice },
            )

            PullToRefreshBox(
                isRefreshing = uiState.isRefreshing,
                onRefresh = onRefresh,
                state = pullToRefreshState,
                modifier = Modifier.fillMaxSize(),
                indicator = {
                    HabitGoldPullToRefreshIndicator(
                        isRefreshing = uiState.isRefreshing,
                        state = pullToRefreshState,
                        modifier = Modifier.align(Alignment.TopCenter),
                    )
                },
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    summary?.forceUpdate?.let { update ->
                        item("force-update") {
                            HomeForceUpdateCard(update = update)
                        }
                    }

                    when {
                        uiState.isLoading && summary == null -> {
                            item("loading") {
                                Box(modifier = Modifier.padding(top = 16.dp)) {
                                    HomeLoadingCard()
                                }
                            }
                        }

                        summary == null && !uiState.errorMessage.isNullOrBlank() -> {
                            item("error") {
                                HomeErrorCard(
                                    errorMessage = uiState.errorMessage,
                                    onRetry = onRefresh,
                                )
                            }
                        }

                        dashboard != null && dashboard.totalGoldBalanceGrams <= 0.0 -> {
                            item("zero-balance") {
                                HomeZeroBalanceSection(
                                    onStartJourneyClick = onOpenBuyGold,
                                )
                            }
                            item("trust-highlights") {
                                HomeTrustHighlightsSection(
                                    onOpenIntroSheet = { activeSheet = HomeBottomSheetState.IntroPager(it) },
                                )
                            }
                            item("gold-savings") {
                                GoldSavingsPlansCard(
                                    mandates = summary.sipMandates,
                                    onOpenSavingsScreen = onOpenSavingsSetup,
                                )
                            }
                            if (summary.sipMandates.isNotEmpty()) {
                                item("sip-mandates") {
                                    HomeSipMandatesSection(
                                        mandates = summary.sipMandates,
                                    )
                                }
                            }
                            item("recent-activity") {
                                RecentActivitySection(
                                    items = summary.recentTransactions,
                                    onOpenTransaction = onOpenTransaction,
                                    onViewAllClick = onOpenAllTransactions,
                                )
                            }
                            item("why-habitgold") {
                                HomeWhyHabitGoldSection(
                                    onOpenIntroSheet = { activeSheet = HomeBottomSheetState.IntroPager(it) },
                                )
                            }
                            item("home-support-footer") {
                                HomeSupportFooter(
                                    onSupportClick = onOpenSupport,
                                )
                            }
                        }

                        dashboard != null -> {
                            item("balance-card") {
                                Box(modifier = Modifier.padding(top = 16.dp)) {
                                    HomeBalanceCard(
                                        dashboard = dashboard,
                                        dashboardError = uiState.errorMessage,
                                        isBalanceVisible = uiState.isBalanceVisible,
                                        isBalanceExpanded = isBalanceExpanded,
                                        onToggleBalanceVisible = onToggleBalanceVisibility,
                                        onToggleBalanceExpanded = { isBalanceExpanded = !isBalanceExpanded },
                                        onViewDetailsClick = onOpenGoldValueDetails,
                                        onBuyGoldClick = onOpenBuyGold,
                                        onSellGoldClick = onOpenSellGold,
                                    )
                                }
                            }
                            item("trust-highlights") {
                                HomeTrustHighlightsSection(
                                    onOpenIntroSheet = { activeSheet = HomeBottomSheetState.IntroPager(it) },
                                )
                            }
                            item("gold-savings") {
                                GoldSavingsPlansCard(
                                    mandates = summary.sipMandates,
                                    onOpenSavingsScreen = onOpenSavingsSetup,
                                )
                            }
                            if (summary.sipMandates.isNotEmpty()) {
                                item("sip-mandates") {
                                    HomeSipMandatesSection(
                                        mandates = summary.sipMandates,
                                    )
                                }
                            }
                            item("recent-activity") {
                                RecentActivitySection(
                                    items = summary.recentTransactions,
                                    onOpenTransaction = onOpenTransaction,
                                    onViewAllClick = onOpenAllTransactions,
                                )
                            }
                            item("why-habitgold") {
                                HomeWhyHabitGoldSection(
                                    onOpenIntroSheet = { activeSheet = HomeBottomSheetState.IntroPager(it) },
                                )
                            }
                            item("home-support-footer") {
                                HomeSupportFooter(
                                    onSupportClick = onOpenSupport,
                                )
                            }
                        }
                    }

                    item("home-bottom-spacer") {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        activeSheet?.let { sheet ->
            HomeBottomSheetHost(
                sheetState = sheet,
                liveRate = dashboard?.liveBuyPricePerGram ?: 0.0,
                getHomePriceHistoryUseCase = getHomePriceHistoryUseCase,
                onDismiss = { activeSheet = null },
                onBuyGoldClick = onOpenBuyGold,
            )
        }
    }
}
