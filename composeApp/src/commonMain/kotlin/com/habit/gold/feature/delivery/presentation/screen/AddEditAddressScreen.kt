package com.habit.gold.feature.delivery.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habit.gold.core.designsystem.theme.*
import com.habit.gold.feature.delivery.domain.model.AddressType
import com.habit.gold.feature.delivery.domain.model.CreateAddressDto
import com.habit.gold.feature.delivery.domain.model.SavedAddress
import com.habit.gold.feature.delivery.domain.model.UpdateAddressDto
import com.habit.gold.feature.delivery.domain.model.normalizeIndianMobileForApi
import com.habit.gold.feature.delivery.presentation.DeliveryCatalogState
import com.habit.gold.feature.delivery.presentation.DeliveryIntent
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

/**
 * Add or Edit Delivery Address screen.
 *
 * Mirrors AddNewAddressScreen from legacy Android:
 *  - All 8+ form fields (name, phone, pincode, line1, line2, city, state, landmark)
 *  - AddressType selection (HOME / WORK / OTHER)
 *  - Pincode-based city/state auto-fill via LookupPostalPincodeUseCase
 *  - Mandatory pincode serviceability verification before saving
 *  - OTP verification dialog after address is created
 *  - Save dispatches CreateAddress or UpdateAddress intent
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditAddressScreen(
    state: DeliveryCatalogState,
    onIntent: (DeliveryIntent) -> Unit,
    onBackClick: () -> Unit,
) {
    val isEditing = state.addressBeingEdited != null
    val existing: SavedAddress? = state.addressBeingEdited

    // Form fields
    var name by remember { mutableStateOf(existing?.name.orEmpty()) }
    var phone by remember { mutableStateOf(existing?.phoneNo.orEmpty()) }
    var pincode by remember { mutableStateOf(existing?.pincode.orEmpty()) }
    var line1 by remember { mutableStateOf(existing?.addressLine1.orEmpty()) }
    var line2 by remember { mutableStateOf(existing?.addressLine2.orEmpty()) }
    var city by remember { mutableStateOf(existing?.city.orEmpty()) }
    var stateField by remember { mutableStateOf(existing?.state.orEmpty()) }
    var landmark by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(AddressType.HOME) }

    // Track the pincode that was last verified so we know when it changes
    var lastVerifiedPincode by remember { mutableStateOf("") }

    // Auto-fill city/state from postal lookup when state updates
    LaunchedEffect(state.postalLookupCity, state.postalLookupState) {
        state.postalLookupCity?.let { if (city.isBlank()) city = it }
        state.postalLookupState?.let { if (stateField.isBlank()) stateField = it }
    }

    // When pincode changes, reset verification status and trigger postal lookup
    LaunchedEffect(pincode) {
        if (pincode != lastVerifiedPincode) {
            // Reset verification when pincode changes
        }
        if (pincode.length == 6 && city.isBlank() && stateField.isBlank()) {
            onIntent(DeliveryIntent.LookupPostalPincode(pincode))
        }
    }

    // Track when pincode is verified
    LaunchedEffect(state.deliveryPincodeVerified) {
        if (state.deliveryPincodeVerified && pincode.length == 6) {
            lastVerifiedPincode = pincode
        }
    }

    val isPincodeVerifiedForCurrent = state.deliveryPincodeVerified && pincode == lastVerifiedPincode && pincode.length == 6

    val canSave = name.isNotBlank() && phone.length >= 10 &&
            pincode.length == 6 && line1.isNotBlank() &&
            city.isNotBlank() && stateField.isNotBlank() &&
            isPincodeVerifiedForCurrent &&
            !state.isSavingAddress

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) stringResource(Res.string.add_edit_address_title_edit)
                        else stringResource(Res.string.add_edit_address_title_add),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.Slate950,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = AppColors.Black,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.White,
                    scrolledContainerColor = AppColors.White,
                    titleContentColor = AppColors.Slate950,
                    navigationIconContentColor = AppColors.Black,
                ),
            )
        },
        containerColor = AppColors.SurfaceLight,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Full Name
            AddressFormField(
                label = stringResource(Res.string.add_edit_address_full_name),
                value = name,
                onValueChange = { name = it },
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Words,
            )

            // Phone
            AddressFormField(
                label = stringResource(Res.string.add_edit_address_phone),
                value = phone,
                onValueChange = { if (it.length <= 10) phone = it.filter(Char::isDigit) },
                keyboardType = KeyboardType.Phone,
                prefix = { Text("+91 ", color = AppColors.Slate500) },
            )

            // Pincode + Verify button
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AddressFormField(
                        modifier = Modifier.weight(1f),
                        label = stringResource(Res.string.add_edit_address_pincode),
                        value = pincode,
                        onValueChange = { if (it.length <= 6) pincode = it.filter(Char::isDigit) },
                        keyboardType = KeyboardType.Number,
                    )

                    if (isPincodeVerifiedForCurrent) {
                        // Verified state
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Verified",
                            tint = AppColors.Green600,
                            modifier = Modifier.size(28.dp),
                        )
                    } else if (state.isVerifyingPincode) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = AppColors.Purple700,
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Button(
                            onClick = { onIntent(DeliveryIntent.VerifyDeliveryPincode(pincode)) },
                            enabled = pincode.length == 6,
                            modifier = Modifier.height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AppColors.Purple700,
                                disabledContainerColor = AppColors.Purple200,
                            ),
                        ) {
                            Text("Verify", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                // Pincode verify error
                state.pincodeVerifyError?.let { error ->
                    Text(
                        text = error,
                        fontSize = 12.sp,
                        color = AppColors.Red600,
                        modifier = Modifier.padding(start = 4.dp),
                    )
                }
            }

            // City & State
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AddressFormField(
                    modifier = Modifier.weight(1f),
                    label = stringResource(Res.string.add_edit_address_city),
                    value = city,
                    onValueChange = { city = it },
                    capitalization = KeyboardCapitalization.Words,
                )
                AddressFormField(
                    modifier = Modifier.weight(1f),
                    label = stringResource(Res.string.add_edit_address_state),
                    value = stateField,
                    onValueChange = { stateField = it },
                    capitalization = KeyboardCapitalization.Words,
                )
            }

            // Address Line 1
            AddressFormField(
                label = stringResource(Res.string.add_edit_address_address_line1),
                value = line1,
                onValueChange = { line1 = it },
                capitalization = KeyboardCapitalization.Sentences,
            )

            // Address Line 2 (optional)
            AddressFormField(
                label = stringResource(Res.string.add_edit_address_address_line2),
                value = line2,
                onValueChange = { line2 = it },
                capitalization = KeyboardCapitalization.Sentences,
            )

            // Landmark (optional)
            AddressFormField(
                label = stringResource(Res.string.add_edit_address_landmark),
                value = landmark,
                onValueChange = { landmark = it },
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Done,
            )

            // Bottom padding to clear FAB
            Spacer(Modifier.height(8.dp))

            // Save as & Save Button moved from bottomBar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Text(
                    text = "Save as",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.Slate700
                )
                Spacer(Modifier.height(8.dp))
                AddressTypeRow(
                    selected = selectedType,
                    onSelect = { selectedType = it },
                )
                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        val normalizedPhone = normalizeIndianMobileForApi(phone)
                        if (isEditing && existing != null) {
                            onIntent(
                                DeliveryIntent.UpdateAddress(
                                    id = existing.id,
                                    body = UpdateAddressDto(
                                        type = selectedType,
                                        recipientName = name.trim(),
                                        phoneNumber = normalizedPhone,
                                        addressLine1 = line1.trim(),
                                        addressLine2 = line2.trim().takeIf { it.isNotEmpty() },
                                        city = city.trim(),
                                        state = stateField.trim(),
                                        pincode = pincode.trim(),
                                        landmark = landmark.trim().takeIf { it.isNotEmpty() },
                                    ),
                                ),
                            )
                        } else {
                            onIntent(
                                DeliveryIntent.CreateAddress(
                                    body = CreateAddressDto(
                                        type = selectedType,
                                        recipientName = name.trim(),
                                        phoneNumber = normalizedPhone,
                                        addressLine1 = line1.trim(),
                                        addressLine2 = line2.trim().takeIf { it.isNotEmpty() },
                                        city = city.trim(),
                                        state = stateField.trim(),
                                        pincode = pincode.trim(),
                                        landmark = landmark.trim().takeIf { it.isNotEmpty() },
                                    ),
                                ),
                            )
                        }
                    },
                    enabled = canSave,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.Purple700,
                        disabledContainerColor = AppColors.Purple200,
                    ),
                ) {
                    if (state.isSavingAddress) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = AppColors.White,
                            strokeWidth = 2.dp,
                        )
                        Spacer(Modifier.width(8.dp))
                    }
                    Text(
                        text = stringResource(Res.string.add_edit_address_save),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }

    // ── OTP Verification Dialog ──────────────────────────────────────────
    val otpAddressId = state.otpAddressId
    if (otpAddressId != null) {
        OtpVerificationDialog(
            onDismiss = { onIntent(DeliveryIntent.DismissOtpDialog) },
            onVerify = { otp -> onIntent(DeliveryIntent.VerifyAddressOtp(otpAddressId, otp)) },
            onResend = { onIntent(DeliveryIntent.SendAddressOtp(otpAddressId)) },
            isVerifying = state.isVerifyingOtp,
            errorMessage = state.otpVerifyError
        )
    }
}

// ── OTP Dialog ───────────────────────────────────────────────────────────────

@Composable
private fun OtpVerificationDialog(
    onDismiss: () -> Unit,
    onVerify: (String) -> Unit,
    onResend: () -> Unit,
    isVerifying: Boolean,
    errorMessage: String? = null
) {
    var otp by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AppColors.White,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = "Verify Address",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.Slate950,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Enter the 6-digit OTP sent to your phone number to verify this address.",
                    fontSize = 14.sp,
                    color = AppColors.Slate600,
                )
                OutlinedTextField(
                    value = otp,
                    onValueChange = { if (it.length <= 6) otp = it.filter(Char::isDigit) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("OTP") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done,
                    ),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (errorMessage != null) AppColors.Red600 else AppColors.Purple700,
                        unfocusedBorderColor = if (errorMessage != null) AppColors.Red600 else AppColors.Slate300,
                        focusedLabelColor = AppColors.Purple700,
                    ),
                    isError = errorMessage != null
                )
                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        fontSize = 12.sp,
                        color = AppColors.Red600,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                TextButton(
                    onClick = onResend,
                    modifier = Modifier.align(Alignment.End),
                ) {
                    Text("Resend OTP", color = AppColors.Purple700, fontSize = 13.sp)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = AppColors.Slate500)
            }
        },
        confirmButton = {
            Button(
                onClick = { onVerify(otp) },
                enabled = otp.length == 6 && !isVerifying,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.Purple700,
                    disabledContainerColor = AppColors.Purple200,
                ),
            ) {
                if (isVerifying) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = AppColors.White,
                        strokeWidth = 2.dp,
                    )
                    Spacer(Modifier.width(6.dp))
                }
                Text("Verify")
            }
        },
    )
}

// ── Reusable Form Components ─────────────────────────────────────────────────

@Composable
private fun AddressTypeRow(
    selected: AddressType,
    onSelect: (AddressType) -> Unit,
) {
    val types = listOf(
        AddressType.HOME to "Home",
        AddressType.WORK to "Work",
        AddressType.OTHER to "Other",
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        types.forEach { (type, label) ->
            val isSelected = selected == type
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) AppColors.Purple700 else AppColors.White)
                    .clickable { onSelect(type) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) AppColors.White else AppColors.Slate600,
                )
            }
        }
    }
}

@Composable
private fun AddressFormField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.None,
    imeAction: ImeAction = ImeAction.Next,
    prefix: @Composable (() -> Unit)? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(label, fontSize = 13.sp) },
        prefix = prefix,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            capitalization = capitalization,
            imeAction = imeAction,
        ),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AppColors.Purple700,
            unfocusedBorderColor = AppColors.Slate300,
            focusedLabelColor = AppColors.Purple700,
        ),
    )
}
