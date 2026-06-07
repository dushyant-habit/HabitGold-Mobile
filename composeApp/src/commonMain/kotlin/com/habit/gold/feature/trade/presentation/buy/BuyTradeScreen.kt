package com.habit.gold.feature.trade.presentation.buy

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.habit.gold.core.designsystem.icons.HabitGoldIcons as Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habit.gold.core.designsystem.icons.HabitGoldPhosphorIcons
import com.habit.gold.feature.trade.domain.TradeLivePriceState
import com.habit.gold.feature.trade.domain.usecase.GetTradeInvoiceUseCase
import com.habit.gold.core.network.ApiResult
import com.habit.gold.feature.trade.presentation.formatMoney
import com.habit.gold.feature.trade.presentation.sanitizeGramInput
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.buy_gold_screen_buy_gold
import habitgoldmobile.composeapp.generated.resources.common_help
import habitgoldmobile.composeapp.generated.resources.trade_buy_enter_amount
import habitgoldmobile.composeapp.generated.resources.trade_buy_fact_earn_extra_gold
import habitgoldmobile.composeapp.generated.resources.trade_buy_fact_insured_vaults
import habitgoldmobile.composeapp.generated.resources.trade_buy_fact_pure_gold_short
import habitgoldmobile.composeapp.generated.resources.trade_buy_fact_start_small
import habitgoldmobile.composeapp.generated.resources.trade_buy_live_price_unavailable
import habitgoldmobile.composeapp.generated.resources.trade_buy_max_upi_limit_message
import habitgoldmobile.composeapp.generated.resources.trade_buy_pending_body
import habitgoldmobile.composeapp.generated.resources.trade_buy_select_grams
import habitgoldmobile.composeapp.generated.resources.trade_buy_youre_buying
import habitgoldmobile.composeapp.generated.resources.trade_buy_failure_body
import habitgoldmobile.composeapp.generated.resources.trade_buy_failure_code_fallback
import habitgoldmobile.composeapp.generated.resources.trade_invoice_viewer_invalid_url
import org.jetbrains.compose.resources.stringResource
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

internal const val OneTimeUpiLimit = 100000.0

internal val BuyWhite = Color.White
internal val BuyPrimary = Color(0xFF7B2CBF)
internal val BuyPrimaryLight = Color(0xFFF4E8FF)
internal val BuyGoldTint = Color(0xFFD2A700)
internal val BuyGoldBackground = Color(0xFFF7F2FC)
internal val BuySlate950 = Color(0xFF020617)
internal val BuySlate800 = Color(0xFF1E293B)
internal val BuySlate600 = Color(0xFF475569)
internal val BuySlate500 = Color(0xFF64748B)
internal val BuySlate400 = Color(0xFF94A3B8)
internal val BuySlate300 = Color(0xFFCBD5E1)
internal val BuySlate200 = Color(0xFFE2E8F0)
internal val BuySlate100 = Color(0xFFF1F5F9)
internal val BuySlate50 = Color(0xFFF8FAFC)
internal val BuyNeutral25 = Color(0xFFFCFCFD)
internal val BuyRed50 = Color(0xFFFEF2F2)
internal val BuyRed400 = Color(0xFFF87171)
internal val BuyRed700 = Color(0xFFB91C1C)
internal val BuyGreen25 = Color(0xFFF0FDF4)
internal val BuyGreen500 = Color(0xFF22C55E)
internal val BuySuccess700 = Color(0xFF15803D)

