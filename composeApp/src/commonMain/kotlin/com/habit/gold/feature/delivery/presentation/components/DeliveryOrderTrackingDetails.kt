package com.habit.gold.feature.delivery.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.habit.gold.core.designsystem.icons.HabitGoldIcons as Icons
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habit.gold.core.designsystem.theme.AppColors
import com.habit.gold.feature.delivery.domain.model.DeliveryOrderDto
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.delivery_tracking_address_unavailable
import habitgoldmobile.composeapp.generated.resources.delivery_tracking_awaiting_update
import habitgoldmobile.composeapp.generated.resources.delivery_tracking_details
import habitgoldmobile.composeapp.generated.resources.delivery_tracking_link_available
import habitgoldmobile.composeapp.generated.resources.delivery_tracking_milestone_delivered
import habitgoldmobile.composeapp.generated.resources.delivery_tracking_milestone_delivered_complete
import habitgoldmobile.composeapp.generated.resources.delivery_tracking_milestone_delivered_pending
import habitgoldmobile.composeapp.generated.resources.delivery_tracking_milestone_in_transit
import habitgoldmobile.composeapp.generated.resources.delivery_tracking_milestone_order_confirmed
import habitgoldmobile.composeapp.generated.resources.delivery_tracking_milestone_packed
import habitgoldmobile.composeapp.generated.resources.delivery_tracking_milestone_packed_complete
import habitgoldmobile.composeapp.generated.resources.delivery_tracking_milestone_packed_pending
import habitgoldmobile.composeapp.generated.resources.delivery_tracking_milestone_transit_complete
import habitgoldmobile.composeapp.generated.resources.delivery_tracking_milestone_transit_pending
import habitgoldmobile.composeapp.generated.resources.delivery_tracking_payment
import habitgoldmobile.composeapp.generated.resources.delivery_tracking_status_pending
import habitgoldmobile.composeapp.generated.resources.delivery_tracking_updated
import habitgoldmobile.composeapp.generated.resources.order_summary_delivery_to
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun DeliveryTrackingDetails(order: DeliveryOrderDto) {
    val awaitingUpdate = stringResource(Res.string.delivery_tracking_awaiting_update)
    val milestones = buildTrackingMilestones(order, awaitingUpdate)
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
                text = stringResource(Res.string.delivery_tracking_details),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.Slate950,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                TrackingDetailItem(
                    label = stringResource(Res.string.delivery_tracking_payment),
                    value = order.paymentStatus ?: stringResource(Res.string.delivery_tracking_status_pending),
                )
                TrackingDetailItem(
                    label = stringResource(Res.string.delivery_tracking_updated),
                    value = formatTrackingTimestamp(order.updatedAt, awaitingUpdate),
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(Res.string.order_summary_delivery_to),
                    fontSize = 12.sp,
                    color = AppColors.Slate500,
                )
                Text(
                    text = buildAddressText(
                        order = order,
                        fallback = stringResource(Res.string.delivery_tracking_address_unavailable),
                    ),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = AppColors.Slate800,
                )
            }

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

@Composable
private fun TrackingDetailItem(label: String, value: String) {
    Column {
        Text(text = label, fontSize = 12.sp, color = AppColors.Slate500)
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = AppColors.Slate800)
    }
}

private data class TrackingMilestone(
    val title: String,
    val subtitle: String,
    val isComplete: Boolean,
)

@Composable
private fun buildTrackingMilestones(
    order: DeliveryOrderDto,
    awaitingUpdateLabel: String,
): List<TrackingMilestone> {
    val status = (order.metadata?.latestDispatchStatus?.message ?: order.status)
        .orEmpty().uppercase()
    val dispatchComplete = status in setOf("DISPATCHED", "IN TRANSIT", "IN_TRANSIT", "DELIVERED")
    val transitComplete = status in setOf("IN TRANSIT", "IN_TRANSIT", "DELIVERED")
    val deliveredComplete = status == "DELIVERED"

    return listOf(
        TrackingMilestone(
            title = stringResource(Res.string.delivery_tracking_milestone_order_confirmed),
            subtitle = formatTrackingTimestamp(order.confirmedAt ?: order.createdAt, awaitingUpdateLabel),
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
            title = stringResource(Res.string.delivery_tracking_milestone_in_transit),
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

private fun buildAddressText(order: DeliveryOrderDto, fallback: String): String {
    return listOfNotNull(
        order.recipientName,
        listOfNotNull(order.addressLine1, order.addressLine2).joinToString(", ").takeIf { it.isNotBlank() },
        listOfNotNull(order.city, order.state, order.pincode).joinToString(", ").takeIf { it.isNotBlank() },
        order.phoneNumber,
    ).joinToString("\n").ifBlank { fallback }
}

private fun formatTrackingTimestamp(raw: String?, fallback: String): String {
    val value = raw?.trim()?.takeIf { it.isNotEmpty() } ?: return fallback
    return try {
        val instant = Instant.parse(value)
        val local = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        val month = local.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
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
