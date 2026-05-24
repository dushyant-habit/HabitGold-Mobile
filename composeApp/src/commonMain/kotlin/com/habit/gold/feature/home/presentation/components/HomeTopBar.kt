package com.habit.gold.feature.home.presentation.components

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
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
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

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
    hasUnreadAlerts: Boolean,
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
            Surface(
                onClick = onOpenGoldPrice,
                shape = RoundedCornerShape(24.dp),
                color = HomeLivePillBackground,
                border = BorderStroke(1.dp, HomePrimary.copy(alpha = 0.15f)),
                modifier = Modifier
                    .padding(end = 10.dp)
                    .alpha(if (liveRate > 0) 1f else 0f),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    HomeLiveWaveIndicator(
                        tint = HomePrimary,
                        modifier = Modifier.size(12.dp),
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(1.dp),
                    ) {
                        Text(
                            text = stringResource(Res.string.home_screen_live_gold_price),
                            fontSize = 8.sp,
                            lineHeight = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = HomePrimary.copy(alpha = 0.85f),
                        )
                        Text(
                            text = if (liveRate > 0) formatLiveRate(liveRate) else " ",
                            fontSize = 10.sp,
                            lineHeight = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = HomePrimary,
                        )
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
                if (hasUnreadAlerts) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 7.dp, end = 7.dp)
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEF4444)),
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeLiveWaveIndicator(
    tint: Color,
    modifier: Modifier = Modifier.size(18.dp),
) {
    val transition = rememberInfiniteTransition(label = "homeLiveWave")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(2000), repeatMode = androidx.compose.animation.core.RepeatMode.Restart),
        label = "homeLiveWavePhase",
    )

    fun smoothPulse(progress: Float, start: Float, end: Float): Float {
        val t = ((progress - start) / (end - start)).coerceIn(0f, 1f)
        return (0.5f - 0.5f * cos(t * PI)).toFloat()
    }

    val innerAlpha = smoothPulse(phase, 0.05f, 0.45f)
    val outerAlpha = smoothPulse(phase, 0.30f, 0.85f)
    val dotScale = 0.95f + 0.1f * sin(phase * (2 * PI)).toFloat()

    Canvas(modifier = modifier) {
        val center = this.center
        val dotRadius = 2.2.dp.toPx()
        val innerRadius = 5.2.dp.toPx()
        val outerRadius = 8.0.dp.toPx()
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round)

        drawCircle(color = tint, radius = dotRadius * dotScale, center = center)

        if (innerAlpha > 0f) {
            drawArc(
                color = tint.copy(alpha = innerAlpha),
                startAngle = 135f,
                sweepAngle = 90f,
                useCenter = false,
                topLeft = Offset(center.x - innerRadius, center.y - innerRadius),
                size = Size(innerRadius * 2, innerRadius * 2),
                style = stroke,
            )
            drawArc(
                color = tint.copy(alpha = innerAlpha),
                startAngle = -45f,
                sweepAngle = 90f,
                useCenter = false,
                topLeft = Offset(center.x - innerRadius, center.y - innerRadius),
                size = Size(innerRadius * 2, innerRadius * 2),
                style = stroke,
            )
        }

        if (outerAlpha > 0f) {
            drawArc(
                color = tint.copy(alpha = outerAlpha),
                startAngle = 135f,
                sweepAngle = 90f,
                useCenter = false,
                topLeft = Offset(center.x - outerRadius, center.y - outerRadius),
                size = Size(outerRadius * 2, outerRadius * 2),
                style = stroke,
            )
            drawArc(
                color = tint.copy(alpha = outerAlpha),
                startAngle = -45f,
                sweepAngle = 90f,
                useCenter = false,
                topLeft = Offset(center.x - outerRadius, center.y - outerRadius),
                size = Size(outerRadius * 2, outerRadius * 2),
                style = stroke,
            )
        }
    }
}
