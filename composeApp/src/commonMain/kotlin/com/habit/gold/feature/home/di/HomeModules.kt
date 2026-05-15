package com.habit.gold.feature.home.di

import com.habit.gold.feature.home.data.remote.HomeRemoteDataSource
import com.habit.gold.feature.home.data.repository.HomeRepositoryImpl
import com.habit.gold.feature.home.domain.HomeRepository
import com.habit.gold.feature.home.domain.usecase.GetHomePriceHistoryUseCase
import com.habit.gold.feature.home.domain.usecase.LoadHomeSummaryUseCase
import com.habit.gold.feature.home.presentation.HomeViewModel
import org.koin.dsl.module

val homeModule = module {
    single { HomeRemoteDataSource(get()) }
    single<HomeRepository> { HomeRepositoryImpl(get()) }
    single { LoadHomeSummaryUseCase(get()) }
    single { GetHomePriceHistoryUseCase(get()) }
    factory { HomeViewModel(get()) }
}
