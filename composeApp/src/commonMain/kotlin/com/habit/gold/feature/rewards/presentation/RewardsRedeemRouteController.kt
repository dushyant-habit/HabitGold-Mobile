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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import com.habit.gold.core.designsystem.icons.HabitGoldIcons as Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.habit.gold.core.presentation.PlatformBackHandler
import com.habit.gold.core.presentation.clearFocusOnTapOutside
import com.habit.gold.feature.home.presentation.components.Purple500
import com.habit.gold.feature.trade.domain.TradeLivePriceState
import com.habit.gold.feature.trade.presentation.TradeRouteDependencies
import com.habit.gold.feature.trade.presentation.buy.BuyTradeEffect
import com.habit.gold.feature.trade.presentation.buy.BuyTradeIntent
import com.habit.gold.feature.trade.presentation.buy.BuyTradeLivePriceBar
import com.habit.gold.feature.trade.presentation.buy.BuyTradeStep
import com.habit.gold.feature.trade.presentation.buy.BuyTradeState
import com.habit.gold.feature.trade.presentation.buy.BuyTradeViewModel
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.common_back
import habitgoldmobile.composeapp.generated.resources.common_cancel
import habitgoldmobile.composeapp.generated.resources.rewards_redeem_amount_field_label
import habitgoldmobile.composeapp.generated.resources.rewards_redeem_amount_subtitle
import habitgoldmobile.composeapp.generated.resources.rewards_redeem_amount_title
import habitgoldmobile.composeapp.generated.resources.rewards_redeem_cta
import habitgoldmobile.composeapp.generated.resources.rewards_redeem_done
import habitgoldmobile.composeapp.generated.resources.rewards_redeem_estimated_gold
import habitgoldmobile.composeapp.generated.resources.rewards_redeem_failure_title
import habitgoldmobile.composeapp.generated.resources.rewards_redeem_hero_label
import habitgoldmobile.composeapp.generated.resources.rewards_redeem_hero_subtitle
import habitgoldmobile.composeapp.generated.resources.rewards_redeem_minimum_amount
import habitgoldmobile.composeapp.generated.resources.rewards_redeem_processing_subtitle
import habitgoldmobile.composeapp.generated.resources.rewards_redeem_processing_title
import habitgoldmobile.composeapp.generated.resources.rewards_redeem_rewards_used
import habitgoldmobile.composeapp.generated.resources.rewards_redeem_success_subtitle
import habitgoldmobile.composeapp.generated.resources.rewards_redeem_success_title
import habitgoldmobile.composeapp.generated.resources.rewards_redeem_summary_title
import habitgoldmobile.composeapp.generated.resources.rewards_redeem_title
import org.jetbrains.compose.resources.stringResource
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.round

