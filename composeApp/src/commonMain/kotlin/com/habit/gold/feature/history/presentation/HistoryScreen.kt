package com.habit.gold.feature.history.presentation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habit.gold.core.designsystem.HabitGoldPullToRefreshIndicator
import com.habit.gold.core.designsystem.HabitGoldPalette
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.common_remove_filter
import habitgoldmobile.composeapp.generated.resources.common_retry
import habitgoldmobile.composeapp.generated.resources.history_screen_filter_all
import habitgoldmobile.composeapp.generated.resources.history_screen_filter_buy_gold
import habitgoldmobile.composeapp.generated.resources.history_screen_filter_delivery
import habitgoldmobile.composeapp.generated.resources.history_screen_filter_sell_gold
import habitgoldmobile.composeapp.generated.resources.history_screen_empty_all_default
import habitgoldmobile.composeapp.generated.resources.history_screen_empty_delivery_default
import habitgoldmobile.composeapp.generated.resources.history_screen_empty_failed
import habitgoldmobile.composeapp.generated.resources.history_screen_empty_pending
import habitgoldmobile.composeapp.generated.resources.history_screen_empty_refund
import habitgoldmobile.composeapp.generated.resources.history_screen_empty_sell_default
import habitgoldmobile.composeapp.generated.resources.history_screen_empty_success
import habitgoldmobile.composeapp.generated.resources.history_screen_empty_trade_default
import habitgoldmobile.composeapp.generated.resources.history_screen_recent_transactions
import habitgoldmobile.composeapp.generated.resources.history_screen_status
import habitgoldmobile.composeapp.generated.resources.history_screen_status_failed
import habitgoldmobile.composeapp.generated.resources.history_screen_status_filter_content_description
import habitgoldmobile.composeapp.generated.resources.history_screen_status_filter_title
import habitgoldmobile.composeapp.generated.resources.history_screen_status_in_progress
import habitgoldmobile.composeapp.generated.resources.history_screen_status_refund
import habitgoldmobile.composeapp.generated.resources.history_screen_status_success
import habitgoldmobile.composeapp.generated.resources.history_screen_title_gold_delivery
import habitgoldmobile.composeapp.generated.resources.history_screen_title_gold_purchase
import habitgoldmobile.composeapp.generated.resources.history_screen_title_gold_sale
import habitgoldmobile.composeapp.generated.resources.history_screen_title_gold_savings
import habitgoldmobile.composeapp.generated.resources.history_screen_title_reward
import habitgoldmobile.composeapp.generated.resources.history_screen_title_transaction
import habitgoldmobile.composeapp.generated.resources.history_screen_transactions
import habitgoldmobile.composeapp.generated.resources.ic_buy_gold_icon
import habitgoldmobile.composeapp.generated.resources.ic_delivery_gold_icon
import habitgoldmobile.composeapp.generated.resources.ic_sell_gold_icon
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private val HistoryBackground = Color.White
private val HistoryToolbarBorder = Color(0x0D000000)
private val HistoryMutedText = Color(0xFF6B7280)
private val HistorySectionBorder = Color(0xFFE2E8F0)
private val HistoryPrimary = HabitGoldPalette.plum
private val HistorySuccess = Color(0xFF15803D)
private val HistorySuccessBg = Color(0xFFF0FDF4)
private val HistoryRefund = Color(0xFF2563EB)
private val HistoryRefundBg = Color(0xFFEFF6FF)
private val HistoryPending = Color(0xFFD97706)
private val HistoryPendingBg = Color(0xFFFFF7ED)
private val HistoryFailure = Color(0xFFB91C1C)
private val HistoryFailureBg = Color(0xFFFEF2F2)
private val HistoryNeutralBg = Color(0xFFF8FAFC)
private val HistoryNeutralText = Color(0xFF475569)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    state: HistoryState,
    onIntent: (HistoryIntent) -> Unit,
    onTransactionClick: (HistoryTransactionItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isStatusSheetVisible by remember { mutableStateOf(false) }
    val statusSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val pullToRefreshState = rememberPullToRefreshState()
    val listState = rememberLazyListState()

    val shouldLoadMore = remember(
        listState,
        state.visibleTransactions.size,
        state.hasMore,
        state.isPaginating,
    ) {
        androidx.compose.runtime.derivedStateOf {
            val lastVisibleItemIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            val lastTransactionRowIndex = if (state.visibleTransactions.isNotEmpty()) state.visibleTransactions.size else 0
            lastVisibleItemIndex >= lastTransactionRowIndex - 4 && state.hasMore && !state.isPaginating
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value) {
            onIntent(HistoryIntent.LoadNextPage)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(HistoryBackground),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .statusBarsPadding()
                .drawBehind {
                    val strokeWidth = 1.dp.toPx()
                    drawLine(
                        color = HistoryToolbarBorder,
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = strokeWidth,
                    )
                },
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.history_screen_transactions),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                )

                Spacer(modifier = Modifier.weight(1f))

                Surface(
                    onClick = { isStatusSheetVisible = true },
                    shape = RoundedCornerShape(14.dp),
                    color = if (state.selectedStatusFilter == null) Color.White else HistoryPrimary.copy(alpha = 0.08f),
                    border = BorderStroke(
                        1.dp,
                        if (state.selectedStatusFilter == null) HistorySectionBorder else HistoryPrimary.copy(alpha = 0.18f),
                    ),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = stringResource(Res.string.history_screen_status_filter_content_description),
                            tint = if (state.selectedStatusFilter == null) HistoryNeutralText else HistoryPrimary,
                            modifier = Modifier.size(14.dp),
                        )
                        Text(
                            text = state.selectedStatusFilter?.displayLabel() ?: stringResource(Res.string.history_screen_status),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (state.selectedStatusFilter == null) HistoryNeutralText else HistoryPrimary,
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                HistoryTypeChip(
                    label = stringResource(Res.string.history_screen_filter_all),
                    selected = state.selectedTypeFilter == HistoryTypeFilter.All,
                    onClick = { onIntent(HistoryIntent.SelectTypeFilter(HistoryTypeFilter.All)) },
                )
                HistoryTypeChip(
                    label = stringResource(Res.string.history_screen_filter_buy_gold),
                    selected = state.selectedTypeFilter == HistoryTypeFilter.BuyGold,
                    onClick = { onIntent(HistoryIntent.SelectTypeFilter(HistoryTypeFilter.BuyGold)) },
                )
                HistoryTypeChip(
                    label = stringResource(Res.string.history_screen_filter_sell_gold),
                    selected = state.selectedTypeFilter == HistoryTypeFilter.SellGold,
                    onClick = { onIntent(HistoryIntent.SelectTypeFilter(HistoryTypeFilter.SellGold)) },
                )
                HistoryTypeChip(
                    label = stringResource(Res.string.history_screen_filter_delivery),
                    selected = state.selectedTypeFilter == HistoryTypeFilter.Delivery,
                    onClick = { onIntent(HistoryIntent.SelectTypeFilter(HistoryTypeFilter.Delivery)) },
                )
            }
        }

        if (isStatusSheetVisible) {
            HistoryStatusFilterSheet(
                selectedStatusFilter = state.selectedStatusFilter,
                sheetState = statusSheetState,
                onDismiss = { isStatusSheetVisible = false },
                onSelect = { filter ->
                    onIntent(HistoryIntent.SelectStatusFilter(filter))
                    isStatusSheetVisible = false
                },
                onClearFilter = {
                    onIntent(HistoryIntent.ClearStatusFilter)
                    isStatusSheetVisible = false
                },
            )
        }

        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { onIntent(HistoryIntent.Refresh) },
            state = pullToRefreshState,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            indicator = {
                HabitGoldPullToRefreshIndicator(
                    isRefreshing = state.isRefreshing,
                    state = pullToRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter),
                )
            },
        ) {
            when {
                state.isLoading && state.transactions.isEmpty() -> {
                    HistoryLoadingContent(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                    )
                }

                !state.errorMessage.isNullOrBlank() && state.transactions.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = state.errorMessage,
                            color = HistoryFailure,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { onIntent(HistoryIntent.Refresh) },
                            colors = ButtonDefaults.buttonColors(containerColor = HistoryPrimary),
                        ) {
                            Text(text = stringResource(Res.string.common_retry))
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        if (state.visibleTransactions.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = emptyStateMessage(
                                            typeFilter = state.selectedTypeFilter,
                                            statusFilter = state.selectedStatusFilter,
                                        ),
                                        color = HistoryMutedText,
                                        textAlign = TextAlign.Center,
                                    )
                                }
                            }
                        } else {
                            /*item {
                                Text(
                                    text = stringResource(Res.string.history_screen_recent_transactions),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HistoryMutedText,
                                    modifier = Modifier.padding(bottom = 8.dp),
                                )
                            }*/

                            items(
                                items = state.visibleTransactions,
                                key = { it.id },
                            ) { transaction ->
                                HistoryTransactionRow(
                                    transaction = transaction,
                                    onClick = { onTransactionClick(transaction) },
                                )
                                HorizontalDivider(color = Color(0xFFF1F5F9))
                            }

                            if (state.isPaginating) {
                                item {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp),
                                    ) {
                                        repeat(2) {
                                            HistoryTransactionRowPlaceholder()
                                            HorizontalDivider(color = Color(0xFFF1F5F9))
                                        }
                                    }
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
private fun HistoryTypeChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .height(32.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        shape = RoundedCornerShape(20.dp),
        color = if (selected) HistoryPrimary else Color.White,
        border = if (selected) null else BorderStroke(1.dp, Color(0x4D94A3B8)),
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = if (selected) Color.White else Color.Black,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistoryStatusFilterSheet(
    selectedStatusFilter: HistoryStatusFilter?,
    sheetState: androidx.compose.material3.SheetState,
    onDismiss: () -> Unit,
    onSelect: (HistoryStatusFilter) -> Unit,
    onClearFilter: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        contentColor = Color.Black,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.history_screen_status_filter_title),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                )

                if (selectedStatusFilter != null) {
                    androidx.compose.material3.TextButton(onClick = onClearFilter) {
                        Text(
                            text = stringResource(Res.string.common_remove_filter),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = HistoryPrimary,
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            HistoryStatusFilter.entries.forEachIndexed { index, option ->
                Surface(
                    onClick = { onSelect(option) },
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    border = BorderStroke(
                        1.dp,
                        if (option == selectedStatusFilter) HistoryPrimary.copy(alpha = 0.18f) else HistorySectionBorder,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        RadioButton(
                            selected = option == selectedStatusFilter,
                            onClick = null,
                            colors = RadioButtonDefaults.colors(
                                selectedColor = HistoryPrimary,
                                unselectedColor = Color(0xFF94A3B8),
                            ),
                        )
                        Text(
                            text = option.displayLabel(),
                            modifier = Modifier.weight(1f),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF0F172A),
                        )
                    }
                }

                if (index != HistoryStatusFilter.entries.lastIndex) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun HistoryLoadingContent(
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            HistoryShimmerBar(
                widthFraction = 0.42f,
                height = 14.dp,
                shape = RoundedCornerShape(999.dp),
            )
        }
        items(6) {
            HistoryTransactionRowPlaceholder()
            HorizontalDivider(color = Color(0xFFF1F5F9))
        }
    }
}

@Composable
private fun HistoryTransactionRowPlaceholder() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.Top,
        ) {
            HistoryShimmerBox(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                HistoryShimmerBar(widthFraction = 0.56f, height = 16.dp, shape = RoundedCornerShape(999.dp))
                Spacer(modifier = Modifier.height(8.dp))
                HistoryShimmerBar(widthFraction = 0.72f, height = 12.dp, shape = RoundedCornerShape(999.dp))
                Spacer(modifier = Modifier.height(8.dp))
                HistoryShimmerBox(
                    modifier = Modifier
                        .width(72.dp)
                        .height(24.dp)
                        .clip(RoundedCornerShape(999.dp)),
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(horizontalAlignment = Alignment.End) {
            HistoryShimmerBox(
                modifier = Modifier
                    .width(88.dp)
                    .height(16.dp)
                    .clip(RoundedCornerShape(999.dp)),
            )
            Spacer(modifier = Modifier.height(8.dp))
            HistoryShimmerBox(
                modifier = Modifier
                    .width(64.dp)
                    .height(12.dp)
                    .clip(RoundedCornerShape(999.dp)),
            )
        }
    }
}

@Composable
private fun HistoryTransactionRow(
    transaction: HistoryTransactionItem,
    onClick: () -> Unit,
) {
    val statusLabel = remember(transaction.rawStatus) { transaction.rawStatus.toHistoryStatusLabel() }
    val statusKind = remember(statusLabel) { statusLabel.toHistoryStatusKind() }
    val statusColor = when (statusKind) {
        HistoryTransactionStatusKind.Success -> HistorySuccess
        HistoryTransactionStatusKind.Refund -> HistoryRefund
        HistoryTransactionStatusKind.Pending -> HistoryPending
        HistoryTransactionStatusKind.Failure -> HistoryFailure
        HistoryTransactionStatusKind.Neutral -> HistoryNeutralText
    }
    val statusContainerColor = when (statusKind) {
        HistoryTransactionStatusKind.Success -> HistorySuccessBg
        HistoryTransactionStatusKind.Refund -> HistoryRefundBg
        HistoryTransactionStatusKind.Pending -> HistoryPendingBg
        HistoryTransactionStatusKind.Failure -> HistoryFailureBg
        HistoryTransactionStatusKind.Neutral -> HistoryNeutralBg
    }
    val amountColor = when (statusKind) {
        HistoryTransactionStatusKind.Failure -> HistorySuccess
        HistoryTransactionStatusKind.Pending -> Color.Black
        HistoryTransactionStatusKind.Refund -> Color.Black
        else -> HistorySuccess
    }
    val weightColor = when (statusKind) {
        HistoryTransactionStatusKind.Failure -> HistorySuccess
        HistoryTransactionStatusKind.Pending -> Color.Black
        HistoryTransactionStatusKind.Refund -> Color.Black
        else -> HistorySuccess
    }
    val sipBadgeLabel = remember(transaction.sipFrequency) {
        transaction.sipFrequency
            ?.lowercase()
            ?.replaceFirstChar { it.titlecase() }
            ?.let { "$it SIP" }
            ?.takeIf { it.isNotBlank() }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Image(
                painter = painterResource(historyTypeIcon(transaction.type)),
                contentDescription = null,
                modifier = Modifier.size(40.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = transaction.displayTitle(),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                )
                Text(
                    text = transaction.dateLabel,
                    fontSize = 12.sp,
                    color = HistoryMutedText,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = statusContainerColor,
                        shape = RoundedCornerShape(999.dp),
                        border = BorderStroke(1.dp, HistorySectionBorder.copy(alpha = 0.85f)),
                    ) {
                        Text(
                            text = statusLabel,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = statusColor,
                        )
                    }
                    if (transaction.isSip && !sipBadgeLabel.isNullOrBlank()) {
                        Surface(
                            color = HistoryPrimary.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(999.dp),
                            border = BorderStroke(1.dp, HistoryPrimary.copy(alpha = 0.18f)),
                        ) {
                            Text(
                                text = sipBadgeLabel,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = HistoryPrimary,
                            )
                        }
                    }
                }
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = transaction.amountLabel,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = amountColor,
            )
            Text(
                text = transaction.weightLabel,
                fontSize = 12.sp,
                color = weightColor,
            )
        }
    }
}

