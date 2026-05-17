package com.habit.gold.feature.home.domain.usecase

import com.habit.gold.core.network.ApiResult
import com.habit.gold.feature.home.domain.HomeRepository
import com.habit.gold.feature.home.domain.model.HomeGoldPricePoint
import kotlin.time.TimeSource

/**
 * Mirrors the Android price-history behavior by caching the 1Y series briefly and slicing shorter
 * ranges locally so the Home chart stays responsive while avoiding redundant API calls.
 */
class GetHomePriceHistoryUseCase(
    private val repository: HomeRepository,
    private val nowProvider: () -> Long = { cacheTimeSource.elapsedNow().inWholeMilliseconds },
) {
    private var cachedYearData: List<HomeGoldPricePoint>? = null
    private var lastFetchTimeMillis: Long = 0L

    suspend operator fun invoke(
        range: String,
        latestLiveBuyPrice: Double? = null,
    ): ApiResult<List<HomeGoldPricePoint>> {
        val currentTimeMillis = nowProvider()
        if (cachedYearData == null || (currentTimeMillis - lastFetchTimeMillis) > CACHE_DURATION_MS) {
            when (val result = repository.getPriceHistory(YEAR_RANGE_DAYS)) {
                is ApiResult.Failure -> return result
                is ApiResult.Success -> {
                    cachedYearData = result.value
                    lastFetchTimeMillis = currentTimeMillis
                }
            }
        }

        val yearData = cachedYearData ?: return ApiResult.Success(emptyList())
        val slicedData = slicePricePointsForRange(yearData, range)
        return ApiResult.Success(appendLatestLivePricePoint(slicedData, latestLiveBuyPrice, currentTimeMillis))
    }

    companion object {
        private const val CACHE_DURATION_MS = 5 * 60 * 1000L
        private const val YEAR_RANGE_DAYS = 365
        private val cacheTimeSource = TimeSource.Monotonic.markNow()
    }
}

internal fun slicePricePointsForRange(
    yearlyData: List<HomeGoldPricePoint>,
    selectedTimeRange: String,
): List<HomeGoldPricePoint> {
    val limit = when (selectedTimeRange.trim().uppercase()) {
        "1W" -> 7
        "1M" -> 30
        "3M" -> 90
        "6M" -> 180
        else -> 365
    }
    return if (yearlyData.size > limit) yearlyData.takeLast(limit) else yearlyData
}

internal fun appendLatestLivePricePoint(
    data: List<HomeGoldPricePoint>,
    latestLiveBuyPrice: Double?,
    currentTimeMillis: Long,
): List<HomeGoldPricePoint> {
    val livePrice = latestLiveBuyPrice?.takeIf { it > 0.0 } ?: return data
    val livePoint = HomeGoldPricePoint(
        timestampMillis = (data.lastOrNull()?.timestampMillis ?: currentTimeMillis) + 1L,
        price = livePrice,
    )
    if (data.isEmpty()) return listOf(livePoint)
    return data.dropLast(1) + livePoint
}
