package com.habit.gold.feature.trade.presentation.sell

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.window.Dialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalFocusManager
import com.habit.gold.core.network.ApiResult
import com.habit.gold.core.designsystem.AppPrimaryButton
import com.habit.gold.core.designsystem.HabitGoldPalette
import com.habit.gold.feature.trade.domain.TradeLivePriceState
import com.habit.gold.feature.trade.domain.model.TradeUserVpa
import com.habit.gold.feature.trade.domain.usecase.GetTradeInvoiceUseCase
import com.habit.gold.feature.trade.presentation.TradeDetailRow
import com.habit.gold.feature.trade.presentation.TradeMutedText
import com.habit.gold.feature.trade.presentation.TradePrimaryText
import com.habit.gold.feature.trade.presentation.TradeSectionBorder
import com.habit.gold.feature.trade.presentation.formatCountdown
import com.habit.gold.feature.trade.presentation.formatGoldQuantity
import com.habit.gold.feature.trade.presentation.formatMoney
import com.habit.gold.feature.trade.presentation.roundToMoney
import com.habit.gold.feature.trade.presentation.sanitizeGramInput
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.common_cancel
import habitgoldmobile.composeapp.generated.resources.common_confirm
import habitgoldmobile.composeapp.generated.resources.common_help
import habitgoldmobile.composeapp.generated.resources.common_safegold
import habitgoldmobile.composeapp.generated.resources.common_go_to_dashboard
import habitgoldmobile.composeapp.generated.resources.common_selected
import habitgoldmobile.composeapp.generated.resources.common_transaction_details
import habitgoldmobile.composeapp.generated.resources.home_screen_powered_by
import habitgoldmobile.composeapp.generated.resources.safegold_image
import habitgoldmobile.composeapp.generated.resources.sell_gold_screen_sell_gold
import habitgoldmobile.composeapp.generated.resources.trade_sell_action_continue
import habitgoldmobile.composeapp.generated.resources.trade_sell_action_sell_gold
import habitgoldmobile.composeapp.generated.resources.trade_sell_all_gold_locked
import habitgoldmobile.composeapp.generated.resources.trade_sell_amount_transferred
import habitgoldmobile.composeapp.generated.resources.trade_sell_badge_insured
import habitgoldmobile.composeapp.generated.resources.trade_sell_badge_physical
import habitgoldmobile.composeapp.generated.resources.trade_sell_badge_vault
import habitgoldmobile.composeapp.generated.resources.trade_sell_balance_information
import habitgoldmobile.composeapp.generated.resources.trade_sell_balance_info
import habitgoldmobile.composeapp.generated.resources.trade_sell_button_locked
import habitgoldmobile.composeapp.generated.resources.trade_sell_confirm_amount
import habitgoldmobile.composeapp.generated.resources.trade_sell_confirm_order_body
import habitgoldmobile.composeapp.generated.resources.trade_sell_confirm_order_title
import habitgoldmobile.composeapp.generated.resources.trade_sell_confirm_quantity
import habitgoldmobile.composeapp.generated.resources.trade_sell_confirm_upi
import habitgoldmobile.composeapp.generated.resources.trade_sell_enter_amount
import habitgoldmobile.composeapp.generated.resources.trade_sell_enter_valid_amount
import habitgoldmobile.composeapp.generated.resources.trade_sell_fact_live_price_refresh
import habitgoldmobile.composeapp.generated.resources.trade_sell_fact_sell_anytime
import habitgoldmobile.composeapp.generated.resources.trade_sell_fact_verified_upi
import habitgoldmobile.composeapp.generated.resources.trade_sell_failure_fallback_body
import habitgoldmobile.composeapp.generated.resources.trade_sell_failure_title
import habitgoldmobile.composeapp.generated.resources.trade_sell_fetching_balance
import habitgoldmobile.composeapp.generated.resources.trade_sell_fetching_price
import habitgoldmobile.composeapp.generated.resources.trade_sell_gold_debited
import habitgoldmobile.composeapp.generated.resources.trade_sell_gold_partner
import habitgoldmobile.composeapp.generated.resources.trade_sell_live_price_label
import habitgoldmobile.composeapp.generated.resources.trade_sell_locked_gold
import habitgoldmobile.composeapp.generated.resources.trade_sell_locked_gold_description
import habitgoldmobile.composeapp.generated.resources.trade_sell_locked_gold_title
import habitgoldmobile.composeapp.generated.resources.trade_sell_mode_grams
import habitgoldmobile.composeapp.generated.resources.trade_sell_mode_rupees
import habitgoldmobile.composeapp.generated.resources.trade_sell_net_amount_credited
import habitgoldmobile.composeapp.generated.resources.trade_sell_next_release_on
import habitgoldmobile.composeapp.generated.resources.trade_sell_no_upi_ids_available
import habitgoldmobile.composeapp.generated.resources.trade_sell_no_gold_available
import habitgoldmobile.composeapp.generated.resources.trade_sell_payout_body
import habitgoldmobile.composeapp.generated.resources.trade_sell_pending_body
import habitgoldmobile.composeapp.generated.resources.trade_sell_pending_title
import habitgoldmobile.composeapp.generated.resources.trade_sell_redeemable_gold_description
import habitgoldmobile.composeapp.generated.resources.trade_sell_redeemable_gold_title
import habitgoldmobile.composeapp.generated.resources.trade_sell_redeemable_amount
import habitgoldmobile.composeapp.generated.resources.trade_sell_select_grams
import habitgoldmobile.composeapp.generated.resources.trade_sell_select_payout_upi
import habitgoldmobile.composeapp.generated.resources.trade_sell_sellable_gold
import habitgoldmobile.composeapp.generated.resources.trade_sell_status_completed
import habitgoldmobile.composeapp.generated.resources.trade_sell_success_body
import habitgoldmobile.composeapp.generated.resources.trade_sell_success_title
import habitgoldmobile.composeapp.generated.resources.trade_sell_swipe_to_sell_gold
import habitgoldmobile.composeapp.generated.resources.trade_sell_summary_gold_price
import habitgoldmobile.composeapp.generated.resources.trade_sell_summary_gold_quantity
import habitgoldmobile.composeapp.generated.resources.trade_sell_summary_order_id
import habitgoldmobile.composeapp.generated.resources.trade_sell_tap_to_select
import habitgoldmobile.composeapp.generated.resources.trade_sell_total_amount
import habitgoldmobile.composeapp.generated.resources.trade_sell_to_upi
import habitgoldmobile.composeapp.generated.resources.trade_sell_total_gold
import habitgoldmobile.composeapp.generated.resources.trade_sell_total_gold_balance_title
import habitgoldmobile.composeapp.generated.resources.trade_sell_total_gold_description
import habitgoldmobile.composeapp.generated.resources.trade_sell_upi_payout
import habitgoldmobile.composeapp.generated.resources.trade_sell_updates_in
import habitgoldmobile.composeapp.generated.resources.trade_sell_what_you_can_do
import habitgoldmobile.composeapp.generated.resources.trade_sell_what_you_can_do_body
import habitgoldmobile.composeapp.generated.resources.trade_sell_you_receive
import habitgoldmobile.composeapp.generated.resources.trade_sell_zero_gm
import habitgoldmobile.composeapp.generated.resources.trade_sell_default
import habitgoldmobile.composeapp.generated.resources.trade_invoice_viewer_invalid_url
import habitgoldmobile.composeapp.generated.resources.trade_route_vpa_message
import habitgoldmobile.composeapp.generated.resources.trade_transaction_details_view_invoice
import habitgoldmobile.composeapp.generated.resources.common_got_it
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlinx.coroutines.delay
import kotlin.math.ceil
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.roundToLong

