package com.habit.gold.feature.rewards.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.habit.gold.core.presentation.PlatformBackHandler
import com.habit.gold.feature.profile.presentation.ProfileContactUsScreen
import com.habit.gold.feature.profile.presentation.ProfileHelpCenterScreen
import com.habit.gold.feature.rewards.domain.usecase.GetReferDetailsUseCase
import com.habit.gold.feature.rewards.domain.usecase.GetRewardsHistoryUseCase
import com.habit.gold.feature.rewards.domain.usecase.GetRewardsMilestonesUseCase
import com.habit.gold.feature.rewards.domain.usecase.GetRewardsUserFeaturesUseCase
import com.habit.gold.feature.savings.presentation.SavingsDestination
import com.habit.gold.feature.savings.presentation.SavingsRoute
import com.habit.gold.feature.savings.presentation.SavingsRouteDependencies
import com.habit.gold.feature.trade.presentation.TradeDestination
import com.habit.gold.feature.trade.presentation.TradeRoute
import com.habit.gold.feature.trade.presentation.TradeRouteDependencies
import io.ktor.client.HttpClient

data class RewardsRouteDependencies(
    val getRewardsMilestonesUseCase: GetRewardsMilestonesUseCase,
    val getRewardsUserFeaturesUseCase: GetRewardsUserFeaturesUseCase,
    val getRewardsHistoryUseCase: GetRewardsHistoryUseCase,
    val getReferDetailsUseCase: GetReferDetailsUseCase,
    val tradeDependencies: TradeRouteDependencies,
    val savingsDependencies: SavingsRouteDependencies,
    val httpClient: HttpClient,
)

private sealed interface RewardsDestination {
    data object Home : RewardsDestination
    data object History : RewardsDestination
    data object ReferDetail : RewardsDestination
    data object Redeem : RewardsDestination
    data class HelpCenter(val returnDestination: RewardsDestination) : RewardsDestination
    data class ContactUs(val returnDestination: RewardsDestination) : RewardsDestination
    data class TradeFlow(val destination: TradeDestination) : RewardsDestination
    data class SavingsFlow(val destination: SavingsDestination) : RewardsDestination
}

enum class RewardsEntryPoint {
    Home,
    ReferDetail,
}

