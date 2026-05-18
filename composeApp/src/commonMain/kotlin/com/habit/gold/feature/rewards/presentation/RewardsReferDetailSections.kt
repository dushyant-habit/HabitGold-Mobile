package com.habit.gold.feature.rewards.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habit.gold.feature.home.presentation.formatInr
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.common_close
import habitgoldmobile.composeapp.generated.resources.common_share
import habitgoldmobile.composeapp.generated.resources.common_try_again
import habitgoldmobile.composeapp.generated.resources.refer_earn_screen_active_friends
import habitgoldmobile.composeapp.generated.resources.refer_earn_screen_booster_active
import habitgoldmobile.composeapp.generated.resources.refer_earn_screen_booster_inactive
import habitgoldmobile.composeapp.generated.resources.refer_earn_screen_copy_referral_code
import habitgoldmobile.composeapp.generated.resources.refer_earn_screen_days_left
import habitgoldmobile.composeapp.generated.resources.refer_earn_screen_estimate_earnings
import habitgoldmobile.composeapp.generated.resources.refer_earn_screen_calculator_hint
import habitgoldmobile.composeapp.generated.resources.refer_earn_screen_extend_your_booster
import habitgoldmobile.composeapp.generated.resources.refer_earn_screen_friends_purchase
import habitgoldmobile.composeapp.generated.resources.refer_earn_screen_friends_count
import habitgoldmobile.composeapp.generated.resources.refer_earn_screen_lifetime_earnings
import habitgoldmobile.composeapp.generated.resources.refer_earn_screen_my_qr
import habitgoldmobile.composeapp.generated.resources.refer_earn_screen_number_of_friends
import habitgoldmobile.composeapp.generated.resources.refer_earn_screen_refer_and_earn_rewards_when_friends_join
import habitgoldmobile.composeapp.generated.resources.refer_earn_screen_rewards
import habitgoldmobile.composeapp.generated.resources.refer_earn_screen_scan_to_share_code
import habitgoldmobile.composeapp.generated.resources.refer_earn_screen_win_assured_cashback_upto
import habitgoldmobile.composeapp.generated.resources.refer_earn_screen_you_get_total
import habitgoldmobile.composeapp.generated.resources.refer_earn_screen_your_referral_code
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

private const val REFER_EARN_CALCULATOR_MIN_PURCHASE = 10_000f
private const val REFER_EARN_CALCULATOR_DEFAULT_EARNINGS = 100_000f
private const val REFER_EARN_CALCULATOR_MAX_PURCHASE_MULTIPLIER = 2f
private const val REFER_EARN_CALCULATOR_DEFAULT_FRIENDS = 50f
private const val REFER_EARN_CALCULATOR_MAX_FRIENDS = 50f

