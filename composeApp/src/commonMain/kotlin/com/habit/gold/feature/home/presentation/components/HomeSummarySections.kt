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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habit.gold.core.designsystem.HabitGoldPalette
import com.habit.gold.feature.home.domain.model.HomeDashboardSummary
import com.habit.gold.feature.home.domain.model.HomeForceUpdate
import com.habit.gold.feature.home.domain.model.HomeRecentTransactionPreview
import com.habit.gold.feature.home.domain.model.HomeSipMandate
import com.habit.gold.feature.home.presentation.formatCreatedAt
import com.habit.gold.feature.home.presentation.formatGoldBalance
import com.habit.gold.feature.home.presentation.formatInr
import com.habit.gold.feature.home.presentation.formatProfitLabel
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.buy_gold_screen_buy_gold
import habitgoldmobile.composeapp.generated.resources.common_need_help_with_something
import habitgoldmobile.composeapp.generated.resources.common_safegold
import habitgoldmobile.composeapp.generated.resources.common_support_is_available_for_payments_shipping_and_orders
import habitgoldmobile.composeapp.generated.resources.common_current_value
import habitgoldmobile.composeapp.generated.resources.common_gold_unit_short
import habitgoldmobile.composeapp.generated.resources.common_invested_value
import habitgoldmobile.composeapp.generated.resources.common_toggle_expansion
import habitgoldmobile.composeapp.generated.resources.common_toggle_visibility
import habitgoldmobile.composeapp.generated.resources.common_view_details
import habitgoldmobile.composeapp.generated.resources.common_why_habitgold
import habitgoldmobile.composeapp.generated.resources.common_your_gold_is_100percent_secured
import habitgoldmobile.composeapp.generated.resources.home_screen_activity_empty_title
import habitgoldmobile.composeapp.generated.resources.home_screen_auto_invest
import habitgoldmobile.composeapp.generated.resources.home_screen_bank_confirmation_pending
import habitgoldmobile.composeapp.generated.resources.home_screen_footer_badge_insured
import habitgoldmobile.composeapp.generated.resources.home_screen_footer_badge_physical
import habitgoldmobile.composeapp.generated.resources.home_screen_footer_badge_vault
import habitgoldmobile.composeapp.generated.resources.home_screen_force_update_fallback_title
import habitgoldmobile.composeapp.generated.resources.home_screen_gold_savings_habit
import habitgoldmobile.composeapp.generated.resources.home_screen_invests_every_day
import habitgoldmobile.composeapp.generated.resources.home_screen_invests_every_month
import habitgoldmobile.composeapp.generated.resources.home_screen_invests_every_week
import habitgoldmobile.composeapp.generated.resources.home_screen_intro_description_gold
import habitgoldmobile.composeapp.generated.resources.home_screen_intro_description_growth
import habitgoldmobile.composeapp.generated.resources.home_screen_intro_description_liquidity
import habitgoldmobile.composeapp.generated.resources.home_screen_intro_description_secure
import habitgoldmobile.composeapp.generated.resources.home_screen_intro_title_gold
import habitgoldmobile.composeapp.generated.resources.home_screen_intro_title_growth
import habitgoldmobile.composeapp.generated.resources.home_screen_intro_title_liquidity
import habitgoldmobile.composeapp.generated.resources.home_screen_intro_title_secure
import habitgoldmobile.composeapp.generated.resources.home_screen_loading_summary
import habitgoldmobile.composeapp.generated.resources.home_screen_next_debit
import habitgoldmobile.composeapp.generated.resources.home_screen_powered_by
import habitgoldmobile.composeapp.generated.resources.home_screen_track_all_savings
import habitgoldmobile.composeapp.generated.resources.home_screen_recent_activity
import habitgoldmobile.composeapp.generated.resources.home_screen_retry_cta
import habitgoldmobile.composeapp.generated.resources.home_screen_resume
import habitgoldmobile.composeapp.generated.resources.home_screen_start_daily_savings
import habitgoldmobile.composeapp.generated.resources.home_screen_start_monthly_savings
import habitgoldmobile.composeapp.generated.resources.home_screen_start_weekly_savings
import habitgoldmobile.composeapp.generated.resources.home_screen_starts_from_daily
import habitgoldmobile.composeapp.generated.resources.home_screen_starts_from_monthly
import habitgoldmobile.composeapp.generated.resources.home_screen_starts_from_weekly
import habitgoldmobile.composeapp.generated.resources.home_screen_started
import habitgoldmobile.composeapp.generated.resources.home_screen_your_savings
import habitgoldmobile.composeapp.generated.resources.home_screen_start_your_gold_journey
import habitgoldmobile.composeapp.generated.resources.home_screen_total_gold_balance
import habitgoldmobile.composeapp.generated.resources.home_screen_trust_slide_earn_every_time_description
import habitgoldmobile.composeapp.generated.resources.home_screen_trust_slide_earn_every_time_title
import habitgoldmobile.composeapp.generated.resources.home_screen_trust_slide_free_delivery_description
import habitgoldmobile.composeapp.generated.resources.home_screen_trust_slide_free_delivery_title
import habitgoldmobile.composeapp.generated.resources.home_screen_trust_slide_real_gold_description
import habitgoldmobile.composeapp.generated.resources.home_screen_trust_slide_real_gold_title
import habitgoldmobile.composeapp.generated.resources.home_screen_trust_slide_stored_safely_description
import habitgoldmobile.composeapp.generated.resources.home_screen_trust_slide_stored_safely_title
import habitgoldmobile.composeapp.generated.resources.home_screen_update_now_cta
import habitgoldmobile.composeapp.generated.resources.img_bis_safety
import habitgoldmobile.composeapp.generated.resources.img_habitgold_intro
import habitgoldmobile.composeapp.generated.resources.img_liquid_accessible
import habitgoldmobile.composeapp.generated.resources.img_proven_growth
import habitgoldmobile.composeapp.generated.resources.safegold_image
import habitgoldmobile.composeapp.generated.resources.home_screen_view_all
import habitgoldmobile.composeapp.generated.resources.sell_gold_screen_sell_gold
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private val HomePrimary = HabitGoldPalette.plum
private val HomePrimaryLight = Color(0xFF9C5DD9)
private val HomeWhite = Color.White
private val Slate200 = Color(0xFFE2E8F0)
private val Slate500 = Color(0xFF64748B)
private val Slate600 = Color(0xFF475569)
private val Slate700 = Color(0xFF334155)
private val Slate900 = Color(0xFF0F172A)
private val Gold500 = Color(0xFFD4A017)
private val Gold650 = Color(0xFF9A6A06)
private val GoldSurface = Color(0xFFFFF7E8)
private val GreenBright = Color(0xFF93F2B5)
private val GreenSoft = Color(0xFFE8FFF0)
private val Green25 = Color(0xFFE8FFF0)
private val Green500Soft = Color(0xFF16A34A)
private val Red25 = Color(0xFFFFEFF0)
private val RedSoft = Color(0xFFFFEFF0)
private val RedTint = Color(0xFFC2414D)
private val Blue25 = Color(0xFFEFF6FF)
private val Blue600 = Color(0xFF2563EB)
private val Purple50 = Color(0xFFFAF5FF)
private val Purple500 = Color(0xFF9C5DD9)
private val Purple700 = Color(0xFF7B2CBF)
private val FooterBackground = Color(0xFFF6F7F9)
private val FooterCard = HomeWhite
private val FooterHeading = Color(0xFF8A919C)
private val FooterPrimaryText = Color(0xFF20242B)
private val FooterSecondaryText = Color(0xFF6F7783)
private val FooterDivider = Color(0xFFE5E7EB)
private val FooterIconTile = Color(0xFFF1F3F6)
private val FooterBorder = Color(0xFFE7EBF0)
private val Blue50Alt = Color(0xFFEFF6FF)

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
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.58f)
                        .height(18.dp)
                        .homeShimmerPlaceholder(
                            shape = RoundedCornerShape(999.dp),
                            baseColor = shimmerBase,
                            highlightColor = shimmerHighlight,
                        ),
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.42f)
                        .height(34.dp)
                        .homeShimmerPlaceholder(
                            shape = RoundedCornerShape(999.dp),
                            baseColor = shimmerBase,
                            highlightColor = shimmerHighlight,
                        ),
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .homeShimmerPlaceholder(
                            shape = RoundedCornerShape(16.dp),
                            baseColor = shimmerBase,
                            highlightColor = shimmerHighlight,
                        ),
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
                            .homeShimmerPlaceholder(
                                shape = RoundedCornerShape(12.dp),
                                baseColor = shimmerBase,
                                highlightColor = shimmerHighlight,
                            ),
                    )
                }
            }
        }

        HomeLoadingSurface(shape = RoundedCornerShape(20.dp)) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(16.dp)
                        .homeShimmerPlaceholder(
                            shape = RoundedCornerShape(999.dp),
                            baseColor = shimmerBase,
                            highlightColor = shimmerHighlight,
                        ),
                )
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .homeShimmerPlaceholder(
                                shape = RoundedCornerShape(14.dp),
                                baseColor = shimmerBase,
                                highlightColor = shimmerHighlight,
                            ),
                    )
                }
            }
        }

        HomeLoadingSurface(shape = RoundedCornerShape(20.dp)) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.34f)
                        .height(16.dp)
                        .homeShimmerPlaceholder(
                            shape = RoundedCornerShape(999.dp),
                            baseColor = shimmerBase,
                            highlightColor = shimmerHighlight,
                        ),
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(108.dp)
                        .homeShimmerPlaceholder(
                            shape = RoundedCornerShape(16.dp),
                            baseColor = shimmerBase,
                            highlightColor = shimmerHighlight,
                        ),
                )
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
                Text(
                    text = stringResource(Res.string.home_screen_retry_cta),
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
internal fun HomeForceUpdateCard(
    update: HomeForceUpdate,
) {
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
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                color = Slate900,
            )
            update.message?.let {
                Text(
                    text = it,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = Slate600,
                )
            }
            update.ctaText?.ifBlank { null }?.let {
                Text(
                    text = it,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Gold650,
                )
            } ?: Text(
                text = stringResource(Res.string.home_screen_update_now_cta),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Gold650,
            )
        }
    }
}

