package com.habit.gold.feature.home.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habit.gold.feature.home.domain.model.HomeDashboardSummary
import com.habit.gold.feature.home.domain.model.HomeForceUpdate
import com.habit.gold.feature.home.presentation.formatGoldBalance
import com.habit.gold.feature.home.presentation.formatInr
import com.habit.gold.feature.home.presentation.formatProfitLabel
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.common_current_value
import habitgoldmobile.composeapp.generated.resources.common_gold_unit_short
import habitgoldmobile.composeapp.generated.resources.common_invested_value
import habitgoldmobile.composeapp.generated.resources.common_toggle_expansion
import habitgoldmobile.composeapp.generated.resources.common_toggle_visibility
import habitgoldmobile.composeapp.generated.resources.common_view_details
import habitgoldmobile.composeapp.generated.resources.home_screen_force_update_fallback_title
import habitgoldmobile.composeapp.generated.resources.home_screen_loading_summary
import habitgoldmobile.composeapp.generated.resources.home_screen_retry_cta
import habitgoldmobile.composeapp.generated.resources.home_screen_save_once
import habitgoldmobile.composeapp.generated.resources.home_screen_start_your_gold_journey
import habitgoldmobile.composeapp.generated.resources.home_screen_total_gold_balance
import habitgoldmobile.composeapp.generated.resources.home_screen_update_now_cta
import habitgoldmobile.composeapp.generated.resources.sell_gold_screen_sell_gold
import kotlin.math.abs
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun HomeLoadingCard() {
    val shimmerBase = Color(0xFFE8ECF3)
    val shimmerHighlight = Color(0xFFF6F8FB)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        HomeLoadingSurface(shape = RoundedCornerShape(18.dp)) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = stringResource(Res.string.home_screen_loading_summary),
                    color = Color.Transparent,
                    fontSize = 1.sp,
                    modifier = Modifier.height(0.dp),
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.58f)
                        .height(18.dp)
                        .homeShimmerPlaceholder(RoundedCornerShape(999.dp), shimmerBase, shimmerHighlight),
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.42f)
                        .height(34.dp)
                        .homeShimmerPlaceholder(RoundedCornerShape(999.dp), shimmerBase, shimmerHighlight),
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .homeShimmerPlaceholder(RoundedCornerShape(16.dp), shimmerBase, shimmerHighlight),
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            repeat(2) {
                HomeLoadingSurface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(84.dp)
                            .padding(14.dp)
                            .homeShimmerPlaceholder(RoundedCornerShape(12.dp), shimmerBase, shimmerHighlight),
                    )
                }
            }
        }

        repeat(2) {
            HomeLoadingSurface(shape = RoundedCornerShape(20.dp)) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(if (it == 0) 0.4f else 0.34f)
                            .height(16.dp)
                            .homeShimmerPlaceholder(RoundedCornerShape(999.dp), shimmerBase, shimmerHighlight),
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(if (it == 0) 56.dp else 108.dp)
                            .homeShimmerPlaceholder(RoundedCornerShape(if (it == 0) 14.dp else 16.dp), shimmerBase, shimmerHighlight),
                    )
                    if (it == 0) {
                        repeat(2) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .homeShimmerPlaceholder(RoundedCornerShape(14.dp), shimmerBase, shimmerHighlight),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeLoadingSurface(
    modifier: Modifier = Modifier,
    shape: Shape,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier,
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = HomeWhite),
        border = BorderStroke(1.dp, Slate200),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(content = content)
    }
}

@Composable
internal fun HomeErrorCard(
    errorMessage: String,
    onRetry: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = HomeWhite),
        border = BorderStroke(1.dp, Slate200),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = errorMessage,
                fontSize = 14.sp,
                color = Slate700,
                lineHeight = 20.sp,
            )
            Button(
                onClick = onRetry,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = HomePrimary),
            ) {
                Text(text = stringResource(Res.string.home_screen_retry_cta), fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            }
        }
    }
}

@Composable
internal fun HomeForceUpdateCard(update: HomeForceUpdate) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GoldSurface),
        border = BorderStroke(1.dp, Gold500.copy(alpha = 0.22f)),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = update.title ?: stringResource(Res.string.home_screen_force_update_fallback_title),
                fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold,
                fontSize = 14.sp,
                color = Slate900,
            )
            update.message?.let {
                Text(text = it, fontSize = 12.sp, lineHeight = 18.sp, color = Slate600)
            }
            Text(
                text = update.ctaText?.ifBlank { null } ?: stringResource(Res.string.home_screen_update_now_cta),
                fontSize = 12.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = Gold650,
            )
        }
    }
}

@Composable
internal fun HomeZeroBalanceSection(onStartJourneyClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onStartJourneyClick)
            .background(HomePrimary)
            .border(1.dp, HomePrimaryLight, RoundedCornerShape(18.dp))
            .height(84.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .height(84.dp)
                .width(74.dp),
            contentAlignment = Alignment.Center,
        ) {
            HomeStartJourneyGoldTower(modifier = Modifier.size(56.dp))
        }

        Row(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp, top = 14.dp, end = 16.dp, bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(Res.string.home_screen_start_your_gold_journey),
                fontSize = 20.sp,
                lineHeight = 16.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold,
                color = HomeWhite,
                maxLines = 2,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = HomeWhite,
                modifier = Modifier.size(30.dp),
            )
        }
    }
}

