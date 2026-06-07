package com.habit.gold.feature.delivery.presentation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.habit.gold.core.designsystem.icons.HabitGoldIcons as Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habit.gold.core.presentation.PlatformBackHandler
import com.habit.gold.core.designsystem.theme.*
import com.habit.gold.feature.delivery.domain.model.DeliveryOrderDto
import com.habit.gold.feature.delivery.presentation.DeliveryTrackingIntent
import com.habit.gold.feature.delivery.presentation.DeliveryTrackingState
import com.habit.gold.feature.delivery.presentation.resolve
import com.habit.gold.feature.delivery.presentation.components.*
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Instant

/**
 * Delivery Tracking screen — lists all past and active delivery orders.
 *
 * Mirrors DeliveryTrackingScreen from legacy Android:
 *  - CenterAligned top bar
 *  - Shimmer skeleton loading state
 *  - Expandable order cards with icon, courier/tracking always visible
 *  - Expand arrow (bordered box), animated visibility for tracking details
 *  - Tracking details panel: Payment, Updated, Delivering To address, timeline
 *  - Tracking link chip (opens external URL)
 *  - Empty/error states
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryTrackingScreen(
    state: DeliveryTrackingState,
    onIntent: (DeliveryTrackingIntent) -> Unit,
    onBackClick: () -> Unit,
) {
    var expandedOrderId by rememberSaveable { mutableStateOf<String?>(null) }

    PlatformBackHandler(
        enabled = true,
        onBack = onBackClick,
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.delivery_tracking_title),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.Purple700,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.common_back),
                            tint = AppColors.Purple700,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.White,
                ),
            )
        },
        containerColor = AppColors.SurfaceLight,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when (state) {
                is DeliveryTrackingState.Loading -> {
                    TrackingShimmerContent(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                    )
                }

                is DeliveryTrackingState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = state.message.resolve(),
                            color = AppColors.Danger,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = { onIntent(DeliveryTrackingIntent.FetchOrders) },
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.Purple700),
                        ) {
                            Text(stringResource(Res.string.delivery_tracking_retry))
                        }
                    }
                }

                is DeliveryTrackingState.Success -> {
                    if (state.orders.isEmpty()) {
                        Text(
                            text = stringResource(Res.string.delivery_tracking_empty),
                            color = AppColors.Slate500,
                            fontSize = 16.sp,
                            modifier = Modifier.align(Alignment.Center),
                            textAlign = TextAlign.Center,
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            items(state.orders, key = { it.id ?: it.hashCode().toString() }) { order ->
                                DeliveryOrderCard(
                                    order = order,
                                    expanded = expandedOrderId == order.id,
                                    onClick = {
                                        expandedOrderId = if (expandedOrderId == order.id) null else order.id
                                    },
                                )
                            }
                            item { Spacer(Modifier.height(8.dp)) }
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Shimmer skeleton (matches Android loading layout)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun TrackingShimmerContent(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(3) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = AppColors.White,
                border = TrackingBorderStroke(),
                shadowElevation = 0.dp,
                tonalElevation = 0.dp,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(AppColors.Slate100),
                        )
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.6f)
                                    .height(18.dp)
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(AppColors.Slate100),
                            )
                            Spacer(Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.42f)
                                    .height(12.dp)
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(AppColors.Slate100),
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Box(
                            modifier = Modifier
                                .width(72.dp)
                                .height(24.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(AppColors.Slate100),
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        color = AppColors.Slate100,
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        repeat(2) {
                            Column(modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.5f)
                                        .height(11.dp)
                                        .clip(RoundedCornerShape(999.dp))
                                        .background(AppColors.Slate100),
                                )
                                Spacer(Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.8f)
                                        .height(14.dp)
                                        .clip(RoundedCornerShape(999.dp))
                                        .background(AppColors.Slate100),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Order card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun DeliveryOrderCard(
    order: DeliveryOrderDto,
    expanded: Boolean,
    onClick: () -> Unit,
) {
    val statusText = order.metadata?.latestDispatchStatus?.message ?: order.status ?: "PENDING"
    val statusUpper = statusText.uppercase()
    val statusColor = if (
        statusUpper == "DISPATCHED" || statusUpper == "DELIVERED" || statusUpper == "IN TRANSIT" || statusUpper == "IN_TRANSIT"
    ) AppColors.Green700 else AppColors.Purple700

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        color = AppColors.White,
        border = TrackingBorderStroke(),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            // ── Header row ────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_delivery_gold_icon),
                        contentDescription = stringResource(Res.string.delivery_tracking_delivery_icon),
                        tint = Color.Unspecified,
                        modifier = Modifier.size(48.dp),
                    )
                }

                Spacer(Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = order.productDescription ?: "Unknown Product",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.Slate900,
                    )
                    Text(
                        text = "Order ID: ${order.id?.take(8) ?: "N/A"}",
                        fontSize = 12.sp,
                        color = AppColors.Slate500,
                    )
                }

                // Status chip + expand arrow
                Column(horizontalAlignment = Alignment.End) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = statusColor.copy(alpha = 0.10f),
                    ) {
                        Text(
                            text = statusUpper,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusColor,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    // Bordered expand/collapse button
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .border(
                                width = 1.dp,
                                color = AppColors.Slate200,
                                shape = RoundedCornerShape(8.dp),
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = if (expanded) "Hide details" else "Show details",
                            tint = AppColors.Slate500,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = AppColors.Slate100,
            )

            // ── Always-visible courier + tracking row ─────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(
                        text = "Courier",
                        fontSize = 12.sp,
                        color = AppColors.Slate500,
                    )
                    Text(
                        text = order.courierCompany ?: "Pending",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.Slate900,
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Tracking ID",
                        fontSize = 12.sp,
                        color = AppColors.Slate500,
                    )
                    Text(
                        text = order.courierTrackingId ?: "Pending",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.Slate900,
                    )
                }
            }

            // ── Estimated dispatch days ───────────────────────────────────
            order.estimatedDispatchDays?.let { days ->
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Estimated dispatch: $days days",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = AppColors.Slate500,
                )
            }

            // ── Expandable tracking details ───────────────────────────────
            AnimatedVisibility(visible = expanded) {
                DeliveryTrackingDetails(order = order)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Tracking details panel (expanded)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun DeliveryTrackingDetails(order: DeliveryOrderDto) {
    val milestones = buildTrackingMilestones(order)
    val trackingLink = order.metadata?.latestDispatchStatus?.trackingLink
    val uriHandler = LocalUriHandler.current

    Spacer(Modifier.height(16.dp))
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = AppColors.Slate50,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = "Tracking Details",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.Slate950,
            )

            // Payment + Updated row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                TrackingDetailItem(
                    label = "Payment",
                    value = order.paymentStatus ?: "Pending",
                )
                TrackingDetailItem(
                    label = stringResource(Res.string.delivery_tracking_updated),
                    value = formatTrackingTimestamp(order.updatedAt),
                )
            }

            // Delivering to address
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(Res.string.order_summary_delivery_to),
                    fontSize = 12.sp,
                    color = AppColors.Slate500,
                )
                Text(
                    text = buildAddressText(order),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = AppColors.Slate800,
                )
            }

            // Timeline
            Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                milestones.forEachIndexed { index, milestone ->
                    TrackingTimelineItem(
                        title = milestone.title,
                        subtitle = milestone.subtitle,
                        isComplete = milestone.isComplete,
                        isLast = index == milestones.lastIndex,
                    )
                }
            }

            // Tracking link chip
            if (!trackingLink.isNullOrBlank()) {
                AssistChip(
                    onClick = { uriHandler.openUri(trackingLink.toExternalTrackingUrl()) },
                    label = { Text(stringResource(Res.string.delivery_tracking_link_available)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = AppColors.White,
                        labelColor = AppColors.Purple700,
                        leadingIconContentColor = AppColors.Purple700,
                    ),
                    border = AssistChipDefaults.assistChipBorder(
                        enabled = true,
                        borderColor = AppColors.Purple700.copy(alpha = 0.3f),
                    ),
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Timeline item with connecting vertical line
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun TrackingTimelineItem(
    title: String,
    subtitle: String,
    isComplete: Boolean,
    isLast: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(if (isComplete) AppColors.Purple700 else AppColors.Slate300),
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(40.dp)
                        .background(
                            if (isComplete) AppColors.Purple700.copy(alpha = 0.35f) else AppColors.Slate200,
                        ),
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.padding(bottom = if (isLast) 0.dp else 4.dp)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.Slate950,
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = AppColors.Slate500,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Detail item (label + value column)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun TrackingDetailItem(label: String, value: String) {
    Column {
        Text(text = label, fontSize = 12.sp, color = AppColors.Slate500)
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = AppColors.Slate800)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Helpers
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun TrackingBorderStroke() =
    androidx.compose.foundation.BorderStroke(1.dp, AppColors.Slate200)

private data class TrackingMilestone(
    val title: String,
    val subtitle: String,
    val isComplete: Boolean,
)

@Composable
private fun buildTrackingMilestones(order: DeliveryOrderDto): List<TrackingMilestone> {
    val status = (order.metadata?.latestDispatchStatus?.message ?: order.status)
        .orEmpty().uppercase()
    val dispatchComplete = status in setOf("DISPATCHED", "IN TRANSIT", "IN_TRANSIT", "DELIVERED")
    val transitComplete = status in setOf("IN TRANSIT", "IN_TRANSIT", "DELIVERED")
    val deliveredComplete = status == "DELIVERED"

    return listOf(
        TrackingMilestone(
            title = stringResource(Res.string.delivery_tracking_milestone_order_confirmed),
            subtitle = formatTrackingTimestamp(order.confirmedAt ?: order.createdAt),
            isComplete = true,
        ),
        TrackingMilestone(
            title = stringResource(Res.string.delivery_tracking_milestone_packed),
            subtitle = if (dispatchComplete) {
                stringResource(Res.string.delivery_tracking_milestone_packed_complete)
            } else {
                stringResource(Res.string.delivery_tracking_milestone_packed_pending)
            },
            isComplete = dispatchComplete,
        ),
        TrackingMilestone(
            title = stringResource(Res.string.delivery_tracking_milestone_transit),
            subtitle = if (transitComplete) {
                stringResource(Res.string.delivery_tracking_milestone_transit_complete)
            } else {
                stringResource(Res.string.delivery_tracking_milestone_transit_pending)
            },
            isComplete = transitComplete,
        ),
        TrackingMilestone(
            title = stringResource(Res.string.delivery_tracking_milestone_delivered),
            subtitle = if (deliveredComplete) {
                stringResource(Res.string.delivery_tracking_milestone_delivered_complete)
            } else {
                stringResource(Res.string.delivery_tracking_milestone_delivered_pending)
            },
            isComplete = deliveredComplete,
        ),
    )
}

@Composable
private fun buildAddressText(order: DeliveryOrderDto): String {
    return listOfNotNull(
        order.recipientName,
        listOfNotNull(order.addressLine1, order.addressLine2)
            .joinToString(", ").takeIf { it.isNotBlank() },
        listOfNotNull(order.city, order.state, order.pincode)
            .joinToString(", ").takeIf { it.isNotBlank() },
        order.phoneNumber,
    ).joinToString("\n").ifBlank { stringResource(Res.string.delivery_tracking_address_unavailable) }
}

@Composable
private fun formatTrackingTimestamp(raw: String?): String {
    val value = raw?.trim()?.takeIf { it.isNotEmpty() }
        ?: return stringResource(Res.string.delivery_tracking_awaiting_update)
    return try {
        val instant = Instant.parse(value)
        val local = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        val month = local.month.name.take(3)
            .lowercase()
            .replaceFirstChar { it.uppercase() }
        val hour = local.hour % 12
        val displayHour = if (hour == 0) 12 else hour
        val minute = local.minute.toString().padStart(2, '0')
        val ampm = if (local.hour < 12) "AM" else "PM"
        "${local.day} $month ${local.year}, $displayHour:$minute $ampm"
    } catch (_: Exception) {
        value
    }
}

private fun String.toExternalTrackingUrl(): String {
    return if (startsWith("http://", ignoreCase = true) || startsWith("https://", ignoreCase = true)) {
        this
    } else {
        "https://$this"
    }
}
