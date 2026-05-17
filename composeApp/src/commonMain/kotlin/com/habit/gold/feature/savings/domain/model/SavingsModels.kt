package com.habit.gold.feature.savings.domain.model

data class SavingsCreateMandateRequest(
    val amount: Int,
    val frequency: String,
    val name: String,
    val goalType: String? = null,
    val executionDay: Int? = null,
    val promoCode: String? = null,
)

data class SavingsMandateSession(
    val mandateId: String,
    val sdkPayloadJson: String? = null,
)

data class SavingsMandateBilling(
    val executionId: String?,
    val executionStatus: String?,
    val nextExecutionOrderId: String?,
    val nextExecutionAmount: String?,
    val currentAmount: String?,
    val amountUpdatedAt: String?,
    val needsAttention: Boolean,
)

data class SavingsMandate(
    val id: String,
    val userId: String?,
    val name: String,
    val amount: String,
    val frequency: String,
    val startDate: String,
    val status: String,
    val juspayMandateId: String?,
    val promoCode: String?,
    val nextExecutionDate: String?,
    val billingCurrentAmount: String?,
    val billingNextExecutionAmount: String?,
    val billingLastEventName: String?,
    val billingLastEventAt: String?,
    val consecutiveFailures: Int,
    val createdAt: String?,
    val updatedAt: String?,
    val billing: SavingsMandateBilling?,
)

data class SavingsExecution(
    val id: String,
    val executionDate: String,
    val amount: String,
    val status: String,
)
