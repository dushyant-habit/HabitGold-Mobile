package com.habit.gold.feature.history.presentation

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
import com.habit.gold.feature.trade.domain.model.TradeTransactionPreview
import com.habit.gold.feature.trade.domain.model.TradeTransactionsPage
import com.habit.gold.feature.trade.domain.model.TradeUserVpa
import com.habit.gold.feature.trade.domain.model.TradeVpaVerification
import com.habit.gold.feature.trade.domain.usecase.GetTradeTransactionsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModelTest {

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
    fun `default filter keeps success refund and payout processing sell items`() = runTest(dispatcher) {
        val repository = FakeTradeRepository(
            pages = mutableMapOf(
                1 to successPage(
                    page = 1,
                    totalPages = 1,
                    items = listOf(
                        preview(id = "buy-success", type = "BUY", status = "COMPLETED"),
                        preview(id = "sell-refund", type = "SELL", status = "REFUND_COMPLETED"),
                        preview(id = "sell-processing", type = "SELL", status = "PAYOUT_PROCESSING"),
                        preview(id = "sell-failed", type = "SELL", status = "FAILED"),
                    ),
                )
            ),
        )
        val viewModel = createViewModel(repository)

        advanceUntilIdle()

        assertEquals(
            listOf("buy-success", "sell-refund", "sell-processing"),
            viewModel.state.value.visibleTransactions.map { it.id },
        )
    }

    @Test
    fun `refresh keeps previous items visible while request is running`() = runTest(dispatcher) {
        val repository = FakeTradeRepository(
            pages = mutableMapOf(
                1 to successPage(
                    page = 1,
                    totalPages = 1,
                    items = listOf(preview(id = "old-item", status = "COMPLETED")),
                )
            ),
        )
        val viewModel = createViewModel(repository, nowMillis = { 100_000L })
        advanceUntilIdle()

        repository.delayNextPage = true
        repository.pages[1] = successPage(
            page = 1,
            totalPages = 1,
            items = listOf(preview(id = "new-item", status = "COMPLETED")),
        )

        viewModel.onIntent(HistoryIntent.Refresh)
        runCurrent()

        assertTrue(viewModel.state.value.isRefreshing)
        assertFalse(viewModel.state.value.isLoading)
        assertEquals(listOf("old-item"), viewModel.state.value.visibleTransactions.map { it.id })

        advanceUntilIdle()

        assertFalse(viewModel.state.value.isRefreshing)
        assertEquals(listOf("new-item"), viewModel.state.value.visibleTransactions.map { it.id })
    }

    @Test
    fun `load next page appends transactions and updates hasMore`() = runTest(dispatcher) {
        val repository = FakeTradeRepository(
            pages = mutableMapOf(
                1 to successPage(
                    page = 1,
                    totalPages = 2,
                    items = listOf(preview(id = "page-1-item", status = "COMPLETED")),
                ),
                2 to successPage(
                    page = 2,
                    totalPages = 2,
                    items = listOf(preview(id = "page-2-item", status = "COMPLETED")),
                ),
            ),
        )
        val viewModel = createViewModel(repository)
        advanceUntilIdle()

        viewModel.onIntent(HistoryIntent.LoadNextPage)
        advanceUntilIdle()

        assertEquals(
            listOf("page-1-item", "page-2-item"),
            viewModel.state.value.transactions.map { it.id },
        )
        assertFalse(viewModel.state.value.hasMore)
    }

    @Test
    fun `mapping small gold quantities formats them cleanly without exponential notation`() {
        val transaction8 = preview(id = "tx-1", type = "BUY", status = "COMPLETED").copy(goldQuantity = "0.0008")
        val transaction4 = preview(id = "tx-2", type = "BUY", status = "COMPLETED").copy(goldQuantity = "0.0004")
        
        val item8 = mapTradeTransaction(transaction8)
        val item4 = mapTradeTransaction(transaction4)
        
        assertEquals("+0.0008 g", item8.weightLabel)
        assertEquals("+0.0004 g", item4.weightLabel)
    }

    @Test
    fun `mapping gold quantities with suffixes and negative signs formats them correctly`() {
        // Test suffix handling (gm, gms, g, and spaces)
        val tx1 = preview(id = "tx-1", type = "BUY", status = "COMPLETED").copy(goldQuantity = "0.50 gm")
        val tx2 = preview(id = "tx-2", type = "BUY", status = "COMPLETED").copy(goldQuantity = " 0.500 gms ")
        val tx3 = preview(id = "tx-3", type = "DELIVERY", status = "COMPLETED").copy(goldQuantity = "-0.50 gm")
        val tx4 = preview(id = "tx-4", type = "SELL", status = "COMPLETED").copy(goldQuantity = "-0.1234")

        assertEquals("+0.5000 g", mapTradeTransaction(tx1).weightLabel)
        assertEquals("+0.5000 g", mapTradeTransaction(tx2).weightLabel)
        assertEquals("-0.5000 g", mapTradeTransaction(tx3).weightLabel)
        assertEquals("-0.1234 g", mapTradeTransaction(tx4).weightLabel)
    }

    private fun createViewModel(
        repository: FakeTradeRepository,
        nowMillis: () -> Long = { 1_000_000L },
    ): HistoryViewModel {
        return HistoryViewModel(
            getTradeTransactionsUseCase = GetTradeTransactionsUseCase(repository),
            nowMillis = nowMillis,
        )
    }
}

