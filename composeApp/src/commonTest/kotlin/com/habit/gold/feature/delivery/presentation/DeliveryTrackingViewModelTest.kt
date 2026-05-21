package com.habit.gold.feature.delivery.presentation

import com.habit.gold.feature.delivery.domain.DeliveryRepository
import com.habit.gold.feature.delivery.domain.model.ConfirmDeliveryOrderDto
import com.habit.gold.feature.delivery.domain.model.CreateDeliveryQuoteDto
import com.habit.gold.feature.delivery.domain.model.DeliveryInvoiceResponseDto
import com.habit.gold.feature.delivery.domain.model.DeliveryOrderConfirmResponseDto
import com.habit.gold.feature.delivery.domain.model.DeliveryOrderDto
import com.habit.gold.feature.delivery.domain.model.DeliveryQuoteResponseDto
import com.habit.gold.feature.delivery.domain.usecase.ListDeliveryOrdersUseCase
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.delivery_error_fetch_orders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class DeliveryTrackingViewModelTest {

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
    fun `init loads delivery orders into success state`() = runTest(dispatcher) {
        val repository = FakeDeliveryOrdersRepository(
            result = Result.success(
                JsonArray(
                    listOf(
                        JsonObject(
                            mapOf(
                                "id" to JsonPrimitive("order-1"),
                                "status" to JsonPrimitive("SHIPPED"),
                            ),
                        ),
                    ),
                ),
            ),
        )
        val viewModel = DeliveryTrackingViewModel(
            listDeliveryOrdersUseCase = ListDeliveryOrdersUseCase(repository),
        )

        advanceUntilIdle()

        val state = viewModel.state.value
        assertIs<DeliveryTrackingState.Success>(state)
        assertEquals(listOf("order-1"), state.orders.mapNotNull { it.id })
    }

    @Test
    fun `refresh failure emits error effect and error state`() = runTest(dispatcher) {
        val repository = FakeDeliveryOrdersRepository(Result.failure(IllegalStateException("Failed to load")))
        val viewModel = DeliveryTrackingViewModel(
            listDeliveryOrdersUseCase = ListDeliveryOrdersUseCase(repository),
        )
        advanceUntilIdle()

        repository.result = Result.failure(IllegalStateException())
        val effectDeferred = async { viewModel.effects.first() }
        viewModel.onIntent(DeliveryTrackingIntent.Refresh)
        advanceUntilIdle()
        val effect = effectDeferred.await()

        val state = viewModel.state.value
        assertIs<DeliveryTrackingState.Error>(state)
        assertEquals(
            DeliveryUiText.Resource(Res.string.delivery_error_fetch_orders),
            state.message,
        )
        assertEquals(
            DeliveryTrackingEffect.ShowError(DeliveryUiText.Resource(Res.string.delivery_error_fetch_orders)),
            effect,
        )
    }
}

private class FakeDeliveryOrdersRepository(
    var result: Result<kotlinx.serialization.json.JsonElement>,
) : DeliveryRepository {
    override suspend fun listDeliveryOrders() = result

    override suspend fun getDeliveryProducts(): Result<kotlinx.serialization.json.JsonElement> = unused()
    override suspend fun validatePincode(pinCode: String, productWeightGrams: Double): Result<JsonObject> = unused()
    override suspend fun createDeliveryQuote(body: CreateDeliveryQuoteDto): Result<DeliveryQuoteResponseDto> = unused()
    override suspend fun confirmDeliveryOrder(
        idempotencyKey: String,
        body: ConfirmDeliveryOrderDto,
    ): Result<DeliveryOrderConfirmResponseDto> = unused()
    override suspend fun getDeliveryOrderDetails(id: String): Result<DeliveryOrderDto> = unused()
    override suspend fun getDeliveryOrderInvoice(id: String): Result<DeliveryInvoiceResponseDto> = unused()
}

private fun <T> unused(): T = error("Unused in test")
