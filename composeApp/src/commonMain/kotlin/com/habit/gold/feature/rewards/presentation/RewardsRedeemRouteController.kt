package com.habit.gold.feature.rewards.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.habit.gold.core.presentation.PlatformBackHandler
import com.habit.gold.core.presentation.clearFocusOnTapOutside
import com.habit.gold.feature.trade.presentation.TradeRouteDependencies
import com.habit.gold.feature.trade.presentation.buy.BuyTradeLivePriceBar
import com.habit.gold.feature.trade.presentation.buy.BuyTradeEffect
import com.habit.gold.feature.trade.presentation.buy.BuyTradeIntent
import com.habit.gold.feature.trade.presentation.buy.BuyTradeStep
import com.habit.gold.feature.trade.presentation.buy.BuyTradeViewModel
import kotlin.math.min
import kotlin.math.floor
import kotlin.math.round

@Composable
fun RewardsRedeemRouteController(
    rewardsState: RewardsHomeState,
    tradeDependencies: TradeRouteDependencies,
    onBackClick: () -> Unit,
    onRefreshRewards: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val buyTradeViewModel = viewModel {
        BuyTradeViewModel(
            createBuyOrderUseCase = tradeDependencies.createBuyOrderUseCase,
            getTradeAvailableCouponsUseCase = tradeDependencies.getTradeAvailableCouponsUseCase,
            validateTradeCouponUseCase = tradeDependencies.validateTradeCouponUseCase,
            pollTradeStatusUseCase = tradeDependencies.pollTradeStatusUseCase,
        )
    }
    val buyState by buyTradeViewModel.state.collectAsStateWithLifecycle()
    val livePriceState by tradeDependencies.livePriceStore.state.collectAsStateWithLifecycle()

    LaunchedEffect(buyTradeViewModel) {
        buyTradeViewModel.effects.collect { effect ->
            when (effect) {
                is BuyTradeEffect.LaunchPayment -> {
                    val result = tradeDependencies.paymentLauncher.launch(effect.request)
                    buyTradeViewModel.onIntent(BuyTradeIntent.HandlePaymentResult(result))
                }
                is BuyTradeEffect.ShowMessage -> Unit
            }
        }
    }

    LaunchedEffect(buyState.step) {
        if (buyState.step == BuyTradeStep.Success || buyState.step == BuyTradeStep.Pending) {
            onRefreshRewards()
        }
    }

    PlatformBackHandler(
        enabled = buyState.step != BuyTradeStep.Processing,
        onBack = {
            if (buyState.step == BuyTradeStep.Entry) {
                onBackClick()
            } else {
                buyTradeViewModel.onIntent(BuyTradeIntent.ResetToEntry)
                onBackClick()
            }
        },
    )

    RewardsRedeemScreen(
        rewardsState = rewardsState,
        buyState = buyState,
        livePriceState = livePriceState,
        onBackClick = onBackClick,
        onAmountSubmit = { amount, buyRateId ->
            buyTradeViewModel.onIntent(
                BuyTradeIntent.SubmitOneTimeOrder(
                    amount = amount,
                    grams = null,
                    buyRateId = buyRateId,
                    couponCode = null,
                    useRewardsInr = amount,
                ),
            )
        },
        onReset = { buyTradeViewModel.onIntent(BuyTradeIntent.ResetToEntry) },
        modifier = modifier,
    )
}

