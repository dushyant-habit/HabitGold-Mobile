package com.habit.gold.feature.trade.presentation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habit.gold.core.designsystem.AppPrimaryButton
import com.habit.gold.core.network.ApiResult
import com.habit.gold.feature.trade.domain.model.TradeTransactionPreview
import com.habit.gold.feature.trade.domain.usecase.GetTradeInvoiceUseCase
import com.habit.gold.feature.trade.domain.usecase.GetTradeTransactionsUseCase
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.common_gold_unit_short
import habitgoldmobile.composeapp.generated.resources.common_retry
import habitgoldmobile.composeapp.generated.resources.history_screen_title_gold_delivery
import habitgoldmobile.composeapp.generated.resources.history_screen_title_gold_purchase
import habitgoldmobile.composeapp.generated.resources.history_screen_title_gold_sale
import habitgoldmobile.composeapp.generated.resources.history_screen_title_gold_savings
import habitgoldmobile.composeapp.generated.resources.history_screen_title_transaction
import habitgoldmobile.composeapp.generated.resources.ic_buy_gold_icon
import habitgoldmobile.composeapp.generated.resources.ic_delivery_gold_icon
import habitgoldmobile.composeapp.generated.resources.ic_sell_gold_icon
import habitgoldmobile.composeapp.generated.resources.trade_transaction_details_amount
import habitgoldmobile.composeapp.generated.resources.trade_transaction_details_gold_price
import habitgoldmobile.composeapp.generated.resources.trade_transaction_details_gold_quantity
import habitgoldmobile.composeapp.generated.resources.trade_transaction_details_gst
import habitgoldmobile.composeapp.generated.resources.trade_transaction_details_load_failed
import habitgoldmobile.composeapp.generated.resources.trade_transaction_details_net_amount
import habitgoldmobile.composeapp.generated.resources.trade_transaction_details_not_found
import habitgoldmobile.composeapp.generated.resources.trade_transaction_details_order_reference_heading
import habitgoldmobile.composeapp.generated.resources.trade_transaction_details_order_title
import habitgoldmobile.composeapp.generated.resources.trade_transaction_details_summary_heading
import habitgoldmobile.composeapp.generated.resources.trade_transaction_details_view_invoice
import habitgoldmobile.composeapp.generated.resources.trade_invoice_viewer_invalid_url
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun TradeTransactionDetailsScreen(
    transactionId: String,
    getTradeTransactionsUseCase: GetTradeTransactionsUseCase,
    getTradeInvoiceUseCase: GetTradeInvoiceUseCase,
    onBackClick: () -> Unit,
    onOpenInvoice: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val transactionNotFoundMessage = stringResource(Res.string.trade_transaction_details_not_found)
    val transactionLoadFailedMessage = stringResource(Res.string.trade_transaction_details_load_failed)
    val invalidInvoiceMessage = stringResource(Res.string.trade_invoice_viewer_invalid_url)
    var reloadToken by remember(transactionId) { mutableStateOf(0) }
    var screenState by remember(transactionId) {
        mutableStateOf<TradeTransactionDetailsUiState>(TradeTransactionDetailsUiState.Loading)
    }
    var isFetchingInvoice by remember(transactionId) { mutableStateOf(false) }
    var invoiceErrorMessage by remember(transactionId) { mutableStateOf<String?>(null) }

    suspend fun loadTransaction() {
        screenState = TradeTransactionDetailsUiState.Loading
        val loadedTransaction = findTradeTransaction(transactionId, getTradeTransactionsUseCase)
        screenState = when (loadedTransaction) {
            is TradeTransactionLookupResult.Found -> TradeTransactionDetailsUiState.Content(loadedTransaction.transaction)
            TradeTransactionLookupResult.NotFound -> TradeTransactionDetailsUiState.Error(transactionNotFoundMessage)
            is TradeTransactionLookupResult.Failure -> TradeTransactionDetailsUiState.Error(
                loadedTransaction.message.ifBlank { transactionLoadFailedMessage },
            )
        }
    }

    LaunchedEffect(transactionId, reloadToken) {
        loadTransaction()
    }

    TradeChildScaffold(
        title = stringResource(Res.string.trade_transaction_details_order_title),
        onBackClick = onBackClick,
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            when (val state = screenState) {
                TradeTransactionDetailsUiState.Loading -> {
                    TradeTransactionDetailsShimmer(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                    )
                }

                is TradeTransactionDetailsUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = state.message,
                            color = TradeMutedText,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                        )
                        AppPrimaryButton(
                            label = stringResource(Res.string.common_retry),
                            onClick = { reloadToken += 1 },
                            modifier = Modifier.padding(top = 16.dp),
                        )
                    }
                }

                is TradeTransactionDetailsUiState.Content -> {
                    val transaction = state.transaction
                    val visual = tradeTransactionVisual(transaction)

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, TradeSectionBorder),
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Image(
                                            painter = painterResource(visual.icon),
                                            contentDescription = null,
                                            modifier = Modifier.size(40.dp),
                                        )

                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(2.dp),
                                        ) {
                                            Text(
                                                text = visual.title,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TradePrimaryText,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                            )
                                            Text(
                                                text = formatTradeTransactionDateTime(transaction.createdAt),
                                                fontSize = 11.sp,
                                                color = TradeMutedText,
                                            )
                                        }
                                    }

                                    Column(
                                        horizontalAlignment = Alignment.End,
                                        verticalArrangement = Arrangement.spacedBy(3.dp),
                                    ) {
                                        Text(
                                            text = formatTradeCurrency(transaction.amount),
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = visual.amountTint,
                                            textAlign = TextAlign.End,
                                        )
                                        Text(
                                            text = formatTradeQuantity(transaction.goldQuantity),
                                            fontSize = 11.sp,
                                            color = TradeMutedText,
                                            fontWeight = FontWeight.Medium,
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    TradeStatusChip(
                                        label = visual.statusLabel,
                                        background = visual.statusBackground,
                                        tint = visual.statusTint,
                                    )
                                    if (transaction.isSip && !transaction.sipFrequency.isNullOrBlank()) {
                                        TradeStatusChip(
                                            label = "${transaction.sipFrequency.replaceFirstChar { it.uppercase() }} SIP",
                                            background = Color(0xFFF3E8FF),
                                            tint = Color(0xFF7E22CE),
                                        )
                                    }
                                }
                            }
                        }

                        TradeSectionCard(
                            title = stringResource(Res.string.trade_transaction_details_summary_heading),
                        ) {
                            TradeReferenceBlock(
                                label = stringResource(Res.string.trade_transaction_details_order_reference_heading),
                                value = transaction.id,
                            )
                            TradeSummaryRow(
                                label = stringResource(Res.string.trade_transaction_details_amount),
                                value = formatTradeCurrency(transaction.amount),
                                emphasize = true,
                                valueColor = visual.amountTint,
                            )
                            TradeSummaryRow(
                                label = stringResource(Res.string.trade_transaction_details_net_amount),
                                value = formatTradeCurrency(transaction.netAmount),
                            )
                            TradeSummaryRow(
                                label = stringResource(Res.string.trade_transaction_details_gst),
                                value = formatTradeCurrency(transaction.gstAmount),
                            )
                            TradeSummaryRow(
                                label = stringResource(Res.string.trade_transaction_details_gold_quantity),
                                value = formatTradeQuantity(transaction.goldQuantity),
                            )
                            TradeSummaryRow(
                                label = stringResource(Res.string.trade_transaction_details_gold_price),
                                value = formatTradeGoldPrice(transaction.goldPrice),
                                showDivider = false,
                            )
                        }

                        invoiceErrorMessage?.takeIf { it.isNotBlank() }?.let {
                            Text(
                                text = it,
                                color = Color(0xFFB42318),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                            )
                        }

                        if (canShowTradeInvoice(transaction)) {
                            AppPrimaryButton(
                                label = stringResource(Res.string.trade_transaction_details_view_invoice),
                                onClick = {
                                    if (isFetchingInvoice) return@AppPrimaryButton
                                    isFetchingInvoice = true
                                },
                                isLoading = isFetchingInvoice,
                            )
                        }
                    }

                    LaunchedEffect(isFetchingInvoice) {
                        if (!isFetchingInvoice) return@LaunchedEffect
                        when (val result = getTradeInvoiceUseCase(transaction.id)) {
                            is ApiResult.Success -> {
                                isFetchingInvoice = false
                                val invoiceUrl = result.value.invoiceUrl
                                if (invoiceUrl.isBlank()) {
                                    invoiceErrorMessage = invalidInvoiceMessage
                                } else {
                                    invoiceErrorMessage = null
                                    onOpenInvoice(invoiceUrl)
                                }
                            }

                            is ApiResult.Failure -> {
                                invoiceErrorMessage = result.error.message
                                isFetchingInvoice = false
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TradeTransactionDetailsShimmer(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, TradeSectionBorder),
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TradeShimmerBox(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp)),
                    )

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        TradeShimmerBar(
                            widthFraction = 0.62f,
                            height = 16.dp,
                        )
                        TradeShimmerBar(
                            widthFraction = 0.48f,
                            height = 11.dp,
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        TradeShimmerFixedBar(
                            width = 92.dp,
                            height = 18.dp,
                        )
                        TradeShimmerFixedBar(
                            width = 64.dp,
                            height = 11.dp,
                        )
                    }
                }

                TradeShimmerFixedBar(
                    width = 84.dp,
                    height = 24.dp,
                )
            }
        }

        TradeSectionCard(
            title = "",
        ) {
            TradeShimmerBar(
                widthFraction = 0.28f,
                height = 14.dp,
            )
            TradeShimmerBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(12.dp)),
            )
            repeat(5) { index ->
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        TradeShimmerBar(
                            widthFraction = 0.32f,
                            height = 12.dp,
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        TradeShimmerFixedBar(
                            width = if (index == 0) 92.dp else 74.dp,
                            height = if (index == 0) 14.dp else 13.dp,
                        )
                    }
                    if (index < 4) {
                        HorizontalDivider(color = TradeSectionBorder)
                    }
                }
            }
        }
    }
}

