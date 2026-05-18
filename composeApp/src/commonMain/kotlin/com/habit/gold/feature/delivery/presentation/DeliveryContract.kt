package com.habit.gold.feature.delivery.presentation

import com.habit.gold.core.presentation.mvi.MviEffect
import com.habit.gold.core.presentation.mvi.MviIntent
import com.habit.gold.core.presentation.mvi.MviState
import com.habit.gold.feature.delivery.domain.model.CreateAddressDto
import com.habit.gold.feature.delivery.domain.model.DeliveryCheckoutPhase
import com.habit.gold.feature.delivery.domain.model.DeliveryCheckoutQuote
import com.habit.gold.feature.delivery.domain.model.PhysicalCoin
import com.habit.gold.feature.delivery.domain.model.SavedAddress
import com.habit.gold.feature.delivery.domain.model.UpdateAddressDto
import kotlin.math.roundToInt

data class DeliveryCatalogState(
    val isLoadingProducts: Boolean = false,
    val isLoadingAddresses: Boolean = false,
    val isRefreshingBalance: Boolean = false,
    val isCheckingOut: Boolean = false,
    
    val totalGoldBalanceGrams: Double = 0.0,
    val redeemableGoldGrams: Double = 0.0,
    val liveBuyPricePerGram: Double = 0.0,
    
    val coins: List<PhysicalCoin> = emptyList(),
    val cartItems: Map<String, Int> = emptyMap(), // coinId -> quantity
    
    val savedAddresses: List<SavedAddress> = emptyList(),
    val selectedAddressId: String? = null,
    
    val checkoutQuote: DeliveryCheckoutQuote? = null,
    val checkoutPhase: DeliveryCheckoutPhase = DeliveryCheckoutPhase.IDLE,
    val pendingPaymentSdkPayloadJson: String? = null,
    
    val couponCode: String? = null,
    val couponDiscountInr: Double = 0.0,
    
    val addressBeingEdited: SavedAddress? = null,

    // ── Pincode verification state (address form) ──────────────────────
    /** City auto-filled from India Post lookup API. */
    val postalLookupCity: String? = null,
    /** State auto-filled from India Post lookup API. */
    val postalLookupState: String? = null,
    /** Whether the pincode has been verified as serviceable for delivery. */
    val deliveryPincodeVerified: Boolean = false,
    /** True while the pincode verification request is in progress. */
    val isVerifyingPincode: Boolean = false,
    /** Human-readable error message when pincode is not serviceable. */
    val pincodeVerifyError: String? = null,

    // ── Address save + OTP flow state ──────────────────────────────────
    /** True while the create/update address API call is in flight. */
    val isSavingAddress: Boolean = false,
    /** The ID returned by createAddress API, needed for OTP flow. */
    val lastCreatedAddressId: String? = null,
    /** When non-null, the OTP dialog is shown for this address ID. */
    val otpAddressId: String? = null,
    /** True while OTP is being verified on the server. */
    val isVerifyingOtp: Boolean = false,
    /** Human-readable error message when OTP verification fails. */
    val otpVerifyError: String? = null,
) : MviState {
    val totalCoinsSelected: Int get() = cartItems.values.sum()
    
    val totalWeightGm: Double get() = cartItems.mapNotNull { (id, qty) ->
        coins.find { it.id == id }?.let { it.weightGm * qty }
    }.sum()
    
    val netAmountPayable: Double get() = cartItems.mapNotNull { (id, qty) ->
        coins.find { it.id == id }?.let { it.makingCharge * qty }
    }.sum() - couponDiscountInr
    
    val isAddressEditingLocked: Boolean get() = checkoutPhase != DeliveryCheckoutPhase.IDLE && checkoutPhase != DeliveryCheckoutPhase.REVIEW_READY
}

sealed interface DeliveryIntent : MviIntent {
    data object LoadProducts : DeliveryIntent
    data class UpdateQuantity(val coinId: String, val delta: Int) : DeliveryIntent
    data class SelectAddress(val id: String) : DeliveryIntent
    data object RefreshAddresses : DeliveryIntent
    data object RefreshGoldBalance : DeliveryIntent
    
    data class PrepareQuote(val couponCode: String? = null) : DeliveryIntent
    data object ConfirmOrder : DeliveryIntent
    data class HandlePaymentResult(val status: String, val payload: String? = null) : DeliveryIntent
    data object DiscardCheckout : DeliveryIntent
    data object ClearError : DeliveryIntent
    
    data class CreateAddress(val body: CreateAddressDto) : DeliveryIntent
    data class UpdateAddress(val id: String, val body: UpdateAddressDto) : DeliveryIntent
    data class DeleteAddress(val id: String) : DeliveryIntent
    data class LoadAddressForEdit(val id: String) : DeliveryIntent
    data class SendAddressOtp(val id: String) : DeliveryIntent
    data class VerifyAddressOtp(val id: String, val otp: String) : DeliveryIntent
    data class CheckServiceability(val id: String) : DeliveryIntent
    
    data class VerifyDeliveryPincode(val pincode: String) : DeliveryIntent
    data class LookupPostalPincode(val pincode: String) : DeliveryIntent
    
    data class ApplyCoupon(val code: String) : DeliveryIntent
    data object RemoveCoupon : DeliveryIntent
    
    data object ClearEditState : DeliveryIntent
    /** Dismiss the OTP dialog without completing verification. */
    data object DismissOtpDialog : DeliveryIntent
}

sealed interface DeliveryEffect : MviEffect {
    data class ShowError(val message: String) : DeliveryEffect
    data class ShowToast(val message: String) : DeliveryEffect
    data object NavigateToCart : DeliveryEffect
    data object NavigateToCheckout : DeliveryEffect
    data class NavigateToOrderSummary(val orderId: String) : DeliveryEffect
    data class LaunchPaymentSdk(val payloadJson: String) : DeliveryEffect
    data object NavigateBack : DeliveryEffect
    data object AddressSaved : DeliveryEffect
    data class OrderCompleted(val orderId: String) : DeliveryEffect
    data class NavigateToBuyGold(val shortfallGrams: Double) : DeliveryEffect
    /** Emitted after address is created + OTP verified + serviceability check passes. */
    data object AddressFullyVerified : DeliveryEffect
}
