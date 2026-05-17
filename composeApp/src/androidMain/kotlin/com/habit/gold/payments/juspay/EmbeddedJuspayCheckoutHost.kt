package com.habit.gold.payments.juspay

import org.json.JSONObject

interface EmbeddedJuspayCheckoutHost {
    fun startEmbeddedJuspayCheckout(
        sdkPayload: JSONObject,
        onResult: (JuspayPaymentResult) -> Unit,
    ): Boolean
}
