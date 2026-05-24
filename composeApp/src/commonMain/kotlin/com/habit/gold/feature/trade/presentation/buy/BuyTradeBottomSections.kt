package com.habit.gold.feature.trade.presentation.buy

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habit.gold.feature.trade.domain.TradeLivePriceState
import com.habit.gold.feature.trade.domain.model.TradeAvailableCoupon
import com.habit.gold.feature.trade.presentation.formatCountdown
import com.habit.gold.feature.trade.presentation.formatMoney
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.trade_buy_apply
import habitgoldmobile.composeapp.generated.resources.trade_buy_available_coupons_title
import habitgoldmobile.composeapp.generated.resources.trade_buy_coupon_estimate_summary
import habitgoldmobile.composeapp.generated.resources.trade_buy_coupon_locked
import habitgoldmobile.composeapp.generated.resources.trade_buy_coupon_sheet_empty
import habitgoldmobile.composeapp.generated.resources.trade_buy_coupon_sheet_subtitle
import habitgoldmobile.composeapp.generated.resources.trade_buy_live_price_label
import habitgoldmobile.composeapp.generated.resources.trade_buy_pay_now
import habitgoldmobile.composeapp.generated.resources.trade_buy_plus_gst
import habitgoldmobile.composeapp.generated.resources.trade_buy_updating_price
import habitgoldmobile.composeapp.generated.resources.trade_buy_updates_in
import habitgoldmobile.composeapp.generated.resources.trade_buy_view_breakdown
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun BuyCouponSheet(
    coupons: List<TradeAvailableCoupon>,
    estimateAmount: Double,
    appliedCouponCode: String?,
    onApplyCoupon: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(420.dp)
            .padding(horizontal = 20.dp)
            .padding(bottom = 20.dp),
    ) {
        Text(
            text = stringResource(Res.string.trade_buy_available_coupons_title),
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = BuySlate950,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = stringResource(Res.string.trade_buy_coupon_sheet_subtitle),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = BuySlate500,
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (coupons.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(Res.string.trade_buy_coupon_sheet_empty),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = BuySlate500,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(coupons) { coupon ->
                    val isSelected = appliedCouponCode == coupon.code
                    val isApplicable = isBuyCouponApplicable(coupon, estimateAmount)
                    val subtitle = buyCouponDisabledReason(coupon, estimateAmount)
                        ?: coupon.description?.takeIf { it.isNotBlank() }
                        ?: stringResource(
                            Res.string.trade_buy_coupon_estimate_summary,
                            coupon.estimatedSaving,
                            coupon.type.name.replace('_', ' '),
                        )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(if (isApplicable || isSelected) 1f else 0.5f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) BuyPrimary.copy(alpha = 0.10f) else BuyWhite)
                            .border(
                                1.dp,
                                if (isSelected) BuyPrimary.copy(alpha = 0.25f) else BuySlate100,
                                RoundedCornerShape(12.dp),
                            )
                            .clickable(enabled = isApplicable || isSelected) { onApplyCoupon(coupon.code) }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = coupon.code,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isApplicable || isSelected) BuySlate950 else BuySlate500,
                                maxLines = 1,
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = subtitle,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = BuySlate500,
                                maxLines = 2,
                            )
                        }
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = BuyPrimary,
                                modifier = Modifier.size(22.dp),
                            )
                        } else if (!isApplicable) {
                            Text(
                                text = stringResource(Res.string.trade_buy_coupon_locked),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = BuySlate400,
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(BuyPrimary)
                                    .padding(horizontal = 14.dp, vertical = 7.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = stringResource(Res.string.trade_buy_apply),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = BuyWhite,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun BuyTradeBottomBar(
    livePriceState: TradeLivePriceState,
    totalPayable: Double,
    showPoweredBySafeGold: Boolean,
    enabled: Boolean,
    isLoading: Boolean,
    errorMessage: String?,
    onShowBreakdown: () -> Unit,
    onPrimaryAction: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding(),
        color = BuyWhite,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 12.dp),
        ) {
            HorizontalDivider(color = BuySlate100)
            errorMessage?.takeIf { it.isNotBlank() }?.let { message ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = BuyRed50),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = BuyRed700,
                            modifier = Modifier.size(18.dp),
                        )
                        Text(
                            text = message,
                            color = BuyRed700,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                        )
                    }
                }
            }
            if (showPoweredBySafeGold) {
                BuyTradePoweredByRow(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                )
            }
            BuyTradeLivePriceBar(livePriceState = livePriceState)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BuyAmountSummaryButton(
                    amount = totalPayable,
                    onClick = onShowBreakdown,
                    modifier = Modifier.weight(1f),
                )

                Button(
                    onClick = onPrimaryAction,
                    enabled = enabled && !isLoading,
                    modifier = Modifier
                        .weight(1.65f)
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BuyPrimary,
                        contentColor = BuyWhite,
                        disabledContainerColor = BuySlate200,
                        disabledContentColor = BuySlate400,
                    ),
                    contentPadding = PaddingValues(0.dp),
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (isLoading) {
                            TradeLoadingDots(dotColor = BuyPrimary)
                        } else {
                            Text(
                                text = stringResource(Res.string.trade_buy_pay_now),
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Black,
                                color = if (enabled) BuyWhite else BuySlate400,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 16.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BuyAmountSummaryButton(
    amount: Double,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(12.dp),
        color = BuyWhite,
        border = androidx.compose.foundation.BorderStroke(1.dp, BuySlate200),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(Res.string.trade_buy_view_breakdown),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = BuyPrimary,
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = BuyPrimary,
                    modifier = Modifier.size(14.dp),
                )
            }
            Text(
                modifier = Modifier.offset(y=(-2).dp),
                text = "₹${formatMoney(amount)}",
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = BuySlate950,
            )
        }
    }
}

