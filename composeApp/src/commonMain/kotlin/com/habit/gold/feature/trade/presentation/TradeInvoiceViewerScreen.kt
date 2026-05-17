package com.habit.gold.feature.trade.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun TradeInvoiceViewerScreen(
    invoiceUrl: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
)