@Composable
internal fun HomeZeroBalanceSection(
    onStartJourneyClick: () -> Unit,
) {
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
                fontWeight = FontWeight.ExtraBold,
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
                        fontWeight = FontWeight.Medium,
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
                    fontWeight = FontWeight.Medium,
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
                        fontWeight = FontWeight.Bold,
                        color = HomeWhite,
                    )
                    Text(
                        text = stringResource(Res.string.common_gold_unit_short),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
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
                            fontWeight = FontWeight.Bold,
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
                        Column(
                            modifier = if (shouldShowCurrentValue) Modifier.weight(1f) else Modifier,
                        ) {
                            Text(
                                text = stringResource(Res.string.common_invested_value),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = HomeWhite.copy(alpha = 0.7f),
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isBalanceVisible) "₹${formatInr(investedValue)}" else "****",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
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
                            Column(
                                modifier = Modifier.weight(1f),
                            ) {
                                Text(
                                    text = stringResource(Res.string.common_current_value),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HomeWhite.copy(alpha = 0.7f),
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (isBalanceVisible) "₹${formatInr(currentValue)}" else "****",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
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
                                    fontWeight = FontWeight.Bold,
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
                                fontWeight = FontWeight.Bold,
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
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(Res.string.buy_gold_screen_buy_gold).uppercase(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = HomePrimary,
                        )
                    }
                }

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
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                    )
                }
            }
        }
    }
}

