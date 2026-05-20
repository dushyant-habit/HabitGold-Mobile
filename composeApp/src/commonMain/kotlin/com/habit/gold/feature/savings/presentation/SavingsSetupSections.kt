package com.habit.gold.feature.savings.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habit.gold.core.designsystem.AppPrimaryButton
import com.habit.gold.core.designsystem.HabitGoldPalette
import com.habit.gold.feature.home.presentation.ChildCardBorder
import com.habit.gold.feature.home.presentation.ChildMutedText
import com.habit.gold.feature.home.presentation.ChildPrimaryText
import com.habit.gold.feature.home.presentation.formatInr
import com.habit.gold.feature.trade.domain.TradeLivePriceState
import com.habit.gold.feature.trade.presentation.buy.BuyCouponSheet
import com.habit.gold.feature.trade.presentation.buy.BuyTradeLivePriceBar
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.common_help
import habitgoldmobile.composeapp.generated.resources.common_retry
import habitgoldmobile.composeapp.generated.resources.start_daily_savings_icon
import habitgoldmobile.composeapp.generated.resources.start_monthly_savings_icon
import habitgoldmobile.composeapp.generated.resources.start_weekly_savings_icon
import habitgoldmobile.composeapp.generated.resources.savings_setup_amount_label
import habitgoldmobile.composeapp.generated.resources.savings_setup_day_label
import habitgoldmobile.composeapp.generated.resources.savings_setup_failure_body
import habitgoldmobile.composeapp.generated.resources.savings_setup_failure_title
import habitgoldmobile.composeapp.generated.resources.savings_setup_frequency_daily_cta
import habitgoldmobile.composeapp.generated.resources.savings_setup_frequency_daily_title
import habitgoldmobile.composeapp.generated.resources.savings_setup_frequency_monthly_cta
import habitgoldmobile.composeapp.generated.resources.savings_setup_frequency_monthly_title
import habitgoldmobile.composeapp.generated.resources.savings_setup_frequency_weekly_cta
import habitgoldmobile.composeapp.generated.resources.savings_setup_frequency_weekly_title
import habitgoldmobile.composeapp.generated.resources.savings_setup_gold_savings
import habitgoldmobile.composeapp.generated.resources.savings_setup_go_home
import habitgoldmobile.composeapp.generated.resources.savings_setup_compounding_breakdown_body
import habitgoldmobile.composeapp.generated.resources.savings_setup_compounding_breakdown_title
import habitgoldmobile.composeapp.generated.resources.savings_setup_compounding_estimated_earnings
import habitgoldmobile.composeapp.generated.resources.savings_setup_compounding_frequency
import habitgoldmobile.composeapp.generated.resources.savings_setup_compounding_illustrative_note
import habitgoldmobile.composeapp.generated.resources.savings_setup_compounding_investment_amount
import habitgoldmobile.composeapp.generated.resources.savings_setup_compounding_maturity_value
import habitgoldmobile.composeapp.generated.resources.savings_setup_compounding_monthly_equivalent
import habitgoldmobile.composeapp.generated.resources.savings_setup_compounding_preview_intro
import habitgoldmobile.composeapp.generated.resources.savings_setup_compounding_total_invested
import habitgoldmobile.composeapp.generated.resources.savings_setup_monthly_date_label
import habitgoldmobile.composeapp.generated.resources.savings_setup_next_payment_label
import habitgoldmobile.composeapp.generated.resources.savings_setup_pending_body
import habitgoldmobile.composeapp.generated.resources.savings_setup_pending_title
import habitgoldmobile.composeapp.generated.resources.savings_setup_polling_body
import habitgoldmobile.composeapp.generated.resources.savings_setup_polling_title
import habitgoldmobile.composeapp.generated.resources.savings_setup_resume_cta
import habitgoldmobile.composeapp.generated.resources.savings_setup_secure_note
import habitgoldmobile.composeapp.generated.resources.savings_setup_submit_upgrade
import habitgoldmobile.composeapp.generated.resources.savings_setup_success_body
import habitgoldmobile.composeapp.generated.resources.savings_setup_success_title
import habitgoldmobile.composeapp.generated.resources.savings_setup_upgrade_current_amount
import habitgoldmobile.composeapp.generated.resources.savings_setup_upgrade_title
import habitgoldmobile.composeapp.generated.resources.trade_buy_apply
import habitgoldmobile.composeapp.generated.resources.trade_buy_change
import habitgoldmobile.composeapp.generated.resources.trade_buy_enter_coupon_code
import habitgoldmobile.composeapp.generated.resources.trade_buy_offers
import habitgoldmobile.composeapp.generated.resources.trade_buy_remove
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlinx.coroutines.delay
import androidx.compose.ui.platform.LocalFocusManager

