package com.habit.gold.feature.delivery.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habit.gold.core.designsystem.theme.*
import com.habit.gold.feature.delivery.domain.model.SavedAddress
import com.habit.gold.feature.delivery.domain.model.isPincodeServiceable
import com.habit.gold.feature.delivery.presentation.DeliveryAddressState
import com.habit.gold.feature.delivery.presentation.DeliveryAddressIntent
import com.habit.gold.feature.delivery.presentation.resolve
import com.habit.gold.feature.delivery.presentation.components.OtpVerificationDialog
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

/**
 * Delivery Address selection screen.
 *
 * Mirrors GetCoinAddressScreen from legacy Android:
 *  - Lists saved addresses with serviceability chip
 *  - Allows OTP-based phone verification
 *  - "Add New Address" opens AddEditAddressScreen
 *  - Selecting a serviceable address and tapping Continue navigates back to cart
 */
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

    // Listen for AddressSaved effect to dismiss OTP dialog
    val selectedAddress = state.savedAddresses.find { it.id == selectedAddressId }

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
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AppColors.Purple700)
                }
            } else if (state.savedAddresses.isEmpty()) {
                EmptyAddressState(onAddNewAddress = {
                    onIntent(DeliveryAddressIntent.ClearEditState)
                    onAddNewAddress()
                })
            } else {
                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
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
                        val isSelected = address.id == selectedAddressId
                        DeliveryAddressCard(
                            address = address,
                            isSelected = isSelected,
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

    // OTP Verification Dialog
    val otpAddressId = state.otpAddressId
    if (otpAddressId != null) {
        val address = state.savedAddresses.find { it.id == otpAddressId }
        if (address != null) {
            OtpVerificationDialog(
                address = address,
                onDismiss = {
                    onIntent(DeliveryAddressIntent.DismissOtpDialog)
                },
                onVerify = { otp ->
                    onIntent(DeliveryAddressIntent.VerifyAddressOtp(address.id, otp))
                },
                onResend = {
                    onIntent(DeliveryAddressIntent.SendAddressOtp(address.id))
                },
                isVerifying = state.isVerifyingOtp,
                errorMessage = state.otpVerifyError?.resolve()
            )
        }
    }

    // Delete Confirmation Dialog
    showDeleteConfirm?.let { address ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = null },
            containerColor = AppColors.White,
            title = { Text(stringResource(Res.string.delivery_address_delete)) },
            text = {
                Text(
                    stringResource(
                        Res.string.delivery_address_delete_confirmation,
                        address.name
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onIntent(DeliveryAddressIntent.DeleteAddress(address.id))
                    showDeleteConfirm = null
                }) {
                    Text(stringResource(Res.string.delivery_address_delete_action), color = AppColors.Danger)
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
