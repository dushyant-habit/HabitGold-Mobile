package com.habit.gold.feature.savings.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import com.habit.gold.core.designsystem.icons.HabitGoldIcons as Icons
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habit.gold.core.designsystem.HabitGoldPalette
import com.habit.gold.core.designsystem.icons.HabitGoldPhosphorIcons
import com.habit.gold.feature.home.presentation.ChildCardBorder
import com.habit.gold.feature.home.presentation.ChildMutedText
import com.habit.gold.feature.home.presentation.ChildPrimaryText
import com.habit.gold.feature.home.presentation.formatInr
import com.habit.gold.feature.trade.domain.TradeLivePriceState
import com.habit.gold.feature.trade.presentation.buy.BuyCouponSheet
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.common_help
import habitgoldmobile.composeapp.generated.resources.common_retry
import habitgoldmobile.composeapp.generated.resources.start_daily_savings_icon
import habitgoldmobile.composeapp.generated.resources.start_monthly_savings_icon
import habitgoldmobile.composeapp.generated.resources.start_weekly_savings_icon
import habitgoldmobile.composeapp.generated.resources.savings_setup_day_label
import habitgoldmobile.composeapp.generated.resources.savings_setup_failure_body
import habitgoldmobile.composeapp.generated.resources.savings_setup_failure_title
import habitgoldmobile.composeapp.generated.resources.savings_setup_go_home
import habitgoldmobile.composeapp.generated.resources.savings_hero_image_content_description
import habitgoldmobile.composeapp.generated.resources.savings_setup_monthly_date_label
import habitgoldmobile.composeapp.generated.resources.savings_setup_pending_body
import habitgoldmobile.composeapp.generated.resources.savings_setup_pending_title
import habitgoldmobile.composeapp.generated.resources.savings_setup_polling_body
import habitgoldmobile.composeapp.generated.resources.savings_setup_polling_title
import habitgoldmobile.composeapp.generated.resources.savings_setup_success_body
import habitgoldmobile.composeapp.generated.resources.savings_setup_success_title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlinx.coroutines.delay
import androidx.compose.ui.platform.LocalFocusManager
import com.habit.gold.feature.trade.presentation.buy.BuySlate400
import habitgoldmobile.composeapp.generated.resources.trade_buy_enter_amount

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
                            imageVector = HabitGoldPhosphorIcons.Regular.Question,
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
                        text = stringResource(Res.string.trade_buy_enter_amount),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BuySlate400,
                        letterSpacing = 2.sp,
                        modifier = Modifier
                            .wrapContentWidth()
                            .padding(start = 2.dp)
                            .offset(y=(4).dp),
                        textAlign = TextAlign.Start,
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
                        SavingsProjectionRow(
                            text = text,
                            animationKey = projectionIndex,
                        )
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

//                Spacer(modifier = Modifier.height(8.dp))

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

                Spacer(modifier = Modifier.height(8.dp))

                compoundingSummary?.let { summary ->
                    SavingsCompoundingPreviewCard(
                        amount = state.amountValue ?: 0,
                        frequency = state.frequency,
                        summary = summary,
                        onInfoClick = { showCompoundingInfo = true },
                    )
                }

                state.nextDebitLabelOrNull()?.let { nextPaymentText ->
                    SavingsNextPaymentCard(nextPaymentText = nextPaymentText)
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
            dragHandle = { BottomSheetDefaults.DragHandle(color = ChildCardBorder) },
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
            dragHandle = { BottomSheetDefaults.DragHandle(color = ChildCardBorder) },
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
            dragHandle = { BottomSheetDefaults.DragHandle(color = ChildCardBorder) },
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
            dragHandle = { BottomSheetDefaults.DragHandle(color = ChildCardBorder) },
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
        contentDescription = stringResource(
            Res.string.savings_hero_image_content_description,
            frequency.displayName(),
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        contentScale = ContentScale.Crop,
    )
}

@Composable
private fun SavingsProjectionRow(
    text: String,
    animationKey: Int,
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
        AnimatedContent(
            targetState = animationKey,
            transitionSpec = {
                if (targetState > initialState) {
                    (slideInVertically { height -> height } + fadeIn()) togetherWith
                        (slideOutVertically { height -> -height } + fadeOut())
                } else {
                    (slideInVertically { height -> -height } + fadeIn()) togetherWith
                        (slideOutVertically { height -> height } + fadeOut())
                }.using(SizeTransform(clip = false))
            },
            label = "SavingsProjectionAnimation",
        ) {
            Text(
                text = text,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = ChildMutedText,
                lineHeight = 18.sp,
            )
        }
    }
}

@Composable
private fun SavingsQuickAmountRow(
    quickAmounts: List<SavingsQuickAmount>,
    selectedAmountText: String,
    onQuickAmountSelected: (Int) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        quickAmounts.forEach { option ->
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.BottomCenter,
            ) {
                OutlinedButton(
                    onClick = { onQuickAmountSelected(option.amount) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = if (option.tag != null) 10.dp else 0.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(
                        if (selectedAmountText == option.amount.toString()) 1.5.dp else 1.dp,
                        if (selectedAmountText == option.amount.toString()) HabitGoldPalette.plum else HabitGoldPalette.plum.copy(alpha = 0.3f),
                    ),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                ) {
                    Text(
                        text = "₹${formatInr(option.amount.toDouble())}",
                        fontSize = 12.sp,
                        color = if (selectedAmountText == option.amount.toString()) HabitGoldPalette.plum else ChildPrimaryText,
                        fontWeight = if (selectedAmountText == option.amount.toString()) FontWeight.Bold else FontWeight.SemiBold,
                        maxLines = 1,
                    )
                }
                option.tag?.let { tag ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter),
                        shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 8.dp, bottomEnd = 8.dp),
                        color = HabitGoldPalette.plum,
//                        border = BorderStroke(1.dp, HabitGoldPalette.white.copy(alpha = 0.22f)),
                    ) {
                        Text(
                            text = tag,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 7.dp, vertical = 0.dp),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = HabitGoldPalette.white,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}
