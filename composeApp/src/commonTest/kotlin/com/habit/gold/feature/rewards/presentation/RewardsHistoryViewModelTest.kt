package com.habit.gold.feature.rewards.presentation

import com.habit.gold.core.network.ApiResult
import com.habit.gold.feature.rewards.domain.RewardsRepository
import com.habit.gold.feature.rewards.domain.model.ReferDetails
import com.habit.gold.feature.rewards.domain.model.ReferDetailsReferralCode
import com.habit.gold.feature.rewards.domain.model.RewardHistoryEntry
import com.habit.gold.feature.rewards.domain.model.RewardsFeatureFlags
import com.habit.gold.feature.rewards.domain.model.RewardsMilestonesSummary
import com.habit.gold.feature.rewards.domain.usecase.GetRewardsHistoryUseCase
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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class RewardsHistoryViewModelTest {

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
    fun `history maps cashback and redemption entries`() = runTest(dispatcher) {
        val repository = FakeRewardsHistoryRepository(
            historyResult = ApiResult.Success(
                listOf(
                    RewardHistoryEntry(
                        id = "credit-1",
                        kind = "CREDIT",
                        amountInr = "50",
                        direction = "+",
                        createdAt = "2026-05-18T10:30:00.000Z",
                        source = "CASHBACK",
                        sourceRef = null,
                        debitType = null,
                        orderId = null,
                        withdrawalId = null,
                        expiresAt = null,
                        expired = false,
                        remaining = null,
                    ),
                    RewardHistoryEntry(
                        id = "debit-1",
                        kind = "DEBIT",
                        amountInr = "100",
                        direction = "-",
                        createdAt = "2026-05-17T10:30:00.000Z",
                        source = "REDEMPTION",
                        sourceRef = null,
                        debitType = null,
                        orderId = null,
                        withdrawalId = null,
                        expiresAt = null,
                        expired = false,
                        remaining = null,
                    ),
                ),
            ),
        )

        val viewModel = RewardsHistoryViewModel(GetRewardsHistoryUseCase(repository))
        advanceUntilIdle()

        assertEquals("Gold purchase reward", viewModel.state.value.items[0].title)
        assertEquals("+₹50", viewModel.state.value.items[0].amountLabel)
        assertEquals("Redeemed", viewModel.state.value.items[1].title)
        assertEquals("-₹100", viewModel.state.value.items[1].amountLabel)
    }

    @Test
    fun `refresh keeps previous items while fetching`() = runTest(dispatcher) {
        val repository = FakeRewardsHistoryRepository(
            historyResult = ApiResult.Success(
                listOf(
                    RewardHistoryEntry(
                        id = "old",
                        kind = "CREDIT",
                        amountInr = "25",
                        direction = "+",
                        createdAt = "2026-05-18T10:30:00.000Z",
                        source = "MILESTONE",
                        sourceRef = null,
                        debitType = null,
                        orderId = null,
                        withdrawalId = null,
                        expiresAt = null,
                        expired = false,
                        remaining = null,
                    ),
                ),
            ),
        )

        val viewModel = RewardsHistoryViewModel(GetRewardsHistoryUseCase(repository))
        advanceUntilIdle()

        repository.delayNextHistory = true
        repository.historyResult = ApiResult.Success(
            listOf(
                RewardHistoryEntry(
                    id = "new",
                    kind = "CREDIT",
                    amountInr = "35",
                    direction = "+",
                    createdAt = "2026-05-18T10:30:00.000Z",
                    source = "REFERRAL",
                    sourceRef = null,
                    debitType = null,
                    orderId = null,
                    withdrawalId = null,
                    expiresAt = null,
                    expired = false,
                    remaining = null,
                ),
            ),
        )

        viewModel.refresh()
        runCurrent()

        assertTrue(viewModel.state.value.isRefreshing)
        assertEquals("old", viewModel.state.value.items.first().id)

        advanceUntilIdle()

        assertEquals(false, viewModel.state.value.isRefreshing)
        assertEquals("new", viewModel.state.value.items.first().id)
    }
}

private class FakeRewardsHistoryRepository(
    var historyResult: ApiResult<List<RewardHistoryEntry>>,
) : RewardsRepository {
    var delayNextHistory: Boolean = false

    override suspend fun getRewardsMilestones(): ApiResult<RewardsMilestonesSummary> = unusedRewardsHistory()

    override suspend fun getRewardsHistory(): ApiResult<List<RewardHistoryEntry>> {
        if (delayNextHistory) {
            delayNextHistory = false
            delay(1)
        }
        return historyResult
    }

    override suspend fun getReferDetails(): ApiResult<ReferDetails> = unusedRewardsHistory()

    override suspend fun getUserFeatures(): ApiResult<RewardsFeatureFlags> = unusedRewardsHistory()
}

private fun <T> unusedRewardsHistory(): ApiResult<T> {
    error("Not used in this test")
}
