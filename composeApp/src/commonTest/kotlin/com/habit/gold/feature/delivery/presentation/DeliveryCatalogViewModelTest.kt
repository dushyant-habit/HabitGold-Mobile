package com.habit.gold.feature.delivery.presentation

import com.habit.gold.core.network.ApiResult
import com.habit.gold.core.storage.InMemorySecureStorage
import com.habit.gold.core.storage.InMemorySessionMetadataStorage
import com.habit.gold.core.storage.InMemoryUserProfileStorage
import com.habit.gold.core.storage.SecureAuthTokenStorage
import com.habit.gold.core.session.SessionStore
import com.habit.gold.feature.delivery.data.DeliveryCheckoutTelemetry
import com.habit.gold.feature.delivery.data.PendingDeliveryCheckout
import com.habit.gold.feature.delivery.data.PendingDeliveryCheckoutStage
import com.habit.gold.feature.delivery.data.PendingDeliveryCheckoutStore
import com.habit.gold.feature.delivery.domain.DeliveryRepository
import com.habit.gold.feature.delivery.domain.model.ConfirmDeliveryOrderDto
import com.habit.gold.feature.delivery.domain.model.DeliveryInvoiceResponseDto
import com.habit.gold.feature.delivery.domain.model.DeliveryOrderConfirmResponseDto
import com.habit.gold.feature.delivery.domain.model.DeliveryOrderDto
import com.habit.gold.feature.delivery.domain.model.DeliveryPaymentLaunchResult
import com.habit.gold.feature.delivery.domain.model.DeliveryQuoteResponseDto
import com.habit.gold.feature.delivery.domain.model.DeliveryVerifyQuoteDto
import com.habit.gold.feature.delivery.domain.model.CreateDeliveryQuoteDto
import com.habit.gold.feature.delivery.domain.usecase.ConfirmDeliveryOrderUseCase
import com.habit.gold.feature.delivery.domain.usecase.CreateDeliveryQuoteUseCase
import com.habit.gold.feature.delivery.domain.usecase.GetDeliveryOrderDetailsUseCase
import com.habit.gold.feature.delivery.domain.usecase.GetDeliveryProductsUseCase
import com.habit.gold.feature.trade.domain.TradeRepository
import com.habit.gold.feature.trade.domain.model.TradeAvailableCoupon
import com.habit.gold.feature.trade.domain.model.TradeBuyOrder
import com.habit.gold.feature.trade.domain.model.TradeBuyOrderRequest
import com.habit.gold.feature.trade.domain.model.TradeCouponOrderType
import com.habit.gold.feature.trade.domain.model.TradeCouponValidation
import com.habit.gold.feature.trade.domain.model.TradeCouponValidationRequest
import com.habit.gold.feature.trade.domain.model.TradeInvoice
import com.habit.gold.feature.trade.domain.model.TradeLivePrice
import com.habit.gold.feature.trade.domain.model.TradeSellAvailability
import com.habit.gold.feature.trade.domain.model.TradeSellOrder
import com.habit.gold.feature.trade.domain.model.TradeSellOrderRequest
import com.habit.gold.feature.trade.domain.model.TradeStatus
import com.habit.gold.feature.trade.domain.model.TradeTransactionsPage
import com.habit.gold.feature.trade.domain.model.TradeUserVpa
import com.habit.gold.feature.trade.domain.model.TradeVpaVerification
import com.habit.gold.feature.trade.domain.usecase.GetSellAvailabilityUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class DeliveryCatalogViewModelTest {

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
    fun `non pollable payment failure returns to review and keeps pending checkout`() = runTest(dispatcher) {
        val pendingStore = FakePendingDeliveryCheckoutStore(
            pendingCheckout(
                stage = PendingDeliveryCheckoutStage.SDK_LAUNCH_READY,
                orderId = "order-123",
            ),
        )
        val viewModel = createViewModel(
            pendingStore = pendingStore,
            deliveryRepository = FakeDeliveryRepository(),
        )

        advanceUntilIdle()
        assertEquals(com.habit.gold.feature.delivery.domain.model.DeliveryCheckoutPhase.PAYMENT_LAUNCH_READY, viewModel.state.value.checkoutPhase)

        viewModel.onIntent(
            DeliveryIntent.HandlePaymentResult(
                DeliveryPaymentLaunchResult.Failure(
                    status = "payment_failed",
                    message = "Payment failed",
                    shouldPollOrderStatus = false,
                ),
            ),
        )
        advanceUntilIdle()

        assertEquals(com.habit.gold.feature.delivery.domain.model.DeliveryCheckoutPhase.REVIEW_READY, viewModel.state.value.checkoutPhase)
        assertEquals(PendingDeliveryCheckoutStage.REVIEW, pendingStore.pendingCheckout.first()?.stage)
        assertEquals("order-123", pendingStore.pendingCheckout.first()?.orderId)
    }

    private fun createViewModel(
        pendingStore: PendingDeliveryCheckoutStore = FakePendingDeliveryCheckoutStore(null),
        deliveryRepository: DeliveryRepository,
    ): DeliveryCatalogViewModel {
        return DeliveryCatalogViewModel(
            getDeliveryProductsUseCase = GetDeliveryProductsUseCase(deliveryRepository),
            createDeliveryQuoteUseCase = CreateDeliveryQuoteUseCase(deliveryRepository),
            confirmDeliveryOrderUseCase = ConfirmDeliveryOrderUseCase(deliveryRepository),
            pendingDeliveryCheckoutStore = pendingStore,
            deliveryCheckoutTelemetry = NoOpDeliveryCheckoutTelemetry(),
            getSellAvailabilityUseCase = GetSellAvailabilityUseCase(FakeTradeRepository()),
            getDeliveryOrderDetailsUseCase = GetDeliveryOrderDetailsUseCase(deliveryRepository),
            sessionStore = createSessionStore(),
        )
    }

    private fun createSessionStore(): SessionStore {
        return SessionStore(
            authTokenStorage = SecureAuthTokenStorage(InMemorySecureStorage()),
            userProfileStorage = InMemoryUserProfileStorage(),
            sessionMetadataStorage = InMemorySessionMetadataStorage(),
        )
    }
}