private const val MinRedeemToGoldInr = 10.0

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

    LaunchedEffect(Unit) {
        onRefreshRewards()
    }

    LaunchedEffect(buyTradeViewModel) {
        buyTradeViewModel.effects.collect { effect ->
            when (effect) {
                is BuyTradeEffect.LaunchPayment -> {
                    val result = tradeDependencies.paymentLauncher.launch(effect.request)
                    buyTradeViewModel.onIntent(BuyTradeIntent.HandlePaymentResult(result))
                }
                BuyTradeEffect.RefreshLivePrice -> tradeDependencies.livePriceStore.refreshPricesAfterRateExpired()
                is BuyTradeEffect.ShowMessage -> Unit
            }
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
        onRefreshRewards = onRefreshRewards,
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
@OptIn(ExperimentalMaterial3Api::class)
private fun RewardsRedeemScreen(
    rewardsState: RewardsHomeState,
    buyState: BuyTradeState,
    livePriceState: TradeLivePriceState,
    onBackClick: () -> Unit,
    onRefreshRewards: () -> Unit,
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
    val isRewardsLoading = rewardsState.isLoading || rewardsState.homeUi == null

    var amountInput by rememberSaveable { mutableStateOf("") }
    var showSuccess by rememberSaveable { mutableStateOf(false) }
    var successfulRedeemAmount by rememberSaveable { mutableStateOf(0.0) }
    var successfulGoldGrams by rememberSaveable { mutableStateOf(0.0) }

    LaunchedEffect(redeemableMaxInr) {
        if (amountInput.isBlank() && redeemableMaxInr > 0.0) {
            amountInput = formatRedeemCurrencyInput(min(MinRedeemToGoldInr, redeemableMaxInr))
        }
        if (parseRedeemCurrencyAmountOrZero(amountInput) > redeemableMaxInr && redeemableMaxInr > 0.0) {
            amountInput = formatRedeemCurrencyInput(redeemableMaxInr)
        }
    }

    val requestedRedeemAmount = parseRedeemCurrencyAmountOrZero(amountInput)
    val redeemAmount = min(requestedRedeemAmount, redeemableMaxInr)
    val redeemProgress = if (redeemableMaxInr > 0.0) {
        (redeemAmount / redeemableMaxInr).toFloat().coerceIn(0f, 1f)
    } else {
        0f
    }
    val redeemGstAmount = inclusiveGstAmountForRedeem(redeemAmount, gstRate)
    val redeemGoldValue = roundRedeemMoney((redeemAmount - redeemGstAmount).coerceAtLeast(0.0))
    val estimatedGoldGrams = if (buyPrice > 0.0) floorRedeemGrams(redeemGoldValue / buyPrice) else 0.0

    LaunchedEffect(buyState.step) {
        when (buyState.step) {
            BuyTradeStep.Success -> {
                successfulRedeemAmount = redeemAmount
                successfulGoldGrams = buyState.order?.goldQuantityGrams ?: estimatedGoldGrams
                showSuccess = true
                onRefreshRewards()
                onReset()
            }
            BuyTradeStep.Pending -> {
                onRefreshRewards()
                onReset()
                onBackClick()
            }
            else -> Unit
        }
    }

    fun updateAmount(rawValue: String) {
        val sanitized = sanitizeRedeemCurrencyInput(rawValue)
        val parsed = parseRedeemCurrencyAmountOrZero(sanitized)
        amountInput = if (redeemableMaxInr > 0.0 && parsed > redeemableMaxInr) {
            formatRedeemCurrencyInput(redeemableMaxInr)
        } else {
            sanitized
        }
    }

    fun startRedeem() {
        focusManager.clearFocus(force = true)
        successfulRedeemAmount = redeemAmount
        successfulGoldGrams = estimatedGoldGrams
        onAmountSubmit(redeemAmount, buyRateId)
    }

    val isSubmitting = buyState.isLoading || buyState.step == BuyTradeStep.Processing
    val minimumRedeemMessage = redeemAmount > 0.0 && redeemAmount < MinRedeemToGoldInr
    val canSubmit = !showSuccess &&
        !isSubmitting &&
        !isRewardsLoading &&
        redeemableMaxInr >= MinRedeemToGoldInr &&
        redeemAmount >= MinRedeemToGoldInr &&
        !livePriceState.isFetching &&
        buyRateId.isNotBlank() &&
        buyPrice > 0.0

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Neutral05,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.rewards_redeem_title),
                        color = Slate900,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (showSuccess) {
                                onReset()
                            }
                            onBackClick()
                        },
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.common_back),
                            tint = Slate900,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = White,
                ),
            )
        },
        bottomBar = {
            if (showSuccess) {
                RewardsRedeemSuccessBottomBar(
                    onDoneClick = {
                        onReset()
                        onBackClick()
                    },
                )
            } else {
                RewardsRedeemBottomBar(
                    livePriceState = livePriceState,
                    isSubmitting = isSubmitting,
                    isEnabled = canSubmit,
                    redeemAmount = redeemAmount,
                    showMinimumRedeemMessage = minimumRedeemMessage,
                    onCancel = {
                        onReset()
                        onBackClick()
                    },
                    onContinue = ::startRedeem,
                )
            }
        },
    ) { innerPadding ->
        if (showSuccess) {
            RewardsRedeemSuccessContent(
                redeemAmount = successfulRedeemAmount,
                estimatedGoldGrams = successfulGoldGrams,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp, vertical = 24.dp),
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clearFocusOnTapOutside { focusManager.clearFocus(force = true) }
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                RewardsRedeemHeroCard(
                    redeemableMaxInr = redeemableMaxInr,
                    isLoading = isRewardsLoading,
                )

                RewardsRedeemAmountCard(
                    amountInput = amountInput,
                    onAmountInputChange = ::updateAmount,
                    redeemableMaxInr = redeemableMaxInr,
                    redeemProgress = redeemProgress,
                    onPresetSelected = { preset ->
                        focusManager.clearFocus(force = true)
                        amountInput = formatRedeemCurrencyInput(min(preset, redeemableMaxInr))
                    },
                )

                RewardsRedeemSummaryCard(
                    redeemAmount = redeemAmount,
                    estimatedGoldGrams = estimatedGoldGrams,
                )

                if (buyState.step == BuyTradeStep.Failure && !buyState.errorMessage.isNullOrBlank()) {
                    RewardsRedeemFailureCard(message = buyState.errorMessage)
                }

                Spacer(modifier = Modifier.height(108.dp))
            }
        }
    }

    if (buyState.step == BuyTradeStep.Processing) {
        RewardsRedeemProcessingDialog()
    }
}

