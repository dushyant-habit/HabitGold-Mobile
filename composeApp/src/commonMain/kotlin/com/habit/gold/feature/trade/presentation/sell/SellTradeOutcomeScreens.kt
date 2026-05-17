package com.habit.gold.feature.trade.presentation.sell

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habit.gold.core.designsystem.HabitGoldPalette
import com.habit.gold.feature.trade.presentation.TradeMutedText
import com.habit.gold.feature.trade.presentation.TradePrimaryText
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.common_go_to_dashboard
import habitgoldmobile.composeapp.generated.resources.common_transaction_details
import habitgoldmobile.composeapp.generated.resources.trade_sell_amount_transferred
import habitgoldmobile.composeapp.generated.resources.trade_sell_failure_title
import habitgoldmobile.composeapp.generated.resources.trade_sell_gold_debited
import habitgoldmobile.composeapp.generated.resources.trade_sell_pending_body
import habitgoldmobile.composeapp.generated.resources.trade_sell_pending_title
import habitgoldmobile.composeapp.generated.resources.trade_sell_status_completed
import habitgoldmobile.composeapp.generated.resources.trade_sell_success_body
import habitgoldmobile.composeapp.generated.resources.trade_sell_success_title
import habitgoldmobile.composeapp.generated.resources.trade_sell_to_upi
import habitgoldmobile.composeapp.generated.resources.trade_sell_summary_order_id
import habitgoldmobile.composeapp.generated.resources.trade_sell_what_you_can_do
import habitgoldmobile.composeapp.generated.resources.trade_sell_what_you_can_do_body
import habitgoldmobile.composeapp.generated.resources.trade_transaction_details_view_invoice
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun SellTradeSuccessScreen(
    amount: String,
    goldDebited: String,
    creditedUpiId: String,
    invoiceErrorMessage: String?,
    isInvoiceLoading: Boolean,
    onViewInvoiceClick: () -> Unit,
    onGoToDashboard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color(0xFFE8F6EC)),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF22C55E)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp),
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(Res.string.trade_sell_success_title),
            color = TradePrimaryText,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(Res.string.trade_sell_success_body),
            color = TradeMutedText,
            fontSize = 14.sp,
            lineHeight = 21.sp,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F2FF)),
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = stringResource(Res.string.common_transaction_details),
                        fontSize = 12.sp,
                        color = TradeMutedText,
                    )
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFE9D5FF), RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                    ) {
                        Text(
                            text = stringResource(Res.string.trade_sell_status_completed),
                            fontSize = 10.sp,
                            color = HabitGoldPalette.plum,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = Color(0xFFF1F5F9))
                Spacer(modifier = Modifier.height(20.dp))

                SellTradeOutcomeValueRow(
                    label = stringResource(Res.string.trade_sell_gold_debited),
                    value = goldDebited,
                    valueColor = TradePrimaryText,
                )
                Spacer(modifier = Modifier.height(16.dp))
                SellTradeOutcomeValueRow(
                    label = stringResource(Res.string.trade_sell_amount_transferred),
                    value = amount,
                    valueColor = HabitGoldPalette.plum,
                )
                Spacer(modifier = Modifier.height(16.dp))
                SellTradeOutcomeValueRow(
                    label = stringResource(Res.string.trade_sell_to_upi),
                    value = creditedUpiId,
                    valueColor = TradePrimaryText,
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.clickable(enabled = !isInvoiceLoading) { onViewInvoiceClick() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (isInvoiceLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = HabitGoldPalette.plum,
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = HabitGoldPalette.plum,
                    modifier = Modifier.size(20.dp),
                )
            }
            Text(
                text = stringResource(Res.string.trade_transaction_details_view_invoice),
                color = HabitGoldPalette.plum,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
            )
        }

        invoiceErrorMessage?.takeIf { it.isNotBlank() }?.let { message ->
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message,
                color = Color(0xFFB42318),
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onGoToDashboard,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = HabitGoldPalette.plum),
        ) {
            Text(
                text = stringResource(Res.string.common_go_to_dashboard),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
internal fun SellTradeFailureScreen(
    message: String,
    onGoToDashboard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color(0xFFFEF2F2)),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF87171)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp),
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(Res.string.trade_sell_failure_title),
            color = TradePrimaryText,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            color = TradeMutedText,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(32.dp))

        TradeOutcomeNoteCard(
            title = stringResource(Res.string.trade_sell_what_you_can_do),
            body = stringResource(Res.string.trade_sell_what_you_can_do_body),
            containerColor = Color(0xFFF8F2FF),
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onGoToDashboard,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = HabitGoldPalette.plum),
        ) {
            Text(
                text = stringResource(Res.string.common_go_to_dashboard),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
internal fun SellTradePendingScreen(
    orderId: String,
    onGoToDashboard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color(0xFFF8F2FF)),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE9D5FF)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = HabitGoldPalette.plum,
                    modifier = Modifier.size(40.dp),
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(Res.string.trade_sell_pending_title),
            color = TradePrimaryText,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(Res.string.trade_sell_pending_body),
            color = TradeMutedText,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(Res.string.trade_sell_summary_order_id),
                    fontSize = 12.sp,
                    color = TradeMutedText,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = orderId,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TradePrimaryText,
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onGoToDashboard,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = HabitGoldPalette.plum),
        ) {
            Text(
                text = stringResource(Res.string.common_go_to_dashboard),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
internal fun SellTradeOutcomeValueRow(
    label: String,
    value: String,
    valueColor: Color,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = TradeMutedText,
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor,
        )
    }
}

@Composable
internal fun TradeOutcomeNoteCard(
    title: String,
    body: String,
    containerColor: Color = Color(0xFFF8FAFC),
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title,
                color = TradeMutedText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = body,
                color = TradePrimaryText,
                fontSize = 14.sp,
                lineHeight = 20.sp,
            )
        }
    }
}