private class FakePendingDeliveryCheckoutStore(initial: PendingDeliveryCheckout?) : PendingDeliveryCheckoutStore {
    private val state = MutableStateFlow(initial)
    override val pendingCheckout: Flow<PendingDeliveryCheckout?> = state

    override suspend fun save(pendingCheckout: PendingDeliveryCheckout) {
        state.value = pendingCheckout
    }

    override suspend fun clear() {
        state.value = null
    }
}

private class NoOpDeliveryCheckoutTelemetry : DeliveryCheckoutTelemetry {
    override fun quoteCreated(quoteId: String, productId: String, addressId: String) = Unit
    override fun confirmStarted(quoteId: String, idempotencyKey: String) = Unit
    override fun paymentSdkOpened(orderId: String?, quoteId: String) = Unit
    override fun finalOrderState(orderId: String?, paymentStatus: String?, orderStatus: String?) = Unit
}

private class FakeDeliveryRepository : DeliveryRepository {
    override suspend fun getDeliveryProducts() = Result.success(buildJsonArray {})

    override suspend fun validatePincode(pinCode: String, productWeightGrams: Double) =
        Result.success(buildJsonObject { put("serviceable", true) })

    override suspend fun createDeliveryQuote(body: CreateDeliveryQuoteDto): Result<DeliveryQuoteResponseDto> {
        return Result.success(
            DeliveryQuoteResponseDto(
                quoteId = "quote-1",
                verifyQuote = DeliveryVerifyQuoteDto(
                    mintingChargeInr = "100",
                    payableChargeInr = "100",
                    goldWeightGrams = "1.0",
                    verifyExpiresAt = "2030-01-01T00:00:00Z",
                    estimatedDispatchDays = 3,
                ),
            ),
        )
    }

