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
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconButtonDefaults.filledTonalIconButtonColors
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
                            text = "WIN ASSURED CASHBACK UPTO",
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
                            text = "For every friend you refer",
                            color = White.copy(alpha = 0.85f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 20.sp,
                        )
                    }

                    IconButton(
                        onClick = onShareClick,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = White.copy(alpha = 0.22f),
                            contentColor = White,
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(White.copy(alpha = 0.22f)),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                        )
                    }
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
                            text = "LIFETIME EARNINGS",
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
                                text = "ACTIVE FRIENDS",
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
                            text = if (ui.boosterIsActive) "BOOSTER ACTIVE" else "BOOSTER INACTIVE",
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
                            text = "REWARDS",
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
                            text = "DAYS LEFT",
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
                    text = "EXTEND YOUR BOOSTER",
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
            (((100_000f / (cashbackFraction * 100f)) * 2f).coerceAtLeast(10_000f))
        } else {
            10_000f
        }
    }
    val defaultPurchaseAmount = remember(cashbackFraction, maxPurchaseAmount) {
        if (cashbackFraction > 0f) {
            ((100_000f / (cashbackFraction * 100f)).coerceAtLeast(10_000f).coerceAtMost(maxPurchaseAmount))
        } else {
            10_000f
        }
    }
    var annualSpend by rememberSaveable(defaultPurchaseAmount) { mutableFloatStateOf(defaultPurchaseAmount) }
    var friendsReferred by rememberSaveable { mutableFloatStateOf(100f) }
    val referralEarnings = ((annualSpend * cashbackFraction * friendsReferred.toInt()).toInt()).coerceAtMost(100_000)

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
                        text = "Estimate your earnings",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Slate800,
                        lineHeight = 22.sp,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Drag sliders to see potential rewards",
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
                        text = "YOU GET TOTAL",
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
                    text = "FRIEND'S PURCHASE",
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
                valueRange = 10_000f..maxPurchaseAmount,
                enabled = maxPurchaseAmount > 10_000f,
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
                    text = "NUMBER OF FRIENDS",
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
                        text = "${friendsReferred.toInt()} Friends",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Purple700,
                    )
                }
            }

            Slider(
                value = friendsReferred,
                onValueChange = { friendsReferred = it },
                valueRange = 1f..100f,
                steps = 98,
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
                text = "YOUR REFERRAL CODE",
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
                FilledTonalIconButton(
                    onClick = onCopyClick,
                    colors = filledTonalIconButtonColors(containerColor = Purple100, contentColor = Purple700),
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ContentCopy,
                        contentDescription = "Copy referral code",
                        modifier = Modifier.size(20.dp),
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
                Text("Try again", color = Purple700, fontWeight = FontWeight.SemiBold)
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
            Text("My QR", fontWeight = FontWeight.Bold, color = Slate800)
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
                    text = "Scan to share your referral code",
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
                    Text("Share", color = Slate800, fontWeight = FontWeight.SemiBold)
                }
                TextButton(onClick = onDismiss) {
                    Text("Close", color = Slate500, fontWeight = FontWeight.SemiBold)
                }
            }
        },
    )
}
