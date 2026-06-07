package com.habit.gold.feature.trade.presentation.sell

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import com.habit.gold.core.designsystem.icons.HabitGoldIcons as Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.habit.gold.core.designsystem.HabitGoldPalette
import com.habit.gold.feature.trade.domain.TradeLivePriceState
import com.habit.gold.feature.trade.presentation.TradeMutedText
import com.habit.gold.feature.trade.presentation.TradePrimaryText
import com.habit.gold.feature.trade.presentation.TradeSectionBorder
import com.habit.gold.feature.trade.presentation.formatCountdown
import com.habit.gold.feature.trade.presentation.formatMoney
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.common_cancel
import habitgoldmobile.composeapp.generated.resources.common_confirm
import habitgoldmobile.composeapp.generated.resources.common_got_it
import habitgoldmobile.composeapp.generated.resources.trade_route_vpa_message
import habitgoldmobile.composeapp.generated.resources.trade_sell_balance_information
import habitgoldmobile.composeapp.generated.resources.trade_sell_confirm_amount
import habitgoldmobile.composeapp.generated.resources.trade_sell_confirm_order_body
import habitgoldmobile.composeapp.generated.resources.trade_sell_confirm_order_title
import habitgoldmobile.composeapp.generated.resources.trade_sell_confirm_quantity
import habitgoldmobile.composeapp.generated.resources.trade_sell_confirm_upi
import habitgoldmobile.composeapp.generated.resources.trade_sell_fetching_price
import habitgoldmobile.composeapp.generated.resources.trade_sell_live_price_label
import habitgoldmobile.composeapp.generated.resources.trade_sell_locked_gold_description
import habitgoldmobile.composeapp.generated.resources.trade_sell_locked_gold_title
import habitgoldmobile.composeapp.generated.resources.trade_sell_next_release_on
import habitgoldmobile.composeapp.generated.resources.trade_sell_no_upi_ids_available
import habitgoldmobile.composeapp.generated.resources.trade_sell_payout_body
import habitgoldmobile.composeapp.generated.resources.trade_sell_redeemable_gold_description
import habitgoldmobile.composeapp.generated.resources.trade_sell_redeemable_gold_title
import habitgoldmobile.composeapp.generated.resources.trade_sell_select_payout_upi
import habitgoldmobile.composeapp.generated.resources.trade_sell_summary_gold_price
import habitgoldmobile.composeapp.generated.resources.trade_sell_summary_gold_quantity
import habitgoldmobile.composeapp.generated.resources.trade_sell_swipe_to_sell_gold
import habitgoldmobile.composeapp.generated.resources.trade_sell_total_gold_balance_title
import habitgoldmobile.composeapp.generated.resources.trade_sell_total_gold_description
import habitgoldmobile.composeapp.generated.resources.trade_sell_updates_in
import habitgoldmobile.composeapp.generated.resources.trade_sell_upi_payout
import habitgoldmobile.composeapp.generated.resources.trade_sell_you_receive
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