@Composable
internal fun HomeTrustHighlightsSection(
    onOpenIntroSheet: (Int) -> Unit,
) {
    val items = remember {
        listOf(
            HomeTrustItem(
                introSheetPage = 0,
                title = Res.string.home_screen_trust_slide_real_gold_title,
                description = Res.string.home_screen_trust_slide_real_gold_description,
                icon = Icons.Default.WorkspacePremium,
                background = listOf(Color(0xFFFDE68A), Gold500.copy(alpha = 0.18f)),
            ),
            HomeTrustItem(
                introSheetPage = 1,
                title = Res.string.home_screen_trust_slide_stored_safely_title,
                description = Res.string.home_screen_trust_slide_stored_safely_description,
                icon = Icons.Default.Verified,
                background = listOf(Blue50Alt, HomeWhite),
            ),
            HomeTrustItem(
                introSheetPage = 2,
                title = Res.string.home_screen_trust_slide_free_delivery_title,
                description = Res.string.home_screen_trust_slide_free_delivery_description,
                icon = Icons.Default.LocalShipping,
                background = listOf(Color(0xFFE8FFF0), HomeWhite),
            ),
            HomeTrustItem(
                introSheetPage = 3,
                title = Res.string.home_screen_trust_slide_earn_every_time_title,
                description = Res.string.home_screen_trust_slide_earn_every_time_description,
                icon = Icons.Default.LocalOffer,
                background = listOf(Purple50, HomeWhite),
            ),
        )
    }
    val pagerState = rememberPagerState(pageCount = { items.size })

    LaunchedEffect(pagerState) {
        while (true) {
            delay(4500)
            if (!pagerState.isScrollInProgress && pagerState.pageCount > 0) {
                pagerState.animateScrollToPage((pagerState.currentPage + 1) % pagerState.pageCount)
            }
        }
    }

    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        HorizontalPager(
            state = pagerState,
            pageSpacing = 12.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(84.dp),
        ) { page ->
            val item = items[page]
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenIntroSheet(item.introSheetPage) },
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = HomeWhite),
                border = BorderStroke(1.dp, Slate200.copy(alpha = 0.9f)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.horizontalGradient(colors = item.background)),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(item.title),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Slate900,
                                maxLines = 1,
                            )
                            Text(
                                text = stringResource(item.description),
                                fontSize = 10.sp,
                                lineHeight = 12.sp,
                                color = Slate600,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(HomeWhite.copy(alpha = 0.88f))
                                .border(1.dp, HomeWhite.copy(alpha = 0.92f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null,
                                tint = HomePrimary,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun RecentActivitySection(
    items: List<HomeRecentTransactionPreview>,
    onOpenHistory: () -> Unit,
) {
    Column {
        Text(
            text = stringResource(Res.string.home_screen_recent_activity),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (items.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = HomeWhite),
                border = BorderStroke(1.dp, Slate200),
            ) {
                Text(
                    text = stringResource(Res.string.home_screen_activity_empty_title),
                    modifier = Modifier.padding(16.dp),
                    fontSize = 13.sp,
                    color = Slate600,
                )
            }
        } else {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(items, key = { it.id }) { item ->
                    val config = recentActivityConfigFor(item)
                    Card(
                        modifier = Modifier
                            .width(220.dp)
                            .clickable(onClick = onOpenHistory),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = HomeWhite),
                        border = BorderStroke(1.dp, Slate200.copy(alpha = 0.72f)),
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(config.iconBackground),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        imageVector = config.icon,
                                        contentDescription = null,
                                        tint = config.iconTint,
                                        modifier = Modifier.size(18.dp),
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = config.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Slate900,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    Text(
                                        text = formatCreatedAt(item.createdAt),
                                        fontSize = 11.sp,
                                        color = Slate500,
                                    )
                                }
                            }

                            Text(
                                text = "₹${item.amount.toDoubleOrNull()?.let(::formatInr) ?: item.amount}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Slate900,
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(999.dp),
                                    color = config.statusBackground,
                                ) {
                                    Text(
                                        text = item.status.replaceFirstChar { it.uppercase() },
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = config.statusTint,
                                    )
                                }
                                if (item.goldQuantity.isNotBlank()) {
                                    Text(
                                        text = "${item.goldQuantity} ${stringResource(Res.string.common_gold_unit_short)}",
                                        fontSize = 11.sp,
                                        color = Slate600,
                                        fontWeight = FontWeight.Medium,
                                    )
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
internal fun GoldSavingsPlansCard(
    mandates: List<HomeSipMandate>,
    onOpenSavingsScreen: () -> Unit,
) {
    val dailyTitle = stringResource(Res.string.home_screen_start_daily_savings)
    val weeklyTitle = stringResource(Res.string.home_screen_start_weekly_savings)
    val monthlyTitle = stringResource(Res.string.home_screen_start_monthly_savings)
    val dailySubtitle = stringResource(Res.string.home_screen_starts_from_daily)
    val weeklySubtitle = stringResource(Res.string.home_screen_starts_from_weekly)
    val monthlySubtitle = stringResource(Res.string.home_screen_starts_from_monthly)
    val mandateByFrequency = remember(mandates) {
        mandates.fold(mutableMapOf<String, HomeSipMandate>()) { acc, mandate ->
            val frequencyKey = mandate.frequency.trim().uppercase()
            val statusKind = homeSipMandateStatusKind(mandate.status)
            val existingMandate = acc[frequencyKey]
            val existingKind = existingMandate?.let { homeSipMandateStatusKind(it.status) }
            if (
                existingKind == null ||
                (existingKind != HomeSipMandateStatusKind.Active && statusKind == HomeSipMandateStatusKind.Active) ||
                (existingKind !in setOf(HomeSipMandateStatusKind.Active, HomeSipMandateStatusKind.Paused) &&
                    statusKind == HomeSipMandateStatusKind.Paused)
            ) {
                acc[frequencyKey] = mandate
            }
            acc
        }
    }
    val planItems = remember(mandateByFrequency, dailyTitle, weeklyTitle, monthlyTitle, dailySubtitle, weeklySubtitle, monthlySubtitle) {
        listOf(
            HomeSavingsPlanItem(
                frequency = "Daily",
                defaultTitle = dailyTitle,
                defaultSubtitle = dailySubtitle,
            ),
            HomeSavingsPlanItem(
                frequency = "Weekly",
                defaultTitle = weeklyTitle,
                defaultSubtitle = weeklySubtitle,
            ),
            HomeSavingsPlanItem(
                frequency = "Monthly",
                defaultTitle = monthlyTitle,
                defaultSubtitle = monthlySubtitle,
            ),
        ).map { item ->
            val mandate = mandateByFrequency[item.frequency.uppercase()]
            val statusKind = mandate?.let { homeSipMandateStatusKind(it.status) }
            val actionableMandate = mandate?.takeIf {
                statusKind == HomeSipMandateStatusKind.Active || statusKind == HomeSipMandateStatusKind.Paused
            }
            val actionTitle = when (statusKind) {
                HomeSipMandateStatusKind.Active -> "Upgrade ${item.frequency} Savings"
                HomeSipMandateStatusKind.Paused -> "Resume ${item.frequency} Savings"
                else -> item.defaultTitle
            }
            item.copy(
                title = actionTitle,
                subtitle = actionableMandate?.let(::currentSavingsAmountSubtitle) ?: item.defaultSubtitle,
                mandate = actionableMandate,
                statusKind = statusKind ?: HomeSipMandateStatusKind.Neutral,
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(Res.string.home_screen_gold_savings_habit),
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1F2937),
            )
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFFDE68A).copy(alpha = 0.24f),
                                Gold500.copy(alpha = 0.72f),
                                Gold650.copy(alpha = 0.42f),
                            )
                        )
                    ),
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            planItems.forEach { item ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (item.frequency == "Daily") {
                                Modifier.homeAnimatedSavingsBorder()
                            } else {
                                Modifier
                            }
                        )
                        .clickable(onClick = onOpenSavingsScreen),
                    color = HomeWhite,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Slate200.copy(alpha = 0.7f)),
                ) {
                    GoldSavingsRowItem(item = item)
                }
            }
        }
    }
}

