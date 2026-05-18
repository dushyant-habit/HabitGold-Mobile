package com.habit.gold.feature.rewards.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardsScreen(
    state: RewardsHomeState,
    onRefresh: () -> Unit,
    onHistoryClick: () -> Unit,
    onReferDetailClick: () -> Unit,
    onBuyGoldJourneyClick: () -> Unit,
    onRedeemSwipe: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Neutral05),
    ) {
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = onRefresh,
        ) {
            RewardsHomeContent(
                state = state,
                onRefresh = onRefresh,
                onHistoryClick = onHistoryClick,
                onReferDetailClick = onReferDetailClick,
                onBuyGoldJourneyClick = onBuyGoldJourneyClick,
                onRedeemSwipe = onRedeemSwipe,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
