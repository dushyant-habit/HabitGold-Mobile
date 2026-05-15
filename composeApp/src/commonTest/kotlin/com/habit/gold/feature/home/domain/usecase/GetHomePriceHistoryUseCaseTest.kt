package com.habit.gold.feature.home.domain.usecase

import com.habit.gold.core.network.ApiResult
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

class GetHomePriceHistoryUseCaseTest {

    @Test
    fun `caches one year data and slices smaller ranges locally`() = runTest {
        val repository = FakePriceHistoryRepository()
        var currentTime = 1_000L
        val useCase = GetHomePriceHistoryUseCase(
            repository = repository,
            nowProvider = { currentTime },
        )

        val first = assertIs<ApiResult.Success<List<HomeGoldPricePoint>>>(useCase("1M", latestLiveBuyPrice = null)).value
        currentTime += 1_000L
        val second = assertIs<ApiResult.Success<List<HomeGoldPricePoint>>>(useCase("1W", latestLiveBuyPrice = null)).value

        assertEquals(1, repository.priceHistoryCalls)
        assertEquals(30, first.size)
        assertEquals(7, second.size)
    }

    @Test
    fun `replaces trailing point with current live price for chart display`() = runTest {
        val repository = FakePriceHistoryRepository()
        val useCase = GetHomePriceHistoryUseCase(
            repository = repository,
            nowProvider = { 9_999L },
        )

        val result = assertIs<ApiResult.Success<List<HomeGoldPricePoint>>>(useCase("1W", latestLiveBuyPrice = 9999.0)).value

        assertEquals(7, result.size)
        assertEquals(9999.0, result.last().price)
        assertEquals(366L, result.last().timestampMillis)
    }
}

private class FakePriceHistoryRepository : HomeRepository {
    var priceHistoryCalls: Int = 0

    override suspend fun getPortfolioDashboard(): ApiResult<HomeDashboardSummary> {
        error("Not used in this test")
    }

    override suspend fun getRecentTransactions(
        page: Int,
        limit: Int,
    ): ApiResult<List<HomeRecentTransactionPreview>> {
        error("Not used in this test")
    }

    override suspend fun getForceUpdate(): ApiResult<HomeForceUpdate?> {
        error("Not used in this test")
    }

    override suspend fun getSipMandates(): ApiResult<List<HomeSipMandate>> {
        error("Not used in this test")
    }

    override suspend fun getPriceHistory(days: Int): ApiResult<List<HomeGoldPricePoint>> {
        priceHistoryCalls += 1
        return ApiResult.Success(
            (1..365).map { index ->
                HomeGoldPricePoint(
                    timestampMillis = index.toLong(),
                    price = 8000.0 + index,
                )
            },
        )
    }
}