@Composable
private fun Modifier.homeAnimatedSavingsBorder(): Modifier {
    val infiniteTransition = androidx.compose.animation.core.rememberInfiniteTransition(label = "dailySavingsBorder")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            animation = androidx.compose.animation.core.tween(
                durationMillis = 2200,
                easing = androidx.compose.animation.core.LinearEasing,
            ),
        ),
        label = "dailySavingsBorderProgress",
    )
    val animatedBrush by rememberUpdatedState(
        Brush.linearGradient(
            colors = listOf(
                Gold500.copy(alpha = 0.18f),
                HomeWhite.copy(alpha = 0.92f),
                HomePrimary.copy(alpha = 0.34f),
                Gold500.copy(alpha = 0.60f),
            ),
            start = Offset(0f, 0f),
            end = Offset(600f * progress.coerceAtLeast(0.2f), 180f),
        ),
    )

    return this.drawWithContent {
        drawContent()
        drawRoundRect(
            color = Gold500.copy(alpha = 0.14f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx(), 16.dp.toPx()),
            style = Stroke(width = 1.dp.toPx()),
        )
        drawRoundRect(
            brush = animatedBrush,
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx(), 16.dp.toPx()),
            style = Stroke(width = 1.5.dp.toPx()),
            alpha = 0.92f,
        )
    }
}

