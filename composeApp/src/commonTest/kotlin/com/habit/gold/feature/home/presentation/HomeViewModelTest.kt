package com.habit.gold.feature.home.presentation

import com.habit.gold.core.network.ApiResult
import com.habit.gold.core.network.NetworkError
import com.habit.gold.core.network.NetworkErrorKind
import com.habit.gold.core.storage.InMemoryAppPreferencesStorage
import com.habit.gold.feature.home.domain.HomeRepository
import com.habit.gold.feature.home.domain.model.HomeDashboardSummary
import com.habit.gold.feature.home.domain.model.HomeForceUpdate
import com.habit.gold.feature.home.domain.model.HomeGoldPricePoint
import com.habit.gold.feature.home.domain.model.HomeRecentTransactionPreview
import com.habit.gold.feature.home.domain.model.HomeSipMandate
import com.habit.gold.feature.home.domain.usecase.LoadHomeSummaryUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

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
    fun `load populates summary and clears loading`() = runTest(dispatcher) {
        val repository = FakeViewModelHomeRepository(
            dashboardResult = ApiResult.Success(viewModelDashboard()),
            transactionsResult = ApiResult.Success(
                listOf(
                    HomeRecentTransactionPreview(
                        id = "txn-1",
                        type = "BUY",
                        status = "COMPLETED",
                        amount = "5000",
                        goldQuantity = "0.5000",
                        createdAt = "2026-05-15T08:30:00.000Z",
                        isSip = false,
                        sipName = null,
                        sipFrequency = null,
                    )
                )
            ),
            forceUpdateResult = ApiResult.Success(
                HomeForceUpdate(
                    title = "Update",
                    message = "Required",
                    ctaText = "Update",
                    updateUrl = null,
                    storeUrl = "https://example.com",
                    minVersion = "1.0.17",
                    latestVersion = "1.0.18",
                    isForced = true,
                )
            ),
        )
        val viewModel = HomeViewModel(
            loadHomeSummaryUseCase = LoadHomeSummaryUseCase(repository),
            appPreferencesStorage = InMemoryAppPreferencesStorage(),
        )

        viewModel.onIntent(HomeIntent.Load)
        advanceUntilIdle()

        assertEquals(false, viewModel.state.value.isLoading)
        assertEquals(null, viewModel.state.value.errorMessage)
        assertEquals("txn-1", viewModel.state.value.summary?.recentTransactions?.firstOrNull()?.id)
        assertEquals("Update", viewModel.state.value.summary?.forceUpdate?.title)
    }

    @Test
    fun `load exposes dashboard failure message`() = runTest(dispatcher) {
        val repository = FakeViewModelHomeRepository(
            dashboardResult = ApiResult.Failure(
                NetworkError(
                    kind = NetworkErrorKind.Server,
                    message = "Failed to load portfolio dashboard",
                )
            ),
        )
        val viewModel = HomeViewModel(
            loadHomeSummaryUseCase = LoadHomeSummaryUseCase(repository),
            appPreferencesStorage = InMemoryAppPreferencesStorage(),
        )

        viewModel.onIntent(HomeIntent.Load)
        advanceUntilIdle()

        assertEquals(false, viewModel.state.value.isLoading)
        assertEquals("Failed to load portfolio dashboard", viewModel.state.value.errorMessage)
        assertEquals(null, viewModel.state.value.summary)
    }

    @Test
    fun `refresh keeps summary visible and uses refresh state instead of first load shimmer`() = runTest(dispatcher) {
        val repository = RefreshAwareHomeRepository()
        val viewModel = HomeViewModel(
            loadHomeSummaryUseCase = LoadHomeSummaryUseCase(repository),
            appPreferencesStorage = InMemoryAppPreferencesStorage(),
        )

        viewModel.onIntent(HomeIntent.Load)
        advanceUntilIdle()

        assertFalse(viewModel.state.value.isLoading)
        assertFalse(viewModel.state.value.isRefreshing)
        assertEquals("txn-initial", viewModel.state.value.summary?.recentTransactions?.firstOrNull()?.id)

        viewModel.onIntent(HomeIntent.Refresh)
        runCurrent()

        assertFalse(viewModel.state.value.isLoading)
        assertTrue(viewModel.state.value.isRefreshing)
        assertEquals("txn-initial", viewModel.state.value.summary?.recentTransactions?.firstOrNull()?.id)

        advanceUntilIdle()

        assertFalse(viewModel.state.value.isLoading)
        assertFalse(viewModel.state.value.isRefreshing)
        assertEquals("txn-refreshed", viewModel.state.value.summary?.recentTransactions?.firstOrNull()?.id)
    }

    @Test
    fun `toggle balance visibility persists preference`() = runTest(dispatcher) {
        val preferencesStorage = InMemoryAppPreferencesStorage()
        val viewModel = HomeViewModel(
            loadHomeSummaryUseCase = LoadHomeSummaryUseCase(
                FakeViewModelHomeRepository(
                    dashboardResult = ApiResult.Success(viewModelDashboard()),
                )
            ),
            appPreferencesStorage = preferencesStorage,
        )

        viewModel.onIntent(HomeIntent.Load)
        advanceUntilIdle()
        assertTrue(viewModel.state.value.isBalanceVisible)

        viewModel.onIntent(HomeIntent.ToggleBalanceVisibility)
        advanceUntilIdle()

        assertFalse(viewModel.state.value.isBalanceVisible)
        assertFalse(preferencesStorage.readPreferences().isBalanceVisible)
    }

    @Test
    fun `restore preferences hydrates unread alerts state`() = runTest(dispatcher) {
        val preferencesStorage = InMemoryAppPreferencesStorage().also {
            it.writePreferences(it.readPreferences().copy(hasUnreadAlerts = true))
        }
        val viewModel = HomeViewModel(
            loadHomeSummaryUseCase = LoadHomeSummaryUseCase(
                FakeViewModelHomeRepository(
                    dashboardResult = ApiResult.Success(viewModelDashboard()),
                )
            ),
            appPreferencesStorage = preferencesStorage,
        )

        viewModel.onIntent(HomeIntent.RestorePreferences)
        advanceUntilIdle()

        assertTrue(viewModel.state.value.hasUnreadAlerts)
    }
}

