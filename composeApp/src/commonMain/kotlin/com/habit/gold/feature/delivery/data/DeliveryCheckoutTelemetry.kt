package com.habit.gold.feature.delivery.data

interface DeliveryCheckoutTelemetry {
    fun quoteCreated(quoteId: String, productId: String, addressId: String)
    fun confirmStarted(quoteId: String, idempotencyKey: String)
    fun paymentSdkOpened(orderId: String?, quoteId: String)
    fun finalOrderState(orderId: String?, paymentStatus: String?, orderStatus: String?)
}

class PrintDeliveryCheckoutTelemetry(
    private val enabled: Boolean = true,
) : DeliveryCheckoutTelemetry {
    override fun quoteCreated(quoteId: String, productId: String, addressId: String) {
        log("quote_created quoteId=$quoteId productId=$productId addressId=$addressId")
    }

    override fun confirmStarted(quoteId: String, idempotencyKey: String) {
        log("confirm_started quoteId=$quoteId idempotencyKey=$idempotencyKey")
    }

    override fun paymentSdkOpened(orderId: String?, quoteId: String) {
        log("payment_sdk_opened quoteId=$quoteId orderId=${orderId.orEmpty()}")
    }

    override fun finalOrderState(orderId: String?, paymentStatus: String?, orderStatus: String?) {
        log(
            "final_order_state orderId=${orderId.orEmpty()} " +
                "paymentStatus=${paymentStatus.orEmpty()} orderStatus=${orderStatus.orEmpty()}"
        )
    }

    private fun log(message: String) {
        if (enabled) {
            println("[$TAG] $message")
        }
    }

    companion object {
        private const val TAG = "DeliveryCheckout"
    }
}