@Composable
fun SellTradeScreen(
    state: SellTradeState,
    livePriceState: TradeLivePriceState,
    onBackClick: () -> Unit,
    onHelpClick: () -> Unit,
    onGoToDashboard: () -> Unit,
    getTradeInvoiceUseCase: GetTradeInvoiceUseCase,
    onOpenInvoice: (String) -> Unit,
    onIntent: (SellTradeIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    var amountInput by rememberSaveable { mutableStateOf("") }
    var hasAutofilled by rememberSaveable { mutableStateOf(false) }
    var validationMessage by rememberSaveable { mutableStateOf<String?>(null) }
    val activeEntryMode = SellTradeEntryMode.Rupees
    var isFetchingInvoice by remember(state.step, state.createdOrder?.orderId, state.pollingSnapshot?.orderId) {
        mutableStateOf(false)
    }
    var invoiceErrorMessage by remember(state.step, state.createdOrder?.orderId, state.pollingSnapshot?.orderId) {
        mutableStateOf<String?>(null)
    }

    val sellPrice = livePriceState.price?.sell ?: 0.0
    val sellRateId = livePriceState.price?.sellRateId.orEmpty()
    val sellableBalance = state.availability?.sellableGoldBalanceGrams ?: 0.0
    val totalBalance = state.availability?.totalGoldBalanceGrams ?: 0.0
    val lockedBalance = state.availability?.lockedGoldBalanceGrams ?: 0.0
    val isSellDisabled = sellableBalance <= 0.0 && totalBalance > 0.0
    val allGoldLockedMessage = stringResource(Res.string.trade_sell_all_gold_locked)
    val fetchingPriceMessage = stringResource(Res.string.trade_sell_fetching_price)
    val noGoldAvailableMessage = stringResource(Res.string.trade_sell_no_gold_available)
    val fetchingBalanceMessage = stringResource(Res.string.trade_sell_fetching_balance)
    val enterValidAmountMessage = stringResource(Res.string.trade_sell_enter_valid_amount)
    val invalidInvoiceMessage = stringResource(Res.string.trade_invoice_viewer_invalid_url)

    LaunchedEffect(state.step) {
        if (state.step != SellTradeStep.Entry) {
            validationMessage = null
        }
    }

    LaunchedEffect(isFetchingInvoice, state.createdOrder?.orderId, state.pollingSnapshot?.orderId) {
        if (!isFetchingInvoice) return@LaunchedEffect
        val orderId = state.createdOrder?.orderId ?: state.pollingSnapshot?.orderId
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

    LaunchedEffect(sellableBalance, sellPrice, activeEntryMode) {
        if (!hasAutofilled && sellableBalance > 0.0 && sellPrice > 0.0) {
            val tenPercentGrams = sellableBalance * 0.10
            amountInput = when (activeEntryMode) {
                SellTradeEntryMode.Rupees -> roundToMoney(tenPercentGrams * sellPrice).toInt().toString()
                SellTradeEntryMode.Grams -> formatGold(tenPercentGrams)
            }
            hasAutofilled = true
        }
    }

    val sellComputation = remember(activeEntryMode, amountInput, sellPrice, sellableBalance) {
        computeSellTrade(
            entryMode = activeEntryMode,
            rawInput = amountInput,
            sellPrice = sellPrice,
            sellableBalance = sellableBalance,
        )
    }

    when (state.step) {
        SellTradeStep.Entry -> SellTradeEntryScreen(
            state = state,
            livePriceState = livePriceState,
            amountInput = amountInput,
            sellComputation = sellComputation,
            validationMessage = validationMessage ?: state.errorMessage,
            totalBalance = totalBalance,
            sellableBalance = sellableBalance,
            lockedBalance = lockedBalance,
            onBackClick = onBackClick,
            onHelpClick = onHelpClick,
            onIntent = onIntent,
            onAmountChange = { next ->
                validationMessage = null
                amountInput = when (activeEntryMode) {
                    SellTradeEntryMode.Rupees -> next.filter(Char::isDigit).take(6)
                    SellTradeEntryMode.Grams -> sanitizeGramInput(next, fractionDigits = 4)
                }
            },
            onContinue = {
                if (isSellDisabled) {
                    validationMessage = allGoldLockedMessage
                    return@SellTradeEntryScreen
                }
                if (sellPrice <= 0.0 || sellRateId.isBlank()) {
                    validationMessage = fetchingPriceMessage
                    return@SellTradeEntryScreen
                }
                if (sellableBalance <= 0.0 && totalBalance <= 0.0) {
                    validationMessage = noGoldAvailableMessage
                    return@SellTradeEntryScreen
                }
                if (sellableBalance <= 0.0) {
                    validationMessage = fetchingBalanceMessage
                    return@SellTradeEntryScreen
                }
                if (sellComputation.tradableGrams <= 0.0) {
                    validationMessage = sellComputation.message ?: enterValidAmountMessage
                    return@SellTradeEntryScreen
                }
                validationMessage = null
                onIntent(
                    SellTradeIntent.ContinueToPayout(
                        grams = sellComputation.tradableGrams,
                        sellRateId = sellRateId,
                        estimatedPayoutAmount = sellComputation.payoutAmount,
                    ),
                )
            },
            modifier = modifier,
        )

        SellTradeStep.PayoutVpa -> SellTradePayoutScreen(
            state = state,
            livePriceState = livePriceState,
            onBackClick = { onIntent(SellTradeIntent.BackToEntry) },
            onIntent = onIntent,
            modifier = modifier,
        )

        SellTradeStep.Success -> SellTradeSuccessScreen(
            amount = "₹${formatMoney(state.createdOrder?.payoutAmount ?: 0.0)}",
            goldDebited = "${formatGold(state.createdOrder?.goldQuantityGrams ?: 0.0)} g",
            creditedUpiId = state.userVpas.firstOrNull { it.id == state.selectedVpaId }?.address.orEmpty(),
            invoiceErrorMessage = invoiceErrorMessage,
            isInvoiceLoading = isFetchingInvoice,
            onViewInvoiceClick = {
                if (!isFetchingInvoice) {
                    isFetchingInvoice = true
                }
            },
            onGoToDashboard = onGoToDashboard,
            modifier = modifier,
        )

        SellTradeStep.Failure -> SellTradeFailureScreen(
            message = state.errorMessage ?: stringResource(Res.string.trade_sell_failure_fallback_body),
            onGoToDashboard = onGoToDashboard,
            modifier = modifier,
        )

        SellTradeStep.Pending -> SellTradePendingScreen(
            orderId = state.createdOrder?.orderId.orEmpty(),
            onGoToDashboard = onGoToDashboard,
            modifier = modifier,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SellTradeEntryScreen(
    state: SellTradeState,
    livePriceState: TradeLivePriceState,
    amountInput: String,
    sellComputation: SellComputation,
    validationMessage: String?,
    totalBalance: Double,
    sellableBalance: Double,
    lockedBalance: Double,
    onBackClick: () -> Unit,
    onHelpClick: () -> Unit,
    onIntent: (SellTradeIntent) -> Unit,
    onAmountChange: (String) -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showBalanceSheet by remember { mutableStateOf(false) }
    var currentFactIndex by rememberSaveable { mutableIntStateOf(0) }
    val balanceSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val focusManager = LocalFocusManager.current
    val facts = listOf(
        stringResource(Res.string.trade_sell_fact_sell_anytime),
        stringResource(Res.string.trade_sell_fact_verified_upi),
        stringResource(Res.string.trade_sell_fact_live_price_refresh),
    )
    val availableAmount = remember(sellableBalance, livePriceState.price?.sell) {
        if (sellableBalance > 0.0 && (livePriceState.price?.sell ?: 0.0) > 0.0) {
            sellableBalance * (livePriceState.price?.sell ?: 0.0)
        } else null
    }
    val totalAmount = remember(totalBalance, livePriceState.price?.sell) {
        if (totalBalance > 0.0 && (livePriceState.price?.sell ?: 0.0) > 0.0) {
            totalBalance * (livePriceState.price?.sell ?: 0.0)
        } else null
    }
    val isSellDisabled = sellableBalance <= 0.0 && totalBalance > 0.0

    LaunchedEffect(Unit) {
        while (true) {
            delay(3500)
            currentFactIndex = (currentFactIndex + 1) % facts.size
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
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
                    text = stringResource(Res.string.sell_gold_screen_sell_gold),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B),
                    modifier = Modifier.align(Alignment.Center),
                )
                IconButton(
                    onClick = onHelpClick,
                    modifier = Modifier.align(Alignment.CenterEnd),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Help,
                        contentDescription = stringResource(Res.string.common_help),
                        tint = HabitGoldPalette.plum,
                        modifier = Modifier.size(24.dp),
                    )
                }
            }
        },
        bottomBar = {
            SellTradeActionFooter(
                livePriceState = livePriceState,
                label = if (isSellDisabled) {
                    stringResource(Res.string.trade_sell_button_locked)
                } else {
                    stringResource(Res.string.trade_sell_action_continue)
                },
                enabled = !isSellDisabled && !state.isLoading && livePriceState.price?.sell != null,
                isLoading = state.isLoading,
                onActionClick = onContinue,
            )
        },
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { focusManager.clearFocus() }
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            SellTradeInfoPill(
                facts = facts,
                currentFactIndex = currentFactIndex,
            )

            Spacer(modifier = Modifier.height(12.dp))

            SellTradeBalanceSummaryRow(
                redeemableAmount = availableAmount,
                totalAmount = totalAmount,
                onInfoClick = { showBalanceSheet = true },
            )

            Spacer(modifier = Modifier.height(16.dp))

            SellTradeInputSection(
                entryMode = SellTradeEntryMode.Rupees,
                value = amountInput,
                sellableBalance = sellableBalance,
                tradableGrams = sellComputation.tradableGrams,
                payoutAmount = sellComputation.payoutAmount,
                onValueChange = onAmountChange,
            )

            Spacer(modifier = Modifier.weight(1f))
            SellTradeSafetyLayer()

            validationMessage?.takeIf { it.isNotBlank() }?.let { message ->
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = message,
                    color = Color(0xFFDC2626),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        if (showBalanceSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBalanceSheet = false },
                sheetState = balanceSheetState,
                containerColor = Color.White,
                dragHandle = {
                    Box(
                        modifier = Modifier
                            .padding(vertical = 12.dp)
                            .size(width = 32.dp, height = 4.dp)
                            .background(Color(0xFFE2E8F0), RoundedCornerShape(999.dp)),
                    )
                },
            ) {
                SellTradeBalanceInformationSheet(
                    totalBalance = totalBalance,
                    sellableBalance = sellableBalance,
                    lockedBalance = lockedBalance,
                    sellPrice = livePriceState.price?.sell ?: 0.0,
                    nextSellableAt = state.availability?.nextSellableAt,
                    onDismiss = { showBalanceSheet = false },
                )
            }
        }
    }
}

