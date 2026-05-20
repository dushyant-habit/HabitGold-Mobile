package com.habit.gold.feature.delivery.presentation

import com.habit.gold.feature.delivery.domain.usecase.CheckAddressServiceabilityUseCase
import com.habit.gold.feature.delivery.domain.usecase.ConfirmDeliveryOrderUseCase
import com.habit.gold.feature.delivery.domain.usecase.CreateDeliveryQuoteUseCase
import com.habit.gold.feature.delivery.domain.usecase.CreateUserAddressUseCase
import com.habit.gold.feature.delivery.domain.usecase.DeleteUserAddressUseCase
import com.habit.gold.feature.delivery.domain.usecase.GetDeliveryOrderDetailsUseCase
import com.habit.gold.feature.delivery.domain.usecase.GetDeliveryProductsUseCase
import com.habit.gold.feature.delivery.domain.usecase.ListUserAddressesUseCase
import com.habit.gold.feature.delivery.domain.usecase.LookupPostalPincodeUseCase
import com.habit.gold.feature.delivery.domain.usecase.SendAddressOtpUseCase
import com.habit.gold.feature.delivery.domain.usecase.UpdateUserAddressUseCase
import com.habit.gold.feature.delivery.domain.usecase.ValidateDeliveryPincodeUseCase
import com.habit.gold.feature.delivery.domain.usecase.VerifyAddressOtpUseCase
import com.habit.gold.feature.delivery.domain.usecase.ListDeliveryOrdersUseCase
import com.habit.gold.feature.delivery.data.PendingDeliveryCheckoutStore
import com.habit.gold.feature.delivery.data.DeliveryCheckoutTelemetry
import com.habit.gold.feature.trade.domain.usecase.GetSellAvailabilityUseCase
import com.habit.gold.feature.trade.domain.usecase.GetTradeAvailableCouponsUseCase
import com.habit.gold.feature.trade.domain.usecase.ValidateTradeCouponUseCase
import com.habit.gold.core.session.SessionStore

data class DeliveryRouteDependencies(
    val getDeliveryProductsUseCase: GetDeliveryProductsUseCase,
    val createDeliveryQuoteUseCase: CreateDeliveryQuoteUseCase,
    val confirmDeliveryOrderUseCase: ConfirmDeliveryOrderUseCase,
    val getDeliveryOrderDetailsUseCase: GetDeliveryOrderDetailsUseCase,
    val pendingDeliveryCheckoutStore: PendingDeliveryCheckoutStore,
    val deliveryCheckoutTelemetry: DeliveryCheckoutTelemetry,
    val getSellAvailabilityUseCase: GetSellAvailabilityUseCase,
    val getTradeAvailableCouponsUseCase: GetTradeAvailableCouponsUseCase,
    val validateTradeCouponUseCase: ValidateTradeCouponUseCase,
    val sessionStore: SessionStore,

    // Address Management
    val listUserAddressesUseCase: ListUserAddressesUseCase,
    val createUserAddressUseCase: CreateUserAddressUseCase,
    val updateUserAddressUseCase: UpdateUserAddressUseCase,
    val deleteUserAddressUseCase: DeleteUserAddressUseCase,
    val sendAddressOtpUseCase: SendAddressOtpUseCase,
    val verifyAddressOtpUseCase: VerifyAddressOtpUseCase,
    val checkAddressServiceabilityUseCase: CheckAddressServiceabilityUseCase,
    val validateDeliveryPincodeUseCase: ValidateDeliveryPincodeUseCase,
    val lookupPostalPincodeUseCase: LookupPostalPincodeUseCase,
    val listDeliveryOrdersUseCase: ListDeliveryOrdersUseCase,
)