internal val BuyPollingScheduleSeconds = listOf(1, 1, 1, 1, 1, 3, 3, 3, 3, 3)
internal const val BuyPollingWindowSeconds = 20

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyTradeScreen(
    state: BuyTradeState,
    livePriceState: TradeLivePriceState,
    onBackClick: () -> Unit,
    onGoToDashboard: () -> Unit,
    onHelpClick: () -> Unit,
    getTradeInvoiceUseCase: GetTradeInvoiceUseCase,
    onOpenInvoice: (String) -> Unit,
    initialAmount: String? = null,
    initialOneTimeUseGrams: Boolean = false,
    onIntent: (BuyTradeIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val seededRupeeAmount = remember(initialAmount, initialOneTimeUseGrams) {
        if (initialOneTimeUseGrams) {
            "100"
        } else {
            initialAmount
                ?.filter(Char::isDigit)
                ?.take(6)
                ?.takeIf { it.isNotBlank() }
                ?: "100"
        }
    }
    val seededGramAmount = remember(initialAmount, initialOneTimeUseGrams) {
        if (initialOneTimeUseGrams) {
            sanitizeGramInput(initialAmount.orEmpty(), fractionDigits = 1).takeIf { it.isNotBlank() } ?: "0.1"
        } else {
            "0.1"
        }
    }

    var amountRupees by rememberSaveable(initialAmount, initialOneTimeUseGrams) { mutableStateOf(seededRupeeAmount) }
    var amountGrams by rememberSaveable(initialAmount, initialOneTimeUseGrams) { mutableStateOf(seededGramAmount) }
    var validationMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var showAmountBreakdown by rememberSaveable { mutableStateOf(false) }
    var couponDraft by rememberSaveable { mutableStateOf("") }
    var showCouponSheet by rememberSaveable { mutableStateOf(false) }
    var isFetchingInvoice by remember(state.currentOrderId, state.step) { mutableStateOf(false) }
    var invoiceErrorMessage by remember(state.currentOrderId, state.step) { mutableStateOf<String?>(null) }

    val livePrice = livePriceState.price
    val buyRateId = livePrice?.buyRateId.orEmpty()
    val goldPrice = livePrice?.buy ?: 0.0
    val gstRate = ((livePrice?.taxPc ?: 0.0).takeIf { it > 0.0 } ?: 0.0) / 100.0
    val numericRupees = amountRupees.toDoubleOrNull() ?: 0.0
    val numericGrams = amountGrams.toDoubleOrNull() ?: 0.0
    val livePriceUnavailableMessage = stringResource(Res.string.trade_buy_live_price_unavailable)
    val maxUpiLimitMessage = stringResource(Res.string.trade_buy_max_upi_limit_message)
    val minAmountMessage = "Minimum buy amount is ₹10."
    val invalidInvoiceMessage = stringResource(Res.string.trade_invoice_viewer_invalid_url)
    val showEntryTopBar = state.step == BuyTradeStep.Entry
    val focusManager = LocalFocusManager.current
    val facts = listOf(
        stringResource(Res.string.trade_buy_fact_earn_extra_gold),
        stringResource(Res.string.trade_buy_fact_pure_gold_short),
        stringResource(Res.string.trade_buy_fact_insured_vaults),
        stringResource(Res.string.trade_buy_fact_start_small),
    )
    var currentFactIndex by rememberSaveable { mutableStateOf(0) }
    val couponSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val baseCalculation = remember(state.entryMode, numericRupees, numericGrams, goldPrice, gstRate) {
        calculateBuyTrade(
            entryMode = state.entryMode,
            numericRupees = numericRupees,
            numericGrams = numericGrams,
            goldPrice = goldPrice,
            gstRate = gstRate,
            appliedCoupon = null,
        )
    }
    val displayCouponValidation = remember(
        state.appliedCoupon,
        state.appliedCouponValidatedAmount,
        state.appliedCouponValidatedGrams,
        baseCalculation.baseTotalPayable,
        baseCalculation.goldQuantity,
    ) {
        val matchesAmount = state.appliedCouponValidatedAmount?.let {
            kotlin.math.abs(it - baseCalculation.baseTotalPayable) <= 0.01
        } ?: false
        val matchesGrams = state.appliedCouponValidatedGrams?.let {
            kotlin.math.abs(it - baseCalculation.goldQuantity) <= 0.0001
        } ?: false
        state.appliedCoupon?.takeIf { matchesAmount && matchesGrams }
    }

    val calculation = remember(state.entryMode, numericRupees, numericGrams, goldPrice, gstRate, displayCouponValidation) {
        calculateBuyTrade(
            entryMode = state.entryMode,
            numericRupees = numericRupees,
            numericGrams = numericGrams,
            goldPrice = goldPrice,
            gstRate = gstRate,
            appliedCoupon = displayCouponValidation,
        )
    }

    val canSubmit = !state.isLoading &&
        !livePriceState.isFetching &&
        livePrice != null &&
        buyRateId.isNotBlank() &&
        calculation.totalPayable in 10.0..OneTimeUpiLimit &&
        calculation.goldQuantity > 0.0

    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            currentFactIndex = (currentFactIndex + 1) % facts.size
        }
    }

    LaunchedEffect(state.appliedCouponCode, calculation.baseTotalPayable, calculation.goldQuantity, state.step) {
        val appliedCouponCode = state.appliedCouponCode?.takeIf { it.isNotBlank() } ?: return@LaunchedEffect
        if (state.step != BuyTradeStep.Entry) return@LaunchedEffect
        delay(350)
        onIntent(
            BuyTradeIntent.ApplyCoupon(
                code = appliedCouponCode,
                amount = calculation.baseTotalPayable,
                grams = calculation.goldQuantity,
                silent = true,
            ),
        )
    }

    val belowMinimumAmount = calculation.totalPayable in 0.01..<10.0
    val isUpiLimitExceeded = calculation.totalPayable > OneTimeUpiLimit
    val displayedMessage = validationMessage
        ?: state.errorMessage
        ?: when {
            isUpiLimitExceeded -> maxUpiLimitMessage
            belowMinimumAmount -> minAmountMessage
            else -> null
        }

    LaunchedEffect(isFetchingInvoice, state.currentOrderId, state.order?.orderId, state.pollingSnapshot?.orderId) {
        if (!isFetchingInvoice) return@LaunchedEffect
        val orderId = state.currentOrderId ?: state.order?.orderId ?: state.pollingSnapshot?.orderId
        if (orderId.isNullOrBlank()) {
            invoiceErrorMessage = invalidInvoiceMessage
            isFetchingInvoice = false
            return@LaunchedEffect
        }
        when (val result = getTradeInvoiceUseCase(orderId)) {
            is ApiResult.Success -> {
                val invoiceUrl = result.value.invoiceUrl
                if (invoiceUrl.isBlank()) {
                    invoiceErrorMessage = invalidInvoiceMessage
                } else {
                    invoiceErrorMessage = null
                    onOpenInvoice(invoiceUrl)
                }
                isFetchingInvoice = false
            }
            is ApiResult.Failure -> {
                invoiceErrorMessage = result.error.message
                isFetchingInvoice = false
            }
        }
    }

    LaunchedEffect(state.isLoading, state.step) {
        if (state.isLoading || state.step != BuyTradeStep.Entry) {
            focusManager.clearFocus(force = true)
        }
    }

    when (state.step) {
        BuyTradeStep.Processing -> {
            BuyTradeProcessingScreen(
                orderId = state.currentOrderId ?: state.order?.orderId ?: state.pollingSnapshot?.orderId.orEmpty(),
                pollingSnapshot = state.pollingSnapshot,
                modifier = modifier,
            )
            return
        }
        BuyTradeStep.Success -> {
            BuyTradeSuccessScreen(
                amount = "₹${formatMoney(state.order?.let(::calculateOrderTotalPaid) ?: calculation.totalPayable)}",
                goldCredited = "${formatConversionGrams(state.order?.goldQuantityGrams ?: calculation.goldQuantity)} g",
                invoiceErrorMessage = invoiceErrorMessage,
                isInvoiceLoading = isFetchingInvoice,
                onGoToDashboard = onGoToDashboard,
                onViewInvoiceClick = {
                    if (!isFetchingInvoice) {
                        isFetchingInvoice = true
                    }
                },
                modifier = modifier,
            )
            return
        }
        BuyTradeStep.Failure -> {
            BuyTradeFailureScreen(
                body = state.errorMessage ?: stringResource(Res.string.trade_buy_failure_body),
                errorCode = state.currentOrderId ?: stringResource(Res.string.trade_buy_failure_code_fallback),
                onRetryClick = { onIntent(BuyTradeIntent.ResetToEntry) },
                onHomeClick = onGoToDashboard,
                modifier = modifier,
            )
            return
        }
        BuyTradeStep.Pending -> {
            BuyTradePendingScreen(
                body = stringResource(Res.string.trade_buy_pending_body),
                orderId = state.currentOrderId ?: state.pollingSnapshot?.orderId,
                status = state.pollingSnapshot?.status,
                onGoToDashboard = onGoToDashboard,
                onRetryClick = { onIntent(BuyTradeIntent.ResetToEntry) },
                modifier = modifier,
            )
            return
        }
        BuyTradeStep.Entry -> Unit
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BuyWhite,
        topBar = {
            if (showEntryTopBar) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.align(Alignment.CenterStart),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = Color.Black,
                        )
                    }
                    Text(
                        text = stringResource(Res.string.buy_gold_screen_buy_gold),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = BuySlate800,
                        modifier = Modifier.align(Alignment.Center),
                    )
                    IconButton(
                        onClick = onHelpClick,
                        modifier = Modifier.align(Alignment.CenterEnd),
                    ) {
                        Icon(
                            imageVector = HabitGoldPhosphorIcons.Regular.Question,
                            contentDescription = stringResource(Res.string.common_help),
                            tint = BuyPrimary,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }
            }
        },
        bottomBar = {
            BuyTradeBottomBar(
                livePriceState = livePriceState,
                totalPayable = calculation.totalPayable,
                showPoweredBySafeGold = false,
                enabled = canSubmit && !isUpiLimitExceeded,
                isLoading = state.isLoading,
                errorMessage = displayedMessage,
                onShowBreakdown = { showAmountBreakdown = true },
                onPrimaryAction = {
                    focusManager.clearFocus(force = true)
                    if (livePriceState.isFetching || livePrice == null || buyRateId.isBlank()) {
                        validationMessage = livePriceUnavailableMessage
                        return@BuyTradeBottomBar
                    }
                    if (isUpiLimitExceeded) {
                        validationMessage = maxUpiLimitMessage
                        return@BuyTradeBottomBar
                    }
                    validationMessage = null
                    onIntent(
                        BuyTradeIntent.SubmitOneTimeOrder(
                            amount = calculation.baseTotalPayable.takeIf { state.entryMode == BuyTradeEntryMode.Rupees },
                            grams = calculation.goldQuantity.takeIf { state.entryMode == BuyTradeEntryMode.Grams },
                            buyRateId = buyRateId,
                            couponCode = state.appliedCouponCode,
                            couponValidationAmount = calculation.baseTotalPayable,
                            couponValidationGrams = calculation.goldQuantity,
                        ),
                    )
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { focusManager.clearFocus() }
                .padding(horizontal = 24.dp)
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            BuyTradeInfoPill(
                facts = facts,
                currentFactIndex = currentFactIndex,
            )
            Spacer(modifier = Modifier.height(12.dp))

            BuyTradeEntryModeTabs(
                activeMode = state.entryMode,
                onSelectMode = {
                    validationMessage = null
                    onIntent(BuyTradeIntent.ChangeEntryMode(it))
                },
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (state.entryMode == BuyTradeEntryMode.Rupees) {
                        stringResource(Res.string.trade_buy_enter_amount)
                    } else {
                        stringResource(Res.string.trade_buy_select_grams)
                    },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = BuySlate400,
                    letterSpacing = 2.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start,
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (state.entryMode == BuyTradeEntryMode.Rupees) {
                    BuyTradeRupeeInput(
                        value = amountRupees,
                        onDone = { focusManager.clearFocus() },
                        onValueChange = {
                            validationMessage = null
                            amountRupees = it.filter(Char::isDigit).take(6)
                        },
                    )
                } else {
                    BuyTradeGramInput(
                        value = amountGrams,
                        maxSelectableGrams = maxSelectableGrams(goldPrice, gstRate),
                        onDone = { focusManager.clearFocus() },
                        onValueChange = {
                            validationMessage = null
                            amountGrams = sanitizeGramInput(it, fractionDigits = 1)
                        },
                        onStepDown = {
                            validationMessage = null
                            amountGrams = stepOneTimeGrams(amountGrams, -1, maxSelectableGrams(goldPrice, gstRate))
                        },
                        onStepUp = {
                            validationMessage = null
                            amountGrams = stepOneTimeGrams(amountGrams, 1, maxSelectableGrams(goldPrice, gstRate))
                        },
                    )
                }
                    if (state.entryMode == BuyTradeEntryMode.Rupees) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(Res.string.trade_buy_youre_buying, formatConversionGrams(calculation.goldQuantity)),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = BuyGoldTint,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    } else {
                        Spacer(modifier = Modifier.height(8.dp))
                        BuyTradeGramSlider(
                            value = amountGrams.toFloatOrNull()?.coerceIn(0.1f, maxSelectableGrams(goldPrice, gstRate).toFloat()) ?: 0.1f,
                            maxValue = maxSelectableGrams(goldPrice, gstRate).toFloat(),
                            onValueChange = {
                                validationMessage = null
                                amountGrams = ((it * 10).roundToInt() / 10f).toString()
                            },
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            BuyTradeQuickAmounts(
                entryMode = state.entryMode,
                maxSelectableGrams = maxSelectableGrams(goldPrice, gstRate),
                selectedValue = if (state.entryMode == BuyTradeEntryMode.Rupees) amountRupees else amountGrams,
                onSelectRupees = {
                    validationMessage = null
                    amountRupees = it
                },
                onSelectGrams = {
                    validationMessage = null
                    amountGrams = it
                },
            )

            Spacer(modifier = Modifier.height(20.dp))

            BuyTradeCouponRow(
                availableCoupons = state.availableCoupons.size,
                couponDraft = couponDraft,
                appliedCouponCode = state.appliedCouponCode,
                appliedBenefitText = if (state.appliedCoupon != null) {
                    buyTradeAppliedCouponSummary(
                        validation = state.appliedCoupon,
                        fallbackAmount = calculation.baseTotalPayable,
                    )
                } else {
                    null
                },
                onCouponDraftChange = {
                    couponDraft = it.uppercase().filter { character ->
                        character.isLetterOrDigit() || character == '_'
                    }.take(20)
                },
                onApplyCoupon = {
                    if (couponDraft.isNotBlank()) {
                        onIntent(
                            BuyTradeIntent.ApplyCoupon(
                                code = couponDraft,
                                amount = calculation.baseTotalPayable,
                                grams = calculation.goldQuantity,
                                silent = false,
                            ),
                        )
                    }
                },
                onRemoveAppliedCoupon = {
                    couponDraft = ""
                    validationMessage = null
                    onIntent(BuyTradeIntent.ClearAppliedCoupon)
                },
                onShowOffers = { showCouponSheet = true },
                isApplyingEnabled = couponDraft.isNotBlank() &&
                    !state.isLoading &&
                    state.appliedCouponCode != couponDraft,
            )

            Spacer(modifier = Modifier.height(14.dp))

            BuyTradePoweredByRow()

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showAmountBreakdown) {
        val amountBreakdownSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showAmountBreakdown = false },
            sheetState = amountBreakdownSheetState,
            containerColor = BuyWhite,
        ) {
            BuyAmountBreakdownSheet(
                calculation = calculation,
                gstRate = gstRate,
                appliedCouponCode = state.appliedCouponCode,
                couponValidation = displayCouponValidation,
                onPayNowClick = {
                    showAmountBreakdown = false
                    if (livePriceState.isFetching || livePrice == null || buyRateId.isBlank()) {
                        validationMessage = livePriceUnavailableMessage
                        return@BuyAmountBreakdownSheet
                    }
                    if (isUpiLimitExceeded) {
                        validationMessage = maxUpiLimitMessage
                        return@BuyAmountBreakdownSheet
                    }
                    onIntent(
                        BuyTradeIntent.SubmitOneTimeOrder(
                            amount = calculation.baseTotalPayable.takeIf { state.entryMode == BuyTradeEntryMode.Rupees },
                            grams = calculation.goldQuantity.takeIf { state.entryMode == BuyTradeEntryMode.Grams },
                            buyRateId = buyRateId,
                            couponCode = state.appliedCouponCode,
                            couponValidationAmount = calculation.baseTotalPayable,
                            couponValidationGrams = calculation.goldQuantity,
                        ),
                    )
                },
                isPayNowEnabled = canSubmit && !isUpiLimitExceeded,
                isLoading = state.isLoading,
            )
        }
    }

    if (showCouponSheet) {
        ModalBottomSheet(
            onDismissRequest = { showCouponSheet = false },
            sheetState = couponSheetState,
            containerColor = BuyWhite,
        ) {
            BuyCouponSheet(
                coupons = state.availableCoupons,
                estimateAmount = calculation.baseTotalPayable,
                appliedCouponCode = state.appliedCouponCode,
                onApplyCoupon = { code ->
                    couponDraft = code
                    onIntent(
                        BuyTradeIntent.ApplyCoupon(
                            code = code,
                            amount = calculation.baseTotalPayable,
                            grams = calculation.goldQuantity,
                            silent = false,
                        ),
                    )
                    showCouponSheet = false
                },
            )
        }
    }

}
