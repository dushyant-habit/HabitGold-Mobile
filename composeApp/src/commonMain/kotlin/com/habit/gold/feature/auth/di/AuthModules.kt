package com.habit.gold.feature.auth.di

import com.habit.gold.feature.auth.data.remote.AuthRemoteDataSource
import com.habit.gold.feature.auth.data.repository.AuthRepositoryImpl
import com.habit.gold.feature.auth.domain.AuthRepository
import org.koin.dsl.module

val authModule = module {
    single { AuthRemoteDataSource(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
}