@Composable
private fun TradeSectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, TradeSectionBorder),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = {
                if (title.isNotBlank()) {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TradePrimaryText,
                    )
                }
                content()
            },
        )
    }
}

@Composable
private fun TradeShimmerBar(
    widthFraction: Float,
    height: androidx.compose.ui.unit.Dp,
) {
    TradeShimmerBox(
        modifier = Modifier
            .fillMaxWidth(widthFraction)
            .height(height)
            .clip(RoundedCornerShape(999.dp)),
    )
}

@Composable
private fun TradeShimmerFixedBar(
    width: androidx.compose.ui.unit.Dp,
    height: androidx.compose.ui.unit.Dp,
) {
    TradeShimmerBox(
        modifier = Modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(999.dp)),
    )
}

@Composable
private fun TradeShimmerBox(
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "trade-details-shimmer")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(durationMillis = 1200, easing = LinearEasing)),
        label = "trade-details-shimmer-progress",
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
private fun TradeStatusChip(
    label: String,
    background: Color,
    tint: Color,
) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = background,
        border = BorderStroke(1.dp, tint.copy(alpha = 0.16f)),
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = tint,
        )
    }
}

@Composable
private fun TradeSummaryRow(
    label: String,
    value: String,
    emphasize: Boolean = false,
    valueColor: Color = TradePrimaryText,
    showDivider: Boolean = true,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = TradeMutedText,
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = value,
                fontSize = if (emphasize) 14.sp else 13.sp,
                fontWeight = if (emphasize) FontWeight.Bold else FontWeight.SemiBold,
                color = valueColor,
                textAlign = TextAlign.End,
            )
        }
        if (showDivider) {
            HorizontalDivider(color = TradeSectionBorder)
        }
    }
}