@Composable
internal fun SellTradePayoutScreen(
    state: SellTradeState,
    livePriceState: TradeLivePriceState,
    onBackClick: () -> Unit,
    onIntent: (SellTradeIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val createdOrder = state.createdOrder
    val draftRequest = state.draftRequest
    val selectedVpa = state.userVpas.firstOrNull { it.id == state.selectedVpaId }
    val summaryPayoutAmount = createdOrder?.payoutAmount ?: draftRequest?.estimatedPayoutAmount ?: 0.0
    val summarySellPrice = createdOrder?.goldPricePerGram ?: livePriceState.price?.sell ?: 0.0
    val summaryGrams = createdOrder?.goldQuantityGrams ?: draftRequest?.grams ?: 0.0
    var showConfirmationDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    IconButton(
                        onClick = onBackClick,
                        enabled = !state.isLoading,
                        modifier = Modifier.align(Alignment.CenterStart),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = Color.Black,
                        )
                    }
                    Text(
                        text = stringResource(Res.string.trade_sell_upi_payout),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B),
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                SellTradeReceiveSummaryCard(
                    payoutAmount = summaryPayoutAmount,
                    sellPrice = summarySellPrice,
                    grams = summaryGrams,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        },
        bottomBar = {
            SellTradeSwipeFooter(
                livePriceState = livePriceState,
                enabled = draftRequest != null && selectedVpa != null && !state.isLoading,
                isLoading = state.isLoading,
                errorMessage = state.resolveErrorMessage(),
                onSwipeComplete = {
                    if (selectedVpa != null) {
                        showConfirmationDialog = true
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(Res.string.trade_sell_select_payout_upi),
                color = TradePrimaryText,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
            )

            if (state.userVpas.isEmpty()) {
                TradeOutcomeNoteCard(
                    title = stringResource(Res.string.trade_sell_no_upi_ids_available),
                    body = stringResource(Res.string.trade_route_vpa_message),
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    state.userVpas.forEach { vpa ->
                        SellTradeVpaCard(
                            vpa = vpa,
                            isSelected = vpa.id == state.selectedVpaId,
                            onClick = { onIntent(SellTradeIntent.SelectVpa(vpa.id)) },
                        )
                    }
                }
            }

            Text(
                text = stringResource(Res.string.trade_sell_payout_body),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                color = Color(0xFF94A3B8),
            )
        }
    }

    if (showConfirmationDialog && selectedVpa != null) {
        SellTradeConfirmationDialog(
            payoutAmount = summaryPayoutAmount,
            grams = summaryGrams,
            upiAddress = selectedVpa.address,
            onConfirm = {
                showConfirmationDialog = false
                onIntent(SellTradeIntent.ConfirmSell(selectedVpa.id))
            },
            onCancel = { showConfirmationDialog = false },
        )
    }
}

@Composable
internal fun SellTradeConfirmationDialog(
    payoutAmount: Double,
    grams: Double,
    upiAddress: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    Dialog(onDismissRequest = onCancel) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
            ) {
                Text(
                    text = stringResource(Res.string.trade_sell_confirm_order_title),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF020617),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(Res.string.trade_sell_confirm_order_body),
                    fontSize = 14.sp,
                    color = Color(0xFF64748B),
                )

                Spacer(modifier = Modifier.height(20.dp))

                SellTradeSummaryValueRow(
                    label = stringResource(Res.string.trade_sell_confirm_amount),
                    value = "₹${formatMoney(payoutAmount)}",
                )
                Spacer(modifier = Modifier.height(10.dp))
                SellTradeSummaryValueRow(
                    label = stringResource(Res.string.trade_sell_confirm_quantity),
                    value = "${formatGold(grams)} g",
                )
                Spacer(modifier = Modifier.height(10.dp))
                SellTradeSummaryValueRow(
                    label = stringResource(Res.string.trade_sell_confirm_upi),
                    value = upiAddress,
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Button(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF334155),
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    ) {
                        Text(
                            text = stringResource(Res.string.common_cancel),
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = HabitGoldPalette.plum),
                    ) {
                        Text(
                            text = stringResource(Res.string.common_confirm),
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun SellTradeBalanceInformationSheet(
    totalBalance: Double,
    sellableBalance: Double,
    lockedBalance: Double,
    sellPrice: Double,
    nextSellableAt: String?,
    onDismiss: () -> Unit,
) {
    val lockedValue = if (sellPrice > 0.0) lockedBalance * sellPrice else 0.0
    val totalValue = if (sellPrice > 0.0) totalBalance * sellPrice else 0.0
    val sellableValue = if (sellPrice > 0.0) sellableBalance * sellPrice else 0.0
    val nextReleaseLabel = nextSellableAt?.let(::formatNextSellableLabel)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 48.dp),
    ) {
        Text(
            text = stringResource(Res.string.trade_sell_balance_information),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B),
        )

        Spacer(modifier = Modifier.height(20.dp))
        SellTradeBalanceInfoItem(
            label = stringResource(Res.string.trade_sell_total_gold_balance_title),
            grams = totalBalance,
            value = totalValue,
            description = stringResource(Res.string.trade_sell_total_gold_description),
            icon = Icons.Default.AutoAwesome,
            iconColor = HabitGoldPalette.plum,
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF1F5F9))
        SellTradeBalanceInfoItem(
            label = stringResource(Res.string.trade_sell_redeemable_gold_title),
            grams = sellableBalance,
            value = sellableValue,
            description = stringResource(Res.string.trade_sell_redeemable_gold_description),
            icon = Icons.Default.Check,
            iconColor = Color(0xFF16A34A),
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF1F5F9))
        SellTradeBalanceInfoItem(
            label = stringResource(Res.string.trade_sell_locked_gold_title),
            grams = lockedBalance,
            value = lockedValue,
            description = stringResource(Res.string.trade_sell_locked_gold_description),
            icon = Icons.Default.Lock,
            iconColor = Color(0xFFF59E0B),
        )

        if (lockedBalance > 0.0 && nextReleaseLabel != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                color = Color(0xFFFFF7ED),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = Color(0xFFEA580C),
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = stringResource(Res.string.trade_sell_next_release_on, nextReleaseLabel),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF9A3412),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onDismiss,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = HabitGoldPalette.plum),
        ) {
            Text(
                text = stringResource(Res.string.common_got_it),
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
        }
    }
}