@Composable
internal fun HomeSipMandatesSection(
    mandates: List<HomeSipMandate>,
    onViewAllClick: () -> Unit,
) {
    val activeMandates = remember(mandates) { mandates.filter(::isHomeVisibleSipMandate) }
    val previewMandates = remember(activeMandates) { activeMandates.take(5) }
    if (previewMandates.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { previewMandates.size })
    val showViewAll = activeMandates.size > 5

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = stringResource(Res.string.home_screen_your_savings),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Slate900,
                )
                Text(
                    text = stringResource(Res.string.home_screen_track_all_savings),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Slate500,
                )
            }

            if (showViewAll) {
                Text(
                    text = stringResource(Res.string.home_screen_view_all),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = HomePrimary,
                    modifier = Modifier.clickable(onClick = onViewAllClick),
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            pageSpacing = 12.dp,
            modifier = Modifier.fillMaxWidth(),
        ) { page ->
            HomeSipMandateCard(
                mandate = previewMandates[page],
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (previewMandates.size > 1) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                repeat(previewMandates.size) { index ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .size(if (pagerState.currentPage == index) 8.dp else 6.dp)
                            .clip(CircleShape)
                            .background(if (pagerState.currentPage == index) HomePrimary else Slate200),
                    )
                }
            }
        }
    }
}

@Composable
internal fun HomeSupportFooter(
    onSupportClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, FooterBorder, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            color = FooterCard,
            tonalElevation = 0.dp,
            onClick = onSupportClick,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(FooterIconTile),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalShipping,
                            contentDescription = null,
                            tint = HomePrimary,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = stringResource(Res.string.common_need_help_with_something),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = FooterPrimaryText,
                        )
                        Text(
                            text = stringResource(Res.string.common_support_is_available_for_payments_shipping_and_orders),
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = FooterSecondaryText,
                        )
                    }
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = FooterSecondaryText,
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(FooterBackground)
                .padding(horizontal = 18.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(Res.string.common_your_gold_is_100percent_secured),
                modifier = Modifier.fillMaxWidth(),
                fontSize = 42.sp,
                lineHeight = 42.sp,
                fontWeight = FontWeight.ExtraBold,
                color = FooterHeading,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(
                color = FooterDivider,
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 32.dp),
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Top,
            ) {
                FooterBadge(
                    icon = Icons.Default.WorkspacePremium,
                    label = stringResource(Res.string.home_screen_footer_badge_physical),
                    modifier = Modifier.weight(1f),
                )
                FooterBadge(
                    icon = Icons.Default.Verified,
                    label = stringResource(Res.string.home_screen_footer_badge_insured),
                    modifier = Modifier.weight(1f),
                )
                FooterBadge(
                    icon = Icons.Default.AccountBalance,
                    label = stringResource(Res.string.home_screen_footer_badge_vault),
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
            PoweredBySafeGoldRow()
        }
    }
}