private class FakeViewModelHomeRepository(
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

private fun viewModelDashboard(): HomeDashboardSummary {
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

private class RefreshAwareHomeRepository : HomeRepository {
    private var invocationCount = 0

    override suspend fun getPortfolioDashboard(): ApiResult<HomeDashboardSummary> {
        invocationCount += 1
        if (invocationCount > 1) delay(1)
        return ApiResult.Success(viewModelDashboard())
    }

    override suspend fun getRecentTransactions(
        page: Int,
        limit: Int,
    ): ApiResult<List<HomeRecentTransactionPreview>> {
        val transactionId = if (invocationCount <= 1) "txn-initial" else "txn-refreshed"
        return ApiResult.Success(
            listOf(
                HomeRecentTransactionPreview(
                    id = transactionId,
                    type = "BUY",
                    status = "COMPLETED",
                    amount = "5000",
                    goldQuantity = "0.5000",
                    createdAt = "2026-05-15T08:30:00.000Z",
                    isSip = false,
                    sipName = null,
                    sipFrequency = null,
                )
            )
        )
    }

    override suspend fun getForceUpdate(): ApiResult<HomeForceUpdate?> = ApiResult.Success(null)

    override suspend fun getSipMandates(): ApiResult<List<HomeSipMandate>> = ApiResult.Success(emptyList())

    override suspend fun getPriceHistory(days: Int): ApiResult<List<HomeGoldPricePoint>> {
        return ApiResult.Success(emptyList())
    }
}
