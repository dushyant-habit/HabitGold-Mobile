package com.habit.gold.feature.auth.di

import com.habit.gold.feature.auth.data.remote.AuthRemoteDataSource
import com.habit.gold.feature.auth.data.repository.AuthRepositoryImpl
import com.habit.gold.feature.auth.data.repository.AuthTokenRefreshHandler
import com.habit.gold.feature.auth.domain.AuthRepository
import com.habit.gold.feature.auth.domain.usecase.RequestOtpUseCase
import com.habit.gold.feature.auth.domain.usecase.SubmitBasicDetailsUseCase
import com.habit.gold.feature.auth.domain.usecase.VerifyOtpUseCase
import com.habit.gold.core.network.TokenRefreshHandler
import org.koin.dsl.module

val authModule = module {
    single<TokenRefreshHandler> { AuthTokenRefreshHandler(get(), get(), get()) }
    single { AuthRemoteDataSource(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single { RequestOtpUseCase(get()) }
    single { VerifyOtpUseCase(get()) }
    single { SubmitBasicDetailsUseCase(get()) }
}
