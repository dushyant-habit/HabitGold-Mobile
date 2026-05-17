package com.habit.gold.feature.trade.presentation

sealed interface TradeDestination {
    data object WithdrawalMode : TradeDestination
    data object GetCoinCatalog : TradeDestination
    data class Buy(
        val frequency: String? = null,
        val amount: String? = null,
        val oneTimeUseGrams: Boolean = false,
    ) : TradeDestination
    data object Sell : TradeDestination
    data class SellPayout(val orderId: String) : TradeDestination
    data class TransactionDetails(val transactionId: String) : TradeDestination
    data class InvoiceViewer(
        val invoiceUrl: String,
        val returnDestination: TradeDestination? = null,
    ) : TradeDestination
    data object VpaList : TradeDestination
    data object HelpCenter : TradeDestination
}