    override suspend fun confirmDeliveryOrder(
        idempotencyKey: String,
        body: ConfirmDeliveryOrderDto,
    ): Result<DeliveryOrderConfirmResponseDto> {
        return Result.success(
            DeliveryOrderConfirmResponseDto(
                order = DeliveryOrderDto(id = "order-123", status = "PENDING", paymentStatus = "PENDING"),
                sdkPayload = buildJsonObject { put("payload", "sdk") },
            ),
        )
    }

    override suspend fun listDeliveryOrders() = Result.success(buildJsonArray {})

    override suspend fun getDeliveryOrderDetails(id: String): Result<DeliveryOrderDto> {
        return Result.success(DeliveryOrderDto(id = id, status = "PENDING", paymentStatus = "PENDING"))
    }

    override suspend fun getDeliveryOrderInvoice(id: String): Result<DeliveryInvoiceResponseDto> {
        return Result.success(DeliveryInvoiceResponseDto(invoiceUrl = null))
    }
}

private class FakeTradeRepository : TradeRepository {
    override suspend fun getSellAvailability(): ApiResult<TradeSellAvailability> {
        return ApiResult.Success(
            TradeSellAvailability(
                totalGoldBalanceGrams = 2.5,
                sellableGoldBalanceGrams = 2.0,
                lockedGoldBalanceGrams = 0.5,
            ),
        )
    }

    override suspend fun getLivePrice(): ApiResult<TradeLivePrice> = unused()
    override suspend fun createBuyOrder(request: TradeBuyOrderRequest): ApiResult<TradeBuyOrder> = unused()
    override suspend fun createSellOrder(request: TradeSellOrderRequest): ApiResult<TradeSellOrder> = unused()
    override suspend fun executeSellOrder(orderId: String, vpaId: String): ApiResult<TradeStatus> = unused()
    override suspend fun getTradeStatus(orderId: String): ApiResult<TradeStatus> = unused()
    override suspend fun getTradeInvoice(orderId: String): ApiResult<TradeInvoice> = unused()
    override suspend fun getTradeTransactions(page: Int, limit: Int): ApiResult<TradeTransactionsPage> = unused()
    override suspend fun getUserVpas(): ApiResult<List<TradeUserVpa>> = unused()
    override suspend fun setDefaultVpa(vpaId: String): ApiResult<Unit> = unused()
    override suspend fun verifyVpa(vpa: String): ApiResult<TradeVpaVerification> = unused()
    override suspend fun getAvailableCoupons(
        orderType: TradeCouponOrderType,
        amount: Double?,
        grams: Double?,
        deliveryFeeInr: Double?,
    ): ApiResult<List<TradeAvailableCoupon>> = unused()

    override suspend fun validateCoupon(request: TradeCouponValidationRequest): ApiResult<TradeCouponValidation> = unused()

    private fun <T> unused(): ApiResult<T> {
        error("Not used in DeliveryCatalogViewModelTest")
    }
}

private fun pendingCheckout(
    stage: PendingDeliveryCheckoutStage,
    orderId: String?,
): PendingDeliveryCheckout {
    return PendingDeliveryCheckout(
        quoteId = "quote-123",
        productId = "coin-1",
        addressId = "address-1",
        couponCode = null,
        verifyQuote = DeliveryVerifyQuoteDto(
            mintingChargeInr = "100",
            payableChargeInr = "100",
            goldWeightGrams = "1.0",
            verifyExpiresAt = "2030-01-01T00:00:00Z",
            estimatedDispatchDays = 3,
        ),
        confirmIdempotencyKey = "idem-1",
        orderId = orderId,
        sdkPayload = buildJsonObject { put("payload", "sdk") },
        stage = stage,
    )
}