@Composable
private fun SellTradeInfoPill(
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
            tint = Color(0xFFD2A700),
            modifier = Modifier.size(16.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        AnimatedContent(
            targetState = currentFactIndex,
            transitionSpec = {
                if (targetState > initialState) {
                    (slideInVertically { height -> height } + fadeIn()) togetherWith
                        (slideOutVertically { height -> -height } + fadeOut())
                } else {
                    (slideInVertically { height -> -height } + fadeIn()) togetherWith
                        (slideOutVertically { height -> height } + fadeOut())
                }.using(SizeTransform(clip = false))
            },
            label = "sellTradeFactAnimation",
        ) { targetIndex ->
            Text(
                text = facts[targetIndex],
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFD2A700),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun SellTradeBalanceSummaryRow(
    redeemableAmount: Double?,
    totalAmount: Double?,
    onInfoClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(Res.string.trade_sell_redeemable_amount),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF64748B),
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = redeemableAmount?.let { "₹${formatMoney(it)}" } ?: "--",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF16A34A),
                )
            }

            if (totalAmount != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(Res.string.trade_sell_total_amount),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF64748B),
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "₹${formatMoney(totalAmount)}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = HabitGoldPalette.plum,
                    )
                }
            }
        }

        IconButton(
            onClick = onInfoClick,
            modifier = Modifier.size(24.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = stringResource(Res.string.trade_sell_balance_info),
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun SellTradeInputSection(
    entryMode: SellTradeEntryMode,
    value: String,
    sellableBalance: Double,
    tradableGrams: Double,
    payoutAmount: Double,
    onValueChange: (String) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    Text(
        text = if (entryMode == SellTradeEntryMode.Rupees) {
            stringResource(Res.string.trade_sell_enter_amount)
        } else {
            stringResource(Res.string.trade_sell_select_grams)
        },
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF94A3B8),
        letterSpacing = 2.sp,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Start,
    )

    Spacer(modifier = Modifier.height(8.dp))

    if (entryMode == SellTradeEntryMode.Rupees) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
                .padding(vertical = 12.dp, horizontal = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "₹",
                fontSize = 48.sp,
                color = Color(0xFF94A3B8),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.CenterStart),
            )

            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF020617),
                    textAlign = TextAlign.Center,
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                cursorBrush = SolidColor(HabitGoldPalette.plum),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 48.dp),
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.Center) {
                        if (value.isEmpty()) {
                            Text(
                                text = "0",
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFCBD5E1),
                                textAlign = TextAlign.Center,
                            )
                        }
                        innerTextField()
                    }
                },
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "You're selling ${formatGold(tradableGrams)}gm of gold.",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD2A700),
                textAlign = TextAlign.Center,
            )
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SellTradeStepButton(
                label = "-",
                onClick = {
                    val current = value.toDoubleOrNull() ?: 0.0
                    if (current >= 0.1) {
                        onValueChange(formatGold(current - 0.1))
                    }
                },
            )

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
                    .padding(vertical = 6.dp),
            ) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF020617),
                        textAlign = TextAlign.Center,
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    cursorBrush = SolidColor(HabitGoldPalette.plum),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp),
                    decorationBox = { innerTextField ->
                        Box(contentAlignment = Alignment.Center) {
                            if (value.isEmpty()) {
                                Text(
                                    text = "0.0",
                                    fontSize = 42.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFCBD5E1),
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
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterEnd).padding(end = 12.dp),
                )
            }

            SellTradeStepButton(
                label = "+",
                onClick = {
                    val current = value.toDoubleOrNull() ?: 0.0
                    val next = if (current + 0.1 <= sellableBalance) current + 0.1 else sellableBalance
                    onValueChange(formatGold(next))
                },
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Slider(
            value = (value.toFloatOrNull() ?: 0f).coerceIn(0f, sellableBalance.toFloat()),
            onValueChange = { onValueChange(formatGold(it.toDouble())) },
            valueRange = 0f..sellableBalance.toFloat().coerceAtLeast(0f),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
                .offset(y = (-8).dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(Res.string.trade_sell_zero_gm),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF94A3B8),
            )
            Text(
                text = "${formatGold(sellableBalance)} gm",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF94A3B8),
            )
        }
    }
}

