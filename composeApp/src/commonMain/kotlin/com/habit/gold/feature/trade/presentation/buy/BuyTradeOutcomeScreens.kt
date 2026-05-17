package com.habit.gold.feature.trade.presentation.buy

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.habit.gold.feature.trade.domain.model.TradeBuyOrder
import com.habit.gold.feature.trade.domain.model.TradePollingSnapshot
import com.habit.gold.feature.trade.presentation.formatMoney
import com.habit.gold.feature.trade.presentation.formatPercent
import com.habit.gold.feature.trade.presentation.roundToMoney
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.common_go_to_dashboard
import habitgoldmobile.composeapp.generated.resources.common_transaction_details
import habitgoldmobile.composeapp.generated.resources.trade_buy_amount_paid
import habitgoldmobile.composeapp.generated.resources.trade_buy_amount_to_be_paid
import habitgoldmobile.composeapp.generated.resources.trade_buy_breakdown_title
import habitgoldmobile.composeapp.generated.resources.trade_buy_error_code
import habitgoldmobile.composeapp.generated.resources.trade_buy_failure_title
import habitgoldmobile.composeapp.generated.resources.trade_buy_gst
import habitgoldmobile.composeapp.generated.resources.trade_buy_gold_credited
import habitgoldmobile.composeapp.generated.resources.trade_buy_gold_value
import habitgoldmobile.composeapp.generated.resources.trade_buy_pending_body
import habitgoldmobile.composeapp.generated.resources.trade_buy_pending_title
import habitgoldmobile.composeapp.generated.resources.trade_buy_pay_now
import habitgoldmobile.composeapp.generated.resources.trade_buy_processing_order_id_label
import habitgoldmobile.composeapp.generated.resources.trade_buy_processing_purchase_body
import habitgoldmobile.composeapp.generated.resources.trade_buy_processing_purchase_title
import habitgoldmobile.composeapp.generated.resources.trade_buy_quantity
import habitgoldmobile.composeapp.generated.resources.trade_buy_retry
import habitgoldmobile.composeapp.generated.resources.trade_buy_secure_100_percent
import habitgoldmobile.composeapp.generated.resources.trade_buy_secure_bhim_registered
import habitgoldmobile.composeapp.generated.resources.trade_buy_status_completed
import habitgoldmobile.composeapp.generated.resources.trade_buy_success_body
import habitgoldmobile.composeapp.generated.resources.trade_buy_success_title
import habitgoldmobile.composeapp.generated.resources.trade_buy_verifying_body
import habitgoldmobile.composeapp.generated.resources.trade_buy_verifying_title
import habitgoldmobile.composeapp.generated.resources.trade_transaction_details_view_invoice
import org.jetbrains.compose.resources.stringResource
import kotlinx.coroutines.delay

@Composable
internal fun BuyTradeProcessingScreen(
    orderId: String,
    pollingSnapshot: TradePollingSnapshot?,
    modifier: Modifier = Modifier,
) {
    if (orderId.isBlank()) {
        BuyTradeVerifyingScreen(modifier = modifier)
        return
    }

    val pollCount = pollingSnapshot?.attempt ?: 1
    val initialRemainingSeconds = remember(orderId, pollCount) {
        (BuyPollingWindowSeconds - ((pollCount - 1) * BuyPollIntervalSeconds)).coerceAtLeast(0)
    }
    var remainingSeconds by remember(orderId, pollCount) {
        mutableStateOf(initialRemainingSeconds)
    }

    LaunchedEffect(orderId, pollCount) {
        remainingSeconds = initialRemainingSeconds
        while (remainingSeconds > 0) {
            delay(1000)
            remainingSeconds -= 1
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BuyWhite)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {},
            )
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                modifier = Modifier.size(120.dp),
                color = BuyPrimary.copy(alpha = 0.18f),
                strokeWidth = 8.dp,
                progress = { 1f },
            )
            CircularProgressIndicator(
                modifier = Modifier.size(120.dp),
                color = BuyPrimary,
                strokeWidth = 8.dp,
                progress = {
                    if (BuyPollingWindowSeconds > 0) {
                        (BuyPollingWindowSeconds - remainingSeconds) / BuyPollingWindowSeconds.toFloat()
                    } else 1f
                },
            )
            Surface(
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                color = BuyPrimaryLight,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "${remainingSeconds}s",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = BuyPrimary,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = stringResource(Res.string.trade_buy_processing_purchase_title),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = BuySlate800,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(Res.string.trade_buy_processing_purchase_body),
            fontSize = 15.sp,
            lineHeight = 22.sp,
            color = BuySlate500,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(modifier = Modifier.height(32.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = BuySlate50,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = stringResource(Res.string.trade_buy_processing_order_id_label),
                    fontSize = 12.sp,
                    color = BuySlate400,
                )
                Text(
                    text = orderId,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BuySlate600,
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
internal fun BuyTradeVerifyingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BuyWhite.copy(alpha = 0.94f)),
    )
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(BuyWhite)
                .padding(horizontal = 32.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(80.dp),
                    color = BuyPrimary,
                    strokeWidth = 6.dp,
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = stringResource(Res.string.trade_buy_verifying_title),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = BuySlate950,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(Res.string.trade_buy_verifying_body),
                fontSize = 14.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Medium,
                color = BuySlate500,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(99.dp))
                    .background(BuySlate50)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = BuySlate500,
                    modifier = Modifier.size(12.dp),
                )
                Text(
                    text = stringResource(Res.string.trade_buy_secure_100_percent),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = BuySlate500,
                    letterSpacing = 1.sp,
                )
                Spacer(modifier = Modifier.width(12.dp))
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = BuySlate500,
                    modifier = Modifier.size(12.dp),
                )
                Text(
                    text = stringResource(Res.string.trade_buy_secure_bhim_registered),
                    fontSize = 10.sp,
                    color = BuySlate500,
                )
            }
        }
    }
}

