package com.habit.gold.feature.trade.domain

import com.habit.gold.core.network.ApiResult
import com.habit.gold.core.network.NetworkError
import com.habit.gold.core.network.NetworkErrorKind
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
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class TradeLivePriceStoreTest {

    @Test
    fun `logging in fetches live price once and reuses valid cache`() = runTest {
        val clock = MutableNow(Instant.parse("2026-05-17T00:00:00Z"))
        val repository = FakeTradeLivePriceRepository(
            responses = listOf(ApiResult.Success(clock.price(validForSeconds = 120))),
        )
        val store = TradeLivePriceStore(
            tradeRepository = repository,
            scope = backgroundScope,
            now = clock::current,
        )

        store.setLoggedIn(true)
        runCurrent()

        assertEquals(1, repository.livePriceCallCount)
        assertEquals(9000.0, store.state.value.price?.buy)
        assertFalse(store.state.value.isFetching)

        store.refreshPrices(force = false)
        runCurrent()

        assertEquals(1, repository.livePriceCallCount)
    }

    @Test
    fun `logging out clears cached live price state`() = runTest {
        val clock = MutableNow(Instant.parse("2026-05-17T00:00:00Z"))
        val repository = FakeTradeLivePriceRepository(
            responses = listOf(ApiResult.Success(clock.price(validForSeconds = 90))),
        )
        val store = TradeLivePriceStore(
            tradeRepository = repository,
            scope = backgroundScope,
            now = clock::current,
        )

        store.setLoggedIn(true)
        runCurrent()
        store.setLoggedIn(false)
        runCurrent()

        assertNull(store.state.value.price)
        assertFalse(store.state.value.isFetching)
        assertNull(store.state.value.errorMessage)
        assertEquals(0, store.state.value.buyRemainingSeconds)
        assertEquals(0, store.state.value.sellRemainingSeconds)
    }

    @Test
    fun `repository failure exposes retryable error state`() = runTest {
        val repository = FakeTradeLivePriceRepository(
            responses = listOf(
                ApiResult.Failure(
                    NetworkError(
                        kind = NetworkErrorKind.Timeout,
                        message = "Timed out while fetching live prices",
                    ),
                ),
            ),
        )
        val store = TradeLivePriceStore(
            tradeRepository = repository,
            scope = backgroundScope,
        )

        store.setLoggedIn(true)
        runCurrent()

        assertNull(store.state.value.price)
        assertEquals("Timed out while fetching live prices", store.state.value.errorMessage)
        assertFalse(store.state.value.isFetching)
    }

    @Test
    fun `expired price window triggers refresh`() = runTest {
        val clock = MutableNow(Instant.parse("2026-05-17T00:00:00Z"))
        val repository = FakeTradeLivePriceRepository(
            responses = listOf(
                ApiResult.Success(clock.price(validForSeconds = 2)),
                ApiResult.Success(clock.price(validForSeconds = 120, buy = 9100.0, sell = 8900.0)),
            ),
        )
        val store = TradeLivePriceStore(
            tradeRepository = repository,
            scope = backgroundScope,
            now = clock::current,
        )

        store.setLoggedIn(true)
        runCurrent()
        assertEquals(1, repository.livePriceCallCount)

        clock.advanceBy(3.seconds)
        advanceTimeBy(3000)
        runCurrent()

        assertEquals(2, repository.livePriceCallCount)
        assertEquals(9100.0, store.state.value.price?.buy)
        assertEquals(8900.0, store.state.value.price?.sell)
    }
}

private class MutableNow(private var instant: Instant) {
    fun current(): Instant = instant

    fun advanceBy(duration: kotlin.time.Duration) {
        instant = instant + duration
    }

    fun price(
        validForSeconds: Int,
        buy: Double = 9000.0,
        sell: Double = 8800.0,
    ): TradeLivePrice {
        val validUntil = (instant + validForSeconds.seconds).toString()
        return TradeLivePrice(
            buy = buy,
            sell = sell,
            buyRateId = "buy-rate-1",
            sellRateId = "sell-rate-1",
            taxPc = 3.0,
            sourceTimestamp = instant.toString(),
            buyValidUntil = validUntil,
            sellValidUntil = validUntil,
        )
    }
}

private class FakeTradeLivePriceRepository(
    private val responses: List<ApiResult<TradeLivePrice>>,
) : TradeRepository {

    var livePriceCallCount: Int = 0
        private set

    override suspend fun getLivePrice(): ApiResult<TradeLivePrice> {
        val response = responses.getOrElse(livePriceCallCount) { responses.last() }
        livePriceCallCount += 1
        return response
    }

    override suspend fun createBuyOrder(request: TradeBuyOrderRequest): ApiResult<TradeBuyOrder> = unsupported()
    override suspend fun createSellOrder(request: TradeSellOrderRequest): ApiResult<TradeSellOrder> = unsupported()
    override suspend fun executeSellOrder(orderId: String, vpaId: String): ApiResult<TradeStatus> = unsupported()
    override suspend fun getTradeStatus(orderId: String): ApiResult<TradeStatus> = unsupported()
    override suspend fun getTradeInvoice(orderId: String): ApiResult<TradeInvoice> = unsupported()
    override suspend fun getTradeTransactions(page: Int, limit: Int): ApiResult<TradeTransactionsPage> = unsupported()
    override suspend fun getSellAvailability(): ApiResult<TradeSellAvailability> = unsupported()
    override suspend fun getUserVpas(): ApiResult<List<TradeUserVpa>> = unsupported()
    override suspend fun setDefaultVpa(vpaId: String): ApiResult<Unit> = unsupported()
    override suspend fun verifyVpa(vpa: String): ApiResult<TradeVpaVerification> = unsupported()
    override suspend fun getAvailableCoupons(
        orderType: TradeCouponOrderType,
        amount: Double?,
        grams: Double?,
        deliveryFeeInr: Double?,
    ): ApiResult<List<TradeAvailableCoupon>> = unsupported()

    override suspend fun validateCoupon(request: TradeCouponValidationRequest): ApiResult<TradeCouponValidation> = unsupported()

    private fun <T> unsupported(): ApiResult<T> {
        return ApiResult.Failure(
            NetworkError(
                kind = NetworkErrorKind.Unknown,
                message = "Not required for this test.",
            ),
        )
    }
}