@Composable
private fun SellTradeStepButton(
    label: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .background(Color.White)
            .border(1.dp, HabitGoldPalette.plum, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = HabitGoldPalette.plum,
        )
    }
}

@Composable
private fun SellTradeSafetyLayer() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 2.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.WorkspacePremium,
                contentDescription = null,
                tint = HabitGoldPalette.plum,
                modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(Res.string.trade_sell_gold_partner),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF64748B),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Image(
                painter = painterResource(Res.drawable.safegold_image),
                contentDescription = stringResource(Res.string.common_safegold),
                modifier = Modifier.height(10.dp).widthIn(min = 56.dp),
                contentScale = ContentScale.Fit,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SellTradeSafetyBadge(Icons.Default.Verified, stringResource(Res.string.trade_sell_badge_insured), Modifier.weight(1f))
            SellTradeSafetyBadge(Icons.Default.AutoAwesome, stringResource(Res.string.trade_sell_badge_physical), Modifier.weight(1f))
            SellTradeSafetyBadge(Icons.Default.Lock, stringResource(Res.string.trade_sell_badge_vault), Modifier.weight(1f))
        }
    }
}

@Composable
private fun SellTradeSafetyBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(horizontal = 4.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF64748B),
            modifier = Modifier.size(12.dp),
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            lineHeight = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
internal fun SellTradeVpaCard(
    vpa: TradeUserVpa,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFF8F2FF) else Color(0xFFF8FAFC),
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) Color(0xFFD8B4FE) else Color(0xFFE2E8F0),
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(Color.White, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Payments,
                        contentDescription = null,
                        tint = HabitGoldPalette.plum,
                        modifier = Modifier.size(18.dp),
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = vpa.address,
                            color = Color(0xFF020617),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        if (vpa.isDefault) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = HabitGoldPalette.plum.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(4.dp),
                            ) {
                                Text(
                                    text = stringResource(Res.string.trade_sell_default),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HabitGoldPalette.plum,
                                )
                            }
                        }
                    }
                    Text(
                        text = if (isSelected) {
                            stringResource(Res.string.common_selected)
                        } else {
                            stringResource(Res.string.trade_sell_tap_to_select)
                        },
                        fontSize = 12.sp,
                        color = if (isSelected) HabitGoldPalette.plum else Color(0xFF64748B),
                    )
                }
            }

            Icon(
                imageVector = if (isSelected) Icons.Default.Check else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (isSelected) HabitGoldPalette.plum else Color(0xFF94A3B8),
            )
        }
    }
}