@Composable
internal fun HomeBalanceCard(
    dashboard: HomeDashboardSummary,
    dashboardError: String?,
    isBalanceVisible: Boolean,
    isBalanceExpanded: Boolean,
    onToggleBalanceVisible: () -> Unit,
    onToggleBalanceExpanded: () -> Unit,
    onViewDetailsClick: () -> Unit,
    onBuyGoldClick: () -> Unit,
    onSellGoldClick: () -> Unit,
) {
    val currentValue = dashboard.currentValue
    val investedValue = dashboard.investedValue
    val profitValue = currentValue - investedValue
    val shouldShowCurrentValue = currentValue > 0.0 && currentValue >= investedValue
    val shouldShowCurrentValueIcon = currentValue > investedValue
    val currentValueColor = if (currentValue > investedValue) GreenBright else HomeWhite
    val profitColor = if (profitValue > 0) GreenBright else HomeWhite
    val profitPercent = if (investedValue > 0) (profitValue / investedValue) * 100 else 0.0
    val profitDirectionIcon = if (profitValue < 0.0) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = HomePrimary),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccountBalance,
                        contentDescription = null,
                        tint = HomeWhite.copy(alpha = 0.9f),
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(Res.string.home_screen_total_gold_balance),
                        fontSize = 13.sp,
                        color = HomeWhite.copy(alpha = 0.9f),
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isBalanceVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = stringResource(Res.string.common_toggle_visibility),
                        tint = HomeWhite.copy(alpha = 0.7f),
                        modifier = Modifier
                            .size(20.dp)
                            .clickable(onClick = onToggleBalanceVisible),
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                        imageVector = if (isBalanceExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = stringResource(Res.string.common_toggle_expansion),
                        tint = HomeWhite.copy(alpha = 0.9f),
                        modifier = Modifier
                            .size(24.dp)
                            .clickable(onClick = onToggleBalanceExpanded),
                    )
                }
            }

            if (!dashboardError.isNullOrBlank()) {
                Text(
                    text = dashboardError,
                    fontSize = 11.sp,
                    color = HomeWhite.copy(alpha = 0.82f),
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggleBalanceExpanded),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = if (isBalanceVisible) formatGoldBalance(dashboard.totalGoldBalanceGrams) else "****",
                        fontSize = 32.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = HomeWhite,
                    )
                    Text(
                        text = stringResource(Res.string.common_gold_unit_short),
                        fontSize = 18.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                        color = HomeWhite.copy(alpha = 0.8f),
                        modifier = Modifier.padding(bottom = 6.dp, start = 4.dp),
                    )
                    GoldCoinCluster(
                        modifier = Modifier
                            .padding(start = 10.dp, bottom = 4.dp)
                            .size(width = 34.dp, height = 22.dp),
                    )
                }

                if (!isBalanceExpanded && profitValue > 0.0) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = HomeWhite.copy(alpha = 0.15f),
                    ) {
                        Text(
                            text = if (isBalanceVisible) "+${formatPercentage(profitPercent)}%" else "****",
                            color = profitColor,
                            fontSize = 13.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = isBalanceExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut(),
            ) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = HomeWhite.copy(alpha = 0.1f), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column(modifier = if (shouldShowCurrentValue) Modifier.weight(1f) else Modifier) {
                            Text(
                                text = stringResource(Res.string.common_invested_value),
                                fontSize = 11.sp,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                color = HomeWhite.copy(alpha = 0.7f),
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isBalanceVisible) "₹${formatInr(investedValue)}" else "****",
                                fontSize = 18.sp,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                color = HomeWhite,
                            )
                        }

                        if (shouldShowCurrentValue) {
                            Box(
                                modifier = Modifier
                                    .height(40.dp)
                                    .width(1.dp)
                                    .background(HomeWhite.copy(alpha = 0.2f)),
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(Res.string.common_current_value),
                                    fontSize = 11.sp,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                    color = HomeWhite.copy(alpha = 0.7f),
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (isBalanceVisible) "₹${formatInr(currentValue)}" else "****",
                                        fontSize = 18.sp,
                                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                        color = currentValueColor,
                                    )
                                    if (shouldShowCurrentValueIcon) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = profitDirectionIcon,
                                            contentDescription = null,
                                            tint = currentValueColor,
                                            modifier = Modifier.size(16.dp),
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (profitValue > 0.0) {
                            Surface(
                                shape = RoundedCornerShape(24.dp),
                                color = HomeWhite.copy(alpha = 0.15f),
                            ) {
                                Text(
                                    text = if (isBalanceVisible) formatProfitLabel(profitValue) else "****",
                                    fontSize = 13.sp,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                    color = profitColor,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable(onClick = onViewDetailsClick),
                        ) {
                            Text(
                                text = stringResource(Res.string.common_view_details),
                                fontSize = 13.sp,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                color = HomeWhite.copy(alpha = 0.9f),
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = HomeWhite.copy(alpha = 0.9f),
                                modifier = Modifier.size(16.dp),
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Button(
                    onClick = onSellGoldClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = HomeWhite,
                    ),
                    border = BorderStroke(1.dp, HomeWhite),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(0.dp),
                    ) {
                    Text(
                        text = stringResource(Res.string.sell_gold_screen_sell_gold).uppercase(),
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        fontSize = 13.sp,
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .shadow(4.dp, RoundedCornerShape(12.dp), clip = false)
                        .clip(RoundedCornerShape(12.dp))
                        .background(HomeWhite)
                        .border(1.dp, HomePrimaryLight, RoundedCornerShape(12.dp))
                        .clickable(onClick = onBuyGoldClick),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = HomePrimary,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(Res.string.home_screen_save_once).uppercase(),
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            fontSize = 13.sp,
                            color = HomePrimary,
                        )
                    }
                }
            }
        }
    }
}

