package com.habit.gold.feature.savings.presentation

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.habit.gold.core.designsystem.icons.HabitGoldIcons as Icons
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habit.gold.core.designsystem.HabitGoldPalette
import com.habit.gold.feature.home.presentation.ChildCardBorder
import com.habit.gold.feature.home.presentation.ChildMutedText
import com.habit.gold.feature.home.presentation.ChildPrimaryText
import com.habit.gold.feature.home.presentation.HomeChildEmptyState
import com.habit.gold.feature.home.presentation.HomeChildScaffold
import com.habit.gold.feature.home.presentation.formatCreatedAt
import com.habit.gold.feature.home.presentation.formatInr
import com.habit.gold.feature.savings.domain.model.SavingsMandate
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.common_retry
import habitgoldmobile.composeapp.generated.resources.ic_buy_gold_icon
import habitgoldmobile.composeapp.generated.resources.savings_manage_action_cancelled
import habitgoldmobile.composeapp.generated.resources.savings_manage_action_paused
import habitgoldmobile.composeapp.generated.resources.savings_manage_action_resumed
import habitgoldmobile.composeapp.generated.resources.savings_manage_cancel
import habitgoldmobile.composeapp.generated.resources.savings_manage_cancel_confirmation
import habitgoldmobile.composeapp.generated.resources.savings_manage_cancel_note
import habitgoldmobile.composeapp.generated.resources.savings_manage_default_name
import habitgoldmobile.composeapp.generated.resources.savings_manage_dismiss_message
import habitgoldmobile.composeapp.generated.resources.savings_manage_empty
import habitgoldmobile.composeapp.generated.resources.savings_manage_failed_note
import habitgoldmobile.composeapp.generated.resources.savings_manage_filter_by_status
import habitgoldmobile.composeapp.generated.resources.savings_manage_frequency
import habitgoldmobile.composeapp.generated.resources.savings_manage_mandate_id
import habitgoldmobile.composeapp.generated.resources.savings_manage_no_cancelled
import habitgoldmobile.composeapp.generated.resources.savings_manage_no_failed
import habitgoldmobile.composeapp.generated.resources.savings_manage_no_paused
import habitgoldmobile.composeapp.generated.resources.savings_manage_no_success
import habitgoldmobile.composeapp.generated.resources.savings_manage_pause
import habitgoldmobile.composeapp.generated.resources.savings_manage_pause_confirmation
import habitgoldmobile.composeapp.generated.resources.savings_manage_pause_note
import habitgoldmobile.composeapp.generated.resources.savings_manage_remove_filter
import habitgoldmobile.composeapp.generated.resources.savings_manage_resume
import habitgoldmobile.composeapp.generated.resources.savings_manage_started_on
import habitgoldmobile.composeapp.generated.resources.savings_manage_started_time
import habitgoldmobile.composeapp.generated.resources.savings_manage_status
import habitgoldmobile.composeapp.generated.resources.savings_manage_status_cancelled
import habitgoldmobile.composeapp.generated.resources.savings_manage_status_failed
import habitgoldmobile.composeapp.generated.resources.savings_manage_status_paused
import habitgoldmobile.composeapp.generated.resources.savings_manage_status_success
import habitgoldmobile.composeapp.generated.resources.savings_manage_title
import habitgoldmobile.composeapp.generated.resources.savings_manage_upcoming_execution
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

