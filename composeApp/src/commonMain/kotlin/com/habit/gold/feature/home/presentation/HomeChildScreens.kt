@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.habit.gold.feature.home.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habit.gold.core.designsystem.AppPrimaryButton
import com.habit.gold.core.designsystem.HabitGoldPalette
import com.habit.gold.feature.home.domain.model.HomeDashboardSummary
import com.habit.gold.feature.home.domain.model.HomeRecentTransactionPreview
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.buy_gold_screen_buy_gold
import habitgoldmobile.composeapp.generated.resources.common_back
import habitgoldmobile.composeapp.generated.resources.common_gold_unit_short
import habitgoldmobile.composeapp.generated.resources.home_screen_total_gold_balance
import habitgoldmobile.composeapp.generated.resources.home_screen_transaction_type_buy
import habitgoldmobile.composeapp.generated.resources.home_screen_transaction_type_generic
import habitgoldmobile.composeapp.generated.resources.home_screen_transaction_type_sell
import habitgoldmobile.composeapp.generated.resources.home_value_details_average_buy_price
import habitgoldmobile.composeapp.generated.resources.home_value_details_buy_sell_spread
import habitgoldmobile.composeapp.generated.resources.home_value_details_current_sell_price
import habitgoldmobile.composeapp.generated.resources.home_value_details_current_value
import habitgoldmobile.composeapp.generated.resources.home_value_details_empty_state
import habitgoldmobile.composeapp.generated.resources.home_value_details_final_payout
import habitgoldmobile.composeapp.generated.resources.home_value_details_gold_purchased
import habitgoldmobile.composeapp.generated.resources.home_value_details_gst_paid
import habitgoldmobile.composeapp.generated.resources.home_value_details_rewards_applied
import habitgoldmobile.composeapp.generated.resources.home_value_details_title
import habitgoldmobile.composeapp.generated.resources.home_value_details_total_cost
import habitgoldmobile.composeapp.generated.resources.sell_gold_screen_sell_gold
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

internal val ChildScreenBackground = Color(0xFFF8F8FB)
internal val ChildCardBorder = Color(0xFFE5E7EB)
internal val ChildMutedText = Color(0xFF64748B)
internal val ChildPrimaryText = Color(0xFF0F172A)
internal val ChildSuccess = Color(0xFF15803D)

@Composable
internal fun HomeGoldValueDetailsScreen(
    dashboard: HomeDashboardSummary?,
    onBackClick: () -> Unit,
    onBuyGoldClick: () -> Unit,
    onSellGoldClick: () -> Unit,
) {
    HomeChildScaffold(
        title = stringResource(Res.string.home_value_details_title),
        onBackClick = onBackClick,
        backgroundColor = Color.White,
    ) { paddingValues ->
        if (dashboard == null) {
            HomeChildEmptyState(
                paddingValues = paddingValues,
                message = stringResource(Res.string.home_value_details_empty_state),
            )
            return@HomeChildScaffold
        }

        val rewardsApplied = dashboard.rewardsApplied ?: 0.0
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = HabitGoldPalette.plum),
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccountBalance,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(Res.string.home_screen_total_gold_balance),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.88f),
                        )
                    }
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = formatGoldBalance(dashboard.totalGoldBalanceGrams),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                        )
                        Text(
                            text = stringResource(Res.string.common_gold_unit_short),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp),
                        )
                    }
                }
            }

            HomeValueBreakdownCard(
                valueRows = buildList {
                    add(HomeValueRow(stringResource(Res.string.home_value_details_gold_purchased), "₹${formatInr(dashboard.totalCost - dashboard.gstPaid - rewardsApplied)}"))
                    if (rewardsApplied > 0.0) {
                        add(HomeValueRow(stringResource(Res.string.home_value_details_rewards_applied), "+₹${formatInr(rewardsApplied)}", valueColor = ChildSuccess))
                    }
                    add(HomeValueRow(stringResource(Res.string.home_value_details_gst_paid), "₹${formatInr(dashboard.gstPaid)}"))
                    add(HomeValueRow(stringResource(Res.string.home_value_details_total_cost), "₹${formatInr(dashboard.totalCost)}"))
                    add(HomeValueRow(stringResource(Res.string.home_value_details_current_value), "₹${formatInr(dashboard.currentValue)}"))
                    add(HomeValueRow(stringResource(Res.string.home_value_details_current_sell_price), "₹${formatInr(dashboard.liveSellPricePerGram)}"))
                    add(HomeValueRow(stringResource(Res.string.home_value_details_average_buy_price), "₹${formatInr(dashboard.averageBuyPricePerGram)}"))
                    add(HomeValueRow(stringResource(Res.string.home_value_details_buy_sell_spread), "₹${formatInr(dashboard.buySellPriceDifference)}"))
                    add(HomeValueRow(stringResource(Res.string.home_value_details_final_payout), "₹${formatInr(dashboard.finalPayoutAmount)}", valueColor = ChildPrimaryText, emphasize = true))
                },
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    AppPrimaryButton(
                        label = stringResource(Res.string.buy_gold_screen_buy_gold),
                        onClick = onBuyGoldClick,
                    )
                }
                Button(
                    onClick = onSellGoldClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, HabitGoldPalette.plum),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = HabitGoldPalette.plum,
                    ),
                    contentPadding = PaddingValues(0.dp),
                ) {
                    Text(
                        text = stringResource(Res.string.sell_gold_screen_sell_gold),
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
internal fun HomeChildScaffold(
    title: String,
    onBackClick: () -> Unit,
    backgroundColor: Color = ChildScreenBackground,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        color = HabitGoldPalette.plum,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.common_back),
                            tint = HabitGoldPalette.plum,
                        )
                    }
                },
                actions = actions,
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor),
            )
        },
    ) { paddingValues ->
        content(paddingValues)
    }
}