@Composable
fun RewardsRoute(
    dependencies: RewardsRouteDependencies,
    onBottomBarVisibilityChange: (Boolean) -> Unit,
    entryPoint: RewardsEntryPoint = RewardsEntryPoint.Home,
    onExitRequested: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val rewardsViewModel = viewModel {
        RewardsViewModel(
            getRewardsMilestonesUseCase = dependencies.getRewardsMilestonesUseCase,
            getRewardsUserFeaturesUseCase = dependencies.getRewardsUserFeaturesUseCase,
        )
    }
    val historyViewModel = viewModel {
        RewardsHistoryViewModel(
            getRewardsHistoryUseCase = dependencies.getRewardsHistoryUseCase,
        )
    }
    val referDetailViewModel = viewModel {
        RewardsReferDetailViewModel(
            getReferDetailsUseCase = dependencies.getReferDetailsUseCase,
        )
    }

    val rewardsState by rewardsViewModel.state.collectAsStateWithLifecycle()
    val historyState by historyViewModel.state.collectAsStateWithLifecycle()
    val referDetailState by referDetailViewModel.state.collectAsStateWithLifecycle()
    var destination by remember(entryPoint) {
        mutableStateOf<RewardsDestination>(
            when (entryPoint) {
                RewardsEntryPoint.Home -> RewardsDestination.Home
                RewardsEntryPoint.ReferDetail -> RewardsDestination.ReferDetail
            },
        )
    }
    var nestedReturnDestination by remember { mutableStateOf<RewardsDestination>(RewardsDestination.Home) }
    val rootDestination = remember(entryPoint) {
        when (entryPoint) {
            RewardsEntryPoint.Home -> RewardsDestination.Home
            RewardsEntryPoint.ReferDetail -> RewardsDestination.ReferDetail
        }
    }

    LaunchedEffect(Unit) {
        rewardsViewModel.onIntent(RewardsHomeIntent.Visible)
    }

    LaunchedEffect(destination) {
        onBottomBarVisibilityChange(destination == RewardsDestination.Home)
    }

    PlatformBackHandler(
        enabled = destination != RewardsDestination.Home &&
            destination !is RewardsDestination.TradeFlow &&
            destination !is RewardsDestination.SavingsFlow,
        onBack = {
            val activeDestination = destination
            destination = when (activeDestination) {
                RewardsDestination.History -> rootDestination
                RewardsDestination.ReferDetail -> {
                    onExitRequested?.invoke()
                    rootDestination
                }
                RewardsDestination.Redeem -> RewardsDestination.Home
                is RewardsDestination.HelpCenter -> activeDestination.returnDestination
                is RewardsDestination.ContactUs -> activeDestination.returnDestination
                RewardsDestination.Home -> RewardsDestination.Home
                is RewardsDestination.TradeFlow,
                is RewardsDestination.SavingsFlow -> nestedReturnDestination
            }
        },
    )

    when (val currentDestination = destination) {
        RewardsDestination.Home -> RewardsScreen(
            state = rewardsState,
            onRefresh = { rewardsViewModel.onIntent(RewardsHomeIntent.Refresh) },
            onHistoryClick = { destination = RewardsDestination.History },
            onReferDetailClick = { destination = RewardsDestination.ReferDetail },
            onBuyGoldJourneyClick = {
                nestedReturnDestination = RewardsDestination.Home
                destination = RewardsDestination.TradeFlow(
                    TradeDestination.Buy(amount = "100", oneTimeUseGrams = false),
                )
            },
            onRedeemSwipe = {
                nestedReturnDestination = RewardsDestination.Home
                destination = RewardsDestination.Redeem
            },
            modifier = modifier,
        )

        RewardsDestination.History -> RewardsHistoryScreen(
            state = historyState,
            onRefresh = historyViewModel::refresh,
            onBackClick = { destination = rootDestination },
            modifier = modifier,
        )

        RewardsDestination.ReferDetail -> RewardsReferDetailScreen(
            state = referDetailState,
            onRefresh = referDetailViewModel::refresh,
            onBackClick = {
                if (entryPoint == RewardsEntryPoint.ReferDetail) {
                    onExitRequested?.invoke()
                } else {
                    destination = RewardsDestination.Home
                }
            },
            onHistoryClick = { destination = RewardsDestination.History },
            onBuyNowClick = {
                nestedReturnDestination = RewardsDestination.ReferDetail
                destination = RewardsDestination.TradeFlow(
                    TradeDestination.Buy(amount = "10000", oneTimeUseGrams = false),
                )
            },
            onStartSipClick = {
                nestedReturnDestination = RewardsDestination.ReferDetail
                destination = RewardsDestination.SavingsFlow(
                    SavingsDestination.Setup(
                        frequency = "Weekly",
                        initialAmount = "2500",
                    ),
                )
            },
            modifier = modifier,
        )

        RewardsDestination.Redeem -> RewardsRedeemRouteController(
            rewardsState = rewardsState,
            tradeDependencies = dependencies.tradeDependencies,
            onBackClick = { destination = nestedReturnDestination },
            onRefreshRewards = { rewardsViewModel.onIntent(RewardsHomeIntent.Refresh) },
            modifier = modifier,
        )

        is RewardsDestination.HelpCenter -> ProfileHelpCenterScreen(
            onBackClick = { destination = currentDestination.returnDestination },
            onOpenContactUs = {
                destination = RewardsDestination.ContactUs(
                    returnDestination = currentDestination,
                )
            },
            modifier = modifier,
        )

        is RewardsDestination.ContactUs -> ProfileContactUsScreen(
            httpClient = dependencies.httpClient,
            userName = "",
            userPhone = "",
            userEmail = "",
            userGender = "",
            userDob = "",
            onBackClick = { destination = currentDestination.returnDestination },
            modifier = modifier,
        )

        is RewardsDestination.TradeFlow -> {
            TradeRoute(
                dependencies = dependencies.tradeDependencies,
                destination = currentDestination.destination,
                onBackToHome = { destination = nestedReturnDestination },
                onNavigate = { nextTradeDestination ->
                    destination = RewardsDestination.TradeFlow(nextTradeDestination)
                },
                onOpenHelp = {
                    destination = RewardsDestination.HelpCenter(
                        returnDestination = currentDestination,
                    )
                },
                modifier = modifier,
            )
        }

        is RewardsDestination.SavingsFlow -> {
            SavingsRoute(
                dependencies = dependencies.savingsDependencies,
                destination = currentDestination.destination,
                onBackToHome = { destination = nestedReturnDestination },
                onOpenHelp = {
                    destination = RewardsDestination.HelpCenter(
                        returnDestination = currentDestination,
                    )
                },
                modifier = modifier,
            )
        }
    }
}
