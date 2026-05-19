package com.habit.gold.feature.delivery.presentation

import androidx.lifecycle.viewModelScope
import com.habit.gold.core.presentation.mvi.MviViewModel
import com.habit.gold.core.session.SessionStore
import com.habit.gold.core.network.fold
import com.habit.gold.feature.delivery.domain.model.CreateAddressDto
import com.habit.gold.feature.delivery.domain.model.UpdateAddressDto
import com.habit.gold.feature.delivery.domain.model.indianMobileNumbersMatch
import com.habit.gold.feature.delivery.domain.model.isPincodeServiceable
import com.habit.gold.feature.delivery.domain.model.toDomain
import com.habit.gold.feature.delivery.domain.usecase.CheckAddressServiceabilityUseCase
import com.habit.gold.feature.delivery.domain.usecase.CreateUserAddressUseCase
import com.habit.gold.feature.delivery.domain.usecase.DeleteUserAddressUseCase
import com.habit.gold.feature.delivery.domain.usecase.ListUserAddressesUseCase
import com.habit.gold.feature.delivery.domain.usecase.LookupPostalPincodeUseCase
import com.habit.gold.feature.delivery.domain.usecase.SendAddressOtpUseCase
import com.habit.gold.feature.delivery.domain.usecase.UpdateUserAddressUseCase
import com.habit.gold.feature.delivery.domain.usecase.ValidateDeliveryPincodeUseCase
import com.habit.gold.feature.delivery.domain.usecase.VerifyAddressOtpUseCase
import kotlinx.coroutines.launch