@Composable
private fun TradeReferenceBlock(
    label: String,
    value: String,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = TradeMutedText,
        )
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFF8FAFC),
            border = BorderStroke(1.dp, TradeSectionBorder),
        ) {
            Text(
                text = value,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                fontSize = 12.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace,
                color = TradePrimaryText,
            )
        }
    }
}

private enum class TradeTransactionStatusTone {
    Success,
    Pending,
    Failure,
    Refund,
    Neutral,
}

private data class TradeTransactionVisual(
    val title: String,
    val icon: DrawableResource,
    val statusLabel: String,
    val statusBackground: Color,
    val statusTint: Color,
    val amountTint: Color,
)

@Composable
private fun tradeTransactionVisual(transaction: TradeTransactionPreview): TradeTransactionVisual {
    val tone = remember(transaction.status) { transaction.status.toTradeStatusTone() }
    val type = remember(transaction.type) { transaction.type.toTradeTransactionType() }
    return TradeTransactionVisual(
        title = when {
            transaction.isSip -> transaction.sipName?.ifBlank { stringResource(Res.string.history_screen_title_gold_savings) }
                ?: stringResource(Res.string.history_screen_title_gold_savings)
            type == TradeTransactionType.Buy -> stringResource(Res.string.history_screen_title_gold_purchase)
            type == TradeTransactionType.Sell -> stringResource(Res.string.history_screen_title_gold_sale)
            type == TradeTransactionType.Delivery -> stringResource(Res.string.history_screen_title_gold_delivery)
            else -> stringResource(Res.string.history_screen_title_transaction)
        },
        icon = when (type) {
            TradeTransactionType.Buy -> Res.drawable.ic_buy_gold_icon
            TradeTransactionType.Sell -> Res.drawable.ic_sell_gold_icon
            TradeTransactionType.Delivery -> Res.drawable.ic_delivery_gold_icon
            TradeTransactionType.Other -> Res.drawable.ic_buy_gold_icon
        },
        statusLabel = transaction.status.toTradeStatusLabel(),
        statusBackground = when (tone) {
            TradeTransactionStatusTone.Success -> Color(0xFFF0FDF4)
            TradeTransactionStatusTone.Pending -> Color(0xFFFFF7ED)
            TradeTransactionStatusTone.Failure -> Color(0xFFFEF2F2)
            TradeTransactionStatusTone.Refund -> Color(0xFFEFF6FF)
            TradeTransactionStatusTone.Neutral -> Color(0xFFF8FAFC)
        },
        statusTint = when (tone) {
            TradeTransactionStatusTone.Success -> Color(0xFF15803D)
            TradeTransactionStatusTone.Pending -> Color(0xFFD97706)
            TradeTransactionStatusTone.Failure -> Color(0xFFB91C1C)
            TradeTransactionStatusTone.Refund -> Color(0xFF2563EB)
            TradeTransactionStatusTone.Neutral -> TradePrimaryText
        },
        amountTint = when (tone) {
            TradeTransactionStatusTone.Success -> Color(0xFF15803D)
            TradeTransactionStatusTone.Pending -> TradePrimaryText
            TradeTransactionStatusTone.Failure -> Color(0xFFB91C1C)
            TradeTransactionStatusTone.Refund -> Color(0xFF2563EB)
            TradeTransactionStatusTone.Neutral -> TradePrimaryText
        },
    )
}

