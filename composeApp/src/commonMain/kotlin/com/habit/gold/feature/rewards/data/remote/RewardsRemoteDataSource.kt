package com.habit.gold.feature.rewards.data.remote

import com.habit.gold.core.network.ApiResult
import com.habit.gold.core.network.safeApiCall
import com.habit.gold.feature.home.data.model.UserFeaturesResponseDto
import com.habit.gold.feature.rewards.data.model.ReferDetailsResponseDto
import com.habit.gold.feature.rewards.data.model.RewardsHistoryResponseDto
import com.habit.gold.feature.rewards.data.model.RewardsMilestonesResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class RewardsRemoteDataSource(
    private val httpClient: HttpClient,
) {
    suspend fun getRewardsMilestones(): ApiResult<RewardsMilestonesResponseDto> = safeApiCall {
        httpClient.get("rewards/milestones").body()
    }

    suspend fun getRewardsHistory(): ApiResult<RewardsHistoryResponseDto> = safeApiCall {
        httpClient.get("rewards/history").body()
    }

    suspend fun getReferDetails(): ApiResult<ReferDetailsResponseDto> = safeApiCall {
        httpClient.get("rewards/refer-details").body()
    }

    suspend fun getUserFeatures(): ApiResult<UserFeaturesResponseDto> = safeApiCall {
        httpClient.get("user/features").body()
    }
}