@Composable
internal fun SavingsCouponRow(
    availableCoupons: Int,
    couponDraft: String,
    appliedCouponCode: String?,
    appliedBenefitText: String?,
    onCouponDraftChange: (String) -> Unit,
    onApplyCoupon: () -> Unit,
    onRemoveAppliedCoupon: () -> Unit,
    onShowOffers: () -> Unit,
    isApplyingEnabled: Boolean,
    onDone: () -> Unit,
) {
    com.habit.gold.core.presentation.CommonCouponCard(
        couponDraft = couponDraft,
        appliedCouponCode = appliedCouponCode,
        appliedBenefitText = appliedBenefitText,
        onCouponDraftChange = onCouponDraftChange,
        onApplyCoupon = onApplyCoupon,
        onRemoveAppliedCoupon = onRemoveAppliedCoupon,
        onShowOffers = onShowOffers,
        isApplyingEnabled = isApplyingEnabled,
        availableCoupons = availableCoupons,
        onDone = onDone
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SavingsAmountCard(
    value: String,
    suffixLabel: String,
    showScheduleSelector: Boolean,
    onScheduleClick: () -> Unit,
    enabled: Boolean,
    onValueChange: (String) -> Unit,
    onDone: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.5.dp, ChildCardBorder),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "₹",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = HabitGoldPalette.plum,
            )
            Spacer(modifier = Modifier.width(8.dp))
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                enabled = enabled,
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = ChildPrimaryText,
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(onDone = { onDone() }),
                cursorBrush = SolidColor(HabitGoldPalette.plum),
                modifier = Modifier.weight(1f),
                decorationBox = { innerTextField ->
                    if (value.isBlank()) {
                        Text(
                            text = "0",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFC7CAD1),
                        )
                    }
                    innerTextField()
                },
            )
            Spacer(modifier = Modifier.width(10.dp))
            Surface(
                onClick = onScheduleClick,
                enabled = showScheduleSelector,
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFF1EDFF),
                border = if (showScheduleSelector) BorderStroke(1.dp, Color(0xFFE1DAFF)) else null,
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = suffixLabel,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ChildMutedText,
                    )
                    if (showScheduleSelector) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = ChildMutedText,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun SavingsMonthlyExecutionSheet(
    month: SavingsCalendarMonth,
    selectedValue: Int?,
    onSelect: (Int) -> Unit,
) {
    val weekdayLabels = remember { listOf("M", "T", "W", "T", "F", "S", "S") }
    val days = remember(month) { (1..month.dayCount).toList() }
    val cells = remember(month) { List(month.firstDayOffset) { 0 } + days }
    val rows = remember(cells) { cells.chunked(7) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text(
            text = "${month.monthLabel} ${month.year}",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = ChildPrimaryText,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            weekdayLabels.forEach { label ->
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ChildMutedText,
                    )
                }
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            rows.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    repeat(7) { index ->
                        val value = row.getOrNull(index) ?: 0
                        if (value == 0) {
                            Spacer(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f),
                            )
                        } else {
                            val selected = value == selectedValue
                            Surface(
                                onClick = { onSelect(value) },
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f),
                                shape = RoundedCornerShape(14.dp),
                                color = if (selected) HabitGoldPalette.plum.copy(alpha = 0.10f) else Color.White,
                                border = BorderStroke(
                                    1.dp,
                                    if (selected) HabitGoldPalette.plum else ChildCardBorder,
                                ),
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = value.toString(),
                                        fontSize = 14.sp,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
                                        color = if (selected) HabitGoldPalette.plum else ChildPrimaryText,
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
internal fun SavingsExecutionSheet(
    title: String,
    options: List<SavingsExecutionOption>,
    selectedValue: Int?,
    onSelect: (Int) -> Unit,
    content: @Composable (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(horizontal = 24.dp),
            fontWeight = FontWeight.Bold,
            color = ChildPrimaryText,
        )
        content?.let {
            it()
            return
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            options.forEach { option ->
                val selected = selectedValue == option.value
                Surface(
                    onClick = { onSelect(option.value) },
                    shape = RoundedCornerShape(14.dp),
                    color = if (selected) HabitGoldPalette.plum.copy(alpha = 0.08f) else Color.White,
                    border = BorderStroke(
                        1.dp,
                        if (selected) HabitGoldPalette.plum.copy(alpha = 0.25f) else ChildCardBorder,
                    ),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = option.label,
                            color = if (selected) HabitGoldPalette.plum else ChildPrimaryText,
                            fontWeight = FontWeight.SemiBold,
                        )
                        if (selected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = HabitGoldPalette.plum,
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
internal fun SavingsCompoundingPreviewCard(
    amount: Int,
    frequency: SavingsFrequency,
    summary: SavingsCompoundingSummary,
    onInfoClick: () -> Unit,
) {
    if (amount <= 0) return
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ChildCardBorder),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFF8F8FB), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                    contentDescription = null,
                    tint = ChildMutedText,
                    modifier = Modifier.size(20.dp),
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(
                        Res.string.savings_setup_compounding_preview_intro,
                        amount,
                        frequency.routeValue.lowercase(),
                    ),
                    color = ChildMutedText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = summary.highlightText,
                        color = Color(0xFFEA7C1D),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFFC7CAD1),
                        modifier = Modifier
                            .size(14.dp)
                            .clickable(onClick = onInfoClick),
                    )
                }
            }
        }
    }
}

