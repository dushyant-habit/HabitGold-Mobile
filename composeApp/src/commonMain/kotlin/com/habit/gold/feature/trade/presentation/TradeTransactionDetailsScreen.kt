package com.habit.gold.feature.trade.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habit.gold.core.network.ApiResult
import com.habit.gold.core.designsystem.AppPrimaryButton
import com.habit.gold.feature.trade.domain.model.TradeTransactionPreview
import com.habit.gold.feature.trade.domain.usecase.GetTradeInvoiceUseCase
import com.habit.gold.feature.trade.domain.usecase.GetTradeTransactionsUseCase
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.common_retry
import habitgoldmobile.composeapp.generated.resources.trade_transaction_details_amount
import habitgoldmobile.composeapp.generated.resources.trade_transaction_details_created_at
import habitgoldmobile.composeapp.generated.resources.trade_transaction_details_gold_price
import habitgoldmobile.composeapp.generated.resources.trade_transaction_details_gold_quantity
import habitgoldmobile.composeapp.generated.resources.trade_transaction_details_gst
import habitgoldmobile.composeapp.generated.resources.trade_transaction_details_load_failed
import habitgoldmobile.composeapp.generated.resources.trade_transaction_details_net_amount
import habitgoldmobile.composeapp.generated.resources.trade_transaction_details_not_found
import habitgoldmobile.composeapp.generated.resources.trade_transaction_details_order_id
import habitgoldmobile.composeapp.generated.resources.trade_transaction_details_order_title
import habitgoldmobile.composeapp.generated.resources.trade_transaction_details_sip_frequency
import habitgoldmobile.composeapp.generated.resources.trade_transaction_details_sip_name
import habitgoldmobile.composeapp.generated.resources.trade_transaction_details_status
import habitgoldmobile.composeapp.generated.resources.trade_transaction_details_type
import habitgoldmobile.composeapp.generated.resources.trade_transaction_details_view_invoice
import habitgoldmobile.composeapp.generated.resources.trade_invoice_viewer_invalid_url
import org.jetbrains.compose.resources.stringResource

