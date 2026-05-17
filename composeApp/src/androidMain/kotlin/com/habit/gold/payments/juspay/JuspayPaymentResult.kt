package com.habit.gold.payments.juspay

sealed class JuspayPaymentResult {
    data class Success(val status: String) : JuspayPaymentResult()
    data class Failure(val status: String, val message: String) : JuspayPaymentResult()
    data object BackPressed : JuspayPaymentResult()
}
