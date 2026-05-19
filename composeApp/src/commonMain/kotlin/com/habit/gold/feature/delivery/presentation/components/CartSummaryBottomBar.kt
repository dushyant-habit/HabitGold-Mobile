package com.habit.gold.feature.delivery.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habit.gold.core.designsystem.theme.AppColors
import com.habit.gold.feature.delivery.domain.model.SavedAddress
import com.habit.gold.feature.delivery.domain.model.compactAddressLine
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun CartSummaryBottomBar(
    selectedAddress: SavedAddress?,
    addressServiceable: Boolean,
    isLoadingAddresses: Boolean,
    addressLocked: Boolean,
    onAddressClick: () -> Unit,
    onProceedClick: () -> Unit,
    ctaEnabled: Boolean,
    ctaLabel: String,
    isLoadingPayment: Boolean
) {
    Surface(
        color = AppColors.White,
        shadowElevation = 10.dp,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AddressStrip(
                selectedAddress = selectedAddress,
                addressServiceable = addressServiceable,
                isLoadingAddresses = isLoadingAddresses,
                addressLocked = addressLocked,
                onClick = onAddressClick
            )

            Button(
                onClick = onProceedClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = ctaEnabled,
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 12.dp,
                    pressedElevation = 16.dp,
                    disabledElevation = 0.dp
                )
            ) {
                if (isLoadingPayment) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = AppColors.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = ctaLabel,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.White
                    )
                }
            }
        }
    }
}

@Composable
fun AddressStrip(
    selectedAddress: SavedAddress?,
    addressServiceable: Boolean,
    isLoadingAddresses: Boolean,
    addressLocked: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !addressLocked, onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = AppColors.Slate50,
        border = BorderStroke(1.dp, AppColors.Slate200)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(AppColors.Primary.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = AppColors.Primary,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                when {
                    isLoadingAddresses -> {
                        Text(
                            text = stringResource(Res.string.delivery_cart_loading_address),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppColors.Slate950
                        )
                    }
                    selectedAddress == null -> {
                        Text(
                            text = stringResource(Res.string.delivery_cart_add_delivery_address),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.Slate950
                        )
                        Text(
                            text = stringResource(Res.string.delivery_cart_select_where_to_deliver),
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            color = AppColors.Slate500
                        )
                    }
                    else -> {
                        Text(
                            text = selectedAddress.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.Slate950,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = selectedAddress.compactAddressLine(),
                            modifier = Modifier.fillMaxWidth(),
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = AppColors.Slate500,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (addressServiceable) {
                                if (addressLocked) "Address locked for this active quote" else "Estimated Delivery 7-14 Days"
                            } else {
                                "Choose a serviceable address"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (addressServiceable) AppColors.Green700 else AppColors.Orange700
                        )
                    }
                }
            }

            if (isLoadingAddresses) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = AppColors.Primary
                )
            } else if (!addressLocked) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = AppColors.Slate400,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
