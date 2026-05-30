package com.habit.gold.feature.delivery.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habit.gold.core.presentation.PlatformBackHandler
import com.habit.gold.core.designsystem.theme.AppColors
import com.habit.gold.feature.delivery.domain.model.SavedAddress
import com.habit.gold.feature.delivery.domain.model.isPincodeServiceable
import com.habit.gold.feature.delivery.presentation.DeliveryAddressIntent
import com.habit.gold.feature.delivery.presentation.DeliveryAddressState
import com.habit.gold.feature.delivery.presentation.components.OtpVerificationDialog
import com.habit.gold.feature.delivery.presentation.resolve
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.common_back
import habitgoldmobile.composeapp.generated.resources.common_cancel
import habitgoldmobile.composeapp.generated.resources.delivery_address_add_new
import habitgoldmobile.composeapp.generated.resources.delivery_address_continue
import habitgoldmobile.composeapp.generated.resources.delivery_address_delete
import habitgoldmobile.composeapp.generated.resources.delivery_address_delete_action
import habitgoldmobile.composeapp.generated.resources.delivery_address_delete_confirmation
import habitgoldmobile.composeapp.generated.resources.delivery_address_title
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryAddressScreen(
    state: DeliveryAddressState,
    selectedAddressId: String? = null,
    onIntent: (DeliveryAddressIntent) -> Unit,
    onSelectAddress: (String) -> Unit = {},
    onBackClick: () -> Unit,
    onAddNewAddress: () -> Unit,
    onEditAddress: (addressId: String) -> Unit,
    onContinue: () -> Unit,
    showCheckoutButton: Boolean = true,
) {
    var showDeleteConfirm by remember { mutableStateOf<SavedAddress?>(null) }
    val selectedAddress = state.savedAddresses.find { it.id == selectedAddressId }
    val showSelectionControl = showCheckoutButton

    PlatformBackHandler(
        enabled = true,
        onBack = onBackClick,
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.delivery_address_title),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.Slate950,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.common_back),
                            tint = AppColors.Black,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.White,
                    scrolledContainerColor = AppColors.White,
                    navigationIconContentColor = AppColors.Black,
                    titleContentColor = AppColors.Slate950,
                ),
            )
        },
        bottomBar = {
            if (showCheckoutButton && selectedAddress != null && selectedAddress.isPincodeServiceable()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 8.dp,
                    color = AppColors.White,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 24.dp, vertical = 24.dp),
                    ) {
                        Button(
                            onClick = onContinue,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AppColors.Purple700,
                                contentColor = AppColors.White,
                            ),
                            enabled = !state.isLoadingAddresses,
                        ) {
                            Text(
                                text = stringResource(Res.string.delivery_address_continue),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }
        },
        containerColor = AppColors.SurfaceLight,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            if (state.isLoadingAddresses) {
                DeliveryAddressLoadingShimmer()
            } else if (state.savedAddresses.isEmpty()) {
                EmptyAddressState(
                    onAddNewAddress = {
                        onIntent(DeliveryAddressIntent.ClearEditState)
                        onAddNewAddress()
                    }
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                ) {
                    OutlinedButton(
                        onClick = {
                            onIntent(DeliveryAddressIntent.ClearEditState)
                            onAddNewAddress()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.5.dp, AppColors.Purple700),
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            tint = AppColors.Purple700,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = stringResource(Res.string.delivery_address_add_new),
                            color = AppColors.Purple700,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(state.savedAddresses, key = { it.id }) { address ->
                        DeliveryAddressCard(
                            address = address,
                            isSelected = address.id == selectedAddressId,
                            showSelectionControl = showSelectionControl,
                            onSelect = { onSelectAddress(address.id) },
                            onEdit = {
                                onIntent(DeliveryAddressIntent.LoadAddressForEdit(address.id))
                                onEditAddress(address.id)
                            },
                            onDelete = { showDeleteConfirm = address },
                            onCheckServiceability = {
                                onIntent(DeliveryAddressIntent.CheckServiceability(address.id))
                            },
                        )
                    }
                }
            }
        }
    }

    state.otpAddressId?.let { otpAddressId ->
        state.savedAddresses.find { it.id == otpAddressId }?.let { address ->
            OtpVerificationDialog(
                address = address,
                onDismiss = { onIntent(DeliveryAddressIntent.DismissOtpDialog) },
                onVerify = { otp ->
                    onIntent(DeliveryAddressIntent.VerifyAddressOtp(address.id, otp))
                },
                onResend = {
                    onIntent(DeliveryAddressIntent.SendAddressOtp(address.id))
                },
                isVerifying = state.isVerifyingOtp,
                errorMessage = state.otpVerifyError?.resolve(),
            )
        }
    }

    showDeleteConfirm?.let { address ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = null },
            containerColor = AppColors.White,
            title = { Text(stringResource(Res.string.delivery_address_delete)) },
            text = {
                Text(
                    stringResource(
                        Res.string.delivery_address_delete_confirmation,
                        address.name,
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onIntent(DeliveryAddressIntent.DeleteAddress(address.id))
                        showDeleteConfirm = null
                    }
                ) {
                    Text(
                        stringResource(Res.string.delivery_address_delete_action),
                        color = AppColors.Danger,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = null }) {
                    Text(stringResource(Res.string.common_cancel))
                }
            },
        )
    }
}

@Composable
private fun DeliveryAddressLoadingShimmer() {
    val transition = rememberInfiniteTransition(label = "delivery-address-shimmer")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(durationMillis = 1300, easing = LinearEasing)),
        label = "delivery-address-shimmer-progress",
    )
    val shimmerBrush = Brush.linearGradient(
        colors = listOf(Color(0xFFE8ECF3), Color(0xFFF6F8FB), Color(0xFFE8ECF3)),
        start = androidx.compose.ui.geometry.Offset(-260f + (520f * progress), 0f),
        end = androidx.compose.ui.geometry.Offset(0f + (520f * progress), 220f),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(shimmerBrush),
        )
        repeat(3) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.42f)
                        .height(16.dp)
                        .clip(RoundedCornerShape(999.dp))
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
                        .fillMaxWidth(0.72f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(shimmerBrush),
                )
            }
        }
    }
}
