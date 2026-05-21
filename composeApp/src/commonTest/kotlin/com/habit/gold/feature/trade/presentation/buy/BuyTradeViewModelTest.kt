package com.habit.gold.feature.trade.presentation.buy

import com.habit.gold.core.network.ApiResult
import com.habit.gold.core.network.NetworkError
import com.habit.gold.core.network.NetworkErrorKind
import com.habit.gold.feature.trade.domain.TradeRepository
import com.habit.gold.feature.trade.domain.model.TradeAvailableCoupon
import com.habit.gold.feature.trade.domain.model.TradeBuyOrder
import com.habit.gold.feature.trade.domain.model.TradeBuyOrderRequest
import com.habit.gold.feature.trade.domain.model.TradeCouponOrderType
import com.habit.gold.feature.trade.domain.model.TradeCouponType
import com.habit.gold.feature.trade.domain.model.TradeCouponValidation
import com.habit.gold.feature.trade.domain.model.TradeCouponValidationRequest
import com.habit.gold.feature.trade.domain.model.TradeInvoice
import com.habit.gold.feature.trade.domain.model.TradeLivePrice
import com.habit.gold.feature.trade.domain.model.TradePaymentContext
import com.habit.gold.feature.trade.domain.model.TradePaymentLaunchRequest
import com.habit.gold.feature.trade.domain.model.TradePaymentLaunchResult
import com.habit.gold.feature.trade.domain.model.TradeSellAvailability
import com.habit.gold.feature.trade.domain.model.TradeSellOrder
import com.habit.gold.feature.trade.domain.model.TradeSellOrderRequest
import com.habit.gold.feature.trade.domain.model.TradeStatus
import com.habit.gold.feature.trade.domain.model.TradeTransactionsPage
import com.habit.gold.feature.trade.domain.model.TradeUserVpa
import com.habit.gold.feature.trade.domain.model.TradeVpaVerification
import com.habit.gold.feature.trade.domain.usecase.CreateBuyOrderUseCase
import com.habit.gold.feature.trade.domain.usecase.GetTradeAvailableCouponsUseCase
import com.habit.gold.feature.trade.domain.usecase.PollTradeStatusUseCase
import com.habit.gold.feature.trade.domain.usecase.ValidateTradeCouponUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class BuyTradeViewModelTest {

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
    fun `submit order without sdk payload starts polling immediately and reaches success`() = runTest(dispatcher) {
        val repository = FakeBuyTradeRepository(
            buyOrderResult = ApiResult.Success(
                tradeBuyOrder(
                    orderId = "order-1",
                    sdkPayloadJson = null,
                )
            ),
            statusResponses = listOf(
                ApiResult.Success(TradeStatus(orderId = "order-1", status = "PENDING")),
                ApiResult.Success(TradeStatus(orderId = "order-1", status = "SUCCESS")),
            ),
        )
        val viewModel = createViewModel(repository)

        viewModel.onIntent(
            BuyTradeIntent.SubmitOneTimeOrder(
                amount = 500.0,
                grams = null,
                buyRateId = "buy-rate-1",
            )
        )
        advanceUntilIdle()

        assertEquals(BuyTradeStep.Success, viewModel.state.value.step)
        assertEquals("order-1", viewModel.state.value.currentOrderId)
        assertEquals("SUCCESS", viewModel.state.value.pollingSnapshot?.status)
        assertNull(viewModel.state.value.pendingPaymentRequest)
    }

    @Test
    fun `sdk failure callback still polls order status and reaches success`() = runTest(dispatcher) {
        val repository = FakeBuyTradeRepository(
            buyOrderResult = ApiResult.Success(
                tradeBuyOrder(
                    orderId = "order-2",
                    sdkPayloadJson = """{"sdk":"payload"}""",
                )
            ),
            statusResponses = listOf(
                ApiResult.Success(TradeStatus(orderId = "order-2", status = "PAYMENT_RECEIVED")),
                ApiResult.Success(TradeStatus(orderId = "order-2", status = "COMPLETED")),
            ),
        )
        val viewModel = createViewModel(repository)

        viewModel.onIntent(
            BuyTradeIntent.SubmitOneTimeOrder(
                amount = 500.0,
                grams = null,
                buyRateId = "buy-rate-2",
            )
        )
        advanceUntilIdle()
        assertEquals("order-2", viewModel.state.value.currentOrderId)

        viewModel.onIntent(
            BuyTradeIntent.HandlePaymentResult(
                TradePaymentLaunchResult.Failure(
                    status = "network_error",
                    message = "Network error from SDK",
                    shouldPollOrderStatus = true,
                )
            )
        )
        advanceUntilIdle()

        assertEquals(BuyTradeStep.Success, viewModel.state.value.step)
        assertEquals("COMPLETED", viewModel.state.value.pollingSnapshot?.status)
    }

    @Test
    fun `pre launch payment failure stays on entry and surfaces error`() = runTest(dispatcher) {
        val repository = FakeBuyTradeRepository(
            buyOrderResult = ApiResult.Success(
                tradeBuyOrder(
                    orderId = "order-3",
                    sdkPayloadJson = """{"sdk":"payload"}""",
                )
            ),
        )
        val viewModel = createViewModel(repository)

        viewModel.onIntent(
            BuyTradeIntent.SubmitOneTimeOrder(
                amount = 500.0,
                grams = null,
                buyRateId = "buy-rate-3",
            )
        )
        advanceUntilIdle()

        viewModel.onIntent(
            BuyTradeIntent.HandlePaymentResult(
                TradePaymentLaunchResult.Failure(
                    status = "juspay_not_configured",
                    message = "Juspay is not configured.",
                    shouldPollOrderStatus = false,
                )
            )
        )
        advanceUntilIdle()

        assertEquals(BuyTradeStep.Entry, viewModel.state.value.step)
        assertEquals("Juspay is not configured.", viewModel.state.value.errorMessage)
    }

    @Test
    fun `apply coupon stores validated coupon and clears prior error`() = runTest(dispatcher) {
        val repository = FakeBuyTradeRepository(
            buyOrderResult = ApiResult.Success(
                tradeBuyOrder(
                    orderId = "order-4",
                    sdkPayloadJson = null,
                )
            ),
        )
        val viewModel = createViewModel(repository)

        viewModel.onIntent(BuyTradeIntent.ApplyCoupon(code = "SAVE50", amount = 1000.0, grams = null))
        advanceUntilIdle()

        val appliedCoupon = assertIs<TradeCouponValidation>(viewModel.state.value.appliedCoupon)
        assertEquals("SAVE50", appliedCoupon.code)
        assertNull(viewModel.state.value.errorMessage)
    }

    @Test
    fun `back pressed after launching payment returns to entry and clears pending request`() = runTest(dispatcher) {
        val repository = FakeBuyTradeRepository(
            buyOrderResult = ApiResult.Success(
                tradeBuyOrder(
                    orderId = "order-5",
                    sdkPayloadJson = """{"sdk":"payload"}""",
                )
            ),
        )
        val viewModel = createViewModel(repository)

        viewModel.onIntent(
            BuyTradeIntent.SubmitOneTimeOrder(
                amount = 500.0,
                grams = null,
                buyRateId = "buy-rate-5",
            )
        )
        advanceUntilIdle()
        assertEquals(BuyTradeStep.Entry, viewModel.state.value.step)
        assertEquals("order-5", viewModel.state.value.currentOrderId)
        val pendingRequest = assertIs<TradePaymentLaunchRequest.Juspay>(viewModel.state.value.pendingPaymentRequest)
        assertEquals(TradePaymentContext.BuyOneTime, pendingRequest.context)

        viewModel.onIntent(BuyTradeIntent.HandlePaymentResult(TradePaymentLaunchResult.BackPressed))
        advanceUntilIdle()

        assertEquals(BuyTradeStep.Entry, viewModel.state.value.step)
        assertNull(viewModel.state.value.pendingPaymentRequest)
        assertNull(viewModel.state.value.errorMessage)
    }

    private fun createViewModel(repository: TradeRepository): BuyTradeViewModel {
        return BuyTradeViewModel(
            createBuyOrderUseCase = CreateBuyOrderUseCase(repository),
            getTradeAvailableCouponsUseCase = GetTradeAvailableCouponsUseCase(repository),
            validateTradeCouponUseCase = ValidateTradeCouponUseCase(repository),
            pollTradeStatusUseCase = PollTradeStatusUseCase(repository),
        )
    }
}