@Composable
private fun RewardsRedeemHeroCard(
    redeemableMaxInr: Double,
    isLoading: Boolean,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Purple700, Purple500),
                ),
            )
            .padding(20.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Column {
                    Text(
                        text = stringResource(Res.string.rewards_redeem_hero_label),
                        color = White.copy(alpha = 0.78f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = if (isLoading) "Loading..." else formatRedeemCurrencyDisplay(redeemableMaxInr),
                        color = White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                    )
                }
            Text(
                text = stringResource(Res.string.rewards_redeem_hero_subtitle),
                color = White.copy(alpha = 0.84f),
                fontSize = 14.sp,
                lineHeight = 20.sp,
            )
        }
    }
}

@Composable
private fun RewardsRedeemAmountCard(
    amountInput: String,
    onAmountInputChange: (String) -> Unit,
    redeemableMaxInr: Double,
    redeemProgress: Float,
    onPresetSelected: (Double) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val presets = remember(redeemableMaxInr) {
        listOf(50.0, 100.0, 250.0)
            .filter { it < redeemableMaxInr }
            .plus(redeemableMaxInr)
            .filter { it > 0.0 }
            .distinctBy { it.toInt() }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = White),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(24.dp), clip = false),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = stringResource(Res.string.rewards_redeem_amount_title),
                color = Slate900,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
            )
            Text(
                text = stringResource(
                    Res.string.rewards_redeem_amount_subtitle,
                    formatRedeemCurrencyDisplay(redeemableMaxInr),
                ),
                color = Slate500,
                fontSize = 13.sp,
                lineHeight = 19.sp,
            )
            OutlinedTextField(
                value = amountInput,
                onValueChange = onAmountInputChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(18.dp),
                label = { Text(stringResource(Res.string.rewards_redeem_amount_field_label)) },
                prefix = {
                    Text(
                        text = "₹",
                        color = Slate900,
                        fontWeight = FontWeight.Bold,
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus(force = true) },
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Purple700,
                    focusedLabelColor = Purple700,
                    unfocusedBorderColor = Slate200,
                ),
            )
            LinearProgressIndicator(
                progress = { redeemProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(999.dp)),
                color = Purple700,
                trackColor = Purple100,
            )
            if (presets.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    presets.forEach { preset ->
                        RewardsRedeemPresetChip(
                            label = if (preset == redeemableMaxInr) "Max" else formatRedeemCurrencyInput(preset),
                            onClick = {
                                focusManager.clearFocus(force = true)
                                onPresetSelected(preset)
                            },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RewardsRedeemSummaryCard(
    redeemAmount: Double,
    estimatedGoldGrams: Double,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = White),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Slate100, RoundedCornerShape(24.dp)),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(Res.string.rewards_redeem_summary_title),
                color = Slate900,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
            )
            RewardsRedeemSummaryRow(
                label = stringResource(Res.string.rewards_redeem_rewards_used),
                value = formatRedeemCurrencyDisplay(redeemAmount),
                valueColor = Purple700,
            )
            RewardsRedeemSummaryRow(
                label = stringResource(Res.string.rewards_redeem_estimated_gold),
                value = "${formatRedeemGoldDisplay(estimatedGoldGrams)} g",
                valueColor = Slate900,
            )
        }
    }
}

@Composable
private fun RewardsRedeemFailureCard(
    message: String,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Red50, RoundedCornerShape(20.dp)),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(Res.string.rewards_redeem_failure_title),
                color = Slate900,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = message,
                color = Slate500,
                fontSize = 13.sp,
                lineHeight = 19.sp,
            )
        }
    }
}