private enum class TradeTransactionType {
    Buy,
    Sell,
    Delivery,
    Other,
}

private fun String.toTradeTransactionType(): TradeTransactionType {
    return when {
        equals("BUY", ignoreCase = true) -> TradeTransactionType.Buy
        equals("SELL", ignoreCase = true) -> TradeTransactionType.Sell
        contains("delivery", ignoreCase = true) -> TradeTransactionType.Delivery
        else -> TradeTransactionType.Other
    }
}

private fun String.toTradeStatusTone(): TradeTransactionStatusTone {
    val normalized = lowercase()
    return when {
        normalized.contains("refund") -> TradeTransactionStatusTone.Refund
        normalized.contains("fail") ||
            normalized.contains("cancel") ||
            normalized.contains("reject") ||
            normalized.contains("expired") -> TradeTransactionStatusTone.Failure
        normalized.contains("pending") ||
            normalized.contains("process") ||
            normalized.contains("progress") ||
            normalized.contains("initiated") ||
            normalized.contains("await") -> TradeTransactionStatusTone.Pending
        normalized.contains("complete") ||
            normalized.contains("success") ||
            normalized.contains("credit") ||
            normalized.contains("confirm") ||
            normalized.contains("deliver") ||
            normalized.contains("paid") -> TradeTransactionStatusTone.Success
        else -> TradeTransactionStatusTone.Neutral
    }
}