@Composable
internal fun BuyTradeSuccessScreen(
    amount: String,
    goldCredited: String,
    invoiceErrorMessage: String?,
    isInvoiceLoading: Boolean,
    onGoToDashboard: () -> Unit,
    onViewInvoiceClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BuyWhite)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(BuyGreen25)
                .border(2.dp, BuyGreen500.copy(alpha = 0.45f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(BuyGreen500),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.Check, null, tint = BuyWhite, modifier = Modifier.size(40.dp))
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(Res.string.trade_buy_success_title),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = BuySlate950,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(Res.string.trade_buy_success_body),
            fontSize = 14.sp,
            lineHeight = 20.sp,
            color = BuySlate500,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(32.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = BuyNeutral25),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(stringResource(Res.string.common_transaction_details), fontSize = 12.sp, color = BuySlate500)
                    Box(
                        modifier = Modifier
                            .background(BuyGreen25, RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                    ) {
                        Text(
                            text = stringResource(Res.string.trade_buy_status_completed),
                            fontSize = 10.sp,
                            color = BuySuccess700,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = BuySlate100)
                Spacer(modifier = Modifier.height(20.dp))
                BuyTradeDetailRow(
                    title = stringResource(Res.string.trade_buy_gold_credited),
                    value = goldCredited,
                    valueColor = BuySlate950,
                )
                Spacer(modifier = Modifier.height(16.dp))
                BuyTradeDetailRow(
                    title = stringResource(Res.string.trade_buy_amount_paid),
                    value = amount,
                    valueColor = BuyPrimary,
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.clickable(enabled = onViewInvoiceClick != null && !isInvoiceLoading) { onViewInvoiceClick?.invoke() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (isInvoiceLoading) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = BuyPrimary, strokeWidth = 2.dp)
            } else {
                Icon(Icons.Default.Description, null, tint = BuyPrimary, modifier = Modifier.size(20.dp))
            }
            Text(
                text = stringResource(Res.string.trade_transaction_details_view_invoice),
                color = BuyPrimary,
                fontWeight = FontWeight.Medium,
            )
        }
        invoiceErrorMessage?.takeIf { it.isNotBlank() }?.let { message ->
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = message,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = BuyRed700,
                textAlign = TextAlign.Center,
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = onGoToDashboard,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BuyPrimary),
        ) {
            Text(stringResource(Res.string.common_go_to_dashboard), fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
internal fun BuyTradeFailureScreen(
    body: String,
    errorCode: String,
    onRetryClick: () -> Unit,
    onHomeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BuyWhite)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier.size(100.dp).clip(CircleShape).background(BuyRed50),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier.size(60.dp).clip(CircleShape).background(BuyRed400),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.Close, null, tint = BuyWhite, modifier = Modifier.size(40.dp))
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(stringResource(Res.string.trade_buy_failure_title), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = BuySlate950)
        Spacer(modifier = Modifier.height(8.dp))
        Text(body, fontSize = 14.sp, lineHeight = 20.sp, color = BuySlate500, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(32.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BuySlate100, RoundedCornerShape(12.dp))
                .background(BuyNeutral25, RoundedCornerShape(12.dp))
                .padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(stringResource(Res.string.trade_buy_error_code), fontSize = 12.sp, color = BuySlate500, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(errorCode, fontSize = 16.sp, color = BuySlate950, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = onRetryClick,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BuyPrimary),
        ) {
            Text(stringResource(Res.string.trade_buy_retry), fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            onClick = onHomeClick,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, BuyPrimary),
        ) {
            Text(
                text = stringResource(Res.string.common_go_to_dashboard),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = BuyPrimary,
            )
        }
    }
}

@Composable
internal fun BuyTradePendingScreen(
    body: String,
    orderId: String?,
    status: String?,
    onGoToDashboard: () -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BuyWhite)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier.size(100.dp).clip(CircleShape).background(BuyPrimaryLight),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(modifier = Modifier.size(60.dp), color = BuyPrimary, strokeWidth = 5.dp)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(stringResource(Res.string.trade_buy_pending_title), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = BuySlate950)
        Spacer(modifier = Modifier.height(8.dp))
        Text(body, fontSize = 14.sp, lineHeight = 20.sp, color = BuySlate500, textAlign = TextAlign.Center)
        if (!orderId.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(32.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BuyNeutral25),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(stringResource(Res.string.common_transaction_details), fontSize = 12.sp, color = BuySlate500)
                        if (!status.isNullOrBlank()) {
                            Box(
                                modifier = Modifier
                                    .background(BuyPrimaryLight, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp),
                            ) {
                                Text(text = status, fontSize = 10.sp, color = BuyPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = BuySlate100)
                    Spacer(modifier = Modifier.height(16.dp))
                    BuyTradeDetailRow(
                        title = stringResource(Res.string.trade_buy_processing_order_id_label),
                        value = orderId,
                        valueColor = BuySlate950,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = onGoToDashboard,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BuyPrimary),
        ) {
            Text(stringResource(Res.string.common_go_to_dashboard), fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            onClick = onRetryClick,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, BuySlate200),
        ) {
            Text(stringResource(Res.string.trade_buy_retry), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BuyPrimary)
        }
    }
}

@Composable
internal fun BuyTradeDetailRow(
    title: String,
    value: String,
    valueColor: Color,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = title, fontSize = 14.sp, color = BuySlate500)
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
}