@Composable
private fun RewardsRedeemScreen(
    rewardsState: RewardsHomeState,
    buyState: com.habit.gold.feature.trade.presentation.buy.BuyTradeState,
    livePriceState: com.habit.gold.feature.trade.domain.TradeLivePriceState,
    onBackClick: () -> Unit,
    onAmountSubmit: (amount: Double, buyRateId: String) -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val livePrice = livePriceState.price
    val buyPrice = livePrice?.buy ?: 0.0
    val buyRateId = livePrice?.buyRateId.orEmpty()
    val gstRate = ((livePrice?.taxPc ?: 0.0).takeIf { it > 0.0 } ?: 0.0) / 100.0
    val redeemableMaxInr = remember(rewardsState.homeUi?.redeemableDisplay) {
        parseRedeemCurrencyAmountOrZero(rewardsState.homeUi?.redeemableDisplay)
    }
    var amountInput by rememberSaveable(redeemableMaxInr) {
        mutableStateOf(if (redeemableMaxInr > 0.0) formatRedeemCurrencyInput(redeemableMaxInr) else "")
    }

    val requestedRedeemAmount = parseRedeemCurrencyAmountOrZero(amountInput)
    val redeemAmount = min(requestedRedeemAmount, redeemableMaxInr)
    val redeemProgress = if (redeemableMaxInr > 0.0) (redeemAmount / redeemableMaxInr).toFloat().coerceIn(0f, 1f) else 0f
    val redeemGstAmount = inclusiveGstAmountForRedeem(redeemAmount, gstRate)
    val redeemGoldValue = roundRedeemMoney((redeemAmount - redeemGstAmount).coerceAtLeast(0.0))
    val estimatedGoldGrams = if (buyPrice > 0.0) floorRedeemGrams(redeemGoldValue / buyPrice) else 0.0
    val canSubmit = redeemAmount >= 10.0 && buyRateId.isNotBlank() && buyPrice > 0.0 && !livePriceState.isFetching && buyState.step == BuyTradeStep.Entry
    val minimumRedeemMessage = redeemAmount > 0.0 && redeemAmount < 10.0

    val title = when (buyState.step) {
        BuyTradeStep.Success -> "Rewards redeemed successfully"
        BuyTradeStep.Pending -> "Redeem request is processing"
        BuyTradeStep.Failure -> "Rewards redemption failed"
        BuyTradeStep.Processing -> "Processing redemption"
        BuyTradeStep.Entry -> "Redeem rewards into gold"
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Neutral05,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(White)
                    .statusBarsPadding(),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Slate900,
                        )
                    }
                    Text(
                        text = "Rewards Redeem",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.size(48.dp))
                }
            }
        },
        bottomBar = {
            if (buyState.step == BuyTradeStep.Entry) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(White)
                ) {
                    BuyTradeLivePriceBar(livePriceState = livePriceState)
                    if (minimumRedeemMessage) {
                        Text(
                            text = "Amount should be at least ₹10 to redeem into Gold.",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 10.dp),
                            color = Red600,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        OutlinedButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .weight(0.9f)
                                .height(54.dp),
                            enabled = !buyState.isLoading,
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                        ) {
                            Text(
                                text = "Cancel",
                                color = Slate500,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        Button(
                            onClick = {
                                focusManager.clearFocus(force = true)
                                onAmountSubmit(redeemAmount, buyRateId)
                            },
                            enabled = canSubmit,
                            modifier = Modifier
                                .weight(1.5f)
                                .height(54.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Purple700,
                                disabledContainerColor = Slate200,
                                disabledContentColor = Slate500,
                            ),
                        ) {
                            if (buyState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = White,
                                    strokeWidth = 2.dp,
                                )
                            } else {
                                Text(
                                    "Redeem ${formatRedeemCurrencyDisplay(redeemAmount)}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                )
                            }
                        }
                    }
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clearFocusOnTapOutside { focusManager.clearFocus(force = true) }
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(28.dp))
                        .background(
                            Brush.linearGradient(colors = listOf(Purple700, Gold500)),
                        )
                        .padding(20.dp),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(White.copy(alpha = 0.18f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Savings,
                                    contentDescription = null,
                                    tint = White,
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "REDEEMABLE REWARDS",
                                    color = White.copy(alpha = 0.78f),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    text = if (redeemableMaxInr > 0.0) formatRedeemCurrencyDisplay(redeemableMaxInr) else "₹0",
                                    color = White,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Black,
                                )
                            }
                        }
                        Text(
                            text = title,
                            color = White.copy(alpha = 0.84f),
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                        )
                    }
                }
            }

            when (buyState.step) {
                BuyTradeStep.Entry -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate100),
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text("Amount to redeem", fontSize = 13.sp, color = Slate500, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = amountInput,
                                onValueChange = {
                                    val sanitized = sanitizeRedeemCurrencyInput(it)
                                    val parsed = parseRedeemCurrencyAmountOrZero(sanitized)
                                    amountInput = if (parsed > redeemableMaxInr && redeemableMaxInr > 0.0) {
                                        formatRedeemCurrencyInput(redeemableMaxInr)
                                    } else {
                                        sanitized
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus(force = true) }),
                                prefix = { Text("₹", color = Slate900, fontWeight = FontWeight.Bold) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Purple700,
                                    unfocusedBorderColor = Slate200,
                                ),
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            LinearProgressIndicator(
                                progress = { redeemProgress },
                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(999.dp)),
                                color = Purple700,
                                trackColor = Purple125Brand,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Available: ${formatRedeemCurrencyDisplay(redeemableMaxInr)}",
                                fontSize = 12.sp,
                                color = Slate500,
                            )
                            if (livePriceState.isFetching || buyRateId.isBlank()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Fetching latest buy price...",
                                    fontSize = 12.sp,
                                    color = Slate500,
                                )
                            }
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate100),
                    ) {
                        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            RewardsSummaryRow("Redeem amount", formatRedeemCurrencyDisplay(redeemAmount))
                            RewardsSummaryRow("GST included", formatRedeemCurrencyDisplay(redeemGstAmount))
                            RewardsSummaryRow("Gold value", formatRedeemCurrencyDisplay(redeemGoldValue))
                            RewardsSummaryRow(
                                "Estimated gold",
                                if (buyPrice > 0.0) "${formatRedeemGoldDisplay(estimatedGoldGrams)} g" else "—",
                            )
                        }
                    }
                }

                BuyTradeStep.Processing -> RewardsRedeemStatusCard(
                    icon = Icons.Default.AutoAwesome,
                    title = "Processing your redemption",
                    body = "We’re confirming your order status and updating your rewards balance.",
                    iconTint = Purple700,
                )

                BuyTradeStep.Success -> RewardsRedeemStatusCard(
                    icon = Icons.Default.CheckCircle,
                    title = "Rewards redeemed into gold",
                    body = "Your rewards were successfully converted into gold.",
                    iconTint = Emerald600,
                    actionLabel = "Back to rewards",
                    onAction = {
                        onReset()
                        onBackClick()
                    },
                )

                BuyTradeStep.Pending -> RewardsRedeemStatusCard(
                    icon = Icons.Default.AutoAwesome,
                    title = "Redemption pending",
                    body = "Payment is still being processed. You can safely return and check the updated balance shortly.",
                    iconTint = Purple700,
                    actionLabel = "Back to rewards",
                    onAction = {
                        onReset()
                        onBackClick()
                    },
                )

                BuyTradeStep.Failure -> RewardsRedeemStatusCard(
                    icon = Icons.Default.AutoAwesome,
                    title = "Redemption failed",
                    body = buyState.errorMessage ?: "We could not complete this redemption.",
                    iconTint = Red600,
                    actionLabel = "Try again",
                    onAction = onReset,
                )
            }
        }
    }
}

