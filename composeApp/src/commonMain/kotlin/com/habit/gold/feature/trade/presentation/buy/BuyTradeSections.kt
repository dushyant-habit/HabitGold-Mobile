package com.habit.gold.feature.trade.presentation.buy

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.habit.gold.feature.trade.domain.TradeLivePriceState
import com.habit.gold.feature.trade.domain.usecase.GetTradeInvoiceUseCase
import com.habit.gold.core.network.ApiResult
import com.habit.gold.feature.trade.presentation.formatCountdown
import com.habit.gold.feature.trade.presentation.formatGoldQuantity
import com.habit.gold.feature.trade.presentation.formatMoney
import com.habit.gold.feature.trade.presentation.formatPercent
import com.habit.gold.feature.trade.presentation.roundToGoldScale
import com.habit.gold.feature.trade.presentation.roundToMoney
import com.habit.gold.feature.trade.presentation.sanitizeGramInput
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.buy_gold_screen_buy_gold
import habitgoldmobile.composeapp.generated.resources.common_go_to_dashboard
import habitgoldmobile.composeapp.generated.resources.common_help
import habitgoldmobile.composeapp.generated.resources.common_safegold
import habitgoldmobile.composeapp.generated.resources.common_transaction_details
import habitgoldmobile.composeapp.generated.resources.safegold_image
import habitgoldmobile.composeapp.generated.resources.home_screen_powered_by
import habitgoldmobile.composeapp.generated.resources.trade_buy_amount_to_be_paid
import habitgoldmobile.composeapp.generated.resources.trade_buy_applied_coupon_format
import habitgoldmobile.composeapp.generated.resources.trade_buy_apply
import habitgoldmobile.composeapp.generated.resources.trade_buy_breakdown_title
import habitgoldmobile.composeapp.generated.resources.trade_buy_chip_max
import habitgoldmobile.composeapp.generated.resources.trade_buy_change
import habitgoldmobile.composeapp.generated.resources.trade_buy_coupon_discount_off
import habitgoldmobile.composeapp.generated.resources.trade_buy_coupon_min_order_required
import habitgoldmobile.composeapp.generated.resources.trade_buy_coupon_sheet_empty
import habitgoldmobile.composeapp.generated.resources.trade_buy_coupon_sheet_subtitle
import habitgoldmobile.composeapp.generated.resources.trade_buy_enter_amount
import habitgoldmobile.composeapp.generated.resources.trade_buy_enter_coupon_code
import habitgoldmobile.composeapp.generated.resources.trade_buy_fact_earn_extra_gold
import habitgoldmobile.composeapp.generated.resources.trade_buy_fact_insured_vaults
import habitgoldmobile.composeapp.generated.resources.trade_buy_fact_pure_gold
import habitgoldmobile.composeapp.generated.resources.trade_buy_fact_pure_gold_short
import habitgoldmobile.composeapp.generated.resources.trade_buy_fact_start_small
import habitgoldmobile.composeapp.generated.resources.trade_buy_gold_value
import habitgoldmobile.composeapp.generated.resources.trade_buy_gst
import habitgoldmobile.composeapp.generated.resources.trade_buy_live_price_label
import habitgoldmobile.composeapp.generated.resources.trade_buy_live_price_unavailable
import habitgoldmobile.composeapp.generated.resources.trade_buy_latest_order_title
import habitgoldmobile.composeapp.generated.resources.trade_buy_max_upi_limit_message
import habitgoldmobile.composeapp.generated.resources.trade_buy_mode_grams
import habitgoldmobile.composeapp.generated.resources.trade_buy_mode_rupees
import habitgoldmobile.composeapp.generated.resources.trade_buy_offers
import habitgoldmobile.composeapp.generated.resources.trade_buy_pay_now
import habitgoldmobile.composeapp.generated.resources.trade_buy_pending_body
import habitgoldmobile.composeapp.generated.resources.trade_buy_pending_title
import habitgoldmobile.composeapp.generated.resources.trade_buy_plus_gst
import habitgoldmobile.composeapp.generated.resources.trade_buy_quantity
import habitgoldmobile.composeapp.generated.resources.trade_buy_remove
import habitgoldmobile.composeapp.generated.resources.trade_buy_retry
import habitgoldmobile.composeapp.generated.resources.trade_buy_select_grams
import habitgoldmobile.composeapp.generated.resources.trade_buy_success_body
import habitgoldmobile.composeapp.generated.resources.trade_buy_success_title
import habitgoldmobile.composeapp.generated.resources.trade_buy_status_completed
import habitgoldmobile.composeapp.generated.resources.trade_buy_available_coupons_title
import habitgoldmobile.composeapp.generated.resources.trade_buy_total_payable
import habitgoldmobile.composeapp.generated.resources.trade_buy_updating_price
import habitgoldmobile.composeapp.generated.resources.trade_buy_updates_in
import habitgoldmobile.composeapp.generated.resources.trade_buy_verifying_body
import habitgoldmobile.composeapp.generated.resources.trade_buy_verifying_title
import habitgoldmobile.composeapp.generated.resources.trade_buy_view_breakdown
import habitgoldmobile.composeapp.generated.resources.trade_buy_youre_buying
import habitgoldmobile.composeapp.generated.resources.trade_buy_amount_paid
import habitgoldmobile.composeapp.generated.resources.trade_buy_error_code
import habitgoldmobile.composeapp.generated.resources.trade_buy_failure_body
import habitgoldmobile.composeapp.generated.resources.trade_buy_failure_code_fallback
import habitgoldmobile.composeapp.generated.resources.trade_buy_failure_title
import habitgoldmobile.composeapp.generated.resources.trade_buy_gold_credited
import habitgoldmobile.composeapp.generated.resources.trade_buy_processing_order_id_label
import habitgoldmobile.composeapp.generated.resources.trade_buy_processing_purchase_body
import habitgoldmobile.composeapp.generated.resources.trade_buy_processing_purchase_title
import habitgoldmobile.composeapp.generated.resources.trade_buy_secure_100_percent
import habitgoldmobile.composeapp.generated.resources.trade_buy_secure_bhim_registered
import habitgoldmobile.composeapp.generated.resources.trade_invoice_viewer_invalid_url
import habitgoldmobile.composeapp.generated.resources.trade_transaction_details_view_invoice
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
internal fun BuyTradeInfoPill(
    facts: List<String>,
    currentFactIndex: Int,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = BuyGoldTint,
            modifier = Modifier.size(16.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        AnimatedContent(
            targetState = currentFactIndex,
            transitionSpec = {
                if (targetState > initialState) {
                    (slideInVertically { height -> height } + fadeIn()).togetherWith(
                        slideOutVertically { height -> -height } + fadeOut(),
                    )
                } else {
                    (slideInVertically { height -> -height } + fadeIn()).togetherWith(
                        slideOutVertically { height -> height } + fadeOut(),
                    )
                }.using(SizeTransform(clip = false))
            },
            label = "BuyFactAnimation",
        ) { targetIndex ->
            Text(
                text = facts[targetIndex],
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = BuyGoldTint,
            )
        }
    }
}