@Composable
private fun RewardsRedeemSuccessContent(
    redeemAmount: Double,
    estimatedGoldGrams: Double,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(86.dp)
                .clip(CircleShape)
                .background(Purple100),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Purple700,
                modifier = Modifier.size(48.dp),
            )
        }
        Spacer(modifier = Modifier.height(22.dp))
        Text(
            text = stringResource(Res.string.rewards_redeem_success_title),
            color = Slate900,
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(Res.string.rewards_redeem_success_subtitle),
            color = Slate500,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = White),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Slate100, RoundedCornerShape(24.dp)),
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                RewardsRedeemSummaryRow(
                    label = stringResource(Res.string.rewards_redeem_rewards_used),
                    value = formatRedeemCurrencyDisplay(redeemAmount),
                    valueColor = Purple700,
                )
                RewardsRedeemSummaryRow(
                    label = stringResource(Res.string.rewards_redeem_estimated_gold),
                    value = "${formatRedeemGoldDisplay(estimatedGoldGrams)} g",
                    valueColor = Slate900,
                )
            }
        }
    }
}

@Composable
private fun RewardsRedeemSuccessBottomBar(
    onDoneClick: () -> Unit,
) {
    Surface(
        color = White,
        tonalElevation = 0.dp,
        shadowElevation = 10.dp,
    ) {
        Button(
            onClick = onDoneClick,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 14.dp)
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Purple700,
                contentColor = White,
            ),
        ) {
            Text(
                text = stringResource(Res.string.rewards_redeem_done),
                fontWeight = FontWeight.Black,
                fontSize = 15.sp,
            )
        }
    }
}

@Composable
private fun RewardsRedeemBottomBar(
    livePriceState: TradeLivePriceState,
    isSubmitting: Boolean,
    isEnabled: Boolean,
    redeemAmount: Double,
    showMinimumRedeemMessage: Boolean,
    onCancel: () -> Unit,
    onContinue: () -> Unit,
) {
    Surface(
        color = White,
        tonalElevation = 0.dp,
        shadowElevation = 10.dp,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            BuyTradeLivePriceBar(livePriceState = livePriceState)
            if (showMinimumRedeemMessage) {
                Text(
                    text = stringResource(Res.string.rewards_redeem_minimum_amount),
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
                    onClick = onCancel,
                    modifier = Modifier
                        .weight(0.9f)
                        .height(54.dp),
                    enabled = !isSubmitting,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                ) {
                    Text(
                        text = stringResource(Res.string.common_cancel),
                        color = Slate800,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Button(
                    onClick = onContinue,
                    modifier = Modifier
                        .weight(1.4f)
                        .height(54.dp),
                    enabled = !isSubmitting && isEnabled,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Purple700,
                        contentColor = White,
                        disabledContainerColor = Slate200,
                        disabledContentColor = Slate500,
                    ),
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = White,
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Text(
                            text = stringResource(
                                Res.string.rewards_redeem_cta,
                                formatRedeemCurrencyDisplay(redeemAmount),
                            ),
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RewardsRedeemPresetChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(42.dp),
        shape = RoundedCornerShape(14.dp),
        color = Purple50,
        border = androidx.compose.foundation.BorderStroke(1.dp, Purple200),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                color = Purple700,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun RewardsRedeemSummaryRow(
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
            color = Slate500,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
        )
        Text(
            text = value,
            color = valueColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End,
        )
    }
}

@Composable
private fun RewardsRedeemProcessingDialog() {
    Dialog(onDismissRequest = { }) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = White,
            tonalElevation = 0.dp,
            shadowElevation = 8.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator(
                    color = Purple700,
                    modifier = Modifier.size(28.dp),
                    strokeWidth = 2.5.dp,
                )
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = stringResource(Res.string.rewards_redeem_processing_title),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate900,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(Res.string.rewards_redeem_processing_subtitle),
                    fontSize = 14.sp,
                    color = Slate500,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                )
            }
        }
    }
}

private fun parseRedeemCurrencyAmountOrZero(raw: String?): Double {
    if (raw.isNullOrBlank()) return 0.0
    return raw
        .trim()
        .removePrefix("₹")
        .replace(",", "")
        .toDoubleOrNull()
        ?.coerceAtLeast(0.0)
        ?: 0.0
}

private fun formatRedeemCurrencyInput(amount: Double): String {
    if (amount <= 0.0) return ""
    return formatDecimalDown(amount, 2)
}

private fun formatRedeemCurrencyDisplay(amount: Double): String =
    "₹${formatDecimalDown(amount, 2)}"

private fun sanitizeRedeemCurrencyInput(raw: String): String =
    raw.filterIndexed { index, char ->
        char.isDigit() || (char == '.' && index == raw.indexOf('.'))
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