private fun String.toTradeStatusLabel(): String {
    val normalized = trim()
        .replace('_', ' ')
        .replace('-', ' ')
        .lowercase()
    if (normalized.isBlank()) return "--"
    if (normalized == "pending") return "In Progress"
    return normalized
        .split(Regex("\\s+"))
        .joinToString(" ") { token ->
            token.replaceFirstChar { char ->
                if (char.isLowerCase()) char.titlecase() else char.toString()
            }
        }
}

private fun formatTradeCurrency(raw: String): String {
    val parsed = raw.trim().removePrefix("₹").replace(",", "").toDoubleOrNull() ?: return raw
    return "₹${formatMoney(parsed)}"
}

private fun formatTradeGoldPrice(raw: String): String {
    val parsed = raw.trim().removePrefix("₹").replace(",", "").toDoubleOrNull() ?: return raw
    return "₹${formatMoney(parsed)}/${"g"}"
}

@Composable
private fun formatTradeQuantity(raw: String): String {
    val parsed = raw.trim().toDoubleOrNull()
    val formatted = parsed?.let(::formatGoldQuantity) ?: raw
    return "$formatted ${stringResource(Res.string.common_gold_unit_short)}"
}

private fun formatTradeTransactionDateTime(raw: String): String {
    return runCatching {
        val local = Instant.parse(raw).toLocalDateTime(TimeZone.UTC)
        val hour24 = local.hour
        val hour12 = when {
            hour24 == 0 -> 12
            hour24 > 12 -> hour24 - 12
            else -> hour24
        }
        val meridiem = if (hour24 >= 12) "PM" else "AM"
        "${local.day.toString().padStart(2, '0')} ${tradeMonthAbbreviation(local.month.name)} ${local.year} • " +
            "${hour12}:${local.minute.toString().padStart(2, '0')} $meridiem"
    }.getOrElse { raw }
}

private fun tradeMonthAbbreviation(monthName: String): String {
    return monthName.lowercase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        .take(3)
}

private sealed interface TradeTransactionDetailsUiState {
    data object Loading : TradeTransactionDetailsUiState
    data class Content(val transaction: TradeTransactionPreview) : TradeTransactionDetailsUiState
    data class Error(val message: String) : TradeTransactionDetailsUiState
}

private sealed interface TradeTransactionLookupResult {
    data class Found(val transaction: TradeTransactionPreview) : TradeTransactionLookupResult
    data object NotFound : TradeTransactionLookupResult
    data class Failure(val message: String) : TradeTransactionLookupResult
}

private suspend fun findTradeTransaction(
    transactionId: String,
    getTradeTransactionsUseCase: GetTradeTransactionsUseCase,
): TradeTransactionLookupResult {
    var currentPage = 1
    var totalPages = 1
    while (currentPage <= totalPages) {
        when (val result = getTradeTransactionsUseCase(currentPage, 20)) {
            is ApiResult.Success -> {
                result.value.data.firstOrNull { it.id == transactionId }?.let {
                    return TradeTransactionLookupResult.Found(it)
                }
                totalPages = result.value.totalPages
                currentPage += 1
            }

            is ApiResult.Failure -> {
                return TradeTransactionLookupResult.Failure(result.error.message)
            }
        }
    }
    return TradeTransactionLookupResult.NotFound
}

private fun canShowTradeInvoice(transaction: TradeTransactionPreview): Boolean {
    val normalizedType = transaction.type.lowercase()
    val normalizedStatus = transaction.status.lowercase()
    val isTradeType = normalizedType.contains("buy") || normalizedType.contains("sell")
    val isSuccessful = normalizedStatus.contains("success") || normalizedStatus.contains("complete")
    return isTradeType && isSuccessful
}