@Composable
internal fun BuyTradeEntryModeTabs(
    activeMode: BuyTradeEntryMode,
    onSelectMode: (BuyTradeEntryMode) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(40.dp))
            .background(BuySlate50)
            .border(1.dp, BuySlate200, RoundedCornerShape(40.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        BuyTradeModeChip(
            label = stringResource(Res.string.trade_buy_mode_rupees),
            selected = activeMode == BuyTradeEntryMode.Rupees,
            onClick = { onSelectMode(BuyTradeEntryMode.Rupees) },
            modifier = Modifier.weight(1f),
        )
        BuyTradeModeChip(
            label = stringResource(Res.string.trade_buy_mode_grams),
            selected = activeMode == BuyTradeEntryMode.Grams,
            onClick = { onSelectMode(BuyTradeEntryMode.Grams) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
internal fun BuyTradeModeChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(40.dp))
            .background(if (selected) BuyWhite else Color.Transparent)
            .then(
                if (selected) {
                    Modifier.border(1.dp, BuySlate200, RoundedCornerShape(40.dp))
                } else {
                    Modifier
                },
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 32.dp, vertical = 11.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = if (selected) BuyPrimary else BuySlate500,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
internal fun BuyTradeRupeeInput(
    value: String,
    onDone: () -> Unit,
    onValueChange: (String) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BuySlate50, RoundedCornerShape(12.dp))
            .padding(vertical = 12.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "₹",
            fontSize = 42.sp,
            color = BuySlate400,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.align(Alignment.CenterStart),
        )
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                fontSize = 42.sp,
                fontWeight = FontWeight.Black,
                color = BuySlate950,
                textAlign = TextAlign.Center,
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(onDone = { onDone() }),
            singleLine = true,
            cursorBrush = SolidColor(BuyPrimary),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 48.dp),
            decorationBox = { innerTextField ->
                Box(contentAlignment = Alignment.Center) {
                    if (value.isEmpty()) {
                        Text(
                            text = "0",
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Black,
                            color = BuySlate300,
                            textAlign = TextAlign.Center,
                        )
                    }
                    innerTextField()
                }
            },
        )
    }
}