@Composable
internal fun SavingsNextPaymentCard(
    nextPaymentText: String,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ChildCardBorder),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF1EDFF)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = HabitGoldPalette.plum,
                    modifier = Modifier.size(14.dp),
                )
            }
            Row {
                Text(
                    text = "${stringResource(Res.string.savings_setup_next_payment_label)}: ",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = ChildMutedText,
                )
                Text(
                    text = nextPaymentText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ChildPrimaryText,
                )
            }
        }
    }
}

@Composable
internal fun SavingsCompoundingInfoSheet(
    amount: Int,
    frequency: SavingsFrequency,
    summary: SavingsCompoundingSummary,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(Res.string.savings_setup_compounding_breakdown_title),
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = ChildPrimaryText,
        )
        Text(
            text = stringResource(
                Res.string.savings_setup_compounding_breakdown_body,
                amount,
                frequency.routeValue.lowercase(),
            ),
            fontSize = 13.sp,
            color = ChildMutedText,
            lineHeight = 18.sp,
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8FB)),
            border = BorderStroke(1.dp, ChildCardBorder),
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = summary.highlightText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFEA7C1D),
                )
                SavingsSummaryRow(
                    stringResource(
                        Res.string.savings_setup_compounding_total_invested,
                        summary.projectionYears,
                    ),
                    "₹${formatRoundedMoney(summary.totalInvested)}",
                )
                SavingsSummaryRow(
                    stringResource(Res.string.savings_setup_compounding_estimated_earnings),
                    "+₹${formatRoundedMoney(summary.estimatedEarnings)}",
                    valueColor = Color(0xFF10B981),
                )
                HorizontalDivider(color = ChildCardBorder)
                SavingsSummaryRow(
                    stringResource(Res.string.savings_setup_compounding_maturity_value),
                    "₹${formatRoundedMoney(summary.totalValue)}",
                    valueColor = HabitGoldPalette.plum,
                    valueWeight = FontWeight.ExtraBold,
                )
                HorizontalDivider(color = ChildCardBorder)
                SavingsSummaryRow(
                    stringResource(Res.string.savings_setup_compounding_frequency),
                    frequency.displayName(),
                )
                SavingsSummaryRow(
                    stringResource(Res.string.savings_setup_compounding_investment_amount),
                    "₹${formatRoundedMoney(amount.toDouble())}",
                )
                SavingsSummaryRow(
                    stringResource(Res.string.savings_setup_compounding_monthly_equivalent),
                    "₹${formatRoundedMoney(summary.monthlyInvestment)}",
                )
            }
        }
        Text(
            text = stringResource(Res.string.savings_setup_compounding_illustrative_note),
            fontSize = 12.sp,
            color = ChildMutedText,
            lineHeight = 18.sp,
        )
    }
}

@Composable
internal fun SavingsSummaryRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = ChildPrimaryText,
    valueWeight: FontWeight = FontWeight.Bold,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = ChildMutedText,
        )
        Text(
            text = value,
            fontSize = 13.sp,
            color = valueColor,
            fontWeight = valueWeight,
        )
    }
}