@Composable
private fun SellTradeBalanceInfoItem(
    label: String,
    grams: Double,
    value: Double,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(iconColor.copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(18.dp),
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B),
            )
            Text(
                text = "${formatGold(grams)} g • ₹${formatMoney(value)}",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF475569),
            )
            Text(
                text = description,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = Color(0xFF64748B),
            )
        }
    }
}

@Composable
internal fun SellTradeSummaryValueRow(
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
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = TradeMutedText,
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF020617),
        )
    }
}

@Composable
internal fun SellTradeSwipeFooter(
    livePriceState: TradeLivePriceState,
    enabled: Boolean,
    isLoading: Boolean,
    errorMessage: String?,
    onSwipeComplete: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White),
    ) {
        HorizontalDivider(color = TradeSectionBorder)
        errorMessage?.takeIf { it.isNotBlank() }?.let { message ->
            Text(
                text = message,
                color = Color(0xFFDC2626),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 10.dp),
            )
            HorizontalDivider(color = TradeSectionBorder)
        }
        SellTradeLivePriceBar(livePriceState = livePriceState)
        HorizontalDivider(color = TradeSectionBorder)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .navigationBarsPadding()
                .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 24.dp),
        ) {
            SellTradeSwipeButton(
                onSwipeComplete = onSwipeComplete,
                isLoading = isLoading,
                enabled = enabled,
            )
        }
    }
}

@Composable
private fun SellTradeSwipeButton(
    onSwipeComplete: () -> Unit,
    isLoading: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    var width by remember { mutableStateOf(0f) }
    var handleWidth by remember { mutableStateOf(0f) }
    val swipeableWidth = remember(width, handleWidth) { width - handleWidth - 12f }
    var offsetX by remember { mutableStateOf(0f) }

    LaunchedEffect(isLoading) {
        if (!isLoading) offsetX = 0f
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (enabled) HabitGoldPalette.plum else Color(0xFFCBD5E1))
            .onSizeChanged { width = it.width.toFloat() },
    ) {
        Text(
            text = stringResource(Res.string.trade_sell_swipe_to_sell_gold),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 56.dp),
            textAlign = TextAlign.Center,
        )

        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .padding(6.dp)
                .fillMaxHeight()
                .width(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.25f))
                .draggable(
                    orientation = Orientation.Horizontal,
                    enabled = enabled && !isLoading,
                    state = rememberDraggableState { delta ->
                        offsetX = (offsetX + delta).coerceIn(0f, swipeableWidth.coerceAtLeast(0f))
                    },
                    onDragStopped = {
                        if (offsetX >= swipeableWidth * 0.85f) {
                            offsetX = 0f
                            onSwipeComplete()
                        } else {
                            offsetX = 0f
                        }
                    },
                )
                .onSizeChanged { handleWidth = it.width.toFloat() },
            contentAlignment = Alignment.Center,
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.5.dp,
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy((-16).dp),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(26.dp),
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(26.dp),
                    )
                }
            }
        }
    }
}

