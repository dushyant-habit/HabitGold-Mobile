package com.habit.gold.feature.alerts.presentation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.alerts_screen_empty_message
import habitgoldmobile.composeapp.generated.resources.alerts_screen_empty_title
import habitgoldmobile.composeapp.generated.resources.alerts_screen_title
import habitgoldmobile.composeapp.generated.resources.common_days_ago
import habitgoldmobile.composeapp.generated.resources.common_hours_ago
import habitgoldmobile.composeapp.generated.resources.common_just_now
import habitgoldmobile.composeapp.generated.resources.common_minutes_ago
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource

private val AlertsBackground = Color(0xFFF8FAFC)
private val AlertsCardBorder = Color(0xFFE2E8F0)
private val AlertsMuted = Color(0xFF64748B)
private val AlertsDot = Color(0xFFEF4444)
private val AlertsIconBackground = Color(0xFFF3F4F6)
private val AlertsTopBarColor = Color.White

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(
    state: AlertsState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AlertsBackground),
    ) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = stringResource(Res.string.alerts_screen_title),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black,
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = AlertsTopBarColor,
            ),
        )
        HorizontalDivider(color = AlertsCardBorder)

        when {
            state.isLoading -> AlertsLoadingState()
            state.alerts.isEmpty() -> AlertsEmptyState()
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(state.alerts, key = { it.id }) { alert ->
                    AlertCard(alert = alert)
                }
            }
        }
    }
}

@Composable
private fun AlertsEmptyState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(AlertsIconBackground),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = Color(0xFF111827),
                    modifier = Modifier.size(24.dp),
                )
            }
            Text(
                text = stringResource(Res.string.alerts_screen_empty_title),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
            )
            Text(
                text = stringResource(Res.string.alerts_screen_empty_message),
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = AlertsMuted,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun AlertCard(
    alert: AlertsUiModel,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, AlertsCardBorder),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(AlertsIconBackground),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = Color(0xFF111827),
                    modifier = Modifier.size(18.dp),
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = alert.title,
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = Color.Black,
                    )
                    if (!alert.isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(AlertsDot),
                        )
                    }
                }
                Text(
                    text = alert.description,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = AlertsMuted,
                )
                val relativeTime = alert.createdAt.relativeTimeLabel()
                if (relativeTime.isNotBlank()) {
                    Text(
                        text = relativeTime,
                        fontSize = 12.sp,
                        color = AlertsMuted,
                    )
                }
            }
        }
    }
}

@Composable
private fun AlertsLoadingState() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(5) {
            AlertsShimmerCard()
        }
    }
}

@Composable
private fun AlertsShimmerCard() {
    val transition = rememberInfiniteTransition(label = "alertsShimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "alertsShimmerAlpha",
    )
    val interactionSource = remember { MutableInteractionSource() }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                enabled = false,
                interactionSource = interactionSource,
                indication = null,
                onClick = {},
        ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, AlertsCardBorder),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .alpha(alpha)
                    .clip(CircleShape)
                    .background(Color(0xFFE5E7EB)),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.55f)
                        .height(14.dp)
                        .alpha(alpha)
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color(0xFFE5E7EB)),
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .alpha(alpha)
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color(0xFFE5E7EB)),
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(12.dp)
                        .alpha(alpha)
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color(0xFFE5E7EB)),
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.25f)
                        .height(10.dp)
                        .alpha(alpha)
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color(0xFFE5E7EB)),
                )
            }
        }
    }
}

@Composable
private fun String.relativeTimeLabel(): String {
    val instant = runCatching { Instant.parse(this) }.getOrNull() ?: return ""
    val nowMillis = Clock.System.now().toEpochMilliseconds()
    val durationMillis = (nowMillis - instant.toEpochMilliseconds()).coerceAtLeast(0L)
    val minutes = durationMillis / 60_000L
    val hours = durationMillis / 3_600_000L
    val days = durationMillis / 86_400_000L
    return when {
        minutes < 1 -> stringResource(Res.string.common_just_now)
        minutes < 60 -> stringResource(Res.string.common_minutes_ago, minutes.toString())
        hours < 24 -> stringResource(Res.string.common_hours_ago, hours.toString())
        days < 7 -> stringResource(Res.string.common_days_ago, days.toString())
        else -> {
            val local = instant.toLocalDateTime(TimeZone.currentSystemDefault())
            val month = local.month.name.lowercase().replaceFirstChar { it.titlecase() }.take(3)
            "${local.day} $month"
        }
    }
}
