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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocalShipping
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
import com.habit.gold.core.designsystem.theme.*
import com.habit.gold.feature.delivery.domain.model.DeliveryOrderDto
import com.habit.gold.feature.delivery.presentation.DeliveryTrackingIntent
import com.habit.gold.feature.delivery.presentation.DeliveryTrackingState
import com.habit.gold.feature.delivery.presentation.resolve
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import com.habit.gold.feature.delivery.presentation.components.*

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
