package com.habit.gold.feature.trade.di

import com.habit.gold.feature.trade.data.remote.TradeRemoteDataSource
import com.habit.gold.feature.trade.data.repository.TradeRepositoryImpl
import com.habit.gold.feature.trade.domain.TradeRepository
import com.habit.gold.feature.trade.domain.TradeLivePriceStore
import com.habit.gold.feature.trade.domain.usecase.CreateBuyOrderUseCase
import com.habit.gold.feature.trade.domain.usecase.CreateSellOrderUseCase
import com.habit.gold.feature.trade.domain.usecase.ExecuteSellOrderUseCase
import com.habit.gold.feature.trade.domain.usecase.GetSellAvailabilityUseCase
import com.habit.gold.feature.trade.domain.usecase.GetTradeAvailableCouponsUseCase
import com.habit.gold.feature.trade.domain.usecase.GetTradeInvoiceUseCase
import com.habit.gold.feature.trade.domain.usecase.GetTradeStatusUseCase
import com.habit.gold.feature.trade.domain.usecase.GetTradeTransactionsUseCase
import com.habit.gold.feature.trade.domain.usecase.GetTradeUserVpasUseCase
import com.habit.gold.feature.trade.domain.usecase.PollTradeStatusUseCase
import com.habit.gold.feature.trade.domain.usecase.SetDefaultTradeVpaUseCase
import com.habit.gold.feature.trade.domain.usecase.ValidateTradeCouponUseCase
import com.habit.gold.feature.trade.domain.usecase.VerifyTradeVpaUseCase
import org.koin.dsl.module

val tradeModule = module {
    single { TradeRemoteDataSource(get()) }
    single<TradeRepository> { TradeRepositoryImpl(get()) }
    single { TradeLivePriceStore(get()) }

    single { CreateBuyOrderUseCase(get()) }
    single { CreateSellOrderUseCase(get()) }
    single { ExecuteSellOrderUseCase(get()) }
    single { GetSellAvailabilityUseCase(get()) }
    single { GetTradeAvailableCouponsUseCase(get()) }
    single { GetTradeInvoiceUseCase(get()) }
    single { GetTradeStatusUseCase(get()) }
    single { GetTradeTransactionsUseCase(get()) }
    single { GetTradeUserVpasUseCase(get()) }
    single { PollTradeStatusUseCase(get()) }
    single { SetDefaultTradeVpaUseCase(get()) }
    single { ValidateTradeCouponUseCase(get()) }
    single { VerifyTradeVpaUseCase(get()) }
}
