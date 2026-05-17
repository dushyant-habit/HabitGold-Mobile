package com.habit.gold.feature.trade.presentation.sell

import com.habit.gold.core.network.ApiResult
import com.habit.gold.core.network.NetworkError
import com.habit.gold.core.network.NetworkErrorKind
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
import com.habit.gold.feature.trade.domain.usecase.CreateSellOrderUseCase
import com.habit.gold.feature.trade.domain.usecase.ExecuteSellOrderUseCase
import com.habit.gold.feature.trade.domain.usecase.GetSellAvailabilityUseCase
import com.habit.gold.feature.trade.domain.usecase.GetTradeStatusUseCase
import com.habit.gold.feature.trade.domain.usecase.GetTradeUserVpasUseCase
import com.habit.gold.feature.trade.domain.usecase.PollTradeStatusUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class SellTradeViewModelTest {

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
    fun `load selects preferred verified default vpa`() = runTest(dispatcher) {
        val repository = FakeSellTradeRepository()
        val viewModel = createViewModel(repository)

        viewModel.onIntent(SellTradeIntent.Load)
        advanceUntilIdle()

        assertEquals(false, viewModel.state.value.isLoading)
        assertEquals("vpa-default", viewModel.state.value.selectedVpaId)
        assertEquals(2, viewModel.state.value.userVpas.size)
    }

    @Test
    fun `continue to payout stores draft request and moves to payout step`() = runTest(dispatcher) {
        val repository = FakeSellTradeRepository()
        val viewModel = createViewModel(repository)

        viewModel.onIntent(
            SellTradeIntent.ContinueToPayout(
                grams = 0.25,
                sellRateId = "sell-rate-1",
                estimatedPayoutAmount = 2400.0,
            ),
        )
        advanceUntilIdle()

        assertEquals(SellTradeStep.PayoutVpa, viewModel.state.value.step)
        assertEquals(0.25, viewModel.state.value.draftRequest?.grams)
        assertEquals("sell-rate-1", viewModel.state.value.draftRequest?.sellRateId)
    }

    @Test
    fun `confirm sell creates order executes and reaches success after polling`() = runTest(dispatcher) {
        val repository = FakeSellTradeRepository(
            statusResponses = listOf(
                ApiResult.Success(TradeStatus(orderId = "sell-order-1", status = "PENDING")),
                ApiResult.Success(TradeStatus(orderId = "sell-order-1", status = "SUCCESS")),
            ),
        )
        val viewModel = createViewModel(repository)

        viewModel.onIntent(
            SellTradeIntent.ContinueToPayout(
                grams = 0.5,
                sellRateId = "sell-rate-1",
                estimatedPayoutAmount = 4500.0,
            ),
        )
        viewModel.onIntent(SellTradeIntent.ConfirmSell("vpa-default"))
        advanceUntilIdle()

        assertEquals(SellTradeStep.Success, viewModel.state.value.step)
        assertEquals("SUCCESS", viewModel.state.value.pollingSnapshot?.status)
        assertEquals("sell-order-1", viewModel.state.value.createdOrder?.orderId)
    }

    @Test
    fun `polling failure falls back to pending with latest known status`() = runTest(dispatcher) {
        val repository = FakeSellTradeRepository(
            statusResponses = listOf(
                ApiResult.Failure(
                    NetworkError(
                        kind = NetworkErrorKind.Timeout,
                        message = "Timed out while polling sell status",
                    ),
                ),
            ),
            latestStatusResult = ApiResult.Success(TradeStatus(orderId = "sell-order-2", status = "PAYOUT_PROCESSING")),
        )
        val viewModel = createViewModel(repository)

        viewModel.onIntent(SellTradeIntent.StartPolling("sell-order-2"))
        advanceUntilIdle()

        assertEquals(SellTradeStep.Pending, viewModel.state.value.step)
        assertEquals("PAYOUT_PROCESSING", viewModel.state.value.pollingSnapshot?.status)
        assertEquals(null, viewModel.state.value.errorMessage)
    }

    private fun createViewModel(repository: TradeRepository): SellTradeViewModel {
        return SellTradeViewModel(
            getSellAvailabilityUseCase = GetSellAvailabilityUseCase(repository),
            getTradeUserVpasUseCase = GetTradeUserVpasUseCase(repository),
            createSellOrderUseCase = CreateSellOrderUseCase(repository),
            executeSellOrderUseCase = ExecuteSellOrderUseCase(repository),
            pollTradeStatusUseCase = PollTradeStatusUseCase(repository),
            getTradeStatusUseCase = GetTradeStatusUseCase(repository),
        )
    }
}

