package com.habit.gold.feature.home.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.habit.gold.feature.home.domain.model.HomeRecentTransactionPreview
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.common_gold_unit_short
import habitgoldmobile.composeapp.generated.resources.home_route_handoff_alerts_message
import habitgoldmobile.composeapp.generated.resources.home_route_handoff_alerts_title
import habitgoldmobile.composeapp.generated.resources.home_route_handoff_profile_message
import habitgoldmobile.composeapp.generated.resources.home_route_handoff_profile_title
import habitgoldmobile.composeapp.generated.resources.home_route_handoff_view_history
import habitgoldmobile.composeapp.generated.resources.home_screen_transaction_amount
import habitgoldmobile.composeapp.generated.resources.home_screen_transaction_date
import habitgoldmobile.composeapp.generated.resources.home_screen_transaction_details_heading
import habitgoldmobile.composeapp.generated.resources.home_screen_transaction_gold_quantity
import habitgoldmobile.composeapp.generated.resources.home_screen_transaction_id
import habitgoldmobile.composeapp.generated.resources.home_screen_transaction_not_available
import habitgoldmobile.composeapp.generated.resources.home_screen_transaction_sip_frequency
import habitgoldmobile.composeapp.generated.resources.home_screen_transaction_status
import habitgoldmobile.composeapp.generated.resources.home_screen_transaction_type
import habitgoldmobile.composeapp.generated.resources.home_screen_transaction_unknown
import habitgoldmobile.composeapp.generated.resources.ic_buy_gold_icon
import habitgoldmobile.composeapp.generated.resources.ic_delivery_gold_icon
import habitgoldmobile.composeapp.generated.resources.ic_sell_gold_icon
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun HomeTransactionDetailsScreen(
    transactionPreview: HomeRecentTransactionPreview,
    onBackClick: () -> Unit,
) {
    val visual = rememberHomeTransactionVisual(transactionPreview)
    val amountColor = homeTransactionValueColor(transactionPreview.status)
    HomeChildScaffold(
        title = stringResource(Res.string.home_screen_transaction_details_heading),
        onBackClick = onBackClick,
        backgroundColor = Color.White,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, ChildCardBorder),
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            painter = painterResource(visual.icon),
                            contentDescription = null,
                            modifier = Modifier.size(44.dp),
                        )
                        Column {
                            Text(
                                text = transactionTitleFor(transactionPreview),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = ChildPrimaryText,
                            )
                            Text(
                                text = formatCreatedAt(transactionPreview.createdAt),
                                fontSize = 12.sp,
                                color = ChildMutedText,
                            )
                        }
                    }
                    HorizontalDivider(color = ChildCardBorder)
                    HomeDetailRow(
                        label = stringResource(Res.string.home_screen_transaction_id),
                        value = transactionPreview.id.ifBlank { stringResource(Res.string.home_screen_transaction_not_available) },
                    )
                    HomeDetailRow(
                        label = stringResource(Res.string.home_screen_transaction_type),
                        value = transactionTypeLabel(transactionPreview),
                    )
                    HomeDetailRow(
                        label = stringResource(Res.string.home_screen_transaction_status),
                        value = transactionPreview.status.ifBlank { stringResource(Res.string.home_screen_transaction_unknown) },
                    )
                    if (transactionPreview.isSip) {
                        HomeDetailRow(
                            label = stringResource(Res.string.home_screen_transaction_sip_frequency),
                            value = transactionPreview.sipFrequency?.replaceFirstChar { it.uppercase() }
                                ?: stringResource(Res.string.home_screen_transaction_not_available),
                        )
                    }
                    HomeDetailRow(
                        label = stringResource(Res.string.home_screen_transaction_amount),
                        value = "₹${transactionPreview.amount.toDoubleOrNull()?.let(::formatInr) ?: transactionPreview.amount}",
                        valueColor = amountColor,
                        emphasize = true,
                    )
                    HomeDetailRow(
                        label = stringResource(Res.string.home_screen_transaction_gold_quantity),
                        value = "${transactionPreview.goldQuantity} ${stringResource(Res.string.common_gold_unit_short)}",
                        valueColor = amountColor,
                    )
                    HomeDetailRow(
                        label = stringResource(Res.string.home_screen_transaction_date),
                        value = formatCreatedAt(transactionPreview.createdAt),
                    )
                }
            }
        }
    }
}

private data class HomeTransactionVisual(
    val icon: DrawableResource,
)

private fun rememberHomeTransactionVisual(transactionPreview: HomeRecentTransactionPreview): HomeTransactionVisual {
    val isBuy = transactionPreview.type.equals("BUY", ignoreCase = true)
    val isSell = transactionPreview.type.equals("SELL", ignoreCase = true)
    val isDelivery = transactionPreview.type.contains("delivery", ignoreCase = true)
    return HomeTransactionVisual(
        icon = when {
            isBuy -> Res.drawable.ic_buy_gold_icon
            isSell -> Res.drawable.ic_sell_gold_icon
            isDelivery -> Res.drawable.ic_delivery_gold_icon
            else -> Res.drawable.ic_buy_gold_icon
        },
    )
}

private fun homeTransactionValueColor(status: String): Color {
    val normalized = status.lowercase()
    return when {
        normalized.contains("refund") -> ChildPrimaryText
        normalized.contains("progress") || normalized.contains("pending") -> ChildPrimaryText
        normalized.contains("fail") || normalized.contains("cancel") -> Color(0xFF15803D)
        normalized.contains("complete") || normalized.contains("success") -> Color(0xFF15803D)
        else -> ChildPrimaryText
    }
}

@Composable
internal fun HomeDeferredRouteScreen(
    target: HomeDeferredTarget,
    onBackClick: () -> Unit,
    onOpenHistoryTab: (() -> Unit)? = null,
) {
    val title = when (target) {
        HomeDeferredTarget.Profile -> stringResource(Res.string.home_route_handoff_profile_title)
        HomeDeferredTarget.Alerts -> stringResource(Res.string.home_route_handoff_alerts_title)
    }
    val message = when (target) {
        HomeDeferredTarget.Profile -> stringResource(Res.string.home_route_handoff_profile_message)
        HomeDeferredTarget.Alerts -> stringResource(Res.string.home_route_handoff_alerts_message)
    }

    HomeChildScaffold(
        title = title,
        onBackClick = onBackClick,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, ChildCardBorder),
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF6F1FB)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = deferredIconFor(target),
                            contentDescription = null,
                            tint = HabitGoldPalette.plum,
                        )
                    }
                    Text(
                        text = title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ChildPrimaryText,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = message,
                        fontSize = 14.sp,
                        lineHeight = 21.sp,
                        color = ChildMutedText,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}
