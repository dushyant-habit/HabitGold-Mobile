package com.habit.gold.feature.delivery.presentation

import com.habit.gold.core.presentation.mvi.MviEffect
import com.habit.gold.core.presentation.mvi.MviIntent
import com.habit.gold.core.presentation.mvi.MviState
import com.habit.gold.feature.delivery.domain.model.CreateAddressDto
import com.habit.gold.feature.delivery.domain.model.SavedAddress
import com.habit.gold.feature.delivery.domain.model.UpdateAddressDto

data class DeliveryAddressState(
    val isLoadingAddresses: Boolean = false,
    val savedAddresses: List<SavedAddress> = emptyList(),
    val addressBeingEdited: SavedAddress? = null,
    
    // ── Pincode verification state (address form) ──────────────────────
    val postalLookupCity: String? = null,
    val postalLookupState: String? = null,
    val deliveryPincodeVerified: Boolean = false,
    val isVerifyingPincode: Boolean = false,
    val pincodeVerifyError: String? = null,

    // ── Address save + OTP flow state ──────────────────────────────────
    val isSavingAddress: Boolean = false,
    val lastCreatedAddressId: String? = null,
    val otpAddressId: String? = null,
    val isVerifyingOtp: Boolean = false,
    val otpVerifyError: String? = null,
) : MviState

sealed interface DeliveryAddressIntent : MviIntent {
    data object RefreshAddresses : DeliveryAddressIntent
    
    data class CreateAddress(val body: CreateAddressDto) : DeliveryAddressIntent
    data class UpdateAddress(val id: String, val body: UpdateAddressDto) : DeliveryAddressIntent
    data class DeleteAddress(val id: String) : DeliveryAddressIntent
    data class LoadAddressForEdit(val id: String) : DeliveryAddressIntent
    data class SendAddressOtp(val id: String) : DeliveryAddressIntent
    data class VerifyAddressOtp(val id: String, val otp: String) : DeliveryAddressIntent
    data class CheckServiceability(val id: String) : DeliveryAddressIntent
    
    data class VerifyDeliveryPincode(val pincode: String) : DeliveryAddressIntent
    data class LookupPostalPincode(val pincode: String) : DeliveryAddressIntent
    
    data object ClearEditState : DeliveryAddressIntent
    /** Dismiss the OTP dialog without completing verification. */
    data object DismissOtpDialog : DeliveryAddressIntent
}

sealed interface DeliveryAddressEffect : MviEffect {
    data class ShowError(val message: String) : DeliveryAddressEffect
    data class ShowToast(val message: String) : DeliveryAddressEffect
    data object AddressSaved : DeliveryAddressEffect
    /** Emitted after address is created + OTP verified + serviceability check passes. */
    data object AddressFullyVerified : DeliveryAddressEffect
}