@Composable
fun TradeTransactionDetailsScreen(
    transactionId: String,
    getTradeTransactionsUseCase: GetTradeTransactionsUseCase,
    getTradeInvoiceUseCase: GetTradeInvoiceUseCase,
    onBackClick: () -> Unit,
    onOpenInvoice: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val transactionNotFoundMessage = stringResource(Res.string.trade_transaction_details_not_found)
    val transactionLoadFailedMessage = stringResource(Res.string.trade_transaction_details_load_failed)
    val invalidInvoiceMessage = stringResource(Res.string.trade_invoice_viewer_invalid_url)
    var reloadToken by remember(transactionId) { mutableStateOf(0) }
    var screenState by remember(transactionId) {
        mutableStateOf<TradeTransactionDetailsUiState>(TradeTransactionDetailsUiState.Loading)
    }
    var isFetchingInvoice by remember(transactionId) { mutableStateOf(false) }
    var invoiceErrorMessage by remember(transactionId) { mutableStateOf<String?>(null) }

    suspend fun loadTransaction() {
        screenState = TradeTransactionDetailsUiState.Loading
        val loadedTransaction = findTradeTransaction(transactionId, getTradeTransactionsUseCase)
        screenState = when (loadedTransaction) {
            is TradeTransactionLookupResult.Found -> TradeTransactionDetailsUiState.Content(loadedTransaction.transaction)
            TradeTransactionLookupResult.NotFound -> TradeTransactionDetailsUiState.Error(transactionNotFoundMessage)
            is TradeTransactionLookupResult.Failure -> TradeTransactionDetailsUiState.Error(
                loadedTransaction.message.ifBlank { transactionLoadFailedMessage },
            )
        }
    }

    LaunchedEffect(transactionId, reloadToken) {
        loadTransaction()
    }

    TradeChildScaffold(
        title = stringResource(Res.string.trade_transaction_details_order_title),
        onBackClick = onBackClick,
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            when (val state = screenState) {
                TradeTransactionDetailsUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is TradeTransactionDetailsUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = state.message,
                            color = TradeMutedText,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                        )
                        AppPrimaryButton(
                            label = stringResource(Res.string.common_retry),
                            onClick = { reloadToken += 1 },
                            modifier = Modifier.padding(top = 16.dp),
                        )
                    }
                }
                is TradeTransactionDetailsUiState.Content -> {
                    val transaction = state.transaction
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, TradeSectionBorder),
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                TradeDetailRow(
                                    label = stringResource(Res.string.trade_transaction_details_order_id),
                                    value = transaction.id,
                                )
                                TradeDetailRow(
                                    label = stringResource(Res.string.trade_transaction_details_type),
                                    value = transaction.type,
                                )
                                TradeDetailRow(
                                    label = stringResource(Res.string.trade_transaction_details_status),
                                    value = transaction.status,
                                )
                                TradeDetailRow(
                                    label = stringResource(Res.string.trade_transaction_details_amount),
                                    value = transaction.amount,
                                )
                                TradeDetailRow(
                                    label = stringResource(Res.string.trade_transaction_details_gst),
                                    value = transaction.gstAmount,
                                )
                                TradeDetailRow(
                                    label = stringResource(Res.string.trade_transaction_details_net_amount),
                                    value = transaction.netAmount,
                                )
                                TradeDetailRow(
                                    label = stringResource(Res.string.trade_transaction_details_gold_quantity),
                                    value = transaction.goldQuantity,
                                )
                                TradeDetailRow(
                                    label = stringResource(Res.string.trade_transaction_details_gold_price),
                                    value = transaction.goldPrice,
                                )
                                TradeDetailRow(
                                    label = stringResource(Res.string.trade_transaction_details_created_at),
                                    value = transaction.createdAt,
                                )
                                transaction.sipName?.takeIf { it.isNotBlank() }?.let {
                                    TradeDetailRow(
                                        label = stringResource(Res.string.trade_transaction_details_sip_name),
                                        value = it,
                                    )
                                }
                                transaction.sipFrequency?.takeIf { it.isNotBlank() }?.let {
                                    TradeDetailRow(
                                        label = stringResource(Res.string.trade_transaction_details_sip_frequency),
                                        value = it,
                                    )
                                }
                            }
                        }

                        invoiceErrorMessage?.takeIf { it.isNotBlank() }?.let {
                            Text(
                                text = it,
                                color = androidx.compose.ui.graphics.Color(0xFFB42318),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                            )
                        }

                        if (canShowTradeInvoice(transaction)) {
                            AppPrimaryButton(
                                label = stringResource(Res.string.trade_transaction_details_view_invoice),
                                onClick = {
                                    if (isFetchingInvoice) return@AppPrimaryButton
                                    isFetchingInvoice = true
                                },
                                isLoading = isFetchingInvoice,
                            )
                        }
                    }

                    LaunchedEffect(isFetchingInvoice) {
                        if (!isFetchingInvoice) return@LaunchedEffect
                        when (val result = getTradeInvoiceUseCase(transaction.id)) {
                            is ApiResult.Success -> {
                                isFetchingInvoice = false
                                val invoiceUrl = result.value.invoiceUrl
                                if (invoiceUrl.isBlank()) {
                                    invoiceErrorMessage = invalidInvoiceMessage
                                } else {
                                    invoiceErrorMessage = null
                                    onOpenInvoice(invoiceUrl)
                                }
                            }
                            is ApiResult.Failure -> {
                                invoiceErrorMessage = result.error.message
                                isFetchingInvoice = false
                            }
                        }
                    }
                }
            }
        }
    }
}

private sealed interface TradeTransactionDetailsUiState {
    data object Loading : TradeTransactionDetailsUiState
    data class Content(val transaction: TradeTransactionPreview) : TradeTransactionDetailsUiState
    data class Error(val message: String) : TradeTransactionDetailsUiState
}

private sealed interface TradeTransactionLookupResult {
    data class Found(val transaction: TradeTransactionPreview) : TradeTransactionLookupResult
    data object NotFound : TradeTransactionLookupResult
    data class Failure(val message: String) : TradeTransactionLookupResult
}

private suspend fun findTradeTransaction(
    transactionId: String,
    getTradeTransactionsUseCase: GetTradeTransactionsUseCase,
): TradeTransactionLookupResult {
    var currentPage = 1
    var totalPages = 1
    while (currentPage <= totalPages) {
        when (val result = getTradeTransactionsUseCase(currentPage, 20)) {
            is ApiResult.Success -> {
                result.value.data.firstOrNull { it.id == transactionId }?.let {
                    return TradeTransactionLookupResult.Found(it)
                }
                totalPages = result.value.totalPages
                currentPage += 1
            }
            is ApiResult.Failure -> {
                return TradeTransactionLookupResult.Failure(result.error.message)
            }
        }
    }
    return TradeTransactionLookupResult.NotFound
}

private fun canShowTradeInvoice(transaction: TradeTransactionPreview): Boolean {
    val normalizedType = transaction.type.lowercase()
    val normalizedStatus = transaction.status.lowercase()
    val isTradeType = normalizedType.contains("buy") || normalizedType.contains("sell")
    val isSuccessful = normalizedStatus.contains("success") || normalizedStatus.contains("complete")
    return isTradeType && isSuccessful
}