@Composable
internal fun RewardsDetailTopWinCard(
    lifetimeEarningsDisplay: String,
    activeFriendsCount: Int,
    onShareClick: () -> Unit,
) {
    val shape = RoundedCornerShape(20.dp)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Purple400Alt, Purple600, Purple875),
                    ),
                    shape = shape,
                )
                .padding(horizontal = 20.dp, vertical = 22.dp),
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    Column(modifier = Modifier.weight(1f, fill = false)) {
                        Text(
                            text = stringResource(Res.string.refer_earn_screen_win_assured_cashback_upto),
                            color = White.copy(alpha = 0.92f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.1.sp,
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "₹1,00,000",
                            color = White,
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Black,
                            lineHeight = 42.sp,
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = stringResource(Res.string.refer_earn_screen_refer_and_earn_rewards_when_friends_join),
                            color = White.copy(alpha = 0.85f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 20.sp,
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    FilledIconShareButton(onClick = onShareClick)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(White.copy(alpha = 0.14f))
                        .border(1.dp, White.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(Res.string.refer_earn_screen_lifetime_earnings),
                            color = White.copy(alpha = 0.82f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.6.sp,
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = lifetimeEarningsDisplay,
                            color = White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(36.dp)
                            .background(White.copy(alpha = 0.25f)),
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp),
                        horizontalAlignment = Alignment.End,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Green400),
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(Res.string.refer_earn_screen_active_friends),
                                color = White.copy(alpha = 0.82f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.6.sp,
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = activeFriendsCount.toString(),
                            color = White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilledIconShareButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(White.copy(alpha = 0.22f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Default.Share,
            contentDescription = stringResource(Res.string.common_share),
            tint = White,
            modifier = Modifier.size(22.dp),
        )
    }
}

@Composable
internal fun RewardsBoosterCard(
    ui: RewardsReferDetailUi,
    onBuyNowClick: () -> Unit,
    onInviteClick: () -> Unit,
    onStartSipClick: () -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val daysLeft = ui.daysLeft
    val totalDaysCap = ui.totalDaysCap
    val progress = remember(ui.boosterIsActive, daysLeft, totalDaysCap) {
        if (!ui.boosterIsActive) return@remember 0f
        val cap = totalDaysCap?.takeIf { it > 0 } ?: return@remember 0f
        val left = daysLeft ?: return@remember 0f
        ((cap - left).coerceIn(0, cap)).toFloat() / cap.toFloat()
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate100),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (ui.boosterIsActive) Emerald500 else Slate400),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (ui.boosterIsActive) {
                                stringResource(Res.string.refer_earn_screen_booster_active)
                            } else {
                                stringResource(Res.string.refer_earn_screen_booster_inactive)
                            },
                            color = Slate800,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp,
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Slate500,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = ui.cashbackPercentLabel,
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Black,
                            color = Purple700,
                            lineHeight = 48.sp,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(Res.string.refer_earn_screen_rewards),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate400,
                            modifier = Modifier.padding(bottom = 8.dp),
                        )
                    }
                }

                Box(
                    modifier = Modifier.size(78.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                        drawArc(
                            color = Slate100,
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = 7.dp.toPx(), cap = StrokeCap.Round),
                        )
                        drawArc(
                            color = Purple700,
                            startAngle = -90f,
                            sweepAngle = 360f * progress,
                            useCenter = false,
                            style = Stroke(width = 7.dp.toPx(), cap = StrokeCap.Round),
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = ui.daysLeft?.toString() ?: "—",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Slate800,
                        )
                        Text(
                            text = stringResource(Res.string.refer_earn_screen_days_left),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate400,
                            letterSpacing = 0.3.sp,
                        )
                    }
                }
            }

            HorizontalDivider(color = Slate100, thickness = 1.dp)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { expanded = !expanded }
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.refer_earn_screen_extend_your_booster),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate400,
                    letterSpacing = 1.sp,
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Slate400,
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    RewardsExtensionCard(
                        title = ui.buyExtensionTitle,
                        subtitle = ui.buyExtensionSubtitle,
                        actionText = "BUY NOW",
                        isPrimary = false,
                        onClick = onBuyNowClick,
                    )
                    RewardsExtensionCard(
                        title = ui.referralExtensionTitle,
                        subtitle = ui.referralExtensionSubtitle,
                        actionText = "INVITE",
                        isPrimary = true,
                        onClick = onInviteClick,
                    )
                    RewardsExtensionCard(
                        title = ui.sipExtensionTitle,
                        subtitle = ui.sipExtensionSubtitle,
                        actionText = "START SIP",
                        isPrimary = false,
                        onClick = onStartSipClick,
                    )
                }
            }
        }
    }
}

