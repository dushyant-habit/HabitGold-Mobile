package com.habit.gold.feature.savings.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class SavingsCreateMandateRequestDto(
    val amount: Int,
    val frequency: String,
    val name: String,
    val goalType: String? = null,
    val executionDay: Int? = null,
    val promoCode: String? = null,
)

@Serializable
data class SavingsCreateMandateResponseDto(
    val mandateId: String,
    val sdk_payload: JsonObject? = null,
)

@Serializable
data class SavingsMandateBillingDto(
    val executionId: String? = null,
    val executionStatus: String? = null,
    val nextExecutionOrderId: String? = null,
    val nextExecutionAmount: String? = null,
    val currentAmount: String? = null,
    val amountUpdatedAt: String? = null,
    val needsAttention: Boolean = false,
)

@Serializable
data class SavingsMandateDto(
    val id: String,
    val userId: String? = null,
    val name: String,
    val amount: String,
    val frequency: String,
    val startDate: String,
    val juspayPlanId: String? = null,
    val status: String,
    val juspayMandateId: String? = null,
    val juspayOrderId: String? = null,
    val juspayBillingExecutionId: String? = null,
    val juspayBillingExecutionStatus: String? = null,
    val billingLastEventName: String? = null,
    val billingLastEventAt: String? = null,
    val billingLastScheduledExecutionOrderId: String? = null,
    val billingLastRetryScheduledExecutionOrderId: String? = null,
    val billingNextExecutionOrderId: String? = null,
    val billingNextExecutionAmount: String? = null,
    val billingCurrentAmount: String? = null,
    val billingAmountUpdatedAt: String? = null,
    val billingRetryExceededAt: String? = null,
    val billingDeactivatedAt: String? = null,
    val promoCode: String? = null,
    val consecutiveFailures: Int = 0,
    val nextExecutionDate: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val lastSyncedAt: String? = null,
    val syncPending: Boolean = false,
    val billing: SavingsMandateBillingDto? = null,
)

@Serializable
data class SavingsExecutionDto(
    val id: String,
    val executionDate: String,
    val amount: String,
    val status: String,
)
