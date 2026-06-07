package com.habit.gold.feature.trade.presentation.buy

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import com.habit.gold.core.designsystem.icons.HabitGoldIcons as Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habit.gold.feature.trade.presentation.formatGoldQuantity
import com.habit.gold.feature.trade.presentation.formatMoney
import com.habit.gold.feature.trade.presentation.roundToGoldScale
import com.habit.gold.feature.trade.presentation.roundToMoney
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.common_gold_unit_short
import habitgoldmobile.composeapp.generated.resources.common_safegold
import habitgoldmobile.composeapp.generated.resources.safegold_image
import habitgoldmobile.composeapp.generated.resources.home_screen_powered_by
import habitgoldmobile.composeapp.generated.resources.trade_buy_coupon_min_order_required
import habitgoldmobile.composeapp.generated.resources.trade_buy_mode_grams
import habitgoldmobile.composeapp.generated.resources.trade_buy_mode_rupees
import habitgoldmobile.composeapp.generated.resources.trade_buy_popular_tag
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt
import kotlin.math.abs

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
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .clip(RoundedCornerShape(32.dp))
            .background(BuySlate50)
            .border(1.dp, BuySlate200, RoundedCornerShape(32.dp))
            .padding(3.dp),
    ) {
        val segmentWidth = maxWidth / 2
        val selectorOffset by animateDpAsState(
            targetValue = if (activeMode == BuyTradeEntryMode.Rupees) 0.dp else segmentWidth,
            animationSpec = tween(durationMillis = 280),
            label = "buyTradeModeSelectorOffset",
        )
        Box(
            modifier = Modifier
                .width(segmentWidth)
                .height(40.dp)
                .padding(end = 3.dp)
                .offset(x = selectorOffset)
                .clip(RoundedCornerShape(32.dp))
                .background(BuyWhite)
                .border(1.dp, BuySlate200, RoundedCornerShape(32.dp)),
        ) {}
        Row(modifier = Modifier.fillMaxWidth()) {
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
}

@Composable
internal fun BuyTradeModeChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val labelColor by animateColorAsState(
        targetValue = if (selected) BuyPrimary else BuySlate500,
        animationSpec = tween(durationMillis = 220),
        label = "buyTradeModeChipLabel",
    )
    Box(
        modifier = modifier
            .height(40.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = labelColor,
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
                text = "g",
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
        Icon(
            imageVector = if (symbol == "+") Icons.Default.Add else Icons.Default.Remove,
            contentDescription = null,
            tint = BuyPrimary,
            modifier = Modifier.size(18.dp),
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
        Text(
            "0.1 ${stringResource(Res.string.common_gold_unit_short)}",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = BuySlate400,
        )
        Text(
            "${formatGramsPlain(parseOneTimeGrams(maxValue.toString(), maxValue.toDouble()))} ${stringResource(Res.string.common_gold_unit_short)}",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = BuySlate400,
        )
    }
}

@Composable
internal fun BuyTradeQuickAmounts(
    entryMode: BuyTradeEntryMode,
    maxSelectableGrams: Double,
    selectedValue: String,
    onSelectRupees: (String) -> Unit,
    onSelectGrams: (String) -> Unit,
) {
    val chips = if (entryMode == BuyTradeEntryMode.Rupees) {
        listOf("₹1000", "₹5000", "₹10000")
    } else {
        listOf("0.5g", "1g", "2g")
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        chips.forEachIndexed { index, chip ->
            val isPopular = chip == "₹10000" || chip == "2g"
            val chipValue = if (entryMode == BuyTradeEntryMode.Rupees) {
                chip.removePrefix("₹")
            } else {
                chip.removeSuffix("g")
            }
            val isSelected = when (entryMode) {
                BuyTradeEntryMode.Rupees -> selectedValue == chipValue
                BuyTradeEntryMode.Grams -> {
                    val selected = selectedValue.toDoubleOrNull()
                    val chipNumeric = chipValue.toDoubleOrNull()
                    selected != null && chipNumeric != null && abs(selected - chipNumeric) < 0.0001
                }
            }
            Column(
                modifier = Modifier.width(84.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(0.dp),
            ) {

                OutlinedButton(
                    onClick = {
                        if (entryMode == BuyTradeEntryMode.Rupees) {
                            val target = when (chip) {
                                "₹1000" -> "1000"
                                "₹5000" -> "5000"
                                else -> "10000"
                            }
                            onSelectRupees(target)
                        } else {
                            val targetGrams = when (chip) {
                                "0.5g" -> 0.5
                                "1g" -> 1.0
                                "2g" -> 2.0
                                else -> maxSelectableGrams
                            }
                            onSelectGrams(formatGramsPlain(parseOneTimeGrams(targetGrams.toString(), maxSelectableGrams)))
                        }
                    },
                    modifier = Modifier
                        .width(84.dp),
                    shape = if (!isPopular) RoundedCornerShape(8.dp) else
                        RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomEnd = 0.dp, bottomStart = 0.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        if (isSelected) 1.5.dp else 0.5.dp,
                        if (isSelected) BuyPrimary else BuySlate200,
                    ),
                    contentPadding = PaddingValues(0.dp),
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        text = chip,
                        fontSize = 13.sp,
                        color = if (isSelected) BuyPrimary else BuySlate950,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                    )
                }

                if (isPopular) {
                    Surface(
                        modifier = Modifier
                            .width(84.dp)
                            .offset(y = (-6).dp)
                            .height(20.dp),
                        shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomEnd = 8.dp, bottomStart = 8.dp),
                        color = BuyPrimary,
                    ) {
                        Text(
                            text = stringResource(Res.string.trade_buy_popular_tag),
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(y = (-2).dp)
                                .padding(horizontal = 7.dp),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BuyWhite,
                            textAlign = TextAlign.Center,
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            if (index < chips.lastIndex) {
                Spacer(modifier = Modifier.width(12.dp))
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
internal fun BuyTradePoweredByRow(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
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
    fallbackAmount: Double,
): String? {
    return appliedCouponSummary(validation = validation, fallbackAmount = fallbackAmount)
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
    val baseTotalPayable: Double,
    val totalPayable: Double,
    val goldValue: Double,
    val gstAmount: Double,
    val goldQuantity: Double,
    val couponDiscount: Double = 0.0,
    val appliedCouponCode: String? = null,
)

internal fun calculateBuyTrade(
    entryMode: BuyTradeEntryMode,
    numericRupees: Double,
    numericGrams: Double,
    goldPrice: Double,
    gstRate: Double,
    appliedCoupon: com.habit.gold.feature.trade.domain.model.TradeCouponValidation? = null,
): BuyTradeCalculation {
    return when (entryMode) {
        BuyTradeEntryMode.Rupees -> {
            val baseTotalAmount = numericRupees
            val gstAmount = inclusiveGstAmount(baseTotalAmount, gstRate)
            val goldValue = (baseTotalAmount - gstAmount).coerceAtLeast(0.0)
            val goldQuantity = if (goldPrice > 0.0) roundToGoldScale(goldValue / goldPrice) else 0.0
            val payableAmount = netPayableFromValidation(appliedCoupon, baseTotalAmount)
            BuyTradeCalculation(
                baseTotalPayable = baseTotalAmount,
                totalPayable = payableAmount,
                goldValue = goldValue,
                gstAmount = gstAmount,
                goldQuantity = goldQuantity,
                couponDiscount = roundToMoney((baseTotalAmount - payableAmount).coerceAtLeast(0.0)),
                appliedCouponCode = appliedCoupon?.code,
            )
        }
        BuyTradeEntryMode.Grams -> {
            val goldQuantity = roundToGoldScale(numericGrams)
            val goldValue = roundToMoney(goldQuantity * goldPrice)
            val gstAmount = roundToMoney(goldValue * gstRate)
            val baseTotalPayable = roundToMoney(goldValue + gstAmount)
            val payableAmount = netPayableFromValidation(appliedCoupon, baseTotalPayable)
            BuyTradeCalculation(
                baseTotalPayable = baseTotalPayable,
                totalPayable = payableAmount,
                goldValue = goldValue,
                gstAmount = gstAmount,
                goldQuantity = goldQuantity,
                couponDiscount = roundToMoney((baseTotalPayable - payableAmount).coerceAtLeast(0.0)),
                appliedCouponCode = appliedCoupon?.code,
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
    return (value.toDoubleOrNull() ?: 0.1).coerceIn(0.1, maxGrams)
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

internal fun netPayableFromValidation(
    validation: com.habit.gold.feature.trade.domain.model.TradeCouponValidation?,
    fallback: Double,
): Double {
    val netAmount = validation?.netOrderAmount?.trim()?.toDoubleOrNull()
    return when {
        netAmount == null || netAmount <= 0.0 -> fallback
        else -> roundToMoney(netAmount)
    }
}

internal fun promoAmount(value: String?): Double = value?.trim()?.toDoubleOrNull() ?: 0.0

private fun formatPromoRupees(value: Double): String {
    val rounded = roundToMoney(value)
    return if (abs(rounded - rounded.toInt()) < 0.01) {
        rounded.toInt().toString()
    } else {
        formatMoney(rounded)
    }
}

internal fun couponBenefitDisplay(
    validation: com.habit.gold.feature.trade.domain.model.TradeCouponValidation,
): Pair<Boolean, String> {
    val rupeeTotal = roundToMoney(
        promoAmount(validation.promotionalDiscount) +
            promoAmount(validation.promotionalCashback) +
            promoAmount(validation.promotionalDeliveryDiscount),
    )
    val extraGold = promoAmount(validation.promotionalExtraGold)
    val parts = buildList {
        if (rupeeTotal > 0.0) {
            add("-₹${formatMoney(rupeeTotal)}")
        }
        if (extraGold > 0.0) {
            add("+${validation.promotionalExtraGold.trim()} g gold")
        }
    }
    return parts.isNotEmpty() to parts.joinToString(" • ")
}

internal fun appliedCouponSummary(
    validation: com.habit.gold.feature.trade.domain.model.TradeCouponValidation?,
    fallbackAmount: Double,
    payLabel: String? = null,
): String? {
    validation ?: return null
    val (showBenefit, benefitText) = couponBenefitDisplay(validation)
    val net = netPayableFromValidation(validation, fallbackAmount)
    val saved = roundToMoney((fallbackAmount - net).coerceAtLeast(0.0))
    val parts = buildList {
        if (showBenefit) {
            add(benefitText)
        } else if (saved > 0.01) {
            add("Save ₹${formatMoney(saved)}")
        }
        if (payLabel != null && net > 0.0 && abs(net - fallbackAmount) > 0.01) {
            add("$payLabel ₹${formatMoney(net)}")
        }
    }
    return parts.takeIf { it.isNotEmpty() }?.joinToString(" • ")
}

internal fun couponBreakdownDisplay(
    validation: com.habit.gold.feature.trade.domain.model.TradeCouponValidation?,
    grossAmount: Double,
    payableAmount: Double,
    appliedCouponCode: String? = validation?.code,
): Pair<String, String>? {
    val code = appliedCouponCode?.trim()?.takeIf { it.isNotEmpty() } ?: return null
    validation ?: return "Coupon ($code)" to "Applied"
    val payableReduction = roundToMoney((grossAmount - payableAmount).coerceAtLeast(0.0))
    val value = if (payableReduction > 0.01) {
        "-₹${formatMoney(payableReduction)}"
    } else {
        val (_, benefitText) = couponBenefitDisplay(validation)
        benefitText.takeIf { it.isNotBlank() } ?: "Applied"
    }
    return "Coupon ($code)" to value
}
