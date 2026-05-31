package com.habit.gold.feature.delivery.di

import com.habit.gold.core.config.AppConfig
import com.habit.gold.core.storage.createPlatformPreferencesStorage
import com.habit.gold.feature.delivery.data.AddressRepositoryImpl
import com.habit.gold.feature.delivery.data.DeliveryCheckoutTelemetry
import com.habit.gold.feature.delivery.data.DeliveryRepositoryImpl
import com.habit.gold.feature.delivery.data.KeyValuePendingDeliveryCheckoutStore
import com.habit.gold.feature.delivery.data.PendingDeliveryCheckoutStore
import com.habit.gold.feature.delivery.data.PostalPincodeRepositoryImpl
import com.habit.gold.feature.delivery.data.PrintDeliveryCheckoutTelemetry
import com.habit.gold.feature.delivery.domain.AddressRepository
import com.habit.gold.feature.delivery.domain.DeliveryRepository
import com.habit.gold.feature.delivery.domain.PostalPincodeRepository
import com.habit.gold.feature.delivery.domain.usecase.CheckAddressServiceabilityUseCase
import com.habit.gold.feature.delivery.domain.usecase.ConfirmDeliveryOrderUseCase
import com.habit.gold.feature.delivery.domain.usecase.CreateDeliveryQuoteUseCase
import com.habit.gold.feature.delivery.domain.usecase.CreateUserAddressUseCase
import com.habit.gold.feature.delivery.domain.usecase.DeleteUserAddressUseCase
import com.habit.gold.feature.delivery.domain.usecase.GetDeliveryOrderDetailsUseCase
import com.habit.gold.feature.delivery.domain.usecase.GetDeliveryProductsUseCase
import com.habit.gold.feature.delivery.domain.usecase.ListDeliveryOrdersUseCase
import com.habit.gold.feature.delivery.domain.usecase.ListUserAddressesUseCase
import com.habit.gold.feature.delivery.domain.usecase.LookupPostalPincodeUseCase
import com.habit.gold.feature.delivery.domain.usecase.SendAddressOtpUseCase
import com.habit.gold.feature.delivery.domain.usecase.UpdateUserAddressUseCase
import com.habit.gold.feature.delivery.domain.usecase.ValidateDeliveryPincodeUseCase
import com.habit.gold.feature.delivery.domain.usecase.VerifyAddressOtpUseCase
import com.habit.gold.feature.delivery.presentation.DeliveryCatalogViewModel
import com.habit.gold.feature.delivery.presentation.DeliveryRouteDependencies
import com.habit.gold.feature.delivery.presentation.DeliveryTrackingViewModel
import org.koin.dsl.module

val deliveryModule = module {
    // --- Data layer ---
    single<DeliveryRepository> { DeliveryRepositoryImpl(get()) }
    single<AddressRepository> { AddressRepositoryImpl(get()) }
    single<PostalPincodeRepository> { PostalPincodeRepositoryImpl(get()) }
    single<PendingDeliveryCheckoutStore> { KeyValuePendingDeliveryCheckoutStore(createPlatformPreferencesStorage()) }
    single<DeliveryCheckoutTelemetry> {
        PrintDeliveryCheckoutTelemetry(enabled = get<AppConfig>().enableNetworkLogs)
    }

    // --- Use cases ---
    single { GetDeliveryProductsUseCase(get()) }
    single { ValidateDeliveryPincodeUseCase(get()) }
    single { CreateDeliveryQuoteUseCase(get()) }
    single { ConfirmDeliveryOrderUseCase(get()) }
    single { GetDeliveryOrderDetailsUseCase(get()) }
    single { ListDeliveryOrdersUseCase(get()) }

    // Address use cases
    single { ListUserAddressesUseCase(get()) }
    single { CreateUserAddressUseCase(get()) }
    single { UpdateUserAddressUseCase(get()) }
    single { DeleteUserAddressUseCase(get()) }
    single { SendAddressOtpUseCase(get()) }
    single { VerifyAddressOtpUseCase(get()) }
    single { CheckAddressServiceabilityUseCase(get()) }

    // Postal use case
    single { LookupPostalPincodeUseCase(get()) }

    // --- ViewModels ---
    factory {
        DeliveryCatalogViewModel(
            getDeliveryProductsUseCase = get(),
            createDeliveryQuoteUseCase = get(),
            confirmDeliveryOrderUseCase = get(),
            pendingDeliveryCheckoutStore = get(),
            deliveryCheckoutTelemetry = get(),
            getSellAvailabilityUseCase = get(),
            getDeliveryOrderDetailsUseCase = get(),
            getTradeAvailableCouponsUseCase = get(),
            validateTradeCouponUseCase = get(),
            sessionStore = get(),
        )
    }

    factory {
        DeliveryRouteDependencies(
            getDeliveryProductsUseCase = get(),
            createDeliveryQuoteUseCase = get(),
            confirmDeliveryOrderUseCase = get(),
            getDeliveryOrderDetailsUseCase = get(),
            pendingDeliveryCheckoutStore = get(),
            deliveryCheckoutTelemetry = get(),
            getSellAvailabilityUseCase = get(),
            getTradeAvailableCouponsUseCase = get(),
            validateTradeCouponUseCase = get(),
            sessionStore = get(),
            listUserAddressesUseCase = get(),
            createUserAddressUseCase = get(),
            updateUserAddressUseCase = get(),
            deleteUserAddressUseCase = get(),
            sendAddressOtpUseCase = get(),
            verifyAddressOtpUseCase = get(),
            checkAddressServiceabilityUseCase = get(),
            listDeliveryOrdersUseCase = get(),
            validateDeliveryPincodeUseCase = get(),
            lookupPostalPincodeUseCase = get(),
        )
    }

    factory {
        DeliveryTrackingViewModel(
            listDeliveryOrdersUseCase = get(),
        )
    }
}