@Composable
internal fun SellTradeActionFooter(
    livePriceState: TradeLivePriceState,
    label: String,
    enabled: Boolean,
    isLoading: Boolean,
    errorMessage: String?,
    onActionClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White),
    ) {
        HorizontalDivider(color = TradeSectionBorder)
        errorMessage?.takeIf { it.isNotBlank() }?.let { message ->
            Text(
                text = message,
                color = Color(0xFFDC2626),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 10.dp),
            )
            HorizontalDivider(color = TradeSectionBorder)
        }
        SellTradeLivePriceBar(livePriceState = livePriceState)
        HorizontalDivider(color = TradeSectionBorder)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .navigationBarsPadding()
                .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 24.dp),
        ) {
            Button(
                onClick = onActionClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (enabled) HabitGoldPalette.plum else Color(0xFFCBD5E1),
                    disabledContainerColor = Color(0xFFCBD5E1),
                ),
                enabled = enabled && !isLoading,
            ) {
                Text(
                    text = label,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }
        }
    }
}

@Composable
internal fun SellTradeLivePriceBar(
    livePriceState: TradeLivePriceState,
) {
    val livePrice = livePriceState.price
    val progressTarget = if (livePriceState.isFetching || livePriceState.sellRemainingSeconds <= 0 || livePriceState.sellRefreshWindowSeconds <= 0) {
        0f
    } else {
        (livePriceState.sellRemainingSeconds.toFloat() / livePriceState.sellRefreshWindowSeconds.toFloat()).coerceIn(0f, 1f)
    }
    val animatedProgress by animateFloatAsState(
        targetValue = progressTarget,
        animationSpec = tween(durationMillis = 650),
        label = "sellLivePriceProgress",
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF6EDFF))
            .drawBehind {
                if (!livePriceState.isFetching && animatedProgress > 0f) {
                    val progressWidth = size.width * animatedProgress.coerceIn(0f, 1f)
                    drawRect(
                        brush = Brush.horizontalGradient(
                            listOf(
                                HabitGoldPalette.plum.copy(alpha = 0.18f),
                                HabitGoldPalette.plum.copy(alpha = 0.12f),
                                HabitGoldPalette.plum.copy(alpha = 0.08f),
                            ),
                        ),
                        size = androidx.compose.ui.geometry.Size(progressWidth, size.height),
                    )
                }
            }
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = if (livePriceState.isFetching) Arrangement.Center else Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (livePriceState.isFetching || livePrice == null) {
            Text(
                text = stringResource(Res.string.trade_sell_fetching_price),
                color = HabitGoldPalette.plum,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
            )
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SellLiveWaveIndicator(
                    tint = HabitGoldPalette.plum,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${stringResource(Res.string.trade_sell_live_price_label).replace("Live ", "")}:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B),
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "₹${formatMoney(livePrice.sell)}/g",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF020617),
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${stringResource(Res.string.trade_sell_updates_in)} ",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF64748B),
                )
                Text(
                    text = formatCountdown(livePriceState.sellRemainingSeconds),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = HabitGoldPalette.plum,
                )
            }
        }
    }
}

@Composable
private fun SellLiveWaveIndicator(
    tint: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val center = center
        val dotRadius = 2.2.dp.toPx()
        val innerRadius = 5.2.dp.toPx()
        val outerRadius = 8.0.dp.toPx()
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round)

        drawCircle(color = tint, radius = dotRadius, center = center)
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
private fun SellTradeReceiveSummaryCard(
    payoutAmount: Double,
    sellPrice: Double,
    grams: Double,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFFCFAFF), RoundedCornerShape(18.dp))
            .border(1.dp, Color(0xFFE9D5FF), RoundedCornerShape(18.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = stringResource(Res.string.trade_sell_you_receive),
            color = TradeMutedText,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
        )
        Text(
            text = "₹${formatMoney(payoutAmount)}",
            color = Color(0xFF020617),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(10.dp),
            )
            Text(
                text = "Adjusted according to proper gold quantity.",
                color = Color(0xFF94A3B8),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        SellTradeSummaryValueRow(
            label = stringResource(Res.string.trade_sell_summary_gold_price),
            value = if (sellPrice > 0.0) "₹${formatMoney(sellPrice)}/g" else "Unavailable",
        )
        SellTradeSummaryValueRow(
            label = stringResource(Res.string.trade_sell_summary_gold_quantity),
            value = "${formatGold(grams)} g",
        )
    }
}