@Composable
internal fun RewardsExtensionCard(
    title: String,
    subtitle: String,
    actionText: String,
    isPrimary: Boolean,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Purple100.copy(alpha = 0.35f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Purple100),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 10.dp),
            ) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate800,
                    lineHeight = 18.sp,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = Slate500,
                )
            }
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPrimary) Purple700 else Purple100,
                    contentColor = if (isPrimary) White else Purple700,
                ),
                shape = RoundedCornerShape(10.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                modifier = Modifier.height(38.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
            ) {
                Text(
                    text = actionText,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    letterSpacing = 0.3.sp,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RewardsEarningsCalculator(
    cashbackFraction: Float,
) {
    val maxPurchaseAmount = remember(cashbackFraction) {
        if (cashbackFraction > 0f) {
            (
                (
                    REFER_EARN_CALCULATOR_DEFAULT_EARNINGS /
                        (cashbackFraction * REFER_EARN_CALCULATOR_MAX_FRIENDS)
                    ) * REFER_EARN_CALCULATOR_MAX_PURCHASE_MULTIPLIER
                )
                    .coerceAtLeast(REFER_EARN_CALCULATOR_MIN_PURCHASE)
        } else {
            REFER_EARN_CALCULATOR_MIN_PURCHASE
        }
    }
    val defaultPurchaseAmount = remember(cashbackFraction, maxPurchaseAmount) {
        if (cashbackFraction > 0f) {
            (
                REFER_EARN_CALCULATOR_DEFAULT_EARNINGS /
                    (cashbackFraction * REFER_EARN_CALCULATOR_DEFAULT_FRIENDS)
                )
                .coerceAtLeast(REFER_EARN_CALCULATOR_MIN_PURCHASE)
                .coerceAtMost(maxPurchaseAmount)
        } else {
            REFER_EARN_CALCULATOR_MIN_PURCHASE
        }
    }
    var annualSpend by rememberSaveable(defaultPurchaseAmount) { mutableFloatStateOf(defaultPurchaseAmount) }
    var friendsReferred by rememberSaveable { mutableFloatStateOf(REFER_EARN_CALCULATOR_DEFAULT_FRIENDS) }
    val referralEarnings = (annualSpend * cashbackFraction * friendsReferred.toInt()).roundToInt()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate100),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(Res.string.refer_earn_screen_estimate_earnings),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Slate800,
                        lineHeight = 22.sp,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(Res.string.refer_earn_screen_calculator_hint),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Slate400,
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Purple100),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = null,
                        tint = Purple700,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Purple10)
                    .border(1.dp, Purple100, RoundedCornerShape(16.dp))
                    .padding(vertical = 20.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(Res.string.refer_earn_screen_you_get_total),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate400,
                        letterSpacing = 1.sp,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "₹${formatInr(referralEarnings.toDouble())}",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Purple700,
                        lineHeight = 38.sp,
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.refer_earn_screen_friends_purchase),
                    fontSize = 10.sp,
                    color = Slate400,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Purple100)
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                ) {
                    Text(
                        text = "₹${formatInr(annualSpend.toDouble())}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Purple700,
                    )
                }
            }

            Slider(
                value = annualSpend,
                onValueChange = { annualSpend = it.coerceAtMost(maxPurchaseAmount) },
                valueRange = REFER_EARN_CALCULATOR_MIN_PURCHASE..maxPurchaseAmount,
                enabled = maxPurchaseAmount > REFER_EARN_CALCULATOR_MIN_PURCHASE,
                colors = SliderDefaults.colors(
                    thumbColor = White,
                    activeTrackColor = Purple700,
                    inactiveTrackColor = Slate100,
                ),
                thumb = {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(Purple700, CircleShape)
                            .border(3.dp, White, CircleShape),
                    )
                },
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.refer_earn_screen_number_of_friends),
                    fontSize = 10.sp,
                    color = Slate400,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Purple100)
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                ) {
                    Text(
                        text = stringResource(
                            Res.string.refer_earn_screen_friends_count,
                            friendsReferred.toInt(),
                        ),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Purple700,
                    )
                }
            }

            Slider(
                value = friendsReferred,
                onValueChange = { friendsReferred = it.roundToInt().toFloat() },
                valueRange = 1f..REFER_EARN_CALCULATOR_MAX_FRIENDS,
                colors = SliderDefaults.colors(
                    thumbColor = White,
                    activeTrackColor = Purple700,
                    inactiveTrackColor = Slate100,
                ),
                thumb = {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(White, CircleShape)
                            .border(3.dp, Purple700, CircleShape),
                    )
                },
            )
        }
    }
}

@Composable
internal fun RewardsReferralCodeCard(
    code: String,
    onCopyClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Purple10, Purple100.copy(alpha = 0.5f)),
                ),
            )
            .border(1.dp, Purple100, RoundedCornerShape(20.dp))
            .padding(horizontal = 20.dp, vertical = 22.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(Res.string.refer_earn_screen_your_referral_code),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Slate400,
                letterSpacing = 1.sp,
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = code,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = Slate800,
                    letterSpacing = 3.sp,
                )
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Purple100)
                        .clickable(onClick = onCopyClick),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ContentCopy,
                        contentDescription = stringResource(Res.string.refer_earn_screen_copy_referral_code),
                        modifier = Modifier.size(20.dp),
                        tint = Purple700,
                    )
                }
            }
        }
    }
}

@Composable
internal fun RewardsInlineRetryCard(
    message: String,
    onRetry: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate100),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = message,
                color = Slate500,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(10.dp))
            TextButton(onClick = onRetry) {
                Text(
                    text = stringResource(Res.string.common_try_again),
                    color = Purple700,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
internal fun RewardsReferralQrDialog(
    referralCode: String,
    onShare: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = White,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                text = stringResource(Res.string.refer_earn_screen_my_qr),
                fontWeight = FontWeight.Bold,
                color = Slate800,
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .size(232.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(White)
                        .border(1.dp, Slate100, RoundedCornerShape(20.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    RewardsQrCodePreview(
                        referralCode = referralCode,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(Res.string.refer_earn_screen_scan_to_share_code),
                    color = Slate500,
                    fontSize = 13.sp,
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = referralCode,
                    color = Slate800,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                )
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(
                    onClick = onShare,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = White, contentColor = Slate800),
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Slate500,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(Res.string.common_share),
                        color = Slate800,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                TextButton(onClick = onDismiss) {
                    Text(
                        text = stringResource(Res.string.common_close),
                        color = Slate500,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        },
    )
}
