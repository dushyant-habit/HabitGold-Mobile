package com.habit.gold.feature.delivery.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.habit.gold.core.designsystem.theme.*
import com.habit.gold.feature.delivery.domain.model.SavedAddress
import com.habit.gold.feature.delivery.domain.model.compactAddressLine
import com.habit.gold.feature.delivery.domain.model.isPincodeServiceable
import com.habit.gold.feature.delivery.presentation.DeliveryAddressState
import com.habit.gold.feature.delivery.presentation.DeliveryAddressIntent
import com.habit.gold.feature.delivery.presentation.DeliveryAddressEffect
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
                            contentDescription = "Back",
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
                    Button(
                        onClick = onContinue,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
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
                            onSendOtp = {
                                onIntent(DeliveryAddressIntent.SendAddressOtp(address.id))
                            },
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
                errorMessage = state.otpVerifyError
            )
        }
    }

    // Delete Confirmation Dialog
    showDeleteConfirm?.let { address ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = null },
            title = { Text(stringResource(Res.string.delivery_address_delete)) },
            text = { Text("Delete address for ${address.name}?") },
            confirmButton = {
                TextButton(onClick = {
                    onIntent(DeliveryAddressIntent.DeleteAddress(address.id))
                    showDeleteConfirm = null
                }) {
                    Text("Delete", color = AppColors.Danger)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = null }) {
                    Text("Cancel")
                }
            },
        )
    }
}

@Composable
private fun EmptyAddressState(onAddNewAddress: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            tint = AppColors.Purple200,
            modifier = Modifier.size(72.dp),
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(Res.string.delivery_address_no_addresses),
            fontSize = 15.sp,
            color = AppColors.Slate600,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onAddNewAddress,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.Purple700),
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(stringResource(Res.string.delivery_address_add_new))
        }
    }
}

@Composable
private fun DeliveryAddressCard(
    address: SavedAddress,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSendOtp: () -> Unit,
    onCheckServiceability: () -> Unit,
) {
    val isServiceable = address.isPincodeServiceable()
    val borderColor = when {
        isSelected -> AppColors.Purple700
        else -> AppColors.Divider
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = AppColors.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Radio indicator
                RadioButton(
                    selected = isSelected,
                    onClick = onSelect,
                    colors = RadioButtonDefaults.colors(selectedColor = AppColors.Purple700),
                )
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = address.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = AppColors.Slate900,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = address.phoneNo,
                        fontSize = 13.sp,
                        color = AppColors.Slate500,
                    )
                }
                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = AppColors.Slate500, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AppColors.Danger, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Text(
                text = address.compactAddressLine(),
                fontSize = 13.sp,
                color = AppColors.Slate600,
                modifier = Modifier.padding(start = 48.dp),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(Modifier.height(10.dp))

            // Status chip + actions row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 40.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (isServiceable) {
                    ServiceableChip()
                } else {
                    // Show check serviceability button if pincode not yet verified
                    OutlinedButton(
                        onClick = onCheckServiceability,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, AppColors.Slate300),
                    ) {
                        Text(
                            stringResource(Res.string.delivery_address_check_serviceability),
                            fontSize = 12.sp,
                            color = AppColors.Slate700,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ServiceableChip() {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(AppColors.Green100)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = AppColors.Green600,
            modifier = Modifier.size(14.dp),
        )
        Text(
            text = "Serviceable",
            fontSize = 12.sp,
            color = AppColors.Green700,
            fontWeight = FontWeight.SemiBold,
        )
    }
}