@Composable
internal fun BuyTradeLivePriceBar(
    livePriceState: TradeLivePriceState,
) {
    val livePrice = livePriceState.price
    val progressTarget = if (livePriceState.isFetching || livePriceState.buyRemainingSeconds <= 0 || livePriceState.buyRefreshWindowSeconds <= 0) {
        0f
    } else {
        (livePriceState.buyRemainingSeconds.toFloat() / livePriceState.buyRefreshWindowSeconds.toFloat()).coerceIn(0f, 1f)
    }
    val animatedProgress by animateFloatAsState(
        targetValue = progressTarget,
        animationSpec = tween(durationMillis = 650),
        label = "buyLivePriceProgress",
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BuyGoldBackground)
            .drawBehind {
                if (!livePriceState.isFetching && animatedProgress > 0f) {
                    val progressWidth = size.width * animatedProgress.coerceIn(0f, 1f)
                    drawRect(
                        brush = Brush.horizontalGradient(
                            listOf(
                                BuyPrimaryLight.copy(alpha = 0.28f),
                                BuyPrimary.copy(alpha = 0.22f),
                                Color(0xFF5B1F8A).copy(alpha = 0.18f),
                            ),
                        ),
                        size = Size(progressWidth, size.height),
                    )
                }
            }
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = if (livePriceState.isFetching) Arrangement.Center else Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (livePriceState.isFetching || livePrice == null) {
            Text(
                text = stringResource(Res.string.trade_buy_updating_price),
                color = BuyPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
            )
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BuyLiveWaveIndicator(
                    tint = BuyPrimary,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${stringResource(Res.string.trade_buy_live_price_label).replace("Live ", "")}:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = BuySlate500,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "₹${formatMoney(livePrice.buy)}/g",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = BuySlate950,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(Res.string.trade_buy_plus_gst),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = BuyPrimary,
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${stringResource(Res.string.trade_buy_updates_in)} ",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = BuySlate500,
                )
                Text(
                    text = formatCountdown(livePriceState.buyRemainingSeconds),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = BuyPrimary,
                )
            }
        }
    }
}

@Composable
internal fun BuyLiveWaveIndicator(
    tint: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val center = center
        val dotRadius = 2.2.dp.toPx()
        val innerRadius = 5.2.dp.toPx()
        val outerRadius = 8.0.dp.toPx()
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round)

        drawCircle(
            color = tint,
            radius = dotRadius,
            center = center,
        )

        drawArc(
            color = tint.copy(alpha = 0.72f),
            startAngle = 135f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(center.x - innerRadius, center.y - innerRadius),
            size = Size(innerRadius * 2, innerRadius * 2),
            style = stroke,
        )
        drawArc(
            color = tint.copy(alpha = 0.72f),
            startAngle = -45f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(center.x - innerRadius, center.y - innerRadius),
            size = Size(innerRadius * 2, innerRadius * 2),
            style = stroke,
        )
        drawArc(
            color = tint.copy(alpha = 0.42f),
            startAngle = 135f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(center.x - outerRadius, center.y - outerRadius),
            size = Size(outerRadius * 2, outerRadius * 2),
            style = stroke,
        )
        drawArc(
            color = tint.copy(alpha = 0.42f),
            startAngle = -45f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(center.x - outerRadius, center.y - outerRadius),
            size = Size(outerRadius * 2, outerRadius * 2),
            style = stroke,
        )
    }
}

@Composable
internal fun TradeLoadingDots(
    dotColor: Color,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "tradeLoadingDots")
    val liftPx = with(LocalDensity.current) { 6.dp.toPx() }
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(3) { index ->
            val offset by transition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 560,
                        delayMillis = index * 140,
                        easing = FastOutSlowInEasing,
                    ),
                    repeatMode = RepeatMode.Reverse,
                ),
                label = "tradeLoadingDot$index",
            )
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .graphicsLayer {
                        translationY = -liftPx * offset
                        alpha = 0.45f + (0.55f * offset)
                    }
                    .clip(RoundedCornerShape(999.dp))
                    .background(dotColor),
            )
        }
    }
}