private val SavingsPendingTint = Color(0xFF5B35C5)
private val SavingsPendingContainer = Color(0xFFF3EEFF)
private val SavingsPendingBorder = Color(0xFFDCCEFF)
private val SavingsSuccessTint = Color(0xFF15803D)
private val SavingsSuccessContainer = Color(0xFFF0FDF4)
private val SavingsSuccessBorder = Color(0xFFBBF7D0)
private val SavingsPausedTint = Color(0xFFB45309)
private val SavingsPausedContainer = Color(0xFFFEF3C7)
private val SavingsPausedBorder = Color(0xFFFCD34D)
private val SavingsCancelledTint = Color(0xFF475569)
private val SavingsCancelledContainer = Color(0xFFF8FAFC)
private val SavingsCancelledBorder = Color(0xFFE2E8F0)
private val SavingsFailedTint = Color(0xFFB91C1C)
private val SavingsFailedContainer = Color(0xFFFEE2E2)
private val SavingsFailedBorder = Color(0xFFFCA5A5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SavingsScreen(
    state: SavingsUiState,
    onBackClick: () -> Unit,
    onRefresh: () -> Unit,
    onFilterSelected: (SavingsStatusFilter) -> Unit,
    onClearFilter: () -> Unit,
    onToggleExpanded: (String) -> Unit,
    onPauseMandate: (String) -> Unit,
    onResumeMandate: (String) -> Unit,
    onCancelMandate: (String) -> Unit,
    onConsumeActionMessage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isFilterSheetVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPauseMandateId by rememberSaveable { mutableStateOf<String?>(null) }
    var confirmCancelMandateId by rememberSaveable { mutableStateOf<String?>(null) }
    val filterSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (isFilterSheetVisible) {
        SavingsFilterSheet(
            selectedFilter = state.selectedFilter,
            onDismiss = { isFilterSheetVisible = false },
            onSelect = {
                onFilterSelected(it)
                isFilterSheetVisible = false
            },
            onClearFilter = {
                onClearFilter()
                isFilterSheetVisible = false
            },
            sheetState = filterSheetState,
        )
    }

    if (confirmPauseMandateId != null) {
        val mandate = state.mandates.firstOrNull { it.id == confirmPauseMandateId }
        if (mandate != null) {
            SavingsConfirmationDialog(
                title = stringResource(Res.string.savings_manage_pause),
                message = stringResource(Res.string.savings_manage_pause_confirmation, mandate.name, mandate.amount),
                note = stringResource(Res.string.savings_manage_pause_note),
                confirmLabel = stringResource(Res.string.savings_manage_pause),
                confirmContainerColor = SavingsPausedTint,
                onDismiss = { confirmPauseMandateId = null },
                onConfirm = {
                    confirmPauseMandateId = null
                    onPauseMandate(mandate.id)
                },
            )
        }
    }

    if (confirmCancelMandateId != null) {
        val mandate = state.mandates.firstOrNull { it.id == confirmCancelMandateId }
        if (mandate != null) {
            SavingsConfirmationDialog(
                title = stringResource(Res.string.savings_manage_cancel),
                message = stringResource(Res.string.savings_manage_cancel_confirmation, mandate.name, mandate.amount),
                note = stringResource(Res.string.savings_manage_cancel_note),
                confirmLabel = stringResource(Res.string.savings_manage_cancel),
                confirmContainerColor = SavingsFailedTint,
                onDismiss = { confirmCancelMandateId = null },
                onConfirm = {
                    confirmCancelMandateId = null
                    onCancelMandate(mandate.id)
                },
            )
        }
    }

    HomeChildScaffold(
        title = stringResource(Res.string.savings_manage_title),
        onBackClick = onBackClick,
        backgroundColor = Color.White,
        actions = {
            Surface(
                modifier = Modifier.padding(end = 8.dp),
                onClick = { isFilterSheetVisible = true },
                shape = RoundedCornerShape(14.dp),
                color = if (state.selectedFilter == null) Color.White else HabitGoldPalette.plum.copy(alpha = 0.08f),
                border = BorderStroke(
                    1.dp,
                    if (state.selectedFilter == null) ChildCardBorder else HabitGoldPalette.plum.copy(alpha = 0.18f),
                ),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = if (state.selectedFilter == null) ChildPrimaryText else HabitGoldPalette.plum,
                        modifier = Modifier.size(14.dp),
                    )
                    Text(
                        text = state.selectedFilter?.label() ?: stringResource(Res.string.savings_manage_status),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (state.selectedFilter == null) ChildPrimaryText else HabitGoldPalette.plum,
                    )
                }
            }
        },
    ) { paddingValues ->
        when {
            state.isLoading && state.mandates.isEmpty() -> {
                SavingsManageLoadingShimmer(paddingValues = paddingValues)
            }

            state.errorMessage != null && state.mandates.isEmpty() -> {
                SavingsErrorState(
                    paddingValues = paddingValues,
                    message = state.errorMessage,
                    onRetry = onRefresh,
                )
            }

            state.mandates.isEmpty() -> {
                HomeChildEmptyState(
                    paddingValues = paddingValues,
                    message = stringResource(Res.string.savings_manage_empty),
                )
            }

            else -> {
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(top = 0.dp, bottom = 24.dp),
                ) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            state.actionMessage?.let { actionMessage ->
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    color = SavingsPendingContainer,
                                    border = BorderStroke(1.dp, SavingsPendingBorder),
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 14.dp, vertical = 12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Text(
                                            text = actionMessage.label(),
                                            modifier = Modifier.weight(1f),
                                            fontSize = 13.sp,
                                            color = SavingsPendingTint,
                                            lineHeight = 18.sp,
                                        )
                                        TextButton(onClick = onConsumeActionMessage) {
                                            Text(
                                                text = stringResource(Res.string.savings_manage_dismiss_message),
                                                color = SavingsPendingTint,
                                                fontWeight = FontWeight.SemiBold,
                                            )
                                        }
                                    }
                                }
                            }

                            state.actionErrorMessage?.let { actionErrorMessage ->
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    color = SavingsFailedContainer,
                                    border = BorderStroke(1.dp, SavingsFailedBorder),
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 14.dp, vertical = 12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Text(
                                            text = actionErrorMessage,
                                            modifier = Modifier.weight(1f),
                                            fontSize = 13.sp,
                                            color = SavingsFailedTint,
                                            lineHeight = 18.sp,
                                        )
                                        TextButton(onClick = onConsumeActionMessage) {
                                            Text(
                                                text = stringResource(Res.string.savings_manage_dismiss_message),
                                                color = SavingsFailedTint,
                                                fontWeight = FontWeight.SemiBold,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (state.visibleMandates.isEmpty()) {
                        item {
                            HomeChildEmptyState(
                                paddingValues = PaddingValues(0.dp),
                                message = state.selectedFilter.emptyMessage(),
                            )
                        }
                    } else {
                        items(state.visibleMandates, key = { it.id }) { mandate ->
                            SavingsMandateCard(
                                mandate = mandate,
                                expanded = mandate.id in state.expandedMandateIds,
                                isActionLoading = state.actionInFlightMandateId == mandate.id,
                                onToggleExpanded = { onToggleExpanded(mandate.id) },
                                onPauseMandate = { confirmPauseMandateId = mandate.id },
                                onResumeMandate = { onResumeMandate(mandate.id) },
                                onCancelMandate = { confirmCancelMandateId = mandate.id },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SavingsManageLoadingShimmer(
    paddingValues: PaddingValues,
) {
    val transition = rememberInfiniteTransition(label = "savings-manage-shimmer")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(durationMillis = 1300, easing = LinearEasing)),
        label = "savings-manage-shimmer-progress",
    )
    val shimmerBrush = Brush.linearGradient(
        colors = listOf(Color(0xFFE8ECF3), Color(0xFFF6F8FB), Color(0xFFE8ECF3)),
        start = androidx.compose.ui.geometry.Offset(-260f + (520f * progress), 0f),
        end = androidx.compose.ui.geometry.Offset(0f + (520f * progress), 220f),
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        items(3) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, ChildCardBorder),
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.42f)
                            .height(18.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(shimmerBrush),
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.68f)
                            .height(28.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(shimmerBrush),
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(shimmerBrush),
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.56f)
                            .height(14.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(shimmerBrush),
                    )
                }
            }
        }
    }
}

