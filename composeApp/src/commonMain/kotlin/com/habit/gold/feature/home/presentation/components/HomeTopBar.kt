package com.habit.gold.feature.home.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habit.gold.core.designsystem.HabitGoldPalette
import com.habit.gold.feature.auth.domain.AuthenticatedUser
import com.habit.gold.feature.home.presentation.formatLiveRate
import com.habit.gold.feature.home.presentation.homeDisplayName
import com.habit.gold.feature.home.presentation.homeInitials
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.common_alerts
import habitgoldmobile.composeapp.generated.resources.home_screen_hello
import habitgoldmobile.composeapp.generated.resources.home_screen_live_gold_price
import org.jetbrains.compose.resources.stringResource

private val HomeToolbarBorder = Color(0x0D000000)
private val HomeAvatarBackground = HabitGoldPalette.plum.copy(alpha = 0.10f)
private val HomeToolbarSubtle = Color(0xFF6B7280)
private val HomeAlertsBackground = Color(0xFFF1F5F9)
private val HomeLivePillBackground = Color(0xFFFAF5FF)
private val HomeToolbarIconSize = 32.dp

@Composable
internal fun HomeTopBar(
    user: AuthenticatedUser?,
    liveRate: Double,
    onProfileClick: () -> Unit,
    onAlertsClick: () -> Unit,
    onOpenGoldPrice: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .statusBarsPadding()
            .drawBehind {
                val strokeWidth = 1.dp.toPx()
                drawLine(
                    color = HomeToolbarBorder,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = strokeWidth,
                )
            }
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onProfileClick,
            ),
        ) {
            Box(
                modifier = Modifier
                    .size(HomeToolbarIconSize)
                    .clip(CircleShape)
                    .background(HomeAvatarBackground),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = (user?.name ?: "").homeInitials(),
                    color = HomePrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = stringResource(Res.string.home_screen_hello),
                    fontSize = 11.sp,
                    lineHeight = 12.sp,
                    color = HomeToolbarSubtle,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = (user?.name ?: "").homeDisplayName(),
                    fontSize = 14.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (liveRate > 0) {
                Surface(
                    onClick = onOpenGoldPrice,
                    shape = RoundedCornerShape(24.dp),
                    color = HomeLivePillBackground,
                    border = BorderStroke(1.dp, HomePrimary.copy(alpha = 0.15f)),
                    modifier = Modifier.padding(end = 10.dp),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        HomeLiveWaveIndicator(
                            tint = HomePrimary,
                            modifier = Modifier.size(10.dp),
                        )
                        Column(
                            verticalArrangement = Arrangement.spacedBy(1.dp),
                        ) {
                            Text(
                                text = stringResource(Res.string.home_screen_live_gold_price),
                                fontSize = 7.sp,
                                lineHeight = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = HomePrimary.copy(alpha = 0.85f),
                            )
                            Text(
                                text = formatLiveRate(liveRate),
                                fontSize = 9.sp,
                                lineHeight = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = HomePrimary,
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .size(HomeToolbarIconSize)
                    .clip(CircleShape)
                    .background(HomeAlertsBackground)
                    .clickable(onClick = onAlertsClick),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = stringResource(Res.string.common_alerts),
                    tint = Color.Black.copy(alpha = 0.6f),
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

@Composable
private fun HomeLiveWaveIndicator(
    tint: Color,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "homeLiveWave")
    val alphaA by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(600, easing = LinearEasing)),
        label = "homeLiveWaveA",
    )
    val alphaB by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.35f,
        animationSpec = infiniteRepeatable(animation = tween(600, easing = LinearEasing)),
        label = "homeLiveWaveB",
    )

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Box(
            modifier = Modifier
                .size(width = 2.dp, height = 6.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(tint.copy(alpha = alphaA)),
        )
        Box(
            modifier = Modifier
                .size(width = 2.dp, height = 10.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(tint.copy(alpha = alphaB)),
        )
        Box(
            modifier = Modifier
                .size(width = 2.dp, height = 7.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(tint.copy(alpha = alphaA)),
        )
    }
}
