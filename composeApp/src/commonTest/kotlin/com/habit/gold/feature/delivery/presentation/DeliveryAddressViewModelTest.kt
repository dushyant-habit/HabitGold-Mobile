package com.habit.gold.feature.delivery.presentation

import com.habit.gold.core.storage.InMemorySecureStorage
import com.habit.gold.core.storage.InMemorySessionMetadataStorage
import com.habit.gold.core.storage.InMemoryUserProfileStorage
import com.habit.gold.core.storage.SecureAuthTokenStorage
import com.habit.gold.core.session.SessionStore
import com.habit.gold.feature.delivery.domain.AddressRepository
import com.habit.gold.feature.delivery.domain.DeliveryRepository
import com.habit.gold.feature.delivery.domain.PostalPincodeRepository
import com.habit.gold.feature.delivery.domain.model.AddressType
import com.habit.gold.feature.delivery.domain.model.ConfirmDeliveryOrderDto
import com.habit.gold.feature.delivery.domain.model.CreateAddressDto
import com.habit.gold.feature.delivery.domain.model.CreateDeliveryQuoteDto
import com.habit.gold.feature.delivery.domain.model.DeliveryAddressDto
import com.habit.gold.feature.delivery.domain.model.DeliveryInvoiceResponseDto
import com.habit.gold.feature.delivery.domain.model.DeliveryOrderConfirmResponseDto
import com.habit.gold.feature.delivery.domain.model.DeliveryOrderDto
import com.habit.gold.feature.delivery.domain.model.DeliveryQuoteResponseDto
import com.habit.gold.feature.delivery.domain.model.UpdateAddressDto
import com.habit.gold.feature.delivery.domain.model.VerifyAddressOtpDto
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
import habitgoldmobile.composeapp.generated.resources.delivery_address_enter_valid_otp
import habitgoldmobile.composeapp.generated.resources.delivery_address_pincode_serviceable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DeliveryAddressViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `short otp shows validation error without calling verify use case`() = runTest(dispatcher) {
        val addressRepository = FakeAddressRepository()
        val viewModel = createViewModel(addressRepository = addressRepository)
        advanceUntilIdle()

        viewModel.onIntent(DeliveryAddressIntent.VerifyAddressOtp(id = "address-1", otp = "12"))

        assertEquals(
            DeliveryUiText.Resource(Res.string.delivery_address_enter_valid_otp),
            viewModel.state.value.otpVerifyError,
        )
        assertFalse(addressRepository.verifyOtpCalled)
    }

    @Test
    fun `serviceable pincode updates state and emits toast`() = runTest(dispatcher) {
        val deliveryRepository = FakeValidatePincodeRepository(
            Result.success(
                JsonObject(mapOf("serviceable" to JsonPrimitive(true))),
            ),
        )
        val viewModel = createViewModel(deliveryRepository = deliveryRepository)
        advanceUntilIdle()

        val effectDeferred = async { viewModel.effects.first() }
        viewModel.onIntent(DeliveryAddressIntent.VerifyDeliveryPincode("560001"))
        advanceUntilIdle()
        val effect = effectDeferred.await()

        assertTrue(viewModel.state.value.deliveryPincodeVerified)
        assertEquals(
            DeliveryAddressEffect.ShowToast(
                DeliveryUiText.Resource(Res.string.delivery_address_pincode_serviceable),
            ),
            effect,
        )
    }

    private fun createViewModel(
        addressRepository: FakeAddressRepository = FakeAddressRepository(),
        deliveryRepository: FakeValidatePincodeRepository = FakeValidatePincodeRepository(Result.failure(IllegalStateException("unused"))),
    ): DeliveryAddressViewModel {
        return DeliveryAddressViewModel(
            listUserAddressesUseCase = ListUserAddressesUseCase(addressRepository),
            createUserAddressUseCase = CreateUserAddressUseCase(addressRepository),
            updateUserAddressUseCase = UpdateUserAddressUseCase(addressRepository),
            deleteUserAddressUseCase = DeleteUserAddressUseCase(addressRepository),
            sendAddressOtpUseCase = SendAddressOtpUseCase(addressRepository),
            verifyAddressOtpUseCase = VerifyAddressOtpUseCase(addressRepository),
            checkAddressServiceabilityUseCase = CheckAddressServiceabilityUseCase(addressRepository),
            validateDeliveryPincodeUseCase = ValidateDeliveryPincodeUseCase(deliveryRepository),
            lookupPostalPincodeUseCase = LookupPostalPincodeUseCase(FakePostalPincodeRepository()),
            sessionStore = SessionStore(
                authTokenStorage = SecureAuthTokenStorage(InMemorySecureStorage()),
                userProfileStorage = InMemoryUserProfileStorage(),
                sessionMetadataStorage = InMemorySessionMetadataStorage(),
            ),
        )
    }
}

private class FakeAddressRepository : AddressRepository {
    var verifyOtpCalled = false

    override suspend fun listAddresses(): Result<List<DeliveryAddressDto>> {
        return Result.success(
            listOf(
                DeliveryAddressDto(
                    id = "address-1",
                    type = AddressType.HOME.name,
                    recipientName = "Dushyant",
                    phoneNumber = "+919999999999",
                    addressLine1 = "Line 1",
                    city = "Bengaluru",
                    state = "Karnataka",
                    pincode = "560001",
                    verificationStatus = "PINCODE_SERVICEABLE",
                ),
            ),
        )
    }

    override suspend fun createAddress(body: CreateAddressDto): Result<JsonObject> = unused()
    override suspend fun getAddress(id: String): Result<DeliveryAddressDto> = unused()
    override suspend fun updateAddress(id: String, body: UpdateAddressDto): Result<JsonObject> = unused()
    override suspend fun deleteAddress(id: String): Result<Unit> = Result.success(Unit)
    override suspend fun sendAddressOtp(id: String): Result<JsonObject> = Result.success(JsonObject(emptyMap()))
    override suspend fun verifyAddressOtp(id: String, body: VerifyAddressOtpDto): Result<JsonObject> {
        verifyOtpCalled = true
        return Result.success(JsonObject(emptyMap()))
    }

    override suspend fun checkAddressServiceability(id: String): Result<JsonObject> = Result.success(
        JsonObject(mapOf("verificationStatus" to JsonPrimitive("PINCODE_SERVICEABLE"))),
    )
}

private class FakeValidatePincodeRepository(
    private val validateResult: Result<JsonObject>,
) : DeliveryRepository {
    override suspend fun validatePincode(pinCode: String, productWeightGrams: Double): Result<JsonObject> = validateResult

    override suspend fun getDeliveryProducts(): Result<kotlinx.serialization.json.JsonElement> = unused()
    override suspend fun createDeliveryQuote(body: CreateDeliveryQuoteDto): Result<DeliveryQuoteResponseDto> = unused()
    override suspend fun confirmDeliveryOrder(
        idempotencyKey: String,
        body: ConfirmDeliveryOrderDto,
    ): Result<DeliveryOrderConfirmResponseDto> = unused()
    override suspend fun listDeliveryOrders(): Result<kotlinx.serialization.json.JsonElement> = unused()
    override suspend fun getDeliveryOrderDetails(id: String): Result<DeliveryOrderDto> = unused()
    override suspend fun getDeliveryOrderInvoice(id: String): Result<DeliveryInvoiceResponseDto> = unused()
}

private class FakePostalPincodeRepository : PostalPincodeRepository {
    override suspend fun lookupDistrictAndState(pincode: String): Result<Pair<String, String>> {
        return Result.success("Bengaluru" to "Karnataka")
    }
}

private fun <T> unused(): T = error("Unused in test")
