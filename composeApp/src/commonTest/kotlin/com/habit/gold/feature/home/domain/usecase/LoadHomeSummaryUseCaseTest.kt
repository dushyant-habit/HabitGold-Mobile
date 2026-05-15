package com.habit.gold.feature.home.domain.usecase

import com.habit.gold.core.network.ApiResult
import com.habit.gold.core.network.NetworkError
import com.habit.gold.core.network.NetworkErrorKind
import com.habit.gold.feature.home.domain.HomeRepository
import com.habit.gold.feature.home.domain.model.HomeDashboardSummary
import com.habit.gold.feature.home.domain.model.HomeForceUpdate
import com.habit.gold.feature.home.domain.model.HomeGoldPricePoint
import com.habit.gold.feature.home.domain.model.HomeRecentTransactionPreview
import com.habit.gold.feature.home.domain.model.HomeSipMandate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlinx.coroutines.test.runTest

class LoadHomeSummaryUseCaseTest {

    @Test
    fun `returns dashboard even when transactions and features fail softly`() = runTest {
        val useCase = LoadHomeSummaryUseCase(
            repository = FakeHomeRepository(
                dashboardResult = ApiResult.Success(sampleDashboard()),
                transactionsResult = ApiResult.Failure(networkError("Transactions failed")),
                forceUpdateResult = ApiResult.Failure(networkError("Features failed")),
            ),
        )

        val result = useCase()
        val summary = assertIs<ApiResult.Success<*>>(result).value as com.habit.gold.feature.home.domain.model.HomeSummary

        assertEquals(sampleDashboard(), summary.dashboard)
        assertEquals(emptyList(), summary.recentTransactions)
        assertEquals(null, summary.forceUpdate)
        assertEquals(emptyList(), summary.sipMandates)
    }

    @Test
    fun `fails when dashboard request fails`() = runTest {
        val useCase = LoadHomeSummaryUseCase(
            repository = FakeHomeRepository(
                dashboardResult = ApiResult.Failure(networkError("Dashboard failed")),
            ),
        )

        val result = useCase()
        val failure = assertIs<ApiResult.Failure>(result)

        assertEquals("Dashboard failed", failure.error.message)
    }
}

private class FakeHomeRepository(
    private val dashboardResult: ApiResult<HomeDashboardSummary>,
    private val transactionsResult: ApiResult<List<HomeRecentTransactionPreview>> = ApiResult.Success(emptyList()),
    private val forceUpdateResult: ApiResult<HomeForceUpdate?> = ApiResult.Success(null),
    private val sipMandatesResult: ApiResult<List<HomeSipMandate>> = ApiResult.Success(emptyList()),
) : HomeRepository {
    override suspend fun getPortfolioDashboard(): ApiResult<HomeDashboardSummary> = dashboardResult

    override suspend fun getRecentTransactions(
        page: Int,
        limit: Int,
    ): ApiResult<List<HomeRecentTransactionPreview>> = transactionsResult

    override suspend fun getForceUpdate(): ApiResult<HomeForceUpdate?> = forceUpdateResult

    override suspend fun getSipMandates(): ApiResult<List<HomeSipMandate>> = sipMandatesResult

    override suspend fun getPriceHistory(days: Int): ApiResult<List<HomeGoldPricePoint>> {
        return ApiResult.Success(emptyList())
    }
}

private fun sampleDashboard(): HomeDashboardSummary {
    return HomeDashboardSummary(
        totalGoldBalanceGrams = 1.25,
        investedValue = 10000.0,
        rewardsApplied = 100.0,
        gstPaid = 27.0,
        totalCost = 10200.0,
        averageBuyPricePerGram = 8000.0,
        currentValue = 11250.0,
        liveBuyPricePerGram = 9000.0,
        liveSellPricePerGram = 8800.0,
        finalPayoutAmount = 11000.0,
        buySellPriceDifference = 200.0,
    )
}

private fun networkError(message: String): NetworkError {
    return NetworkError(
        kind = NetworkErrorKind.Server,
        message = message,
    )
}
