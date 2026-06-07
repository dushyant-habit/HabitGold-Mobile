package com.habit.gold.feature.rewards.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.habit.gold.core.designsystem.icons.HabitGoldIcons as Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.common_buy_now
import habitgoldmobile.composeapp.generated.resources.refer_earn_screen_buy_half_gram_unlock_rewards
import habitgoldmobile.composeapp.generated.resources.refer_earn_screen_extra_cashback_after_you_finish_all_milestones
import habitgoldmobile.composeapp.generated.resources.refer_earn_screen_gold_purchase_cashback
import habitgoldmobile.composeapp.generated.resources.refer_earn_screen_insured
import habitgoldmobile.composeapp.generated.resources.refer_earn_screen_insured_vault
import habitgoldmobile.composeapp.generated.resources.refer_earn_screen_milestone_rewards_path
import habitgoldmobile.composeapp.generated.resources.refer_earn_screen_milestones_done_tap_below_to_turn_on_your
import habitgoldmobile.composeapp.generated.resources.refer_earn_screen_no_fees
import habitgoldmobile.composeapp.generated.resources.refer_earn_screen_no_hidden_fees
import habitgoldmobile.composeapp.generated.resources.refer_earn_screen_refer_and_earn_rewards_when_friends_join
import habitgoldmobile.composeapp.generated.resources.refer_earn_screen_refer_now
import habitgoldmobile.composeapp.generated.resources.refer_earn_screen_referral_cashback
import habitgoldmobile.composeapp.generated.resources.refer_earn_screen_redeemable
import habitgoldmobile.composeapp.generated.resources.refer_earn_screen_redeemed
import habitgoldmobile.composeapp.generated.resources.refer_earn_screen_start_your_gold_journey
import habitgoldmobile.composeapp.generated.resources.refer_earn_screen_total_rewards_earned
import habitgoldmobile.composeapp.generated.resources.refer_earn_screen_unlock_booster
import habitgoldmobile.composeapp.generated.resources.refer_earn_screen_unlock_extra_gold
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun RewardsHomeJourneySections(
    state: RewardsHomeState,
    onReferDetailClick: () -> Unit,
    onBuyGoldJourneyClick: () -> Unit,
    onRedeemSwipe: () -> Unit,
) {
    val rewardsUi = state.homeUi
    val showPostJourney = rewardsUi?.usePostJourneyHeader == true

    if (showPostJourney) {
        TopRewardsCard(
            totalRewardsText = rewardsUi.totalEarnedDisplay,
            goldPurchaseCashback = rewardsUi.goldCashbackDisplay,
            referralCashback = rewardsUi.referralCashbackDisplay,
            redeemable = rewardsUi.redeemableDisplay,
            redeemed = rewardsUi.redeemedDisplay,
            onReferNowClick = onReferDetailClick,
            onSwipeRedeem = onRedeemSwipe,
        )
    } else {
        PreJourneyHeroCard(onActivateClick = onBuyGoldJourneyClick)
    }

    ExtraGoldRewardCard(onBuyGoldClick = onBuyGoldJourneyClick)

    Spacer(modifier = Modifier.height(18.dp))

    ReferWinCard(onReferDetailClick = onReferDetailClick)
}

@Composable
internal fun RewardsMilestoneSection(
    state: RewardsHomeState,
    onReferDetailClick: () -> Unit,
    onBuyGoldJourneyClick: () -> Unit,
) {
    val rewardsUi = state.homeUi
    val rows = rewardsUi?.rows ?: defaultRewardsMilestoneRows()
    val lifetimeBooster = rewardsUi?.lifetimeBooster ?: defaultRewardsLifetimeBoosterRowUi(rows)
    val showRewardsMilestoneJourney = state.rewardsFeatureActive && rewardsUi?.milestonesActive == true

    if (showRewardsMilestoneJourney) {
        Spacer(modifier = Modifier.height(24.dp))
        MilestoneRewardsSection(
            rows = rows,
            lifetimeBooster = lifetimeBooster,
            onBuyGoldClick = onBuyGoldJourneyClick,
            onReferEarnDetailClick = onReferDetailClick,
        )
    }
}

