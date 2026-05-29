package com.habit.gold.feature.rewards.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habit.gold.core.designsystem.HabitGoldPullToRefreshIndicator
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.common_retry
import habitgoldmobile.composeapp.generated.resources.ic_buy_gold_icon
import habitgoldmobile.composeapp.generated.resources.rewards_flow_toolbar_rewards_history
import habitgoldmobile.composeapp.generated.resources.rewards_history_screen_empty
import habitgoldmobile.composeapp.generated.resources.rewards_history_screen_helper
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardsHistoryScreen(
    state: RewardsHistoryState,
    onRefresh: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pullToRefreshState = rememberPullToRefreshState()

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
            RewardsCenterTitleTopBar(
                title = stringResource(Res.string.rewards_flow_toolbar_rewards_history),
                onBackClick = onBackClick,
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(androidx.compose.ui.graphics.RectangleShape),
        ) {
            PullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = onRefresh,
                state = pullToRefreshState,
                indicator = {
                    HabitGoldPullToRefreshIndicator(
                        isRefreshing = state.isRefreshing,
                        state = pullToRefreshState,
                        modifier = Modifier.align(Alignment.TopCenter),
                    )
                },
            ) {
                when {
                    state.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(color = Purple700)
                        }
                    }

                    state.errorMessage != null && state.items.isEmpty() -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                text = state.errorMessage,
                                fontSize = 15.sp,
                                color = Slate500,
                                textAlign = TextAlign.Center,
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            TextButton(
                                onClick = onRefresh,
                                colors = ButtonDefaults.textButtonColors(contentColor = Purple700),
                            ) {
                                Text(
                                    text = stringResource(Res.string.common_retry),
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                        }
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            item {
                                Text(
                                    text = stringResource(Res.string.rewards_history_screen_helper),
                                    fontSize = 13.sp,
                                    color = Slate500,
                                    lineHeight = 18.sp,
                                    modifier = Modifier.padding(bottom = 6.dp),
                                )
                            }
                            if (state.items.isEmpty()) {
                                item {
                                    Text(
                                        text = stringResource(Res.string.rewards_history_screen_empty),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Slate400,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 32.dp),
                                        textAlign = TextAlign.Center,
                                    )
                                }
                            } else {
                                items(
                                    items = state.items,
                                    key = { it.id },
                                ) { row ->
                                    RewardHistoryListRow(row = row)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RewardHistoryListRow(
    row: RewardHistoryRowUi,
) {
    val visuals = rewardHistoryVisuals(row)
    val amountColor = if (row.isCredit) Emerald600 else Red600

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (row.expired) 0.55f else 1f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.Top,
        ) {
            when {
                visuals.drawable != null -> Image(
                    painter = painterResource(visuals.drawable),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                )
                visuals.imageVector != null -> Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(visuals.background),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = visuals.imageVector,
                        contentDescription = null,
                        tint = visuals.tint,
                        modifier = Modifier.size(24.dp),
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp),
            ) {
                Row {
                    Text(
                        text = row.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Slate800,
                        lineHeight = 20.sp,
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = row.amountLabel,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = amountColor,
                        modifier = Modifier.padding(top = 1.dp),
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = row.dateLabel,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Slate400,
                        lineHeight = 16.sp,
                    )
                    row.expiryLabel?.let { expiryLabel ->
                        Text(
                            text = "|",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Slate300,
                        )
                        Text(
                            text = expiryLabel,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Slate400,
                            lineHeight = 16.sp,
                        )
                    }
                }
            }
        }
    }
}

private data class RewardHistoryVisuals(
    val imageVector: androidx.compose.ui.graphics.vector.ImageVector? = null,
    val drawable: DrawableResource? = null,
    val tint: Color,
    val background: Color,
)

private fun rewardHistoryVisuals(row: RewardHistoryRowUi): RewardHistoryVisuals {
    val isGoldPurchaseReward = row.sourceChip == "CASHBACK" || row.title.contains("Gold purchase", ignoreCase = true)
    if (isGoldPurchaseReward) {
        return RewardHistoryVisuals(
            drawable = Res.drawable.ic_buy_gold_icon,
            tint = Teal600,
            background = Emerald50,
        )
    }
    if (!row.isCredit) {
        return RewardHistoryVisuals(
            imageVector = Icons.AutoMirrored.Filled.TrendingDown,
            tint = Red600,
            background = Red50,
        )
    }
    return when (row.sourceChip) {
        "MILESTONE" -> RewardHistoryVisuals(
            imageVector = Icons.Default.Stars,
            tint = Purple700,
            background = Purple100,
        )
        else -> RewardHistoryVisuals(
            imageVector = Icons.Default.CardGiftcard,
            tint = Purple700,
            background = Purple100,
        )
    }
}
