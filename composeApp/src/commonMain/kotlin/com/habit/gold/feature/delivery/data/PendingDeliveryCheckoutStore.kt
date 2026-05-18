package com.habit.gold.feature.delivery.data

import com.habit.gold.core.storage.KeyValueStorage
import com.habit.gold.feature.delivery.domain.model.DeliveryVerifyQuoteDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

@Serializable
enum class PendingDeliveryCheckoutStage {
    REVIEW,
    CONFIRM_IN_FLIGHT,
    SDK_LAUNCH_READY,
    SDK_OPENED,
    VERIFYING_ORDER
}

@Serializable
data class PendingDeliveryCheckout(
    val quoteId: String,
    val productId: String,
    val addressId: String,
    val couponCode: String? = null,
    val verifyQuote: DeliveryVerifyQuoteDto,
    val confirmIdempotencyKey: String? = null,
    val orderId: String? = null,
    val sdkPayload: JsonObject? = null,
    val stage: PendingDeliveryCheckoutStage = PendingDeliveryCheckoutStage.REVIEW
)

interface PendingDeliveryCheckoutStore {
    val pendingCheckout: Flow<PendingDeliveryCheckout?>
    suspend fun save(pendingCheckout: PendingDeliveryCheckout)
    suspend fun clear()
}

class KeyValuePendingDeliveryCheckoutStore(
    private val keyValueStorage: KeyValueStorage
) : PendingDeliveryCheckoutStore {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val _pendingCheckout = MutableStateFlow<PendingDeliveryCheckout?>(null)
    override val pendingCheckout: Flow<PendingDeliveryCheckout?> = _pendingCheckout.asStateFlow()

    suspend fun initialize() {
        val raw = keyValueStorage.read(KEY)
        val checkout = raw?.takeIf { it.isNotBlank() }?.let {
            runCatching {
                json.decodeFromString(PendingDeliveryCheckout.serializer(), it)
            }.getOrNull()
        }
        _pendingCheckout.value = checkout
    }

    override suspend fun save(pendingCheckout: PendingDeliveryCheckout) {
        val str = json.encodeToString(PendingDeliveryCheckout.serializer(), pendingCheckout)
        keyValueStorage.write(KEY, str)
        _pendingCheckout.value = pendingCheckout
    }

    override suspend fun clear() {
        keyValueStorage.delete(KEY)
        _pendingCheckout.value = null
    }

    companion object {
        private const val KEY = "delivery.pending_checkout"
    }
}