@Composable
private fun RewardsSummaryRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, fontSize = 13.sp, color = Slate500)
        Text(value, fontSize = 14.sp, color = Slate900, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun RewardsRedeemStatusCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    body: String,
    iconTint: Color,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate100),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(title, fontSize = 18.sp, color = Slate900, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(8.dp))
            Text(body, fontSize = 13.sp, color = Slate500, textAlign = TextAlign.Center, lineHeight = 20.sp)
            if (actionLabel != null && onAction != null) {
                Spacer(modifier = Modifier.height(18.dp))
                Button(
                    onClick = onAction,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Purple700),
                ) {
                    Text(actionLabel, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

private fun parseRedeemCurrencyAmountOrZero(raw: String?): Double {
    return raw
        ?.trim()
        ?.removePrefix("₹")
        ?.replace(",", "")
        ?.toDoubleOrNull()
        ?: 0.0
}

private fun formatRedeemCurrencyInput(amount: Double): String {
    if (amount <= 0.0) return ""
    return formatDecimalDown(amount, 2)
}

private fun formatRedeemCurrencyDisplay(amount: Double): String {
    return "₹${formatDecimalDown(amount, 2)}"
}

private fun sanitizeRedeemCurrencyInput(raw: String): String {
    val filtered = raw.filterIndexed { index, char ->
        char.isDigit() || (char == '.' && index == raw.indexOf('.'))
    }
    return filtered
}

private fun inclusiveGstAmountForRedeem(redeemAmount: Double, gstRate: Double): Double {
    if (redeemAmount <= 0.0 || gstRate <= 0.0) return 0.0
    return roundRedeemMoney(redeemAmount - (redeemAmount / (1.0 + gstRate)))
}

private fun roundRedeemMoney(value: Double): Double =
    round(value * 100.0) / 100.0

private fun floorRedeemGrams(grams: Double): Double {
    if (grams <= 0.0) return 0.0
    return floor(grams * 10_000.0) / 10_000.0
}

private fun formatRedeemGoldDisplay(grams: Double): String {
    if (grams <= 0.0) return "0"
    return formatDecimalDown(grams, 4)
}

private fun formatDecimalDown(value: Double, scale: Int): String {
    if (value <= 0.0) return "0"
    val multiplier = when (scale) {
        0 -> 1L
        1 -> 10L
        2 -> 100L
        3 -> 1_000L
        else -> 10_000L
    }
    val scaled = floor(value * multiplier.toDouble()).toLong()
    val whole = scaled / multiplier
    val fraction = (scaled % multiplier).toString().padStart(scale, '0')
    val trimmedFraction = fraction.trimEnd('0')
    return if (trimmedFraction.isEmpty()) {
        whole.toString()
    } else {
        "$whole.$trimmedFraction"
    }
}
