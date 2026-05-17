package com.habit.gold.feature.savings.presentation

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
import habitgoldmobile.composeapp.generated.resources.savings_setup_monthly_date_label
import habitgoldmobile.composeapp.generated.resources.savings_setup_next_debit
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SavingsSetupScreen(
    state: SavingsSetupUiState,
    livePriceState: TradeLivePriceState,
    onBackClick: () -> Unit,
    onHelpClick: () -> Unit,
    onAmountChange: (String) -> Unit,
    onExecutionDaySelect: (Int) -> Unit,
    onQuickAmountSelected: (Int) -> Unit,
    onCouponDraftChange: (String) -> Unit,
    onApplyCoupon: (String) -> Unit,
    onRemoveAppliedCoupon: () -> Unit,
    onSubmit: () -> Unit,
    onRetryPolling: () -> Unit,
    onGoHome: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    when (val phase = state.phase) {
        is SavingsSetupPhase.Polling -> {
            SavingsSetupProgressState(
                title = stringResource(Res.string.savings_setup_polling_title),
                body = stringResource(Res.string.savings_setup_polling_body, phase.attempt),
                modifier = modifier,
            )
            return
        }

        is SavingsSetupPhase.Processing -> {
            SavingsSetupTerminalState(
                icon = Icons.Default.Sync,
                title = stringResource(Res.string.savings_setup_pending_title),
                body = stringResource(Res.string.savings_setup_pending_body),
                primaryLabel = stringResource(Res.string.savings_setup_go_home),
                onPrimaryClick = onGoHome,
                modifier = modifier,
            )
            return
        }

        is SavingsSetupPhase.Success -> {
            SavingsSetupTerminalState(
                icon = Icons.Default.CheckCircle,
                title = stringResource(Res.string.savings_setup_success_title),
                body = stringResource(Res.string.savings_setup_success_body, state.frequency.displayName()),
                primaryLabel = stringResource(Res.string.savings_setup_go_home),
                onPrimaryClick = onGoHome,
                modifier = modifier,
            )
            return
        }

        is SavingsSetupPhase.Failure -> {
            SavingsSetupTerminalState(
                icon = Icons.Default.Warning,
                title = stringResource(Res.string.savings_setup_failure_title),
                body = phase.message.ifBlank { stringResource(Res.string.savings_setup_failure_body) },
                primaryLabel = stringResource(Res.string.common_retry),
                onPrimaryClick = onRetryPolling,
                secondaryLabel = stringResource(Res.string.savings_setup_go_home),
                onSecondaryClick = onGoHome,
                modifier = modifier,
            )
            return
        }

        SavingsSetupPhase.Form -> Unit
    }

    val title = state.setupTitle()
    val submitLabel = state.submitLabel()
    var showWeeklySelector by remember(state.frequency) { mutableStateOf(false) }
    var showMonthlySelector by remember(state.frequency) { mutableStateOf(false) }
    var showCouponSheet by remember { mutableStateOf(false) }
    var showCompoundingInfo by remember { mutableStateOf(false) }
    val weeklySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val monthlySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val compoundingSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val monthlyInvestment = remember(state.amountValue, state.frequency) {
        monthlyContribution(state.amountValue ?: 0, state.frequency)
    }
    val compoundingSummary = remember(monthlyInvestment) {
        calculateCompoundingSummary(monthlyInvestment)
    }
    val estimateAmount = remember(state.amountValue) { state.amountValue?.toDouble() ?: 0.0 }
    val liveBuyPrice = livePriceState.price?.buy ?: 0.0
    val projectionMessages = remember(state.amountValue, state.frequency, liveBuyPrice) {
        projectionMessages(
            amount = state.amountValue ?: 0,
            goldPrice = liveBuyPrice,
            frequency = state.frequency,
        )
    }
    var projectionIndex by remember(projectionMessages) { mutableStateOf(0) }
    val projectionText = projectionMessages.getOrNull(projectionIndex)
    val calendarMonth = remember { currentSavingsMonth() }

    LaunchedEffect(projectionMessages) {
        projectionIndex = 0
        if (projectionMessages.size > 1) {
            while (true) {
                delay(2200)
                projectionIndex = (projectionIndex + 1) % projectionMessages.size
            }
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        color = ChildPrimaryText,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = ChildPrimaryText,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onHelpClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Help,
                            contentDescription = stringResource(Res.string.common_help),
                            tint = HabitGoldPalette.plum,
                        )
                    }
                },
            )
        },
        bottomBar = {
            SavingsSetupBottomBar(
                livePriceState = livePriceState,
                isSubmitting = state.isSubmitting,
                submitLabel = submitLabel,
                onSubmit = onSubmit,
            )
        },
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { focusManager.clearFocus(force = true) }
                .verticalScroll(rememberScrollState()),
        ) {
            SavingsHeroImage(frequency = state.frequency)
            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = stringResource(Res.string.savings_setup_amount_label),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ChildPrimaryText,
                    )
                    SavingsAmountCard(
                        value = state.amountText,
                        suffixLabel = state.scheduleSuffixLabel(),
                        showScheduleSelector = state.frequency != SavingsFrequency.Daily,
                        onScheduleClick = {
                            focusManager.clearFocus(force = true)
                            when (state.frequency) {
                                SavingsFrequency.Daily -> Unit
                                SavingsFrequency.Weekly -> showWeeklySelector = true
                                SavingsFrequency.Monthly -> showMonthlySelector = true
                            }
                        },
                        onValueChange = onAmountChange,
                        enabled = state.canEditAmount,
                        onDone = { focusManager.clearFocus(force = true) },
                    )
                    projectionText?.let { text ->
                        SavingsProjectionRow(text = text)
                    }
                    if (!state.inlineErrorMessage.isNullOrBlank()) {
                        Text(
                            text = state.inlineErrorMessage,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFB91C1C),
                        )
                    }
                    SavingsQuickAmountRow(
                        quickAmounts = state.frequency.quickAmounts,
                        selectedAmountText = state.amountText,
                        onQuickAmountSelected = onQuickAmountSelected,
                    )
                }

                SavingsCouponRow(
                    availableCoupons = state.availableCoupons.size,
                    couponDraft = state.couponDraft,
                    appliedCouponCode = state.appliedCoupon?.code,
                    appliedBenefitText = state.appliedCoupon?.let(::savingsAppliedCouponSummary),
                    onCouponDraftChange = onCouponDraftChange,
                    onApplyCoupon = { onApplyCoupon(state.couponDraft) },
                    onRemoveAppliedCoupon = onRemoveAppliedCoupon,
                    onShowOffers = { showCouponSheet = true },
                    isApplyingEnabled = state.couponDraft.isNotBlank() && state.appliedCoupon?.code != state.couponDraft,
                    onDone = { focusManager.clearFocus(force = true) },
                )

                compoundingSummary?.let { summary ->
                    SavingsCompoundingPreviewCard(
                        amount = state.amountValue ?: 0,
                        frequency = state.frequency,
                        summary = summary,
                        onInfoClick = { showCompoundingInfo = true },
                    )
                }

                state.selectedExecutionDay?.let { executionDay ->
                    SavingsNextPaymentCard(
                        nextPaymentText = state.nextDebitLabel(executionDay),
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showWeeklySelector) {
        ModalBottomSheet(
            onDismissRequest = { showWeeklySelector = false },
            sheetState = weeklySheetState,
            containerColor = Color.White,
            tonalElevation = 0.dp,
            dragHandle = { BottomSheetDefaults.DragHandle() },
        ) {
            SavingsExecutionSheet(
                title = stringResource(Res.string.savings_setup_day_label),
                options = SavingsWeeklyExecutionDays.map { (value, label) ->
                    SavingsExecutionOption(value = value, label = label)
                },
                selectedValue = state.selectedExecutionDay,
                onSelect = {
                    onExecutionDaySelect(it)
                    showWeeklySelector = false
                },
            )
        }
    }

    if (showMonthlySelector) {
        ModalBottomSheet(
            onDismissRequest = { showMonthlySelector = false },
            sheetState = monthlySheetState,
            containerColor = Color.White,
            tonalElevation = 0.dp,
            dragHandle = { BottomSheetDefaults.DragHandle() },
        ) {
            SavingsExecutionSheet(
                title = stringResource(Res.string.savings_setup_monthly_date_label),
                options = emptyList(),
                selectedValue = state.selectedExecutionDay,
                onSelect = {},
                content = {
                    SavingsMonthlyExecutionSheet(
                        month = calendarMonth,
                        selectedValue = state.selectedExecutionDay,
                        onSelect = {
                            onExecutionDaySelect(it)
                            showMonthlySelector = false
                        },
                    )
                },
            )
        }
    }

    if (showCouponSheet) {
        ModalBottomSheet(
            onDismissRequest = { showCouponSheet = false },
            containerColor = Color.White,
            tonalElevation = 0.dp,
            dragHandle = { BottomSheetDefaults.DragHandle() },
        ) {
            BuyCouponSheet(
                coupons = state.availableCoupons,
                estimateAmount = estimateAmount,
                appliedCouponCode = state.appliedCoupon?.code,
                onApplyCoupon = {
                    onApplyCoupon(it)
                    showCouponSheet = false
                },
            )
        }
    }

    if (showCompoundingInfo && compoundingSummary != null) {
        ModalBottomSheet(
            onDismissRequest = { showCompoundingInfo = false },
            sheetState = compoundingSheetState,
            containerColor = Color.White,
            tonalElevation = 0.dp,
            dragHandle = { BottomSheetDefaults.DragHandle() },
        ) {
            SavingsCompoundingInfoSheet(
                amount = state.amountValue ?: 0,
                frequency = state.frequency,
                summary = compoundingSummary,
            )
        }
    }
}

