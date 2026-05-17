package com.habit.gold.feature.savings.di

import com.habit.gold.feature.savings.data.remote.SavingsRemoteDataSource
import com.habit.gold.feature.savings.data.repository.SavingsRepositoryImpl
import com.habit.gold.feature.savings.domain.SavingsRepository
import com.habit.gold.feature.savings.domain.usecase.CancelSavingsMandateUseCase
import com.habit.gold.feature.savings.domain.usecase.CreateSavingsMandateSessionUseCase
import com.habit.gold.feature.savings.domain.usecase.GetSavingsExecutionHistoryUseCase
import com.habit.gold.feature.savings.domain.usecase.GetSavingsMandateUseCase
import com.habit.gold.feature.savings.domain.usecase.GetSavingsMandatesUseCase
import com.habit.gold.feature.savings.domain.usecase.PauseSavingsMandateUseCase
import com.habit.gold.feature.savings.domain.usecase.ResumeSavingsMandateUseCase
import com.habit.gold.feature.savings.domain.usecase.UpdateSavingsMandateSessionUseCase
import org.koin.dsl.module

val savingsModule = module {
    single { SavingsRemoteDataSource(get()) }
    single<SavingsRepository> { SavingsRepositoryImpl(get()) }

    single { CreateSavingsMandateSessionUseCase(get()) }
    single { UpdateSavingsMandateSessionUseCase(get()) }
    single { GetSavingsMandatesUseCase(get()) }
    single { GetSavingsMandateUseCase(get()) }
    single { GetSavingsExecutionHistoryUseCase(get()) }
    single { PauseSavingsMandateUseCase(get()) }
    single { ResumeSavingsMandateUseCase(get()) }
    single { CancelSavingsMandateUseCase(get()) }
}