internal fun savingsAppliedCouponSummary(
    validation: com.habit.gold.feature.trade.domain.model.TradeCouponValidation,
): String? {
    fun hasValue(raw: String?): Boolean = raw
        ?.trim()
        ?.takeIf { it.isNotBlank() }
        ?.toDoubleOrNull()
        ?.let { it > 0.0 }
        ?: false

    val parts = buildList {
        validation.promotionalDiscount.takeIf(::hasValue)?.let { add("₹$it off") }
        validation.promotionalCashback.takeIf(::hasValue)?.let { add("₹$it cashback") }
        validation.promotionalExtraGold.takeIf(::hasValue)?.let { add("+$it g gold") }
    }
    return parts.takeIf { it.isNotEmpty() }?.joinToString(" • ")
}

internal fun formatRoundedMoney(value: Double): String = formatInr(value)

@Composable
internal fun SavingsSetupBottomBar(
    livePriceState: TradeLivePriceState,
    isSubmitting: Boolean,
    submitLabel: String,
    onSubmit: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding(),
        color = Color.White,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 12.dp),
        ) {
            HorizontalDivider(color = ChildCardBorder)
            BuyTradeLivePriceBar(livePriceState = livePriceState)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
            ) {
                AppPrimaryButton(
                    label = submitLabel,
                    onClick = onSubmit,
                    enabled = !isSubmitting,
                )
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 20.dp)
                            .size(18.dp),
                        strokeWidth = 2.dp,
                        color = Color.White,
                    )
                }
            }
        }
    }
}

@Composable
internal fun SavingsSetupProgressState(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier = Modifier.padding(horizontal = 24.dp),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, ChildCardBorder),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                CircularProgressIndicator(color = HabitGoldPalette.plum)
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    color = ChildPrimaryText,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = body,
                    color = ChildMutedText,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                )
            }
        }
    }
}

@Composable
internal fun SavingsSetupTerminalState(
    icon: ImageVector,
    title: String,
    body: String,
    primaryLabel: String,
    onPrimaryClick: () -> Unit,
    secondaryLabel: String? = null,
    onSecondaryClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier = Modifier.padding(horizontal = 24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, ChildCardBorder),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(HabitGoldPalette.plum.copy(alpha = 0.08f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = HabitGoldPalette.plum,
                        modifier = Modifier.size(34.dp),
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = ChildPrimaryText,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = body,
                    color = ChildMutedText,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                )
                Box(modifier = Modifier.fillMaxWidth()) {
                    AppPrimaryButton(
                        label = primaryLabel,
                        onClick = onPrimaryClick,
                    )
                }
                if (secondaryLabel != null && onSecondaryClick != null) {
                    Button(
                        onClick = onSecondaryClick,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = HabitGoldPalette.plum,
                        ),
                        border = BorderStroke(1.dp, HabitGoldPalette.plum.copy(alpha = 0.25f)),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Text(text = secondaryLabel, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

internal data class SavingsExecutionOption(
    val value: Int,
    val label: String,
)

@Composable
internal fun SavingsSetupUiState.setupTitle(): String {
    if (isPausedMandate) return stringResource(Res.string.savings_setup_resume_cta, frequency.displayName())
    if (isUpgradeFlow) return stringResource(Res.string.savings_setup_upgrade_title, frequency.displayName())
    return when (frequency) {
        SavingsFrequency.Daily -> stringResource(Res.string.savings_setup_frequency_daily_title)
        SavingsFrequency.Weekly -> stringResource(Res.string.savings_setup_frequency_weekly_title)
        SavingsFrequency.Monthly -> stringResource(Res.string.savings_setup_frequency_monthly_title)
    }
}

@Composable
internal fun SavingsSetupUiState.submitLabel(): String {
    return when {
        isPausedMandate -> stringResource(Res.string.savings_setup_resume_cta, frequency.displayName())
        isUpgradeFlow -> stringResource(Res.string.savings_setup_submit_upgrade, frequency.displayName())
        frequency == SavingsFrequency.Daily -> stringResource(Res.string.savings_setup_frequency_daily_cta)
        frequency == SavingsFrequency.Weekly -> stringResource(Res.string.savings_setup_frequency_weekly_cta)
        else -> stringResource(Res.string.savings_setup_frequency_monthly_cta)
    }
}