class DeliveryAddressViewModel(
    private val listUserAddressesUseCase: ListUserAddressesUseCase,
    private val createUserAddressUseCase: CreateUserAddressUseCase,
    private val updateUserAddressUseCase: UpdateUserAddressUseCase,
    private val deleteUserAddressUseCase: DeleteUserAddressUseCase,
    private val sendAddressOtpUseCase: SendAddressOtpUseCase,
    private val verifyAddressOtpUseCase: VerifyAddressOtpUseCase,
    private val checkAddressServiceabilityUseCase: CheckAddressServiceabilityUseCase,
    private val validateDeliveryPincodeUseCase: ValidateDeliveryPincodeUseCase,
    private val lookupPostalPincodeUseCase: LookupPostalPincodeUseCase,
    private val sessionStore: SessionStore,
) : MviViewModel<DeliveryAddressState, DeliveryAddressIntent, DeliveryAddressEffect>(DeliveryAddressState()) {

    init {
        onIntent(DeliveryAddressIntent.RefreshAddresses)
    }

    override fun onIntent(intent: DeliveryAddressIntent) {
        when (intent) {
            is DeliveryAddressIntent.RefreshAddresses -> refreshAddresses()
            is DeliveryAddressIntent.CreateAddress -> createAddress(intent.body)
            is DeliveryAddressIntent.UpdateAddress -> updateAddress(intent.id, intent.body)
            is DeliveryAddressIntent.DeleteAddress -> deleteAddress(intent.id)
            is DeliveryAddressIntent.LoadAddressForEdit -> loadAddressForEdit(intent.id)
            is DeliveryAddressIntent.SendAddressOtp -> sendAddressOtp(intent.id)
            is DeliveryAddressIntent.VerifyAddressOtp -> verifyAddressOtp(intent.id, intent.otp)
            is DeliveryAddressIntent.CheckServiceability -> checkServiceability(intent.id)
            is DeliveryAddressIntent.VerifyDeliveryPincode -> verifyDeliveryPincode(intent.pincode)
            is DeliveryAddressIntent.LookupPostalPincode -> lookupPostalPincode(intent.pincode)
            is DeliveryAddressIntent.ClearEditState -> updateState {
                it.copy(
                    addressBeingEdited = null,
                    postalLookupCity = null,
                    postalLookupState = null,
                    deliveryPincodeVerified = false,
                    isVerifyingPincode = false,
                    pincodeVerifyError = null,
                    isSavingAddress = false,
                    lastCreatedAddressId = null,
                    otpAddressId = null,
                    isVerifyingOtp = false,
                    otpVerifyError = null,
                )
            }
            is DeliveryAddressIntent.DismissOtpDialog -> updateState {
                it.copy(
                    otpAddressId = null,
                    otpVerifyError = null,
                    lastCreatedAddressId = null,
                )
            }
        }
    }

    private fun refreshAddresses() {
        viewModelScope.launch {
            refreshAddressesSuspend()
        }
    }

    private suspend fun refreshAddressesSuspend() {
        updateState { it.copy(isLoadingAddresses = true) }
        listUserAddressesUseCase().fold(
            onSuccess = { addressesDto ->
                val addresses = addressesDto.map { it.toDomain() }
                updateState {
                    it.copy(
                        isLoadingAddresses = false,
                        savedAddresses = addresses,
                    )
                }
            },
            onFailure = {
                updateState { it.copy(isLoadingAddresses = false) }
            }
        )
    }

    private fun createAddress(body: CreateAddressDto) {
        val alreadyCreatedId = state.value.lastCreatedAddressId
        if (!alreadyCreatedId.isNullOrBlank()) {
            val updateDto = UpdateAddressDto(
                type = body.type,
                recipientName = body.recipientName,
                phoneNumber = body.phoneNumber,
                addressLine1 = body.addressLine1,
                addressLine2 = body.addressLine2,
                city = body.city,
                state = body.state,
                pincode = body.pincode,
                landmark = body.landmark
            )
            updateState { it.copy(isSavingAddress = true) }
            viewModelScope.launch {
                updateUserAddressUseCase(alreadyCreatedId, updateDto).fold(
                    onSuccess = {
                        updateState { it.copy(isSavingAddress = false) }
                        refreshAddressesSuspend()
                        afterAddressCreated(alreadyCreatedId, body.phoneNumber)
                    },
                    onFailure = { error ->
                        updateState { it.copy(isSavingAddress = false) }
                        emitEffect(DeliveryAddressEffect.ShowError(error.message ?: "Failed to update address"))
                    }
                )
            }
            return
        }

        updateState { it.copy(isSavingAddress = true) }
        viewModelScope.launch {
            createUserAddressUseCase(body).fold(
                onSuccess = { json ->
                    val newId = json.extractAddressId()
                    if (newId.isNullOrBlank()) {
                        updateState { it.copy(isSavingAddress = false) }
                        emitEffect(DeliveryAddressEffect.ShowError("Could not read new address ID"))
                        return@fold
                    }
                    updateState {
                        it.copy(
                            isSavingAddress = false,
                            lastCreatedAddressId = newId,
                        )
                    }
                    refreshAddressesSuspend()
                    afterAddressCreated(newId, body.phoneNumber)
                },
                onFailure = { error ->
                    updateState { it.copy(isSavingAddress = false) }
                    emitEffect(DeliveryAddressEffect.ShowError(error.message ?: "Failed to save address"))
                }
            )
        }
    }

    private suspend fun afterAddressCreated(addressId: String, addressPhone: String) {
        val userPhone = sessionStore.state.value.user?.phoneNumber
        val shouldSkipOtp = indianMobileNumbersMatch(userPhone, addressPhone)
        if (shouldSkipOtp) {
            completeAddressVerification(addressId)
        } else {
            sendAddressOtp(addressId)
        }
    }

    private fun updateAddress(id: String, body: UpdateAddressDto) {
        viewModelScope.launch {
            updateUserAddressUseCase(id, body).fold(
                onSuccess = {
                    refreshAddresses()
                    emitEffect(DeliveryAddressEffect.AddressSaved)
                },
                onFailure = { error ->
                    emitEffect(DeliveryAddressEffect.ShowError(error.message ?: "Failed to update address"))
                }
            )
        }
    }

    private fun deleteAddress(id: String) {
        viewModelScope.launch {
            deleteUserAddressUseCase(id).fold(
                onSuccess = { refreshAddresses() },
                onFailure = { error ->
                    emitEffect(DeliveryAddressEffect.ShowError(error.message ?: "Failed to delete address"))
                }
            )
        }
    }

    private fun loadAddressForEdit(id: String) {
        val address = state.value.savedAddresses.find { it.id == id }
        updateState { it.copy(addressBeingEdited = address) }
    }

    private fun sendAddressOtp(id: String) {
        viewModelScope.launch {
            updateState { it.copy(otpVerifyError = null) }
            sendAddressOtpUseCase(id).fold(
                onSuccess = {
                    updateState { it.copy(otpAddressId = id) }
                    emitEffect(DeliveryAddressEffect.ShowToast("OTP sent to your phone"))
                },
                onFailure = { error ->
                    updateState { it.copy(otpVerifyError = error.message ?: "Failed to send OTP") }
                    emitEffect(DeliveryAddressEffect.ShowError(error.message ?: "Failed to send OTP"))
                }
            )
        }
    }

    private fun verifyAddressOtp(id: String, otp: String) {
        val digits = otp.filter { it.isDigit() }.take(6)
        if (digits.length != 6) {
            updateState { it.copy(otpVerifyError = "Enter the 6-digit OTP") }
            return
        }
        updateState { it.copy(isVerifyingOtp = true, otpVerifyError = null) }
        viewModelScope.launch {
            verifyAddressOtpUseCase(id, com.habit.gold.feature.delivery.domain.model.VerifyAddressOtpDto(otp = digits)).fold(
                onSuccess = {
                    completeAddressVerification(id)
                },
                onFailure = { error ->
                    updateState {
                        it.copy(
                            isVerifyingOtp = false,
                            otpVerifyError = error.message ?: "Invalid OTP. Please try again."
                        )
                    }
                }
            )
        }
    }

    private fun checkServiceability(id: String) {
        viewModelScope.launch {
            completeAddressVerification(id)
        }
    }

    private suspend fun completeAddressVerification(addressId: String) {
        checkAddressServiceabilityUseCase(addressId).fold(
            onSuccess = { json ->
                val root = json.dataPayloadOrSelf()
                val fromApi = root.string("verificationStatus")
                    ?.equals("PINCODE_SERVICEABLE", ignoreCase = true) == true
                refreshAddressesSuspend()
                val fromList = state.value.savedAddresses
                    .find { it.id == addressId }
                    ?.isPincodeServiceable() == true
                updateState { it.copy(otpAddressId = null, isVerifyingOtp = false, otpVerifyError = null) }
                if (fromApi || fromList) {
                    emitEffect(DeliveryAddressEffect.AddressFullyVerified)
                } else {
                    emitEffect(
                        DeliveryAddressEffect.ShowError(
                            "Delivery is not available for this address. Verification status must be PINCODE_SERVICEABLE."
                        )
                    )
                }
            },
            onFailure = { error ->
                updateState { it.copy(isVerifyingOtp = false) }
                emitEffect(DeliveryAddressEffect.ShowError(error.message ?: "Could not verify delivery serviceability"))
            }
        )
    }

    private fun verifyDeliveryPincode(pincode: String) {
        updateState { it.copy(isVerifyingPincode = true, pincodeVerifyError = null) }
        viewModelScope.launch {
            val weight = 1.0 // In the future, we could pass actual cart weight
            validateDeliveryPincodeUseCase(pinCode = pincode, productWeightGrams = weight)
                .onSuccess { json ->
                    val root = json.dataPayloadOrSelf()
                    val ok = root.pincodeAccepted()
                    updateState {
                        it.copy(
                            isVerifyingPincode = false,
                            deliveryPincodeVerified = ok,
                            pincodeVerifyError = if (ok) null else (root.string("message") ?: "Pincode not serviceable"),
                        )
                    }
                    if (ok) {
                        emitEffect(DeliveryAddressEffect.ShowToast("Pincode is serviceable ✓"))
                    } else {
                        emitEffect(DeliveryAddressEffect.ShowError(root.string("message") ?: "Pincode not serviceable"))
                    }
                }
                .onFailure { error ->
                    updateState {
                        it.copy(
                            isVerifyingPincode = false,
                            deliveryPincodeVerified = false,
                            pincodeVerifyError = error.message ?: "Could not verify pincode",
                        )
                    }
                    emitEffect(DeliveryAddressEffect.ShowError(error.message ?: "Could not verify pincode"))
                }
        }
    }

    private fun lookupPostalPincode(pincode: String) {
        viewModelScope.launch {
            lookupPostalPincodeUseCase(pincode).fold(
                onSuccess = { (district, lookupState) ->
                    updateState {
                        it.copy(
                            postalLookupCity = district,
                            postalLookupState = lookupState,
                        )
                    }
                },
                onFailure = {
                    updateState { it.copy(postalLookupCity = null, postalLookupState = null) }
                }
            )
        }
    }
}
