package com.habit.gold.app

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habit.gold.core.designsystem.HabitGoldDesignSystem
import com.habit.gold.core.designsystem.HabitGoldPalette
import com.habit.gold.core.localization.appStrings
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.ic_habit_gold_white_transparent_bg
import org.jetbrains.compose.resources.painterResource

@Composable
fun AppSplashScreen(
    modifier: Modifier = Modifier,
) {
    val strings = appStrings

    Surface(
        modifier = modifier.fillMaxSize(),
        color = HabitGoldPalette.splashBackground,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(HabitGoldPalette.splashBackground)
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(HabitGoldPalette.white.copy(alpha = 0.04f), HabitGoldPalette.transparent),
                            center = Offset(0f, 0f),
                            radius = size.width * 0.8f,
                        ),
                        center = Offset(0f, 0f),
                        radius = size.width * 0.8f,
                    )

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(HabitGoldPalette.gold.copy(alpha = 0.06f), HabitGoldPalette.transparent),
                            center = Offset(size.width, size.height),
                            radius = size.width * 0.9f,
                        ),
                        center = Offset(size.width, size.height),
                        radius = size.width * 0.9f,
                    )
                },
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = HabitGoldDesignSystem.spacing.xxl),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(Res.drawable.ic_habit_gold_white_transparent_bg),
                    contentDescription = null,
                    modifier = Modifier.size(200.dp),
                )

                Box(
                    modifier = Modifier
                        .width(72.dp)
                        .height(4.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    HabitGoldPalette.transparent,
                                    HabitGoldPalette.gold.copy(alpha = 0.6f),
                                    HabitGoldPalette.white.copy(alpha = 0.95f),
                                    HabitGoldPalette.gold.copy(alpha = 0.6f),
                                    HabitGoldPalette.transparent,
                                ),
                            ),
                            shape = RoundedCornerShape(50),
                        ),
                )

                Text(
                    text = strings.splashTagline,
                    modifier = Modifier.padding(top = HabitGoldDesignSystem.spacing.xxl),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.5.sp,
                    ),
                    color = HabitGoldPalette.white.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center,
                )
            }

            SplashLoadingBadge(
                message = strings.splashLoadingMessage,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 60.dp),
            )
        }
    }
}

@Composable
private fun SplashLoadingBadge(
    message: String,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "splashLoading")
    val liftPx = with(LocalDensity.current) { 5.dp.toPx() }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        HabitGoldPalette.white.copy(alpha = 0.10f),
                        HabitGoldPalette.gold.copy(alpha = 0.12f),
                        HabitGoldPalette.white.copy(alpha = 0.08f),
                    ),
                ),
            )
            .border(
                width = 1.dp,
                color = HabitGoldPalette.white.copy(alpha = 0.14f),
                shape = RoundedCornerShape(24.dp),
            )
            .padding(horizontal = 18.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(3) { index ->
                val offset by transition.animateFloat(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(
                            durationMillis = 420,
                            delayMillis = index * 110,
                            easing = FastOutSlowInEasing,
                        ),
                        repeatMode = RepeatMode.Reverse,
                    ),
                    label = "splashDot$index",
                )

                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .graphicsLayer {
                            translationY = -liftPx * offset
                            alpha = 0.45f + (0.55f * offset)
                        }
                        .clip(CircleShape)
                        .background(
                            lerp(
                                start = HabitGoldPalette.white.copy(alpha = 0.55f),
                                stop = HabitGoldPalette.gold,
                                fraction = offset,
                            ),
                        ),
                )
            }
        }

        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.2.sp,
            ),
            color = HabitGoldPalette.white.copy(alpha = 0.92f),
        )
    }
}