private class FakeTradeRepository(
    val pages: MutableMap<Int, ApiResult<TradeTransactionsPage>>,
    var delayNextPage: Boolean = false,
) : TradeRepository {
    override suspend fun getTradeTransactions(page: Int, limit: Int): ApiResult<TradeTransactionsPage> {
        if (delayNextPage) {
            delayNextPage = false
            delay(1)
        }
        return pages[page]
            ?: ApiResult.Failure(
                NetworkError(
                    kind = NetworkErrorKind.NotFound,
                    message = "Missing page",
                )
            )
    }

    override suspend fun getLivePrice(): ApiResult<TradeLivePrice> = unused()
    override suspend fun createBuyOrder(request: TradeBuyOrderRequest): ApiResult<TradeBuyOrder> = unused()
    override suspend fun createSellOrder(request: TradeSellOrderRequest): ApiResult<TradeSellOrder> = unused()
    override suspend fun executeSellOrder(orderId: String, vpaId: String): ApiResult<TradeStatus> = unused()
    override suspend fun getTradeStatus(orderId: String): ApiResult<TradeStatus> = unused()
    override suspend fun getTradeInvoice(orderId: String): ApiResult<TradeInvoice> = unused()
    override suspend fun getSellAvailability(): ApiResult<TradeSellAvailability> = unused()
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
}

private fun successPage(
    page: Int,
    totalPages: Int,
    items: List<TradeTransactionPreview>,
): ApiResult<TradeTransactionsPage> {
    return ApiResult.Success(
        TradeTransactionsPage(
            data = items,
            total = items.size * totalPages,
            page = page,
            limit = 20,
            totalPages = totalPages,
        )
    )
}

private fun preview(
    id: String,
    type: String = "BUY",
    status: String,
): TradeTransactionPreview {
    return TradeTransactionPreview(
        id = id,
        type = type,
        status = status,
        amount = "2500",
        gstAmount = "75",
        netAmount = "2425",
        goldQuantity = "0.1234",
        goldPrice = "7432",
        createdAt = "2026-05-18T10:30:00.000Z",
        isSip = false,
        sipMandateId = null,
        sipExecutionId = null,
        sipName = null,
        sipFrequency = null,
    )
}

private fun <T> unused(): ApiResult<T> {
    error("Not used in HistoryViewModelTest")
}