@Composable
private fun SavingsErrorState(
    paddingValues: PaddingValues,
    message: String,
    onRetry: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, ChildCardBorder),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = message,
                    color = ChildMutedText,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                )
                Button(
                    onClick = onRetry,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HabitGoldPalette.plum),
                ) {
                    Text(text = stringResource(Res.string.common_retry), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun SavingsMandateCard(
    mandate: SavingsMandate,
    expanded: Boolean,
    isActionLoading: Boolean,
    onToggleExpanded: () -> Unit,
    onPauseMandate: () -> Unit,
    onResumeMandate: () -> Unit,
    onCancelMandate: () -> Unit,
) {
    val style = mandate.statusBucket().style()
    val amountLabel = "₹${mandate.amount.toDoubleOrNull()?.let(::formatInr) ?: mandate.amount}"
    val primaryActionLabel = when (mandate.statusBucket()) {
        SavingsStatusBucket.Success -> stringResource(Res.string.savings_manage_pause)
        SavingsStatusBucket.Paused -> stringResource(Res.string.savings_manage_resume)
        else -> null
    }
    val helperNote = when (mandate.statusBucket()) {
        SavingsStatusBucket.Paused -> stringResource(Res.string.savings_manage_pause_note)
        SavingsStatusBucket.Cancelled -> stringResource(Res.string.savings_manage_cancel_note)
        SavingsStatusBucket.Failed -> stringResource(Res.string.savings_manage_failed_note)
        else -> null
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ChildCardBorder),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onToggleExpanded,
                )
                .padding(18.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                if (style.showIconBackground) style.iconContainerColor else Color.Transparent,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        when {
                            style.iconRes != null -> Icon(
                                painter = painterResource(style.iconRes),
                                contentDescription = null,
                                tint = style.iconTint,
                                modifier = Modifier.size(style.iconSize),
                            )
                            style.icon != null -> Icon(
                                imageVector = style.icon,
                                contentDescription = null,
                                tint = style.iconTint,
                                modifier = Modifier.size(style.iconSize),
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = mandate.name.ifBlank { stringResource(Res.string.savings_manage_default_name) },
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = ChildPrimaryText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        SavingsStatusChip(style = style)
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = amountLabel,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = HabitGoldPalette.plum,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = ChildMutedText,
                    )
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(18.dp))
                HorizontalDivider(color = ChildCardBorder.copy(alpha = 0.8f))
                Spacer(modifier = Modifier.height(18.dp))

                val startedAt = remember(mandate.startDate) { mandate.startDate.takeIf { it.isNotBlank() }?.let(::formatSavingsDateTimeParts) }

                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        SavingsDetailCell(
                            modifier = Modifier.weight(1f),
                            label = stringResource(Res.string.savings_manage_frequency),
                            value = mandate.frequency.replaceFirstChar { it.uppercase() },
                        )
                        startedAt?.let { (dateText, _) ->
                            SavingsDetailCell(
                                modifier = Modifier.weight(1f),
                                label = stringResource(Res.string.savings_manage_started_on),
                                value = dateText,
                            )
                        } ?: Spacer(modifier = Modifier.weight(1f))
                    }

                    startedAt?.let { (_, timeText) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            SavingsDetailCell(
                                modifier = Modifier.weight(1f),
                                label = stringResource(Res.string.savings_manage_started_time),
                                value = timeText,
                            )
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    } ?: mandate.nextExecutionDate?.takeIf { it.isNotBlank() }?.let {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            SavingsDetailCell(
                                modifier = Modifier.weight(1f),
                                label = stringResource(Res.string.savings_manage_upcoming_execution),
                                value = formatCreatedAt(it),
                            )
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }

                    mandate.juspayMandateId?.takeIf { it.isNotBlank() }?.let {
                        SavingsDetailCell(
                            modifier = Modifier.fillMaxWidth(),
                            label = stringResource(Res.string.savings_manage_mandate_id),
                            value = it,
                            valueMaxLines = 1,
                        )
                    }
                }

                helperNote?.let {
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = style.containerColor,
                        border = BorderStroke(1.dp, style.borderColor),
                    ) {
                        Text(
                            text = it,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            fontSize = 12.sp,
                            color = style.textColor,
                            lineHeight = 17.sp,
                        )
                    }
                }

                if (primaryActionLabel != null || mandate.statusBucket() in setOf(SavingsStatusBucket.Success, SavingsStatusBucket.Paused)) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        primaryActionLabel?.let { label ->
                            if (mandate.statusBucket() == SavingsStatusBucket.Paused) {
                                Button(
                                    onClick = onResumeMandate,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(44.dp),
                                    enabled = !isActionLoading,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = HabitGoldPalette.plum,
                                        contentColor = Color.White,
                                    ),
                                ) {
                                    Text(text = label, fontWeight = FontWeight.SemiBold)
                                }
                            } else {
                                OutlinedButton(
                                    onClick = onPauseMandate,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(44.dp),
                                    enabled = !isActionLoading,
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, style.primaryActionBorderColor),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = style.primaryActionContentColor),
                                ) {
                                    Text(text = label, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }

                        if (mandate.statusBucket() == SavingsStatusBucket.Success || mandate.statusBucket() == SavingsStatusBucket.Paused) {
                            TextButton(
                                onClick = onCancelMandate,
                                enabled = !isActionLoading,
                            ) {
                                Text(
                                    text = stringResource(Res.string.savings_manage_cancel),
                                    fontSize = 13.sp,
                                    color = ChildMutedText,
                                    fontWeight = FontWeight.Medium,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SavingsDetailCell(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueMaxLines: Int = 2,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = ChildMutedText,
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = ChildPrimaryText,
            maxLines = valueMaxLines,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun SavingsStatusChip(style: SavingsStatusStyle) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = style.containerColor,
        border = BorderStroke(1.dp, style.borderColor),
    ) {
        Text(
            text = style.label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            fontSize = 10.sp,
            color = style.textColor,
            fontWeight = FontWeight.Medium,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SavingsFilterSheet(
    selectedFilter: SavingsStatusFilter?,
    onDismiss: () -> Unit,
    onSelect: (SavingsStatusFilter) -> Unit,
    onClearFilter: () -> Unit,
    sheetState: androidx.compose.material3.SheetState,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.savings_manage_filter_by_status),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ChildPrimaryText,
                )
                if (selectedFilter != null) {
                    TextButton(onClick = onClearFilter) {
                        Text(
                            text = stringResource(Res.string.savings_manage_remove_filter),
                            color = HabitGoldPalette.plum,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            SavingsStatusFilter.entries.forEachIndexed { index, filter ->
                Surface(
                    onClick = { onSelect(filter) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White,
                    border = BorderStroke(
                        1.dp,
                        if (filter == selectedFilter) HabitGoldPalette.plum.copy(alpha = 0.18f) else ChildCardBorder,
                    ),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        RadioButton(
                            selected = filter == selectedFilter,
                            onClick = null,
                            colors = RadioButtonDefaults.colors(
                                selectedColor = HabitGoldPalette.plum,
                                unselectedColor = ChildMutedText,
                            ),
                        )
                        Text(
                            text = filter.label(),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ChildPrimaryText,
                        )
                    }
                }
                if (index != SavingsStatusFilter.entries.lastIndex) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SavingsConfirmationDialog(
    title: String,
    message: String,
    note: String,
    confirmLabel: String,
    confirmContainerColor: Color,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = ChildPrimaryText,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = message,
                    fontSize = 14.sp,
                    color = ChildMutedText,
                    lineHeight = 20.sp,
                )
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SavingsPausedContainer,
                    border = BorderStroke(1.dp, SavingsPausedBorder),
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = SavingsPausedTint,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = note,
                            fontSize = 12.sp,
                            color = ChildPrimaryText,
                            lineHeight = 16.sp,
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = confirmContainerColor,
                    contentColor = Color.White,
                ),
            ) {
                Text(text = stringResource(Res.string.savings_manage_dismiss_message))
            }
        },
        dismissButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = confirmLabel,
                    color = ChildMutedText,
                )
            }
        },
    )
}

private data class SavingsStatusStyle(
    val label: String,
    val textColor: Color,
    val containerColor: Color,
    val borderColor: Color,
    val icon: ImageVector? = null,
    val iconRes: DrawableResource? = null,
    val iconTint: Color,
    val iconContainerColor: Color,
    val iconSize: Dp = 22.dp,
    val showIconBackground: Boolean = true,
    val primaryActionContentColor: Color,
    val primaryActionBorderColor: Color,
)

@Composable
private fun SavingsStatusBucket.style(): SavingsStatusStyle {
    return when (this) {
        SavingsStatusBucket.Success -> SavingsStatusStyle(
            label = stringResource(Res.string.savings_manage_status_success),
            textColor = SavingsSuccessTint,
            containerColor = SavingsSuccessContainer,
            borderColor = SavingsSuccessBorder,
            iconRes = Res.drawable.ic_buy_gold_icon,
            iconTint = SavingsSuccessTint,
            iconContainerColor = SavingsSuccessContainer,
            iconSize = 40.dp,
            showIconBackground = false,
            primaryActionContentColor = SavingsSuccessTint,
            primaryActionBorderColor = SavingsSuccessBorder,
        )
        SavingsStatusBucket.Paused -> SavingsStatusStyle(
            label = stringResource(Res.string.savings_manage_status_paused),
            textColor = SavingsPausedTint,
            containerColor = SavingsPausedContainer,
            borderColor = SavingsPausedBorder,
            icon = Icons.Default.Autorenew,
            iconTint = SavingsPausedTint,
            iconContainerColor = SavingsPausedContainer,
            primaryActionContentColor = Color.White,
            primaryActionBorderColor = HabitGoldPalette.plum,
        )
        SavingsStatusBucket.Cancelled -> SavingsStatusStyle(
            label = stringResource(Res.string.savings_manage_status_cancelled),
            textColor = SavingsCancelledTint,
            containerColor = SavingsCancelledContainer,
            borderColor = SavingsCancelledBorder,
            icon = Icons.Default.Cancel,
            iconTint = SavingsCancelledTint,
            iconContainerColor = SavingsCancelledContainer,
            primaryActionContentColor = SavingsCancelledTint,
            primaryActionBorderColor = SavingsCancelledBorder,
        )
        SavingsStatusBucket.Failed -> SavingsStatusStyle(
            label = stringResource(Res.string.savings_manage_status_failed),
            textColor = SavingsFailedTint,
            containerColor = SavingsFailedContainer,
            borderColor = SavingsFailedBorder,
            icon = Icons.Default.Warning,
            iconTint = SavingsFailedTint,
            iconContainerColor = SavingsFailedContainer,
            primaryActionContentColor = SavingsFailedTint,
            primaryActionBorderColor = SavingsFailedBorder,
        )
        SavingsStatusBucket.Pending,
        SavingsStatusBucket.Unknown -> SavingsStatusStyle(
            label = stringResource(Res.string.savings_manage_status),
            textColor = SavingsPendingTint,
            containerColor = SavingsPendingContainer,
            borderColor = SavingsPendingBorder,
            icon = Icons.Default.Autorenew,
            iconTint = SavingsPendingTint,
            iconContainerColor = SavingsPendingContainer,
            primaryActionContentColor = SavingsPendingTint,
            primaryActionBorderColor = SavingsPendingBorder,
        )
    }
}

private fun formatSavingsDateTimeParts(raw: String): Pair<String, String>? {
    return runCatching {
        val local = Instant.parse(raw).toLocalDateTime(TimeZone.UTC)
        val day = local.day.toString().padStart(2, '0')
        val month = monthAbbreviation(local.month.name)
        val hour24 = local.hour
        val minute = local.minute.toString().padStart(2, '0')
        val meridiem = if (hour24 >= 12) "PM" else "AM"
        val hour12 = ((hour24 + 11) % 12 + 1).toString()
        "$day $month" to "$hour12:$minute $meridiem"
    }.getOrNull()
}

private fun monthAbbreviation(monthName: String): String {
    return monthName.lowercase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        .take(3)
}

@Composable
private fun SavingsStatusFilter.label(): String {
    return when (this) {
        SavingsStatusFilter.Success -> stringResource(Res.string.savings_manage_status_success)
        SavingsStatusFilter.Paused -> stringResource(Res.string.savings_manage_status_paused)
        SavingsStatusFilter.Cancelled -> stringResource(Res.string.savings_manage_status_cancelled)
        SavingsStatusFilter.Failed -> stringResource(Res.string.savings_manage_status_failed)
    }
}

@Composable
private fun SavingsStatusFilter?.emptyMessage(): String {
    return when (this) {
        null -> stringResource(Res.string.savings_manage_empty)
        SavingsStatusFilter.Success -> stringResource(Res.string.savings_manage_no_success)
        SavingsStatusFilter.Paused -> stringResource(Res.string.savings_manage_no_paused)
        SavingsStatusFilter.Cancelled -> stringResource(Res.string.savings_manage_no_cancelled)
        SavingsStatusFilter.Failed -> stringResource(Res.string.savings_manage_no_failed)
    }
}

@Composable
private fun SavingsActionMessage.label(): String {
    return when (this) {
        SavingsActionMessage.Paused -> stringResource(Res.string.savings_manage_action_paused)
        SavingsActionMessage.Resumed -> stringResource(Res.string.savings_manage_action_resumed)
        SavingsActionMessage.Cancelled -> stringResource(Res.string.savings_manage_action_cancelled)
    }
}