private class FakeBuyTradeRepository(
    private val buyOrderResult: ApiResult<TradeBuyOrder>,
    private val statusResponses: List<ApiResult<TradeStatus>> = emptyList(),
) : TradeRepository {

    private var statusIndex = 0

    override suspend fun createBuyOrder(request: TradeBuyOrderRequest): ApiResult<TradeBuyOrder> = buyOrderResult

    override suspend fun getTradeStatus(orderId: String): ApiResult<TradeStatus> {
        val fallback = ApiResult.Failure(
            NetworkError(
                kind = NetworkErrorKind.Unknown,
                message = "No fake status configured for $orderId",
            )
        )
        val response = statusResponses.getOrElse(statusIndex) { fallback }
        statusIndex += 1
        return response
    }

    override suspend fun getAvailableCoupons(
        orderType: TradeCouponOrderType,
        amount: Double?,
        grams: Double?,
        deliveryFeeInr: Double?,
    ): ApiResult<List<TradeAvailableCoupon>> {
        return ApiResult.Success(
            listOf(
                TradeAvailableCoupon(
                    code = "SAVE50",
                    description = "Save more on buy gold",
                    type = TradeCouponType.CASHBACK,
                    estimatedSaving = "₹50",
                    maxDiscountAmount = null,
                    minOrderValue = null,
                    expiresAt = "",
                    applicableOrderTypes = listOf(TradeCouponOrderType.BUY),
                    isAssigned = true,
                )
            )
        )
    }

    override suspend fun validateCoupon(request: TradeCouponValidationRequest): ApiResult<TradeCouponValidation> {
        return ApiResult.Success(
            TradeCouponValidation(
                code = request.code,
                promoRuleId = "promo-1",
                promotionalDiscount = "50",
                promotionalCashback = "0",
                promotionalExtraGold = "0",
                promotionalDeliveryDiscount = "0",
                netOrderAmount = request.amount?.toString().orEmpty(),
            )
        )
    }

    override suspend fun getLivePrice(): ApiResult<TradeLivePrice> = notUsed()
    override suspend fun createSellOrder(request: TradeSellOrderRequest): ApiResult<TradeSellOrder> = notUsed()
    override suspend fun executeSellOrder(orderId: String, vpaId: String): ApiResult<TradeStatus> = notUsed()
    override suspend fun getTradeInvoice(orderId: String): ApiResult<TradeInvoice> = notUsed()
    override suspend fun getTradeTransactions(page: Int, limit: Int): ApiResult<TradeTransactionsPage> = notUsed()
    override suspend fun getSellAvailability(): ApiResult<TradeSellAvailability> = notUsed()
    override suspend fun getUserVpas(): ApiResult<List<TradeUserVpa>> = notUsed()
    override suspend fun setDefaultVpa(vpaId: String): ApiResult<Unit> = notUsed()
    override suspend fun verifyVpa(vpa: String): ApiResult<TradeVpaVerification> = notUsed()

    private fun <T> notUsed(): ApiResult<T> {
        error("Not used in this test")
    }
}

private fun tradeBuyOrder(
    orderId: String,
    sdkPayloadJson: String?,
): TradeBuyOrder {
    return TradeBuyOrder(
        orderId = orderId,
        status = "PENDING",
        paymentProvider = "JUSPAY",
        paymentProviderOrderId = "provider-$orderId",
        priceLockId = "lock-$orderId",
        priceLockExpiresAt = "2026-05-17T12:00:00.000Z",
        goldQuantityGrams = 0.5,
        goldPricePerGram = 9000.0,
        gstGrossAmount = 15.0,
        gstNetAmount = 500.0,
        gstAmount = 15.0,
        sdkPayloadJson = sdkPayloadJson,
    )
}