@Composable
internal fun HomeWhyHabitGoldSection(
    onOpenIntroSheet: (Int) -> Unit,
) {
    val items = remember {
        listOf(
            HomeIntroCardItem(
                title = Res.string.home_screen_intro_title_gold,
                description = Res.string.home_screen_intro_description_gold,
                image = Res.drawable.img_habitgold_intro,
            ),
            HomeIntroCardItem(
                title = Res.string.home_screen_intro_title_secure,
                description = Res.string.home_screen_intro_description_secure,
                image = Res.drawable.img_bis_safety,
            ),
            HomeIntroCardItem(
                title = Res.string.home_screen_intro_title_liquidity,
                description = Res.string.home_screen_intro_description_liquidity,
                image = Res.drawable.img_liquid_accessible,
            ),
            HomeIntroCardItem(
                title = Res.string.home_screen_intro_title_growth,
                description = Res.string.home_screen_intro_description_growth,
                image = Res.drawable.img_proven_growth,
            ),
        )
    }

    Column {
        Text(
            text = stringResource(Res.string.common_why_habitgold),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(items.withIndex().toList(), key = { it.index }) { indexedItem ->
                HomeWhyIntroCard(
                    item = indexedItem.value,
                    onClick = { onOpenIntroSheet(indexedItem.index) },
                )
            }
        }
    }
}

private data class HomeTrustItem(
    val introSheetPage: Int,
    val title: org.jetbrains.compose.resources.StringResource,
    val description: org.jetbrains.compose.resources.StringResource,
    val icon: ImageVector,
    val background: List<Color>,
)

private data class HomeIntroCardItem(
    val title: StringResource,
    val description: StringResource,
    val image: DrawableResource,
)

private data class RecentActivityConfig(
    val title: String,
    val icon: ImageVector,
    val iconBackground: Color,
    val iconTint: Color,
    val statusBackground: Color,
    val statusTint: Color,
)

private fun recentActivityConfigFor(item: HomeRecentTransactionPreview): RecentActivityConfig {
    val isBuy = item.type.equals("BUY", ignoreCase = true)
    val isSell = item.type.equals("SELL", ignoreCase = true)
    val isDelivery = item.type.contains("delivery", ignoreCase = true)
    val title = when {
        item.isSip -> item.sipName?.ifBlank { "Gold Savings SIP" } ?: "Gold Savings SIP"
        isSell -> "Sell Gold"
        isDelivery -> "Coin Delivery"
        isBuy -> "Buy Gold"
        else -> "Transaction"
    }
    val isFailure = item.status.contains("fail", ignoreCase = true) || item.status.contains("cancel", ignoreCase = true)
    return RecentActivityConfig(
        title = title,
        icon = when {
            isBuy -> Icons.Default.Savings
            isSell -> Icons.AutoMirrored.Filled.TrendingDown
            isDelivery -> Icons.Default.LocalShipping
            else -> Icons.Default.CheckCircle
        },
        iconBackground = when {
            isBuy -> Green25
            isSell -> Red25
            isDelivery -> Blue25
            else -> Blue50Alt
        },
        iconTint = when {
            isBuy -> Green500Soft
            isSell -> RedTint
            isDelivery -> Blue600
            else -> HomePrimary
        },
        statusBackground = if (isFailure) RedSoft else GreenSoft,
        statusTint = if (isFailure) RedTint else Color(0xFF15803D),
    )
}

@Composable
private fun HomeSipMandateCard(
    mandate: HomeSipMandate,
    modifier: Modifier = Modifier,
) {
    val statusStyle = homeSipMandateStatusStyle(mandate.status)
    val nextExecutionText = mandate.nextExecutionDate?.let(::formatCreatedAt)
    val startedOnText = mandate.startDate.take(10)
    val promoText = mandate.promoCode?.trim()?.takeIf { it.isNotEmpty() }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (statusStyle.kind) {
                HomeSipMandateStatusKind.Active -> HomeWhite
                HomeSipMandateStatusKind.Paused -> GoldSurface.copy(alpha = 0.35f)
                HomeSipMandateStatusKind.Failed -> Red25.copy(alpha = 0.35f)
                HomeSipMandateStatusKind.Neutral -> HomeWhite
            }
        ),
        border = BorderStroke(
            1.dp,
            when (statusStyle.kind) {
                HomeSipMandateStatusKind.Active -> Slate200
                HomeSipMandateStatusKind.Paused -> Gold500.copy(alpha = 0.45f)
                HomeSipMandateStatusKind.Failed -> RedTint.copy(alpha = 0.25f)
                HomeSipMandateStatusKind.Neutral -> Slate200
            }
        ),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 13.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = HomePrimary.copy(alpha = 0.08f),
                ) {
                    Text(
                        text = stringResource(Res.string.home_screen_auto_invest),
                        color = HomePrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    )
                }
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = statusStyle.bg,
                ) {
                    Text(
                        text = statusStyle.label,
                        color = statusStyle.fg,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        text = mandate.name.ifBlank { "Gold Savings SIP" },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Slate700,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "₹${mandate.amount}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Slate900,
                    )
                    Text(
                        text = homeSipCadenceLabel(mandate.frequency),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Slate500,
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = if (nextExecutionText != null) stringResource(Res.string.home_screen_next_debit) else stringResource(Res.string.home_screen_started),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Slate500,
                        )
                        Text(
                            text = nextExecutionText ?: startedOnText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate900,
                        )
                    }

                    promoText?.let {
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = HomePrimary.copy(alpha = 0.1f),
                        ) {
                            Text(
                                text = it,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = HomePrimary,
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }

            if (mandate.billing?.needsAttention == true) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Savings,
                        contentDescription = null,
                        tint = Gold650,
                        modifier = Modifier.size(14.dp),
                    )
                    Text(
                        text = stringResource(Res.string.home_screen_bank_confirmation_pending),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Gold650,
                    )
                }
            }
        }
    }
}

