package com.habit.gold.feature.profile.di

import com.habit.gold.feature.profile.data.remote.ProfileRemoteDataSource
import com.habit.gold.feature.profile.data.repository.ProfileRepositoryImpl
import com.habit.gold.feature.profile.domain.ProfileRepository
import com.habit.gold.feature.profile.domain.usecase.GetProfileSummaryUseCase
import com.habit.gold.feature.profile.domain.usecase.LogoutProfileUseCase
import com.habit.gold.feature.profile.domain.usecase.RequestDeleteAccountUseCase
import com.habit.gold.feature.profile.domain.usecase.UpdateProfileUseCase
import com.habit.gold.feature.profile.domain.usecase.VerifyProfileKycUseCase
import org.koin.dsl.module

val profileModule = module {
    single { ProfileRemoteDataSource(get()) }
    single<ProfileRepository> { ProfileRepositoryImpl(get(), get(), get()) }
    single { GetProfileSummaryUseCase(get()) }
    single { UpdateProfileUseCase(get()) }
    single { VerifyProfileKycUseCase(get()) }
    single { LogoutProfileUseCase(get()) }
    single { RequestDeleteAccountUseCase(get()) }
}