@Composable
internal fun HomeChildEmptyState(
    paddingValues: PaddingValues,
    message: String,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = message,
            color = ChildMutedText,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
        )
    }
}

@Composable
internal fun HomeValueBreakdownCard(
    valueRows: List<HomeValueRow>,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ChildCardBorder),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            valueRows.forEachIndexed { index, valueRow ->
                HomeDetailRow(
                    label = valueRow.label,
                    value = valueRow.value,
                    valueColor = valueRow.valueColor,
                    emphasize = valueRow.emphasize,
                )
                if (index != valueRows.lastIndex) {
                    HorizontalDivider(color = ChildCardBorder.copy(alpha = 0.8f))
                }
            }
        }
    }
}

@Composable
internal fun HomeDetailRow(
    label: String,
    value: String,
    valueColor: Color = ChildPrimaryText,
    emphasize: Boolean = false,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            color = ChildMutedText,
            fontSize = 13.sp,
            lineHeight = 19.sp,
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = value,
            color = valueColor,
            fontSize = if (emphasize) 15.sp else 13.sp,
            fontWeight = if (emphasize) FontWeight.ExtraBold else FontWeight.SemiBold,
            textAlign = TextAlign.End,
        )
    }
}

@Composable
internal fun HomeFaqCard(homeFaqItem: HomeFaqItem) {
    var isExpanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ChildCardBorder),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF6F1FB)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = HabitGoldPalette.plum,
                        modifier = Modifier.size(18.dp),
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stringResource(homeFaqItem.question),
                    modifier = Modifier.weight(1f),
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ChildPrimaryText,
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = ChildMutedText,
                    modifier = Modifier.size(18.dp),
                )
            }
            if (isExpanded) {
                Text(
                    text = stringResource(homeFaqItem.answer),
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    color = ChildMutedText,
                )
            }
        }
    }
}

internal data class HomeFaqItem(
    val question: StringResource,
    val answer: StringResource,
)

internal data class HomeValueRow(
    val label: String,
    val value: String,
    val valueColor: Color = ChildPrimaryText,
    val emphasize: Boolean = false,
)

@Composable
internal fun transactionTypeLabel(transactionPreview: HomeRecentTransactionPreview): String {
    return when {
        transactionPreview.isSip && !transactionPreview.sipName.isNullOrBlank() -> transactionPreview.sipName
        transactionPreview.type.equals("BUY", ignoreCase = true) -> stringResource(Res.string.home_screen_transaction_type_buy)
        transactionPreview.type.equals("SELL", ignoreCase = true) -> stringResource(Res.string.home_screen_transaction_type_sell)
        transactionPreview.type.isBlank() -> stringResource(Res.string.home_screen_transaction_type_generic)
        else -> transactionPreview.type.replaceFirstChar { it.uppercase() }
    }
}

@Composable
internal fun transactionTitleFor(transactionPreview: HomeRecentTransactionPreview): String {
    return when {
        transactionPreview.isSip && !transactionPreview.sipName.isNullOrBlank() -> transactionPreview.sipName
        transactionPreview.type.equals("BUY", ignoreCase = true) -> stringResource(Res.string.home_screen_transaction_type_buy)
        transactionPreview.type.equals("SELL", ignoreCase = true) -> stringResource(Res.string.home_screen_transaction_type_sell)
        else -> transactionPreview.type.ifBlank { stringResource(Res.string.home_screen_transaction_type_generic) }
    }
}

internal fun deferredIconFor(homeDeferredTarget: HomeDeferredTarget): ImageVector {
    return when (homeDeferredTarget) {
        HomeDeferredTarget.Profile -> Icons.Default.AccountBalance
        HomeDeferredTarget.Alerts -> Icons.Default.Info
    }
}