private class FakeSellTradeRepository(
    private val statusResponses: List<ApiResult<TradeStatus>> = emptyList(),
    private val latestStatusResult: ApiResult<TradeStatus>? = null,
) : TradeRepository {

    private var statusIndex = 0

    override suspend fun getSellAvailability(): ApiResult<TradeSellAvailability> {
        return ApiResult.Success(
            TradeSellAvailability(
                totalGoldBalanceGrams = 5.0,
                sellableGoldBalanceGrams = 4.5,
                lockedGoldBalanceGrams = 0.5,
                nextSellableAt = "2026-05-17T12:00:00.000Z",
            ),
        )
    }

    override suspend fun getUserVpas(): ApiResult<List<TradeUserVpa>> {
        return ApiResult.Success(
            listOf(
                TradeUserVpa(
                    id = "vpa-default",
                    address = "primary@upi",
                    holderName = "Primary",
                    isVerified = true,
                    isDefault = true,
                ),
                TradeUserVpa(
                    id = "vpa-secondary",
                    address = "backup@upi",
                    holderName = "Backup",
                    isVerified = true,
                    isDefault = false,
                ),
            ),
        )
    }

    override suspend fun createSellOrder(request: TradeSellOrderRequest): ApiResult<TradeSellOrder> {
        return ApiResult.Success(
            TradeSellOrder(
                orderId = "sell-order-1",
                status = "CREATED",
                priceLockId = "sell-lock-1",
                priceLockExpiresAt = "2026-05-17T12:05:00.000Z",
                goldQuantityGrams = request.grams,
                goldPricePerGram = 9000.0,
                payoutAmount = 4500.0,
                transactionId = "txn-1",
            ),
        )
    }

    override suspend fun executeSellOrder(orderId: String, vpaId: String): ApiResult<TradeStatus> {
        return ApiResult.Success(
            TradeStatus(
                orderId = orderId,
                status = "PROCESSING",
                message = "Execution started for $vpaId",
            ),
        )
    }

    override suspend fun getTradeStatus(orderId: String): ApiResult<TradeStatus> {
        if (statusIndex < statusResponses.size) {
            val response = statusResponses[statusIndex]
            statusIndex += 1
            return response
        }
        latestStatusResult?.let { return it }
        return ApiResult.Failure(
            NetworkError(
                kind = NetworkErrorKind.Unknown,
                message = "No fake trade status configured for $orderId",
            ),
        )
    }

    override suspend fun createBuyOrder(request: TradeBuyOrderRequest): ApiResult<TradeBuyOrder> = notUsed()
    override suspend fun getLivePrice(): ApiResult<TradeLivePrice> = notUsed()
    override suspend fun getAvailableCoupons(
        orderType: TradeCouponOrderType,
        amount: Double?,
        grams: Double?,
        deliveryFeeInr: Double?,
    ): ApiResult<List<TradeAvailableCoupon>> = notUsed()
    override suspend fun validateCoupon(request: TradeCouponValidationRequest): ApiResult<TradeCouponValidation> = notUsed()
    override suspend fun getTradeInvoice(orderId: String): ApiResult<TradeInvoice> = notUsed()
    override suspend fun getTradeTransactions(page: Int, limit: Int): ApiResult<TradeTransactionsPage> = notUsed()
    override suspend fun setDefaultVpa(vpaId: String): ApiResult<Unit> = notUsed()
    override suspend fun verifyVpa(vpa: String): ApiResult<TradeVpaVerification> = notUsed()

    private fun <T> notUsed(): ApiResult<T> {
        error("Not used in this test")
    }
}
