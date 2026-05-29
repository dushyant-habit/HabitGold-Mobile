package com.habit.gold.feature.rewards.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

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
    RewardsHomeContent(
        state = state,
        onRefresh = onRefresh,
        onHistoryClick = onHistoryClick,
        onReferDetailClick = onReferDetailClick,
        onBuyGoldJourneyClick = onBuyGoldJourneyClick,
        onRedeemSwipe = onRedeemSwipe,
        modifier = modifier
            .fillMaxSize()
            .background(Neutral05),
    )
}