@Composable
private fun HistoryShimmerBar(
    widthFraction: Float,
    height: androidx.compose.ui.unit.Dp,
    shape: RoundedCornerShape,
) {
    HistoryShimmerBox(
        modifier = Modifier
            .fillMaxWidth(widthFraction)
            .height(height)
            .clip(shape),
    )
}

@Composable
private fun HistoryShimmerBox(
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "history-shimmer")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(durationMillis = 1200, easing = LinearEasing)),
        label = "history-shimmer-progress",
    )
    val shimmerBrush = Brush.linearGradient(
        colors = listOf(Color(0xFFE5EAF2), Color(0xFFF5F7FB), Color(0xFFE5EAF2)),
        start = Offset(x = -400f + progress * 800f, y = 0f),
        end = Offset(x = progress * 800f, y = 0f),
    )
    Box(
        modifier = modifier.background(shimmerBrush),
    )
}

@Composable
private fun HistoryStatusFilter.displayLabel(): String {
    return when (this) {
        HistoryStatusFilter.Success -> stringResource(Res.string.history_screen_status_success)
        HistoryStatusFilter.Refund -> stringResource(Res.string.history_screen_status_refund)
        HistoryStatusFilter.Failure -> stringResource(Res.string.history_screen_status_failed)
        HistoryStatusFilter.Pending -> stringResource(Res.string.history_screen_status_in_progress)
    }
}

