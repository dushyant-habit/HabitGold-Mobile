package com.habit.gold.feature.auth.domain

import kotlinx.serialization.Serializable

@Serializable
data class AuthenticatedUser(
    val id: String? = null,
    val phoneNumber: String,
    val name: String = "",
    val email: String = "",
    val pinCode: String = "",
)

data class OtpRequestResult(
    val refId: String,
    val message: String,
)

data class VerifyOtpResult(
    val user: AuthenticatedUser,
    val requiresBasicDetails: Boolean,
    val isPinCodeRequired: Boolean,
)
