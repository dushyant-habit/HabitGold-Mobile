package com.habit.gold.feature.rewards.presentation

import com.habit.gold.core.network.ApiResult
import com.habit.gold.core.network.NetworkError
import com.habit.gold.core.network.NetworkErrorKind
import com.habit.gold.feature.rewards.domain.RewardsRepository
import com.habit.gold.feature.rewards.domain.model.ReferDetails
import com.habit.gold.feature.rewards.domain.model.ReferDetailsReferralCode
import com.habit.gold.feature.rewards.domain.model.RewardHistoryEntry
import com.habit.gold.feature.rewards.domain.model.RewardMilestone
import com.habit.gold.feature.rewards.domain.model.RewardsFeatureFlags
import com.habit.gold.feature.rewards.domain.model.RewardsMilestonesSummary
import com.habit.gold.feature.rewards.domain.usecase.GetRewardsMilestonesUseCase
import com.habit.gold.feature.rewards.domain.usecase.GetRewardsUserFeaturesUseCase
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
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class RewardsViewModelTest {

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
    fun `load maps post journey ui and feature flag`() = runTest(dispatcher) {
        val repository = FakeRewardsRepository(
            milestonesResult = ApiResult.Success(
                RewardsMilestonesSummary(
                    totalPaidGoldGrams = "0.6500",
                    milestonesActive = true,
                    milestones = listOf(
                        RewardMilestone(
                            key = "m1",
                            name = "Genesis",
                            thresholdGrams = "0.5g",
                            rewardInr = "50",
                            status = "COMPLETED",
                        ),
                    ),
                    ongoingPercent = "0.5",
                    totalEarnedInr = "350",
                    goldCashbackInr = "200",
                    referralCashbackInr = "150",
                    redeemableInr = "120",
                    redeemedInr = "230",
                    boosterActive = false,
                ),
            ),
            featuresResult = ApiResult.Success(RewardsFeatureFlags(rewardsActive = true)),
        )

        val viewModel = createViewModel(repository)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertTrue(state.rewardsFeatureActive)
        assertEquals(true, state.homeUi?.usePostJourneyHeader)
        assertEquals("₹350", state.homeUi?.totalEarnedDisplay)
        assertEquals(RewardsLevelState.Completed, state.homeUi?.rows?.firstOrNull()?.state)
    }

    @Test
    fun `refresh keeps previous content visible while request runs`() = runTest(dispatcher) {
        val repository = FakeRewardsRepository(
            milestonesResult = ApiResult.Success(successSummary(totalEarnedInr = "100")),
            featuresResult = ApiResult.Success(RewardsFeatureFlags(rewardsActive = true)),
        )
        val viewModel = createViewModel(repository, nowMillis = { 100_000L })
        advanceUntilIdle()

        repository.delayNextMilestones = true
        repository.milestonesResult = ApiResult.Success(successSummary(totalEarnedInr = "500"))

        viewModel.onIntent(RewardsHomeIntent.Refresh)
        runCurrent()

        assertTrue(viewModel.state.value.isRefreshing)
        assertEquals("₹100", viewModel.state.value.homeUi?.totalEarnedDisplay)

        advanceUntilIdle()

        assertFalse(viewModel.state.value.isRefreshing)
        assertEquals("₹500", viewModel.state.value.homeUi?.totalEarnedDisplay)
    }

    private fun createViewModel(
        repository: FakeRewardsRepository,
        nowMillis: () -> Long = { 1_000_000L },
    ): RewardsViewModel {
        return RewardsViewModel(
            getRewardsMilestonesUseCase = GetRewardsMilestonesUseCase(repository),
            getRewardsUserFeaturesUseCase = GetRewardsUserFeaturesUseCase(repository),
            nowMillis = nowMillis,
        )
    }
}

private class FakeRewardsRepository(
    var milestonesResult: ApiResult<RewardsMilestonesSummary>,
    private val featuresResult: ApiResult<RewardsFeatureFlags>,
) : RewardsRepository {
    var delayNextMilestones: Boolean = false

    override suspend fun getRewardsMilestones(): ApiResult<RewardsMilestonesSummary> {
        if (delayNextMilestones) {
            delayNextMilestones = false
            delay(1)
        }
        return milestonesResult
    }

    override suspend fun getRewardsHistory(): ApiResult<List<RewardHistoryEntry>> = unusedRewards()

    override suspend fun getReferDetails(): ApiResult<ReferDetails> = unusedRewards()

    override suspend fun getUserFeatures(): ApiResult<RewardsFeatureFlags> = featuresResult
}

private fun successSummary(totalEarnedInr: String): RewardsMilestonesSummary {
    return RewardsMilestonesSummary(
        totalPaidGoldGrams = "0.2500",
        milestonesActive = true,
        milestones = listOf(
            RewardMilestone(
                key = "m1",
                name = "Genesis",
                thresholdGrams = "0.5g",
                rewardInr = "50",
                status = "IN_PROGRESS",
            ),
            RewardMilestone(
                key = "m2",
                name = "Accumulator",
                thresholdGrams = "1g",
                rewardInr = "100",
                status = "LOCKED",
            ),
        ),
        ongoingPercent = "0.5",
        totalEarnedInr = totalEarnedInr,
        goldCashbackInr = "60",
        referralCashbackInr = "40",
        redeemableInr = "75",
        redeemedInr = "25",
        boosterActive = false,
    )
}

private fun <T> unusedRewards(): ApiResult<T> {
    error("Not used in this test")
}
