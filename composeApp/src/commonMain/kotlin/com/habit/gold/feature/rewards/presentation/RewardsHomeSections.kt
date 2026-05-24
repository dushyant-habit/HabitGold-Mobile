package com.habit.gold.feature.rewards.presentation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.common_retry
import habitgoldmobile.composeapp.generated.resources.rewards_home_error
import org.jetbrains.compose.resources.stringResource

@Composable
fun RewardsHomeContent(
    state: RewardsHomeState,
    onRefresh: () -> Unit,
    onHistoryClick: () -> Unit,
    onReferDetailClick: () -> Unit,
    onBuyGoldJourneyClick: () -> Unit,
    onRedeemSwipe: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        state.isLoading && state.homeUi == null -> RewardsHomeShimmer(modifier = modifier)
        state.errorMessage != null && state.homeUi == null -> RewardsHomeError(
            message = state.errorMessage,
            onRetry = onRefresh,
            modifier = modifier,
        )
        else -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(Neutral05),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(White)
                        .statusBarsPadding(),
                ) {
                    RewardsHomeTopBar(onRewardsHistoryClick = onHistoryClick)
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                ) {
                    RewardsHomeJourneySections(
                        state = state,
                        onReferDetailClick = onReferDetailClick,
                        onBuyGoldJourneyClick = onBuyGoldJourneyClick,
                        onRedeemSwipe = onRedeemSwipe,
                    )
                    RewardsMilestoneSection(
                        state = state,
                        onReferDetailClick = onReferDetailClick,
                        onBuyGoldJourneyClick = onBuyGoldJourneyClick,
                    )

                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
private fun RewardsHomeError(
    message: String?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Neutral05)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = message ?: stringResource(Res.string.rewards_home_error),
            color = Slate500,
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = Purple700),
        ) {
            Text(stringResource(Res.string.common_retry))
        }
    }
}

@Composable
private fun RewardsHomeShimmer(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Neutral05),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(White)
                .statusBarsPadding(),
        ) {
            RewardsHomeTopBar(onRewardsHistoryClick = {})
        }
        Spacer(modifier = Modifier.height(12.dp))
        RewardsShimmerCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(190.dp),
            shape = RoundedCornerShape(20.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        RewardsShimmerCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .height(246.dp),
            shape = RoundedCornerShape(24.dp),
        )
        Spacer(modifier = Modifier.height(18.dp))
        RewardsShimmerCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(128.dp),
            shape = RoundedCornerShape(12.dp),
        )
        Spacer(modifier = Modifier.height(24.dp))
        RewardsShimmerBar(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .width(180.dp)
                .height(14.dp),
        )
        repeat(4) {
            Spacer(modifier = Modifier.height(12.dp))
            RewardsShimmerCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(120.dp),
                shape = RoundedCornerShape(16.dp),
            )
        }
    }
}

@Composable
internal fun RewardsShimmerCard(
    modifier: Modifier,
    shape: RoundedCornerShape,
) {
    val transition = rememberInfiniteTransition(label = "rewards-shimmer")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = LinearEasing),
        ),
        label = "rewards-shimmer-progress",
    )
    val shimmerBrush = Brush.linearGradient(
        colors = listOf(Color(0xFFE7EBF0), Color(0xFFF6F8FB), Color(0xFFE7EBF0)),
        start = Offset.Zero,
        end = Offset(800f * progress + 300f, 800f * progress + 300f),
    )
    Box(
        modifier = modifier
            .clip(shape)
            .background(shimmerBrush),
    )
}

@Composable
private fun RewardsShimmerBar(
    modifier: Modifier = Modifier,
) {
    RewardsShimmerCard(
        modifier = modifier,
        shape = RoundedCornerShape(999.dp),
    )
}
