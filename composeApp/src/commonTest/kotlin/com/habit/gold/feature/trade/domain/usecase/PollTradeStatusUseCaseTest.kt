package com.habit.gold.feature.trade.domain.usecase

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
import com.habit.gold.feature.trade.domain.model.TradePollingOutcome
import com.habit.gold.feature.trade.domain.model.TradePollingPolicies
import com.habit.gold.feature.trade.domain.model.TradeSellAvailability
import com.habit.gold.feature.trade.domain.model.TradeSellOrder
import com.habit.gold.feature.trade.domain.model.TradeSellOrderRequest
import com.habit.gold.feature.trade.domain.model.TradeStatus
import com.habit.gold.feature.trade.domain.model.TradeTransactionsPage
import com.habit.gold.feature.trade.domain.model.TradeUserVpa
import com.habit.gold.feature.trade.domain.model.TradeVpaVerification
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlinx.coroutines.test.runTest

class PollTradeStatusUseCaseTest {

    @Test
    fun `returns success when trade status reaches completed within policy window`() = runTest {
        val useCase = PollTradeStatusUseCase(
            repository = FakeTradeRepository(
                statusResponses = listOf(
                    ApiResult.Success(TradeStatus(orderId = "order-1", status = "pending")),
                    ApiResult.Success(TradeStatus(orderId = "order-1", status = "success")),
                )
            )
        )

        val result = useCase(orderId = "order-1", policy = TradePollingPolicies.buy())
        val outcome = assertIs<ApiResult.Success<*>>(result).value
        val success = assertIs<TradePollingOutcome.Success>(outcome)

        assertEquals("SUCCESS", success.snapshot.status)
        assertEquals(2, success.snapshot.attempt)
    }

    @Test
    fun `returns pending when sell polling times out without terminal status`() = runTest {
        val useCase = PollTradeStatusUseCase(
            repository = FakeTradeRepository(
                statusResponses = listOf(
                    ApiResult.Success(TradeStatus(orderId = "order-2", status = "PENDING")),
                    ApiResult.Success(TradeStatus(orderId = "order-2", status = "PAYOUT_PROCESSING")),
                )
            )
        )

        val result = useCase(orderId = "order-2", policy = TradePollingPolicies.sell())
        val outcome = assertIs<ApiResult.Success<*>>(result).value
        val pending = assertIs<TradePollingOutcome.Pending>(outcome)

        assertEquals("PAYOUT_PROCESSING", pending.snapshot?.status)
        assertEquals(2, pending.snapshot?.attempt)
    }

    @Test
    fun `returns repository failure when final polling attempt still errors`() = runTest {
        val useCase = PollTradeStatusUseCase(
            repository = FakeTradeRepository(
                statusResponses = listOf(
                    ApiResult.Failure(
                        NetworkError(kind = NetworkErrorKind.Timeout, message = "Timed out", isRetryable = true)
                    ),
                    ApiResult.Failure(
                        NetworkError(kind = NetworkErrorKind.Server, message = "Server down")
                    ),
                )
            )
        )

        val result = useCase(orderId = "order-3", policy = TradePollingPolicies.sell())
        val failure = assertIs<ApiResult.Failure>(result)

        assertEquals("Server down", failure.error.message)
    }

    @Test
    fun `returns failure outcome when trade status is unexpected`() = runTest {
        val useCase = PollTradeStatusUseCase(
            repository = FakeTradeRepository(
                statusResponses = listOf(
                    ApiResult.Success(TradeStatus(orderId = "order-4", status = "CREATED")),
                )
            )
        )

        val result = useCase(orderId = "order-4", policy = TradePollingPolicies.buy())
        val outcome = assertIs<ApiResult.Success<*>>(result).value
        val failure = assertIs<TradePollingOutcome.Failure>(outcome)

        assertEquals("CREATED", failure.snapshot.status)
        assertEquals("Unexpected order status: CREATED", failure.message)
    }
}

private class FakeTradeRepository(
    private val statusResponses: List<ApiResult<TradeStatus>>,
) : TradeRepository {

    private var statusIndex = 0

    override suspend fun getLivePrice(): ApiResult<TradeLivePrice> {
        error("Not used in this test")
    }

    override suspend fun getTradeStatus(orderId: String): ApiResult<TradeStatus> {
        val response = statusResponses.getOrElse(statusIndex) { statusResponses.last() }
        statusIndex += 1
        return response
    }

    override suspend fun createBuyOrder(request: TradeBuyOrderRequest): ApiResult<TradeBuyOrder> {
        error("Not used in this test")
    }

    override suspend fun createSellOrder(request: TradeSellOrderRequest): ApiResult<TradeSellOrder> {
        error("Not used in this test")
    }

    override suspend fun executeSellOrder(orderId: String, vpaId: String): ApiResult<TradeStatus> {
        error("Not used in this test")
    }

    override suspend fun getTradeInvoice(orderId: String): ApiResult<TradeInvoice> {
        error("Not used in this test")
    }

    override suspend fun getTradeTransactions(page: Int, limit: Int): ApiResult<TradeTransactionsPage> {
        error("Not used in this test")
    }

    override suspend fun getSellAvailability(): ApiResult<TradeSellAvailability> {
        error("Not used in this test")
    }

    override suspend fun getUserVpas(): ApiResult<List<TradeUserVpa>> {
        error("Not used in this test")
    }

    override suspend fun setDefaultVpa(vpaId: String): ApiResult<Unit> {
        error("Not used in this test")
    }

    override suspend fun verifyVpa(vpa: String): ApiResult<TradeVpaVerification> {
        error("Not used in this test")
    }

    override suspend fun getAvailableCoupons(
        orderType: TradeCouponOrderType,
        amount: Double?,
        grams: Double?,
        deliveryFeeInr: Double?,
    ): ApiResult<List<TradeAvailableCoupon>> {
        error("Not used in this test")
    }

    override suspend fun validateCoupon(request: TradeCouponValidationRequest): ApiResult<TradeCouponValidation> {
        error("Not used in this test")
    }
}