private data class HomeSavingsPlanItem(
    val frequency: String,
    val defaultTitle: String,
    val defaultSubtitle: String,
    val title: String = defaultTitle,
    val subtitle: String = defaultSubtitle,
    val mandate: HomeSipMandate? = null,
    val statusKind: HomeSipMandateStatusKind = HomeSipMandateStatusKind.Neutral,
)

private enum class HomeSipMandateStatusKind {
    Active,
    Paused,
    Failed,
    Neutral,
}

private data class HomeSipMandateStatusStyle(
    val label: String,
    val bg: Color,
    val fg: Color,
    val kind: HomeSipMandateStatusKind,
)

private fun homeSipMandateStatusKind(status: String): HomeSipMandateStatusKind {
    return homeSipMandateStatusStyle(status).kind
}

private fun homeSipMandateStatusStyle(status: String): HomeSipMandateStatusStyle {
    val normalized = status.trim().uppercase()
    return when (normalized) {
        "ACTIVE", "COMPLETED", "SUCCESS", "REGISTERED" -> HomeSipMandateStatusStyle("Active", Green25, Green500Soft, HomeSipMandateStatusKind.Active)
        "PAUSED" -> HomeSipMandateStatusStyle("Paused", GoldSurface, Gold650, HomeSipMandateStatusKind.Paused)
        "FAILED_REGISTRATION", "FAILED", "CANCELLED" -> HomeSipMandateStatusStyle(
            status.lowercase().replaceFirstChar { it.uppercase() },
            Red25,
            RedTint,
            HomeSipMandateStatusKind.Failed,
        )
        else -> HomeSipMandateStatusStyle(
            status.lowercase().replaceFirstChar { it.uppercase() },
            Blue50Alt,
            Slate700,
            HomeSipMandateStatusKind.Neutral,
        )
    }
}

private fun isHomeVisibleSipMandate(mandate: HomeSipMandate): Boolean =
    homeSipMandateStatusKind(mandate.status) == HomeSipMandateStatusKind.Active

@Composable
private fun GoldSavingsRowItem(
    item: HomeSavingsPlanItem,
) {
    val accent = when (item.frequency) {
        "Daily" -> HomePrimary
        "Weekly" -> Purple700
        else -> Purple500
    }
    val icon = when (item.frequency) {
        "Daily" -> Icons.Default.CalendarToday
        "Weekly" -> Icons.Default.DateRange
        else -> Icons.Default.Event
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(HomeWhite)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(accent.copy(alpha = 0.12f))
                .border(1.dp, accent.copy(alpha = 0.10f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(18.dp),
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Slate900,
            )
            Text(
                text = item.subtitle,
                fontSize = 11.sp,
                color = Slate600,
            )
        }

        if (item.statusKind == HomeSipMandateStatusKind.Paused) {
            Button(
                onClick = {},
                modifier = Modifier.height(36.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = accent.copy(alpha = 0.10f),
                    contentColor = accent,
                ),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
            ) {
                Text(
                    text = stringResource(Res.string.home_screen_resume),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

@Composable
private fun homeSipCadenceLabel(frequency: String): String {
    return when (frequency.trim().uppercase()) {
        "DAILY" -> stringResource(Res.string.home_screen_invests_every_day)
        "WEEKLY" -> stringResource(Res.string.home_screen_invests_every_week)
        "MONTHLY" -> stringResource(Res.string.home_screen_invests_every_month)
        else -> frequency.replaceFirstChar { it.uppercase() }
    }
}

private fun currentSavingsAmountSubtitle(mandate: HomeSipMandate): String {
    val cadenceLabel = when (mandate.frequency.trim().uppercase()) {
        "DAILY" -> "day"
        "WEEKLY" -> "week"
        "MONTHLY" -> "month"
        else -> "cycle"
    }
    val rawAmount = mandate.billing?.currentAmount
        ?: mandate.billingCurrentAmount
        ?: mandate.billing?.nextExecutionAmount
        ?: mandate.billingNextExecutionAmount
        ?: mandate.amount
    return "Current Savings ${formatHomeSavingsPlanAmount(rawAmount)}/$cadenceLabel"
}

@Composable
private fun HomeWhyIntroCard(
    item: HomeIntroCardItem,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(160.dp)
            .padding(vertical = 4.dp, horizontal = 2.dp),
        onClick = onClick,
        border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.08f)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = HomeWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(HomePrimary.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(item.image),
                    contentDescription = null,
                    modifier = Modifier.size(26.dp),
                )
            }

            Text(
                text = stringResource(item.title),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                color = Slate900,
                lineHeight = 20.sp,
                modifier = Modifier.padding(top = 10.dp),
            )
        }
    }
}

@Composable
private fun FooterBadge(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .height(108.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(HomeWhite)
            .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
            .padding(vertical = 12.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFF9E5)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Gold500,
                modifier = Modifier.size(20.dp),
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Slate600,
            textAlign = TextAlign.Center,
            lineHeight = 14.sp,
        )
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
private fun PoweredBySafeGoldRow(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(Res.string.home_screen_powered_by),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF94A3B8),
            letterSpacing = 1.2.sp,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Image(
            painter = painterResource(Res.drawable.safegold_image),
            contentDescription = stringResource(Res.string.common_safegold),
            modifier = Modifier
                .height(10.dp)
                .wrapContentWidth(),
        )
    }
}

