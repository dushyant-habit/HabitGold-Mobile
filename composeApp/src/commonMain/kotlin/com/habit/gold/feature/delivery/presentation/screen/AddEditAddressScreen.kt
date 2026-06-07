package com.habit.gold.feature.delivery.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import com.habit.gold.core.designsystem.icons.HabitGoldIcons as Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habit.gold.core.designsystem.theme.*
import com.habit.gold.core.presentation.clearFocusOnTapOutside
import com.habit.gold.feature.delivery.domain.model.AddressType
import com.habit.gold.feature.delivery.domain.model.CreateAddressDto
import com.habit.gold.feature.delivery.domain.model.SavedAddress
import com.habit.gold.feature.delivery.domain.model.UpdateAddressDto
import com.habit.gold.feature.delivery.domain.model.normalizeIndianMobileForApi
import com.habit.gold.feature.delivery.domain.model.isPincodeServiceable
import com.habit.gold.feature.delivery.presentation.DeliveryAddressState
import com.habit.gold.feature.delivery.presentation.DeliveryAddressIntent
import com.habit.gold.feature.delivery.presentation.resolve
import com.habit.gold.feature.delivery.presentation.components.*
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
    state: DeliveryAddressState,
    onIntent: (DeliveryAddressIntent) -> Unit,
    onBackClick: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val isEditing = state.addressBeingEdited != null
    val existing: SavedAddress? = state.addressBeingEdited
    val defaultName = state.defaultRecipientName
    val defaultPhone = state.defaultRecipientPhone

    // Form fields
    var name by remember(existing?.id, defaultName) {
        mutableStateOf(existing?.name.orEmpty().ifBlank { defaultName })
    }
    var phone by remember(existing?.id, defaultPhone) {
        mutableStateOf(existing?.phoneNo.orEmpty().ifBlank { defaultPhone })
    }
    var pincode by remember(existing?.id) { mutableStateOf(existing?.pincode.orEmpty()) }
    var line1 by remember(existing?.id) { mutableStateOf(existing?.addressLine1.orEmpty()) }
    var line2 by remember(existing?.id) { mutableStateOf(existing?.addressLine2.orEmpty()) }
    var city by remember(existing?.id) { mutableStateOf(existing?.city.orEmpty()) }
    var stateField by remember(existing?.id) { mutableStateOf(existing?.state.orEmpty()) }
    var landmark by remember(existing?.id) { mutableStateOf(existing?.landmark.orEmpty()) }
    var selectedType by remember(existing?.id) { mutableStateOf(existing?.type ?: AddressType.HOME) }

    // Track the pincode that was last verified so we know when it changes
    var lastVerifiedPincode by remember {
        mutableStateOf(
            if (isEditing && existing?.isPincodeServiceable() == true) {
                existing.pincode.orEmpty()
            } else {
                ""
            }
        )
    }

    // Auto-fill city/state from postal lookup when state updates
    LaunchedEffect(state.postalLookupCity, state.postalLookupState) {
        state.postalLookupCity?.let { city = it }
        state.postalLookupState?.let { stateField = it }
    }

    // When pincode changes, reset verification status and trigger postal lookup
    LaunchedEffect(pincode) {
        if (pincode != lastVerifiedPincode) {
            if (pincode.length < 6 || pincode != existing?.pincode.orEmpty()) {
                city = ""
                stateField = ""
            }
        }
        if (pincode.length == 6) {
            onIntent(DeliveryAddressIntent.LookupPostalPincode(pincode))
        }
    }

    // Track when pincode is verified
    LaunchedEffect(state.deliveryPincodeVerified) {
        if (state.deliveryPincodeVerified && pincode.length == 6) {
            lastVerifiedPincode = pincode
        }
    }

    val isPincodeVerifiedForCurrent = (state.deliveryPincodeVerified && pincode == lastVerifiedPincode && pincode.length == 6) ||
            (isEditing && existing?.isPincodeServiceable() == true && pincode == existing.pincode)
    val cityStateLocked = isPincodeVerifiedForCurrent && city.isNotBlank() && stateField.isNotBlank()

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
                            contentDescription = stringResource(Res.string.common_back),
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
        bottomBar = {
            Surface(
                tonalElevation = 0.dp,
                shadowElevation = 8.dp,
                color = AppColors.White,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Button(
                        onClick = {
                            val normalizedPhone = normalizeIndianMobileForApi(phone)
                            if (isEditing && existing != null) {
                                onIntent(
                                    DeliveryAddressIntent.UpdateAddress(
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
                                    DeliveryAddressIntent.CreateAddress(
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
                            contentColor = AppColors.White,
                            disabledContainerColor = AppColors.Slate200,
                            disabledContentColor = AppColors.Slate500,
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
        },
        containerColor = AppColors.SurfaceLight,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clearFocusOnTapOutside { focusManager.clearFocus(force = true) }
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
                            contentDescription = stringResource(Res.string.delivery_address_serviceable),
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
                            onClick = {
                                onIntent(DeliveryAddressIntent.LookupPostalPincode(pincode))
                                onIntent(DeliveryAddressIntent.VerifyDeliveryPincode(pincode))
                            },
                            enabled = pincode.length == 6,
                            modifier = Modifier.height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AppColors.Purple700,
                                contentColor = AppColors.White,
                                disabledContainerColor = AppColors.Slate200,
                                disabledContentColor = AppColors.Slate500,
                            ),
                        ) {
                            Text(
                                stringResource(Res.string.delivery_address_verify),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Pincode verify error
                state.pincodeVerifyError?.let { error ->
                    Text(
                        text = error.resolve(),
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
                    enabled = !cityStateLocked,
                )
                AddressFormField(
                    modifier = Modifier.weight(1f),
                    label = stringResource(Res.string.add_edit_address_state),
                    value = stateField,
                    onValueChange = { stateField = it },
                    capitalization = KeyboardCapitalization.Words,
                    enabled = !cityStateLocked,
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

            // Bottom padding to clear sticky bar
            Spacer(Modifier.height(16.dp))
        }
    }

    // ── OTP Verification Dialog ──────────────────────────────────────────
    val otpAddressId = state.otpAddressId
    if (otpAddressId != null) {
        val otpAddress = state.savedAddresses.find { it.id == otpAddressId }
        if (otpAddress != null) {
            OtpVerificationDialog(
                address = otpAddress,
                onDismiss = {
                    onIntent(DeliveryAddressIntent.DismissOtpDialog)
                },
                onVerify = { otp ->
                    onIntent(DeliveryAddressIntent.VerifyAddressOtp(otpAddress.id, otp))
                },
                onResend = {
                    onIntent(DeliveryAddressIntent.SendAddressOtp(otpAddress.id))
                },
                isVerifying = state.isVerifyingOtp,
                errorMessage = state.otpVerifyError?.resolve()
            )
        }
    }
}

// ── Reusable Form Components ─────────────────────────────────────────────────

@Composable
private fun AddressFormField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.None,
    imeAction: ImeAction = ImeAction.Next,
    prefix: @Composable (() -> Unit)? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
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