@Composable
private fun SavingsHeroImage(
    frequency: SavingsFrequency,
) {
    val image = when (frequency) {
        SavingsFrequency.Daily -> Res.drawable.start_daily_savings_icon
        SavingsFrequency.Weekly -> Res.drawable.start_weekly_savings_icon
        SavingsFrequency.Monthly -> Res.drawable.start_monthly_savings_icon
    }
    Image(
        painter = painterResource(image),
        contentDescription = "${frequency.displayName()} savings",
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        contentScale = ContentScale.Crop,
    )
}

@Composable
private fun SavingsProjectionRow(
    text: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
            contentDescription = null,
            tint = HabitGoldPalette.plum,
            modifier = Modifier.size(18.dp),
        )
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = ChildMutedText,
            lineHeight = 18.sp,
        )
    }
}

@Composable
private fun SavingsQuickAmountRow(
    quickAmounts: List<SavingsQuickAmount>,
    selectedAmountText: String,
    onQuickAmountSelected: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        quickAmounts.forEach { option ->
            Box(
                contentAlignment = Alignment.TopCenter,
            ) {
                OutlinedButton(
                    onClick = { onQuickAmountSelected(option.amount) },
                    modifier = Modifier.padding(bottom = if (option.tag != null) 12.dp else 0.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(
                        if (selectedAmountText == option.amount.toString()) 1.5.dp else 1.dp,
                        if (selectedAmountText == option.amount.toString()) HabitGoldPalette.plum else HabitGoldPalette.plum.copy(alpha = 0.3f),
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = "₹${formatInr(option.amount.toDouble())}",
                        color = if (selectedAmountText == option.amount.toString()) HabitGoldPalette.plum else ChildPrimaryText,
                        fontWeight = if (selectedAmountText == option.amount.toString()) FontWeight.Bold else FontWeight.SemiBold,
                    )
                }
                option.tag?.let { tag ->
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomCenter),
                        shape = RoundedCornerShape(2.dp),
                        color = HabitGoldPalette.plum,
                        border = BorderStroke(1.dp, HabitGoldPalette.white.copy(alpha = 0.22f)),
                    ) {
                        Text(
                            text = tag,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 0.dp),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = HabitGoldPalette.white,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SavingsCouponRow(
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, ChildCardBorder, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        if (appliedCouponCode != null) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF10B981),
                modifier = Modifier.size(16.dp),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(0.dp),
            ) {
                Text(
                    text = appliedCouponCode,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = ChildPrimaryText,
                )
                appliedBenefitText?.let {
                    Text(
                        text = it,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = ChildMutedText,
                    )
                }
            }
            Text(
                text = stringResource(Res.string.trade_buy_change),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = HabitGoldPalette.plum,
                modifier = Modifier.clickable(onClick = onShowOffers),
            )
            Text(
                text = stringResource(Res.string.trade_buy_remove),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFB91C1C),
                modifier = Modifier.clickable(onClick = onRemoveAppliedCoupon),
            )
        } else {
            BasicTextField(
                value = couponDraft,
                onValueChange = onCouponDraftChange,
                singleLine = true,
                cursorBrush = SolidColor(HabitGoldPalette.plum),
                textStyle = TextStyle(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ChildPrimaryText,
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .background(Color.White, RoundedCornerShape(10.dp))
                    .border(1.dp, ChildCardBorder, RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(onDone = { onDone() }),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        if (couponDraft.isEmpty()) {
                            Text(
                                text = stringResource(Res.string.trade_buy_enter_coupon_code),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFC7CAD1),
                            )
                        }
                        innerTextField()
                    }
                },
            )
            Button(
                onClick = onApplyCoupon,
                enabled = isApplyingEnabled,
                modifier = Modifier.height(36.dp),
                shape = RoundedCornerShape(999.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = HabitGoldPalette.plum,
                    contentColor = Color.White,
                    disabledContainerColor = ChildCardBorder,
                    disabledContentColor = ChildMutedText,
                ),
            ) {
                Text(
                    text = stringResource(Res.string.trade_buy_apply),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Row(
                modifier = Modifier.clickable(onClick = onShowOffers),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.trade_buy_offers),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = HabitGoldPalette.plum,
                )
                if (availableCoupons > 0) {
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = HabitGoldPalette.plum,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SavingsAmountCard(
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
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = HabitGoldPalette.plum,
            )
            Spacer(modifier = Modifier.width(10.dp))
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                enabled = enabled,
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 30.sp,
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
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFC7CAD1),
                        )
                    }
                    innerTextField()
                },
            )
            Spacer(modifier = Modifier.width(12.dp))
            Surface(
                onClick = onScheduleClick,
                enabled = showScheduleSelector,
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFF1EDFF),
                border = if (showScheduleSelector) BorderStroke(1.dp, Color(0xFFE1DAFF)) else null,
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = suffixLabel,
                        fontSize = 13.sp,
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
private fun SavingsMonthlyExecutionSheet(
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
private fun SavingsExecutionSheet(
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
private fun SavingsCompoundingPreviewCard(
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
                    text = "Your ₹$amount ${frequency.routeValue.lowercase()} will grow to",
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
private fun SavingsNextPaymentCard(
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
                    text = "Next payment: ",
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
private fun SavingsCompoundingInfoSheet(
    amount: Int,
    frequency: SavingsFrequency,
    summary: SavingsCompoundingSummary,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.94f)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Compounding breakdown",
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = ChildPrimaryText,
        )
        Text(
            text = "Estimated growth at ~12% annual return on your ₹$amount ${frequency.routeValue.lowercase()} investment.",
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
                SavingsSummaryRow("Total Invested (${summary.projectionYears}Y)", "₹${formatRoundedMoney(summary.totalInvested)}")
                SavingsSummaryRow("Estimated Earnings", "+₹${formatRoundedMoney(summary.estimatedEarnings)}", valueColor = Color(0xFF10B981))
                HorizontalDivider(color = ChildCardBorder)
                SavingsSummaryRow(
                    "Maturity Value",
                    "₹${formatRoundedMoney(summary.totalValue)}",
                    valueColor = HabitGoldPalette.plum,
                    valueWeight = FontWeight.ExtraBold,
                )
                HorizontalDivider(color = ChildCardBorder)
                SavingsSummaryRow("Frequency", frequency.displayName())
                SavingsSummaryRow("Investment amount", "₹${formatRoundedMoney(amount.toDouble())}")
                SavingsSummaryRow("Monthly equivalent", "₹${formatRoundedMoney(summary.monthlyInvestment)}")
            }
        }
        Text(
            text = "Illustrative returns only. Actual growth may vary with live gold prices and market conditions.",
            fontSize = 12.sp,
            color = ChildMutedText,
            lineHeight = 18.sp,
        )
    }
}