private fun Modifier.homeShimmerPlaceholder(
    shape: Shape,
    baseColor: Color,
    highlightColor: Color,
): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "home-loading-shimmer")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(durationMillis = 1200)),
        label = "home-loading-shimmer-progress",
    )
    val shimmerBrush = Brush.linearGradient(
        colors = listOf(baseColor, highlightColor, baseColor),
        start = Offset(x = -260f + (520f * progress), y = 0f),
        end = Offset(x = 0f + (520f * progress), y = 220f),
    )
    clip(shape).background(shimmerBrush)
}

@Composable
private fun GoldCoinCluster(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val coinRadius = size.height * 0.32f
        val coinCenters = listOf(
            Offset(size.width * 0.28f, size.height * 0.62f),
            Offset(size.width * 0.52f, size.height * 0.42f),
            Offset(size.width * 0.76f, size.height * 0.62f),
        )
        val fillBrush = Brush.linearGradient(
            colors = listOf(Color(0xFFFFF1B8), Color(0xFFF7D36A), Gold500, Gold650),
            start = Offset.Zero,
            end = Offset(size.width, size.height),
        )

        coinCenters.forEachIndexed { index, center ->
            val alpha = if (index == 1) 1f else 0.94f
            drawCircle(brush = fillBrush, radius = coinRadius, center = center, alpha = alpha)
            drawCircle(
                color = HomeWhite.copy(alpha = 0.24f * alpha),
                radius = coinRadius,
                center = center,
                style = Stroke(width = 1.2.dp.toPx()),
            )
            drawCircle(
                color = Gold650.copy(alpha = 0.22f * alpha),
                radius = coinRadius * 0.62f,
                center = center,
                style = Stroke(width = 0.9.dp.toPx()),
            )
            drawCircle(
                color = HomeWhite.copy(alpha = 0.16f * alpha),
                radius = coinRadius * 0.22f,
                center = Offset(center.x - (coinRadius * 0.28f), center.y - (coinRadius * 0.28f)),
            )
            drawLine(
                color = HomeWhite.copy(alpha = 0.36f * alpha),
                start = Offset(center.x - coinRadius * 0.28f, center.y),
                end = Offset(center.x + coinRadius * 0.28f, center.y),
                strokeWidth = 1.2.dp.toPx(),
                cap = StrokeCap.Round,
            )
        }
    }
}

@Composable
private fun HomeStartJourneyGoldTower(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val brush = Brush.linearGradient(
            colors = listOf(Color(0xFFFFF1B8), Color(0xFFF7D36A), Gold500, Gold650),
            start = Offset.Zero,
            end = Offset(size.width, size.height),
        )
        val barSpecs = listOf(
            Triple(Offset(size.width * 0.08f, size.height * 0.58f), Size(size.width * 0.30f, size.height * 0.14f), 0.88f),
            Triple(Offset(size.width * 0.24f, size.height * 0.38f), Size(size.width * 0.40f, size.height * 0.18f), 0.94f),
            Triple(Offset(size.width * 0.43f, size.height * 0.16f), Size(size.width * 0.46f, size.height * 0.23f), 1f),
        )
        barSpecs.forEach { (offset, sizeSpec, alpha) ->
            drawRoundRect(brush = brush, topLeft = offset, size = sizeSpec, cornerRadius = CornerRadius(8f, 8f), alpha = alpha)
            drawRoundRect(
                color = HomeWhite.copy(alpha = 0.16f * alpha),
                topLeft = offset,
                size = sizeSpec,
                cornerRadius = CornerRadius(8f, 8f),
                style = Stroke(width = 1.dp.toPx()),
            )
        }
    }
}

private fun formatPercentage(value: Double): String {
    val rounded = abs((value * 100).roundToInt() / 100.0)
    val whole = rounded.toInt()
    val decimals = ((rounded - whole) * 100).roundToInt()
    return if (decimals == 0) whole.toString() else "$whole.${decimals.toString().padStart(2, '0')}"
}
