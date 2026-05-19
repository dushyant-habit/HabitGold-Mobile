package com.habit.gold.feature.delivery.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habit.gold.core.designsystem.theme.AppColors
import com.habit.gold.feature.delivery.domain.model.DeliveryOrderDto
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun DeliveryOrderCard(
    order: DeliveryOrderDto,
    expanded: Boolean,
    onClick: () -> Unit,
) {
    val statusText = order.metadata?.latestDispatchStatus?.message
        ?: order.status
        ?: stringResource(Res.string.delivery_tracking_status_pending)
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
        border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Slate200),
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
                // Icon circle
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(AppColors.Gold100),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalShipping,
                        contentDescription = stringResource(Res.string.history_screen_type_delivery),
                        tint = AppColors.Purple700,
                    )
                }

                Spacer(Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = order.productDescription ?: stringResource(Res.string.delivery_tracking_unknown_product),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.Slate900,
                    )
                    Text(
                        text = stringResource(
                            Res.string.delivery_tracking_order_id,
                            order.id?.take(8) ?: stringResource(Res.string.delivery_tracking_not_available)
                        ),
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
                            contentDescription = if (expanded) {
                                stringResource(Res.string.delivery_tracking_hide_details)
                            } else {
                                stringResource(Res.string.delivery_tracking_show_details)
                            },
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
                        text = stringResource(Res.string.delivery_tracking_courier),
                        fontSize = 12.sp,
                        color = AppColors.Slate500,
                    )
                    Text(
                        text = order.courierCompany ?: stringResource(Res.string.delivery_tracking_status_pending),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.Slate900,
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = stringResource(Res.string.delivery_tracking_tracking_id),
                        fontSize = 12.sp,
                        color = AppColors.Slate500,
                    )
                    Text(
                        text = order.courierTrackingId ?: stringResource(Res.string.delivery_tracking_status_pending),
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
                    text = stringResource(Res.string.delivery_tracking_estimated_dispatch_days, days),
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