private fun historyTypeIcon(type: HistoryTransactionType): DrawableResource {
    return when (type) {
        HistoryTransactionType.Buy -> Res.drawable.ic_buy_gold_icon
        HistoryTransactionType.Sell -> Res.drawable.ic_sell_gold_icon
        HistoryTransactionType.Delivery -> Res.drawable.ic_delivery_gold_icon
        HistoryTransactionType.Reward,
        HistoryTransactionType.Other -> Res.drawable.ic_buy_gold_icon
    }
}

private fun historyIconBackground(type: HistoryTransactionType): Color {
    return when (type) {
        HistoryTransactionType.Buy -> Color(0xFFF0FDF4)
        HistoryTransactionType.Sell -> Color(0xFFFEF2F2)
        HistoryTransactionType.Delivery -> Color(0xFFEFF6FF)
        HistoryTransactionType.Reward,
        HistoryTransactionType.Other -> Color(0xFFF3E8FF)
    }
}

@Composable
private fun emptyStateMessage(
    typeFilter: HistoryTypeFilter,
    statusFilter: HistoryStatusFilter?,
): String {
    return if (typeFilter == HistoryTypeFilter.Delivery) {
        when (statusFilter) {
            null -> stringResource(Res.string.history_screen_empty_delivery_default)
            HistoryStatusFilter.Success -> stringResource(Res.string.history_screen_empty_success)
            HistoryStatusFilter.Refund -> stringResource(Res.string.history_screen_empty_refund)
            HistoryStatusFilter.Failure -> stringResource(Res.string.history_screen_empty_failed)
            HistoryStatusFilter.Pending -> stringResource(Res.string.history_screen_empty_pending)
        }
    } else {
        when (statusFilter) {
            null -> when (typeFilter) {
                HistoryTypeFilter.SellGold -> stringResource(Res.string.history_screen_empty_sell_default)
                HistoryTypeFilter.All -> stringResource(Res.string.history_screen_empty_all_default)
                else -> stringResource(Res.string.history_screen_empty_trade_default)
            }
            HistoryStatusFilter.Success -> stringResource(Res.string.history_screen_empty_success)
            HistoryStatusFilter.Refund -> stringResource(Res.string.history_screen_empty_refund)
            HistoryStatusFilter.Failure -> stringResource(Res.string.history_screen_empty_failed)
            HistoryStatusFilter.Pending -> stringResource(Res.string.history_screen_empty_pending)
        }
    }
}

@Composable
private fun HistoryTransactionItem.displayTitle(): String {
    return when {
        isSip -> sipName ?: stringResource(Res.string.history_screen_title_gold_savings)
        type == HistoryTransactionType.Buy -> stringResource(Res.string.history_screen_title_gold_purchase)
        type == HistoryTransactionType.Sell -> stringResource(Res.string.history_screen_title_gold_sale)
        type == HistoryTransactionType.Delivery -> stringResource(Res.string.history_screen_title_gold_delivery)
        type == HistoryTransactionType.Reward -> stringResource(Res.string.history_screen_title_reward)
        else -> stringResource(Res.string.history_screen_title_transaction)
    }
}