internal fun calculateOrderTotalPaid(order: TradeBuyOrder): Double {
    return roundToMoney((order.goldQuantityGrams * order.goldPricePerGram) + order.gstAmount)
}

@Composable
internal fun BuyAmountBreakdownSheet(
    calculation: BuyTradeCalculation,
    gstRate: Double,
    onPayNowClick: () -> Unit,
    isPayNowEnabled: Boolean,
    isLoading: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BuyWhite)
            .padding(horizontal = 24.dp)
            .padding(bottom = 24.dp),
    ) {
        Text(
            text = stringResource(Res.string.trade_buy_breakdown_title),
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = BuySlate950,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(BuySlate50, RoundedCornerShape(16.dp))
                .border(1.dp, BuySlate100, RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            BreakdownRow(title = stringResource(Res.string.trade_buy_gold_value), value = "₹${formatMoney(calculation.goldValue)}")
            BreakdownRow(
                title = "${stringResource(Res.string.trade_buy_gst)} (${formatPercent(gstRate * 100.0)})",
                value = "₹${formatMoney(calculation.gstAmount)}",
            )
            BreakdownRow(
                title = stringResource(Res.string.trade_buy_quantity),
                value = "${formatConversionGrams(calculation.goldQuantity)} gm",
            )
            HorizontalDivider(color = BuySlate200)
            BreakdownRow(
                title = stringResource(Res.string.trade_buy_amount_to_be_paid),
                value = "₹${formatMoney(calculation.totalPayable)}",
                titleWeight = FontWeight.Bold,
                valueWeight = FontWeight.ExtraBold,
                valueColor = BuyPrimary,
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onPayNowClick,
            enabled = isPayNowEnabled && !isLoading,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BuyPrimary,
                contentColor = BuyWhite,
                disabledContainerColor = BuySlate200,
                disabledContentColor = BuySlate400,
            ),
        ) {
            Text(stringResource(Res.string.trade_buy_pay_now), fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
internal fun BreakdownRow(
    title: String,
    value: String,
    titleWeight: FontWeight = FontWeight.Medium,
    valueWeight: FontWeight = FontWeight.Bold,
    valueColor: Color = BuySlate950,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = title, fontSize = 14.sp, fontWeight = titleWeight, color = BuySlate500)
        Text(text = value, fontSize = 14.sp, fontWeight = valueWeight, color = valueColor)
    }
}