@Composable
internal fun BuyTradeGramInput(
    value: String,
    maxSelectableGrams: Double,
    onDone: () -> Unit,
    onValueChange: (String) -> Unit,
    onStepDown: () -> Unit,
    onStepUp: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BuyTradeCircleStepper("-", onClick = {
            if (parseOneTimeGrams(value, maxSelectableGrams) > 0.1) onStepDown()
        })

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1f)
                .background(BuySlate50, RoundedCornerShape(12.dp))
                .padding(vertical = 6.dp),
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black,
                    color = BuySlate950,
                    textAlign = TextAlign.Center,
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(onDone = { onDone() }),
                singleLine = true,
                cursorBrush = SolidColor(BuyPrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.Center) {
                        if (value.isEmpty()) {
                            Text(
                                text = "0.0",
                                fontSize = 42.sp,
                                fontWeight = FontWeight.Black,
                                color = BuySlate300,
                                textAlign = TextAlign.Center,
                            )
                        }
                        innerTextField()
                    }
                },
            )

            Text(
                text = "gm",
                fontSize = 20.sp,
                color = BuySlate500,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 12.dp),
            )
        }

        BuyTradeCircleStepper("+", onClick = {
            val current = value.toDoubleOrNull() ?: 0.1
            val next = (current + 0.1).coerceAtMost(maxSelectableGrams)
            if (next > current) onStepUp()
        })
    }
}

@Composable
internal fun BuyTradeCircleStepper(
    symbol: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .background(BuyWhite)
            .border(1.dp, BuyPrimary, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = symbol,
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            color = BuyPrimary,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BuyTradeGramSlider(
    value: Float,
    maxValue: Float,
    onValueChange: (Float) -> Unit,
) {
    androidx.compose.material3.Slider(
        value = value.coerceIn(0.1f, maxValue),
        onValueChange = onValueChange,
        valueRange = 0.1f..maxValue,
        steps = (((maxValue - 0.1f) * 10f).toInt() - 1).coerceAtLeast(0),
        modifier = Modifier.fillMaxWidth(),
        thumb = {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(BuyPrimary, CircleShape)
                    .border(2.dp, BuyWhite, CircleShape),
            )
        },
        track = { sliderState ->
            val fraction = (sliderState.value - sliderState.valueRange.start) /
                (sliderState.valueRange.endInclusive - sliderState.valueRange.start)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(BuySlate200),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction)
                        .height(6.dp)
                        .background(BuyPrimary),
                )
            }
        },
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text("0.1 gm", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BuySlate400)
        Text("${formatGramsPlain(parseOneTimeGrams(maxValue.toString(), maxValue.toDouble()))} gm", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BuySlate400)
    }
}

@Composable
internal fun BuyTradeQuickAmounts(
    entryMode: BuyTradeEntryMode,
    maxSelectableGrams: Double,
    onSelectRupees: (String) -> Unit,
    onSelectGrams: (String) -> Unit,
) {
    val chips = if (entryMode == BuyTradeEntryMode.Rupees) {
        listOf("1000", "5000", stringResource(Res.string.trade_buy_chip_max))
    } else {
        listOf("1g", "5g", stringResource(Res.string.trade_buy_chip_max))
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        chips.forEachIndexed { index, chip ->
            Box(
                modifier = Modifier
                    .width(74.dp)
                    .height(34.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .clickable {
                        if (entryMode == BuyTradeEntryMode.Rupees) {
                            val target = when (chip) {
                                "1000" -> "1000"
                                "5000" -> "5000"
                                else -> OneTimeUpiLimit.toInt().toString()
                            }
                            onSelectRupees(target)
                        } else {
                            val targetGrams = when (chip) {
                                "1g" -> 1.0
                                "5g" -> 5.0
                                else -> maxSelectableGrams
                            }
                            onSelectGrams(formatGramsPlain(parseOneTimeGrams(targetGrams.toString(), maxSelectableGrams)))
                        }
                    }
                    .background(BuyWhite)
                    .border(1.dp, BuySlate200, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = chip,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = BuySlate600,
                )
            }

            if (index < chips.lastIndex) {
                Spacer(modifier = Modifier.width(10.dp))
            }
        }
    }
}

@Composable
internal fun BuyTradeCouponRow(
    availableCoupons: Int,
    couponDraft: String,
    appliedCouponCode: String?,
    appliedBenefitText: String?,
    onCouponDraftChange: (String) -> Unit,
    onApplyCoupon: () -> Unit,
    onRemoveAppliedCoupon: () -> Unit,
    onShowOffers: () -> Unit,
    isApplyingEnabled: Boolean,
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
        availableCoupons = availableCoupons
    )
}

@Composable
internal fun BuyTradePoweredByRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(Res.string.home_screen_powered_by),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = BuySlate400,
            letterSpacing = 1.2.sp,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Image(
            painter = painterResource(Res.drawable.safegold_image),
            contentDescription = stringResource(Res.string.common_safegold),
            modifier = Modifier.height(10.dp),
            contentScale = ContentScale.Fit,
        )
    }
}