@Composable
private fun SavingsSummaryRow(
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

private fun savingsAppliedCouponSummary(
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

private fun formatRoundedMoney(value: Double): String = formatInr(value)

@Composable
private fun SavingsSetupBottomBar(
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
private fun SavingsSetupProgressState(
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
private fun SavingsSetupTerminalState(
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

private data class SavingsExecutionOption(
    val value: Int,
    val label: String,
)

@Composable
private fun SavingsSetupUiState.setupTitle(): String {
    if (isPausedMandate) return stringResource(Res.string.savings_setup_resume_cta, frequency.displayName())
    if (isUpgradeFlow) return stringResource(Res.string.savings_setup_upgrade_title, frequency.displayName())
    return when (frequency) {
        SavingsFrequency.Daily -> stringResource(Res.string.savings_setup_frequency_daily_title)
        SavingsFrequency.Weekly -> stringResource(Res.string.savings_setup_frequency_weekly_title)
        SavingsFrequency.Monthly -> stringResource(Res.string.savings_setup_frequency_monthly_title)
    }
}

@Composable
private fun SavingsSetupUiState.submitLabel(): String {
    return when {
        isPausedMandate -> stringResource(Res.string.savings_setup_resume_cta, frequency.displayName())
        isUpgradeFlow -> stringResource(Res.string.savings_setup_submit_upgrade, frequency.displayName())
        frequency == SavingsFrequency.Daily -> stringResource(Res.string.savings_setup_frequency_daily_cta)
        frequency == SavingsFrequency.Weekly -> stringResource(Res.string.savings_setup_frequency_weekly_cta)
        else -> stringResource(Res.string.savings_setup_frequency_monthly_cta)
    }
}