@Composable
private fun PreJourneyHeroCard(
    onActivateClick: () -> Unit,
) {
    Spacer(modifier = Modifier.height(12.dp))
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Purple850),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .background(White.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = null,
                        tint = White,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(Res.string.refer_earn_screen_start_your_gold_journey),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = White,
                        letterSpacing = 1.sp,
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = Slate200,
                        spotColor = Slate200,
                    )
                    .background(White, RoundedCornerShape(16.dp))
                    .border(1.dp, White, RoundedCornerShape(16.dp))
                    .clickable(onClick = onActivateClick),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(Res.string.refer_earn_screen_buy_half_gram_unlock_rewards),
                        color = Purple850,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Purple850,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = stringResource(Res.string.refer_earn_screen_insured),
                        tint = White.copy(alpha = 0.8f),
                        modifier = Modifier.size(14.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(Res.string.refer_earn_screen_insured_vault),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = White.copy(alpha = 0.8f),
                        letterSpacing = 0.5.sp,
                    )
                }

                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .background(White.copy(alpha = 0.5f), CircleShape),
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CurrencyRupee,
                        contentDescription = stringResource(Res.string.refer_earn_screen_no_fees),
                        tint = White.copy(alpha = 0.8f),
                        modifier = Modifier.size(14.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(Res.string.refer_earn_screen_no_hidden_fees),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = White.copy(alpha = 0.8f),
                        letterSpacing = 0.5.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun ExtraGoldRewardCard(
    onBuyGoldClick: () -> Unit,
) {
    val cardShape = RoundedCornerShape(24.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .shadow(
                elevation = 18.dp,
                shape = cardShape,
                ambientColor = Purple700.copy(alpha = 0.28f),
                spotColor = Purple950.copy(alpha = 0.18f),
            )
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Purple950Alt, Purple850, Purple700),
                ),
                shape = cardShape,
            )
            .border(1.dp, White.copy(alpha = 0.08f), cardShape),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 22.dp),
        ) {
            Text(
                text = "Boost Your Savings",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = White,
                lineHeight = 29.sp,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Turn every gold purchase into extra savings",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = White.copy(alpha = 0.74f),
                lineHeight = 20.sp,
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(White.copy(alpha = 0.06f), RoundedCornerShape(18.dp))
                    .border(1.dp, White.copy(alpha = 0.08f), RoundedCornerShape(18.dp))
                    .padding(horizontal = 18.dp, vertical = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start,
                ) {
                    Text(
                        text = "On Every\nPurchase",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = White,
                        lineHeight = 22.sp,
                    )
                }

                Box(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .size(34.dp)
                        .background(White.copy(alpha = 0.08f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = PurpleGlow200,
                        modifier = Modifier.size(18.dp),
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End,
                ) {
                    Text(
                        text = "0.5%",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = White,
                        lineHeight = 26.sp,
                        textAlign = TextAlign.End,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "extra gold",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PurpleGlow200,
                        lineHeight = 14.sp,
                        textAlign = TextAlign.End,
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onBuyGoldClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = White,
                    contentColor = Purple850,
                ),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 0.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                modifier = Modifier.fillMaxWidth().height(52.dp),
            ) {
                Text(
                    text = stringResource(Res.string.refer_earn_screen_unlock_extra_gold),
                    fontWeight = FontWeight.Black,
                    color = Gold500,
                    fontSize = 15.sp,
                    letterSpacing = 0.2.sp,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Gold500,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

@Composable
private fun ReferWinCard(
    onReferDetailClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clickable(onClick = onReferDetailClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Purple850)
                .padding(start = 4.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(White, RoundedCornerShape(12.dp)),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "REFER & WIN",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Purple850.copy(alpha = 0.6f),
                            letterSpacing = 1.sp,
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "WIN ASSURED REWARDS UPTO ₹1,00,000",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Purple850,
                            lineHeight = 25.sp,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(Res.string.refer_earn_screen_refer_and_earn_rewards_when_friends_join),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Slate500,
                            lineHeight = 18.sp,
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(Purple850, RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = White,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TopRewardsCard(
    totalRewardsText: String,
    goldPurchaseCashback: String,
    referralCashback: String,
    redeemable: String,
    redeemed: String,
    onReferNowClick: () -> Unit,
    onSwipeRedeem: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val cardShape = RoundedCornerShape(18.dp)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .shadow(
                elevation = 18.dp,
                shape = cardShape,
                ambientColor = Purple700.copy(alpha = 0.28f),
                spotColor = Purple950.copy(alpha = 0.18f),
            ),
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = Purple850),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Purple850)
                .border(1.dp, White.copy(alpha = 0.08f), cardShape),
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(Res.string.refer_earn_screen_total_rewards_earned),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = White.copy(alpha = 0.8f),
                            letterSpacing = 1.sp,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = totalRewardsText,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = White,
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) { expanded = !expanded }
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = White,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                }

                AnimatedVisibility(visible = expanded) {
                    Column {
                        Spacer(modifier = Modifier.height(18.dp))

                        RewardMetricRow(
                            label = stringResource(Res.string.refer_earn_screen_gold_purchase_cashback),
                            value = goldPurchaseCashback,
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = stringResource(Res.string.refer_earn_screen_referral_cashback),
                                    color = White.copy(alpha = 0.9f),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .border(1.dp, White.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                        .clickable(onClick = onReferNowClick)
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = null,
                                            tint = White,
                                            modifier = Modifier.size(10.dp),
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = stringResource(Res.string.refer_earn_screen_refer_now),
                                            color = White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = referralCashback,
                                color = White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(White.copy(alpha = 0.1f), RoundedCornerShape(14.dp))
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                        ) {
                            RewardStatColumn(
                                title = stringResource(Res.string.refer_earn_screen_redeemable),
                                value = redeemable,
                                valueColor = White,
                                modifier = Modifier.weight(1f),
                            )
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(40.dp)
                                    .background(White.copy(alpha = 0.2f)),
                            )
                            RewardStatColumn(
                                title = stringResource(Res.string.refer_earn_screen_redeemed),
                                value = redeemed,
                                valueColor = White.copy(alpha = 0.7f),
                                modifier = Modifier.weight(1f),
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        RewardsSwipeToRedeemTrack(
                            onSwipeComplete = onSwipeRedeem,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RewardMetricRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            color = White.copy(alpha = 0.9f),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
        )
        Text(
            text = value,
            color = White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun RewardStatColumn(
    title: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = title,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = White.copy(alpha = 0.8f),
            letterSpacing = 1.sp,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = valueColor,
        )
    }
}

@Composable
private fun MilestoneRewardsSection(
    rows: List<RewardsMilestoneRowUi>,
    lifetimeBooster: RewardsLifetimeBoosterRowUi,
    onBuyGoldClick: () -> Unit,
    onReferEarnDetailClick: () -> Unit,
) {
    if (rows.isEmpty()) return

    Text(
        text = stringResource(Res.string.refer_earn_screen_milestone_rewards_path),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = Slate400,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(bottom = 12.dp, start = 20.dp, end = 20.dp),
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        rows.forEachIndexed { index, row ->
            TimelineNode(
                row = row,
                isLast = index == rows.lastIndex,
                onBuyGoldClick = onBuyGoldClick,
            )
        }
        LifetimeBoosterTimelineNode(
            booster = lifetimeBooster,
            onUnlockBoosterClick = onReferEarnDetailClick,
        )
    }
}

@Composable
private fun TimelineNode(
    row: RewardsMilestoneRowUi,
    isLast: Boolean,
    onBuyGoldClick: () -> Unit,
) {
    val lineColor = if (row.state == RewardsLevelState.Completed) Purple600 else Slate200
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(32.dp)
                .fillMaxHeight(),
        ) {
            val iconBg = when (row.state) {
                RewardsLevelState.Completed, RewardsLevelState.Active -> Purple600
                RewardsLevelState.Locked -> Color(0xFFF8FAFC)
            }
            val iconTint = when (row.state) {
                RewardsLevelState.Completed, RewardsLevelState.Active -> White
                RewardsLevelState.Locked -> Slate400
            }
            val iconVector = when (row.state) {
                RewardsLevelState.Completed -> Icons.Default.Check
                RewardsLevelState.Active -> Icons.AutoMirrored.Filled.TrendingUp
                RewardsLevelState.Locked -> Icons.Default.Lock
            }

            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .size(28.dp)
                    .background(iconBg, CircleShape)
                    .border(2.dp, White, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(14.dp),
                )
            }

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .weight(1f)
                        .background(lineColor),
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Box(
            modifier = Modifier
                .padding(top = 4.dp, bottom = 20.dp)
                .weight(1f),
        ) {
            when (row.state) {
                RewardsLevelState.Completed -> CompletedMilestoneCard(row.data)
                RewardsLevelState.Active -> ActiveMilestoneCard(
                    data = row.data,
                    progressFraction = row.progressFraction,
                    totalPaidGoldGrams = row.totalPaidGoldGramsForUi,
                    onBuyGoldClick = onBuyGoldClick,
                )
                RewardsLevelState.Locked -> LockedMilestoneCard(row.data)
            }
        }
    }
}

@Composable
private fun CompletedMilestoneCard(
    data: RewardsMilestoneData,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, Slate100),
    ) {
        Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = "Reached ${data.targetGold}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = Slate800,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp),
                )
                Box(
                    modifier = Modifier
                        .background(Emerald50, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = "COMPLETED",
                        color = Emerald500,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = milestoneRewardCreditedDisplay(data.rewardAmount),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Purple600,
            )
        }
    }
}

@Composable
private fun ActiveMilestoneCard(
    data: RewardsMilestoneData,
    progressFraction: Float,
    totalPaidGoldGrams: Float?,
    onBuyGoldClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp), clip = false),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, Slate100),
    ) {
        Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = "When you reach ${data.targetGold}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = Slate800,
                    lineHeight = 24.sp,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp),
                )
                Text(
                    text = milestoneCashbackDisplay(data.rewardAmount),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = Purple600,
                    textAlign = TextAlign.End,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            val targetGrams = data.targetGold.replace("g", "", ignoreCase = true).replace("+", "").toFloatOrNull() ?: 0.5f
            val completedGrams = totalPaidGoldGrams ?: (targetGrams * progressFraction.coerceIn(0f, 1f))
            val remainingGrams = (targetGrams - completedGrams.coerceAtMost(targetGrams)).coerceAtLeast(0f)

            LinearProgressIndicator(
                progress = { progressFraction.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = Purple600,
                trackColor = Purple100,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${formatGramsQuantityForDisplay(completedGrams)}G OF ${data.targetGold.uppercase()} COMPLETED",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = Slate400,
                letterSpacing = 0.5.sp,
            )

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Slate100)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${formatGramsQuantityForDisplay(remainingGrams)}g more to unlock",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate500,
                    modifier = Modifier.weight(1f),
                )

                Spacer(modifier = Modifier.width(12.dp))

                Button(
                    onClick = onBuyGoldClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Purple600),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                    modifier = Modifier.height(40.dp),
                ) {
                    Text(
                        text = stringResource(Res.string.common_buy_now),
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        letterSpacing = 0.5.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun LockedMilestoneCard(
    data: RewardsMilestoneData,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(White, RoundedCornerShape(16.dp))
            .drawBehind {
                drawRoundRect(
                    color = Slate200,
                    style = Stroke(
                        width = 4f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f),
                    ),
                    cornerRadius = CornerRadius(16.dp.toPx()),
                )
            }
            .padding(horizontal = 18.dp, vertical = 18.dp),
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = "When you reach ${data.targetGold}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = Slate400,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp),
                )
                Text(
                    text = milestoneCashbackDisplay(data.rewardAmount),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Purple600.copy(alpha = 0.75f),
                    textAlign = TextAlign.End,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (data.level <= 1) {
                    "Start buying gold to unlock this milestone."
                } else {
                    "Complete the previous milestone to unlock."
                },
                fontSize = 13.sp,
                color = Slate400,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun LifetimeBoosterTimelineNode(
    booster: RewardsLifetimeBoosterRowUi,
    onUnlockBoosterClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(32.dp)
                .fillMaxHeight(),
        ) {
            val iconBg = when (booster.state) {
                RewardsLevelState.Completed, RewardsLevelState.Active -> Purple600
                RewardsLevelState.Locked -> Color(0xFFF8FAFC)
            }
            val iconTint = when (booster.state) {
                RewardsLevelState.Completed, RewardsLevelState.Active -> White
                RewardsLevelState.Locked -> Slate400
            }
            val iconVector = when (booster.state) {
                RewardsLevelState.Completed -> Icons.Default.Check
                RewardsLevelState.Active -> Icons.Default.Lightbulb
                RewardsLevelState.Locked -> Icons.Default.Lock
            }
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .size(28.dp)
                    .background(iconBg, CircleShape)
                    .border(2.dp, White, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(14.dp),
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Box(
            modifier = Modifier
                .padding(top = 4.dp, bottom = 20.dp)
                .weight(1f),
        ) {
            GrandRewardCard(
                state = booster.state,
                boosterRateLabel = booster.boosterRateLabel,
                onUnlockBoosterClick = onUnlockBoosterClick,
            )
        }
    }
}

@Composable
private fun GrandRewardCard(
    state: RewardsLevelState,
    boosterRateLabel: String,
    onUnlockBoosterClick: () -> Unit,
) {
    val isLocked = state == RewardsLevelState.Locked
    val boosterLive = state == RewardsLevelState.Completed
    val showUnlockBooster = state == RewardsLevelState.Active

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { alpha = if (isLocked) 0.6f else 1f }
            .shadow(
                elevation = when {
                    isLocked -> 0.dp
                    boosterLive -> 2.dp
                    else -> 4.dp
                },
                shape = RoundedCornerShape(16.dp),
                clip = false,
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, if (boosterLive || showUnlockBooster) Purple200 else Slate100),
    ) {
        Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if (boosterLive) "Booster is active" else "Booster is not active",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isLocked) Slate400 else Slate800,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp),
                )
                if (!boosterLive) {
                    Text(
                        text = milestoneCashbackDisplay(boosterRateLabel),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isLocked) Purple600.copy(alpha = 0.55f) else Purple600,
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Slate100)
            Spacer(modifier = Modifier.height(12.dp))

            when {
                boosterLive -> {
                    Text(
                        text = "Earning ${boosterRateLabel.trim().ifBlank { "0.5%" }} cashback on every gold purchase.",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Emerald600,
                        lineHeight = 18.sp,
                    )
                }

                showUnlockBooster -> {
                    Text(
                        text = stringResource(Res.string.refer_earn_screen_milestones_done_tap_below_to_turn_on_your),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Slate500,
                        lineHeight = 18.sp,
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = onUnlockBoosterClick,
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Purple600),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text(
                            text = stringResource(Res.string.refer_earn_screen_unlock_booster),
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            letterSpacing = 0.5.sp,
                        )
                    }
                }

                else -> {
                    Text(
                        text = stringResource(Res.string.refer_earn_screen_extra_cashback_after_you_finish_all_milestones),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Slate400,
                    )
                }
            }
        }
    }
}

private fun milestoneCashbackDisplay(rewardAmount: String): String {
    val trimmed = rewardAmount.trim()
    if (trimmed.isEmpty() || trimmed == "—") return "—"
    return if (trimmed.contains("%")) trimmed else "Get $trimmed"
}

private fun milestoneRewardCreditedDisplay(rewardAmount: String): String {
    val trimmed = rewardAmount.trim()
    if (trimmed.isEmpty() || trimmed == "—") return "—"
    return if (trimmed.contains("%")) trimmed else "Credited $trimmed"
}