internal data class SellComputation(
    val tradableGrams: Double,
    val payoutAmount: Double,
    val message: String? = null,
)

private const val MinSellValueRupees = 10.0
private const val MinTradeUnitGrams = 0.0001
private const val UnitsPerGram = 10_000L

internal fun computeSellTrade(
    entryMode: SellTradeEntryMode,
    rawInput: String,
    sellPrice: Double,
    sellableBalance: Double,
): SellComputation {
    val safeInput = max(rawInput.toDoubleOrNull() ?: 0.0, 0.0)
    if (sellPrice <= 0.0 || safeInput <= 0.0) return SellComputation(0.0, 0.0)
    if (sellableBalance <= 0.0) return SellComputation(0.0, 0.0, "No sellable balance available right now.")

    val eps = 1e-9
    val availableValue = sellableBalance * sellPrice

    if (entryMode == SellTradeEntryMode.Rupees && safeInput > availableValue + eps) {
        return SellComputation(0.0, 0.0, "Amount exceeds your sellable gold balance.")
    }

    val rawGrams = when (entryMode) {
        SellTradeEntryMode.Rupees -> safeInput / sellPrice
        SellTradeEntryMode.Grams -> safeInput
    }

    if (entryMode == SellTradeEntryMode.Grams && rawGrams > sellableBalance + eps) {
        return SellComputation(0.0, 0.0, "Amount exceeds your sellable gold balance.")
    }

    val rawUnits = rawGrams * UnitsPerGram.toDouble()
    val nearestUnits = rawUnits.roundToLong()
    val isExactMultiple = kotlin.math.abs(rawUnits - nearestUnits.toDouble()) < 1e-9
    val upUnits = if (isExactMultiple) nearestUnits else ceil(rawUnits - 1e-12).toLong()
    val downUnits = if (isExactMultiple) nearestUnits else floor(rawUnits + 1e-12).toLong()

    val chosenUnits = if (isExactMultiple) {
        nearestUnits
    } else {
        val upGrams = upUnits.toDouble() / UnitsPerGram.toDouble()
        if (upGrams <= sellableBalance + eps) upUnits else downUnits
    }

    if (chosenUnits <= 0L) {
        return SellComputation(0.0, 0.0, "Minimum sell quantity is 0.0001g.")
    }

    val chosenGrams = chosenUnits.toDouble() / UnitsPerGram.toDouble()
    if (chosenGrams + eps < MinTradeUnitGrams) {
        return SellComputation(0.0, 0.0, "Minimum sell quantity is 0.0001g.")
    }
    if ((chosenGrams * sellPrice) + eps < MinSellValueRupees) {
        return SellComputation(0.0, 0.0, "Amount must be at least ₹10.")
    }

    return SellComputation(
        tradableGrams = chosenGrams,
        payoutAmount = roundSellPayoutToPaise(chosenGrams * sellPrice),
        message = null,
    )
}

internal fun roundSellPayoutToPaise(value: Double): Double {
    val scaledToMill = floor(max(value, 0.0) * 1000 + 1e-9) / 1000.0
    return kotlin.math.ceil(scaledToMill * 100 - 1e-9) / 100.0
}

internal fun formatGold(value: Double): String {
    return formatGoldQuantity(value, unitsPerGram = UnitsPerGram)
}

internal fun formatNextSellableLabel(raw: String): String {
    return runCatching {
        val local = Instant.parse(raw).toLocalDateTime(TimeZone.currentSystemDefault())
        val hour = local.hour % 12
        val displayHour = if (hour == 0) 12 else hour
        val meridiem = if (local.hour >= 12) "PM" else "AM"
        "${local.day.toString().padStart(2, '0')} ${monthAbbreviation(local.month.name)}, " +
            "$displayHour:${local.minute.toString().padStart(2, '0')} $meridiem"
    }.getOrElse { raw }
}

private fun monthAbbreviation(monthName: String): String {
    return monthName.lowercase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        .take(3)
}
