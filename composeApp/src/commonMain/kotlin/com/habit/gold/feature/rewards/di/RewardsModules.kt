package com.habit.gold.feature.rewards.di

import com.habit.gold.feature.rewards.data.remote.RewardsRemoteDataSource
import com.habit.gold.feature.rewards.data.repository.RewardsRepositoryImpl
import com.habit.gold.feature.rewards.domain.RewardsRepository
import com.habit.gold.feature.rewards.domain.usecase.GetReferDetailsUseCase
import com.habit.gold.feature.rewards.domain.usecase.GetRewardsHistoryUseCase
import com.habit.gold.feature.rewards.domain.usecase.GetRewardsMilestonesUseCase
import com.habit.gold.feature.rewards.domain.usecase.GetRewardsUserFeaturesUseCase
import org.koin.dsl.module

val rewardsModule = module {
    single { RewardsRemoteDataSource(get()) }
    single<RewardsRepository> { RewardsRepositoryImpl(get()) }

    single { GetRewardsMilestonesUseCase(get()) }
    single { GetRewardsHistoryUseCase(get()) }
    single { GetReferDetailsUseCase(get()) }
    single { GetRewardsUserFeaturesUseCase(get()) }
}
