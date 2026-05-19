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
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.delivery_address_delivery_not_available
import habitgoldmobile.composeapp.generated.resources.delivery_address_enter_valid_otp
import habitgoldmobile.composeapp.generated.resources.delivery_address_otp_invalid
import habitgoldmobile.composeapp.generated.resources.delivery_address_otp_sent
import habitgoldmobile.composeapp.generated.resources.delivery_address_pincode_not_serviceable
import habitgoldmobile.composeapp.generated.resources.delivery_address_pincode_serviceable
import habitgoldmobile.composeapp.generated.resources.delivery_error_create_address
import habitgoldmobile.composeapp.generated.resources.delivery_error_delete_address
import habitgoldmobile.composeapp.generated.resources.delivery_error_read_address_id
import habitgoldmobile.composeapp.generated.resources.delivery_error_send_otp
import habitgoldmobile.composeapp.generated.resources.delivery_error_update_address
import habitgoldmobile.composeapp.generated.resources.delivery_error_verify_pincode
import habitgoldmobile.composeapp.generated.resources.delivery_error_verify_serviceability
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
                        emitEffect(
                            DeliveryAddressEffect.ShowError(
                                error.message?.asDeliveryUiText()
                                    ?: DeliveryUiText.Resource(Res.string.delivery_error_update_address)
                            )
                        )
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
                        emitEffect(DeliveryAddressEffect.ShowError(DeliveryUiText.Resource(Res.string.delivery_error_read_address_id)))
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
                    emitEffect(
                        DeliveryAddressEffect.ShowError(
                            error.message?.asDeliveryUiText()
                                ?: DeliveryUiText.Resource(Res.string.delivery_error_create_address)
                        )
                    )
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
                    emitEffect(
                        DeliveryAddressEffect.ShowError(
                            error.message?.asDeliveryUiText()
                                ?: DeliveryUiText.Resource(Res.string.delivery_error_update_address)
                        )
                    )
                }
            )
        }
    }

    private fun deleteAddress(id: String) {
        viewModelScope.launch {
            deleteUserAddressUseCase(id).fold(
                onSuccess = { refreshAddresses() },
                onFailure = { error ->
                    emitEffect(
                        DeliveryAddressEffect.ShowError(
                            error.message?.asDeliveryUiText()
                                ?: DeliveryUiText.Resource(Res.string.delivery_error_delete_address)
                        )
                    )
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
                    emitEffect(DeliveryAddressEffect.ShowToast(DeliveryUiText.Resource(Res.string.delivery_address_otp_sent)))
                },
                onFailure = { error ->
                    val message = error.message?.asDeliveryUiText()
                        ?: DeliveryUiText.Resource(Res.string.delivery_error_send_otp)
                    updateState { it.copy(otpVerifyError = message) }
                    emitEffect(DeliveryAddressEffect.ShowError(message))
                }
            )
        }
    }

    private fun verifyAddressOtp(id: String, otp: String) {
        val digits = otp.filter { it.isDigit() }.take(6)
        if (digits.length != 6) {
            updateState { it.copy(otpVerifyError = DeliveryUiText.Resource(Res.string.delivery_address_enter_valid_otp)) }
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
                            otpVerifyError = error.message?.asDeliveryUiText()
                                ?: DeliveryUiText.Resource(Res.string.delivery_address_otp_invalid)
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
                            DeliveryUiText.Resource(Res.string.delivery_address_delivery_not_available)
                        )
                    )
                }
            },
            onFailure = { error ->
                updateState { it.copy(isVerifyingOtp = false) }
                emitEffect(
                    DeliveryAddressEffect.ShowError(
                        error.message?.asDeliveryUiText()
                            ?: DeliveryUiText.Resource(Res.string.delivery_error_verify_serviceability)
                    )
                )
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
                            pincodeVerifyError = if (ok) {
                                null
                            } else {
                                root.string("message")?.asDeliveryUiText()
                                    ?: DeliveryUiText.Resource(Res.string.delivery_address_pincode_not_serviceable)
                            },
                        )
                    }
                    if (ok) {
                        emitEffect(DeliveryAddressEffect.ShowToast(DeliveryUiText.Resource(Res.string.delivery_address_pincode_serviceable)))
                    } else {
                        emitEffect(
                            DeliveryAddressEffect.ShowError(
                                root.string("message")?.asDeliveryUiText()
                                    ?: DeliveryUiText.Resource(Res.string.delivery_address_pincode_not_serviceable)
                            )
                        )
                    }
                }
                .onFailure { error ->
                    val message = error.message?.asDeliveryUiText()
                        ?: DeliveryUiText.Resource(Res.string.delivery_error_verify_pincode)
                    updateState {
                        it.copy(
                            isVerifyingPincode = false,
                            deliveryPincodeVerified = false,
                            pincodeVerifyError = message,
                        )
                    }
                    emitEffect(DeliveryAddressEffect.ShowError(message))
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

private fun String.asDeliveryUiText(): DeliveryUiText = DeliveryUiText.Dynamic(this)