@Composable
internal fun buyTradeAppliedCouponSummary(
    validation: com.habit.gold.feature.trade.domain.model.TradeCouponValidation,
): String? {
    val parts = buildList {
//        validation.promotionalCashback.takeIf { it != "0" && it.isNotBlank() }?.let { add("₹$it cashback") }
        validation.promotionalDiscount.takeIf { it != "0" && it.isNotBlank() }?.let {
            add(stringResource(Res.string.trade_buy_coupon_discount_off, it))
        }
//        validation.promotionalExtraGold.takeIf { it != "0" && it.isNotBlank() }?.let { add("+$it g gold") }
//        validation.promotionalDeliveryDiscount.takeIf { it != "0" && it.isNotBlank() }?.let { add("₹$it delivery off") }
    }
    return parts.takeIf { it.isNotEmpty() }?.joinToString(" • ")
}

internal fun buyCouponMinOrderAmount(
    coupon: com.habit.gold.feature.trade.domain.model.TradeAvailableCoupon,
): Double? = coupon.minOrderValue?.toDoubleOrNull()

internal fun isBuyCouponApplicable(
    coupon: com.habit.gold.feature.trade.domain.model.TradeAvailableCoupon,
    estimateAmount: Double,
): Boolean {
    val minOrder = buyCouponMinOrderAmount(coupon) ?: return true
    return estimateAmount >= minOrder
}

@Composable
internal fun buyCouponDisabledReason(
    coupon: com.habit.gold.feature.trade.domain.model.TradeAvailableCoupon,
    estimateAmount: Double,
): String? {
    val minOrder = buyCouponMinOrderAmount(coupon) ?: return null
    if (estimateAmount >= minOrder) return null
    return stringResource(Res.string.trade_buy_coupon_min_order_required, formatMoney(minOrder))
}

internal data class BuyTradeCalculation(
    val totalPayable: Double,
    val goldValue: Double,
    val gstAmount: Double,
    val goldQuantity: Double,
)

internal fun calculateBuyTrade(
    entryMode: BuyTradeEntryMode,
    numericRupees: Double,
    numericGrams: Double,
    goldPrice: Double,
    gstRate: Double,
): BuyTradeCalculation {
    return when (entryMode) {
        BuyTradeEntryMode.Rupees -> {
            val totalAmount = numericRupees
            val gstAmount = inclusiveGstAmount(totalAmount, gstRate)
            val goldValue = (totalAmount - gstAmount).coerceAtLeast(0.0)
            val goldQuantity = if (goldPrice > 0.0) roundToGoldScale(goldValue / goldPrice) else 0.0
            BuyTradeCalculation(
                totalPayable = totalAmount,
                goldValue = goldValue,
                gstAmount = gstAmount,
                goldQuantity = goldQuantity,
            )
        }
        BuyTradeEntryMode.Grams -> {
            val goldQuantity = roundToGoldScale(numericGrams)
            val goldValue = roundToMoney(goldQuantity * goldPrice)
            val gstAmount = roundToMoney(goldValue * gstRate)
            BuyTradeCalculation(
                totalPayable = roundToMoney(goldValue + gstAmount),
                goldValue = goldValue,
                gstAmount = gstAmount,
                goldQuantity = goldQuantity,
            )
        }
    }
}

internal fun maxSelectableGrams(goldPrice: Double, gstRate: Double): Double {
    return if (goldPrice > 0.0) {
        ((OneTimeUpiLimit * (1.0 - gstRate)) / goldPrice).coerceIn(0.1, 99.9)
    } else {
        99.9
    }
}

internal fun inclusiveGstAmount(totalAmount: Double, gstRate: Double): Double {
    if (totalAmount <= 0.0 || gstRate <= 0.0) return 0.0
    return roundToMoney(totalAmount * gstRate / (1.0 + gstRate))
}

internal fun parseOneTimeGrams(value: String, maxGrams: Double): Double {
    return (value.toDoubleOrNull() ?: 0.5).coerceIn(0.1, maxGrams)
}

internal fun stepOneTimeGrams(current: String, deltaSteps: Int, maxGrams: Double): String {
    val currentValue = parseOneTimeGrams(current, maxGrams)
    val steppedValue = (currentValue + (deltaSteps * 0.1)).coerceIn(0.1, maxGrams)
    return formatGramsPlain(steppedValue)
}

internal fun formatGramsPlain(value: Double): String {
    val scaled = ((value * 10).roundToInt() / 10.0)
    return scaled.toString().trimEnd('0').trimEnd('.').ifBlank { "0" }
}

internal fun formatConversionGrams(value: Double): String {
    return formatGoldQuantity(value)
}
