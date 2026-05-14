package com.habit.gold.feature.auth.data.model

import kotlinx.serialization.Serializable

@Serializable
data class RequestOtpRequestDto(
    val mobileNumber: String,
)

@Serializable
data class RequestOtpResponseDto(
    val message: String,
    val refId: String,
)

@Serializable
data class VerifyOtpRequestDto(
    val mobileNumber: String,
    val otp: String,
)

@Serializable
data class VerifyOtpResponseDto(
    val accessToken: String,
    val refreshToken: String,
    val user: AuthUserDto? = null,
    val newUser: Boolean = true,
)

@Serializable
data class AuthUserDto(
    val id: String,
    val mobileNumber: String,
)

@Serializable
data class UserProfileDto(
    val id: String? = null,
    val name: String? = null,
    val email: String? = null,
    val mobileNumber: String? = null,
    val pinCode: String? = null,
)

@Serializable
data class UpdateBasicInfoRequestDto(
    val name: String,
    val email: String? = null,
    val pinCode: String? = null,
)
