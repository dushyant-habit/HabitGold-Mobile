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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.habit.gold.core.session.AuthSession
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
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.home_screen_action_alerts_pending
import habitgoldmobile.composeapp.generated.resources.home_screen_action_buy_pending
import habitgoldmobile.composeapp.generated.resources.home_screen_action_profile_pending
import habitgoldmobile.composeapp.generated.resources.home_screen_action_savings_pending
import habitgoldmobile.composeapp.generated.resources.home_screen_action_sell_pending
import habitgoldmobile.composeapp.generated.resources.home_screen_action_support_pending
import habitgoldmobile.composeapp.generated.resources.home_screen_action_value_pending
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

private val HomeBackground = Color(0xFFF8F8FB)

@Composable
fun HomeScreen(
    session: AuthSession,
    uiState: HomeState,
    getHomePriceHistoryUseCase: GetHomePriceHistoryUseCase,
    onRefresh: () -> Unit,
    onOpenHistory: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val summary = uiState.summary
    val dashboard = summary?.dashboard
    val listState = rememberLazyListState()
    var isBalanceVisible by rememberSaveable { mutableStateOf(true) }
    var isBalanceExpanded by rememberSaveable { mutableStateOf(false) }
    var activeSheet by remember { mutableStateOf<HomeBottomSheetState?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val profilePendingMessage = stringResource(Res.string.home_screen_action_profile_pending)
    val alertsPendingMessage = stringResource(Res.string.home_screen_action_alerts_pending)
    val buyPendingMessage = stringResource(Res.string.home_screen_action_buy_pending)
    val sellPendingMessage = stringResource(Res.string.home_screen_action_sell_pending)
    val supportPendingMessage = stringResource(Res.string.home_screen_action_support_pending)
    val savingsPendingMessage = stringResource(Res.string.home_screen_action_savings_pending)
    val valuePendingMessage = stringResource(Res.string.home_screen_action_value_pending)

    fun showMessage(message: String) {
        coroutineScope.launch {
            snackbarHostState.showSnackbar(message)
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
                onProfileClick = { showMessage(profilePendingMessage) },
                onAlertsClick = { showMessage(alertsPendingMessage) },
                onOpenGoldPrice = { activeSheet = HomeBottomSheetState.GoldPrice },
            )

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
                            HomeLoadingCard()
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
                                onStartJourneyClick = { showMessage(buyPendingMessage) },
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
                                onOpenSavingsScreen = { showMessage(savingsPendingMessage) },
                            )
                        }
                        item("recent-activity") {
                            RecentActivitySection(
                                items = summary.recentTransactions,
                                onOpenHistory = onOpenHistory,
                            )
                        }
                        item("why-habitgold") {
                            HomeWhyHabitGoldSection(
                                onOpenIntroSheet = { activeSheet = HomeBottomSheetState.IntroPager(it) },
                            )
                        }
                        item("home-support-footer") {
                            HomeSupportFooter(
                                onSupportClick = { showMessage(supportPendingMessage) },
                            )
                        }
                    }

                    dashboard != null -> {
                        item("balance-card") {
                            Box(modifier = Modifier.padding(top = 6.dp)) {
                                HomeBalanceCard(
                                    dashboard = dashboard,
                                    dashboardError = uiState.errorMessage,
                                    isBalanceVisible = isBalanceVisible,
                                    isBalanceExpanded = isBalanceExpanded,
                                    onToggleBalanceVisible = { isBalanceVisible = !isBalanceVisible },
                                    onToggleBalanceExpanded = { isBalanceExpanded = !isBalanceExpanded },
                                    onViewDetailsClick = { showMessage(valuePendingMessage) },
                                    onBuyGoldClick = { showMessage(buyPendingMessage) },
                                    onSellGoldClick = { showMessage(sellPendingMessage) },
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
                                onOpenSavingsScreen = { showMessage(savingsPendingMessage) },
                            )
                        }
                        if (summary.sipMandates.isNotEmpty()) {
                            item("sip-mandates") {
                                HomeSipMandatesSection(
                                    mandates = summary.sipMandates,
                                    onViewAllClick = { showMessage(savingsPendingMessage) },
                                )
                            }
                        }
                        item("recent-activity") {
                            RecentActivitySection(
                                items = summary.recentTransactions,
                                onOpenHistory = onOpenHistory,
                            )
                        }
                        item("why-habitgold") {
                            HomeWhyHabitGoldSection(
                                onOpenIntroSheet = { activeSheet = HomeBottomSheetState.IntroPager(it) },
                            )
                        }
                        item("home-support-footer") {
                            HomeSupportFooter(
                                onSupportClick = { showMessage(supportPendingMessage) },
                            )
                        }
                    }
                }

                item("home-bottom-spacer") {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
        )

        activeSheet?.let { sheet ->
            HomeBottomSheetHost(
                sheetState = sheet,
                liveRate = dashboard?.liveBuyPricePerGram ?: 0.0,
                getHomePriceHistoryUseCase = getHomePriceHistoryUseCase,
                onDismiss = { activeSheet = null },
                onBuyGoldClick = { showMessage(buyPendingMessage) },
            )
        }
    }
}