private fun formatHomeSavingsPlanAmount(rawAmount: String?): String {
    val parsedAmount = rawAmount?.trim()?.toDoubleOrNull() ?: return "₹0"
    return if (parsedAmount % 1.0 == 0.0) {
        "₹${formatInr(parsedAmount)}"
    } else {
        "₹${formatInr(parsedAmount)}"
    }
}

@Composable
private fun GoldCoinCluster(
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val coinRadius = size.height * 0.32f
        val coinCenters = listOf(
            Offset(size.width * 0.28f, size.height * 0.62f),
            Offset(size.width * 0.52f, size.height * 0.42f),
            Offset(size.width * 0.76f, size.height * 0.62f),
        )
        val fillBrush = Brush.linearGradient(
            colors = listOf(
                Color(0xFFFFF1B8),
                Color(0xFFF7D36A),
                Gold500,
                Gold650,
            ),
            start = Offset.Zero,
            end = Offset(size.width, size.height),
        )

        coinCenters.forEachIndexed { index, center ->
            val alpha = if (index == 1) 1f else 0.94f
            drawCircle(
                brush = fillBrush,
                radius = coinRadius,
                center = center,
                alpha = alpha,
            )
            drawCircle(
                color = HomeWhite.copy(alpha = 0.24f * alpha),
                radius = coinRadius,
                center = center,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.2.dp.toPx()),
            )
            drawCircle(
                color = Gold650.copy(alpha = 0.22f * alpha),
                radius = coinRadius * 0.62f,
                center = center,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 0.9.dp.toPx()),
            )
            drawCircle(
                color = HomeWhite.copy(alpha = 0.16f * alpha),
                radius = coinRadius * 0.22f,
                center = Offset(
                    x = center.x - (coinRadius * 0.28f),
                    y = center.y - (coinRadius * 0.28f),
                ),
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
private fun HomeStartJourneyGoldTower(
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFFFFF1B8),
                Color(0xFFF7D36A),
                Gold500,
                Gold650,
            ),
            start = Offset.Zero,
            end = Offset(size.width, size.height),
        )
        val barSpecs = listOf(
            Triple(Offset(size.width * 0.08f, size.height * 0.58f), androidx.compose.ui.geometry.Size(size.width * 0.30f, size.height * 0.14f), 0.88f),
            Triple(Offset(size.width * 0.24f, size.height * 0.38f), androidx.compose.ui.geometry.Size(size.width * 0.40f, size.height * 0.18f), 0.94f),
            Triple(Offset(size.width * 0.43f, size.height * 0.16f), androidx.compose.ui.geometry.Size(size.width * 0.46f, size.height * 0.23f), 1f),
        )
        barSpecs.forEach { (offset, sizeSpec, alpha) ->
            drawRoundRect(
                brush = brush,
                topLeft = offset,
                size = sizeSpec,
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f),
                alpha = alpha,
            )
            drawRoundRect(
                color = HomeWhite.copy(alpha = 0.16f * alpha),
                topLeft = offset,
                size = sizeSpec,
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx()),
            )
        }
    }
}

private fun formatPercentage(value: Double): String {
    val rounded = abs((value * 100).roundToInt() / 100.0)
    val whole = rounded.toInt()
    val decimals = ((rounded - whole) * 100).roundToInt()
    return if (decimals == 0) {
        whole.toString()
    } else {
        "$whole.${decimals.toString().padStart(2, '0')}"
    }
}
