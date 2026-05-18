package com.habit.gold.feature.alerts.di

import com.habit.gold.feature.alerts.data.local.AlertsStorage
import com.habit.gold.feature.alerts.data.local.JsonAlertsStorage
import com.habit.gold.feature.alerts.data.repository.AlertsRepositoryImpl
import com.habit.gold.feature.alerts.domain.repository.AlertsRepository
import com.habit.gold.feature.alerts.domain.usecase.GetAlertsUseCase
import com.habit.gold.feature.alerts.domain.usecase.MarkAllAlertsReadUseCase
import com.habit.gold.core.storage.createPlatformPreferencesStorage
import org.koin.dsl.module

val alertsModule = module {
    single<AlertsStorage> { JsonAlertsStorage(createPlatformPreferencesStorage()) }
    single<AlertsRepository> { AlertsRepositoryImpl(get(), get()) }
    single { GetAlertsUseCase(get()) }
    single { MarkAllAlertsReadUseCase(get()) }
}
