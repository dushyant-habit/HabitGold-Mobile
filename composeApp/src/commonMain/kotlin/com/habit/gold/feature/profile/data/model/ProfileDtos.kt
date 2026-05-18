package com.habit.gold.feature.profile.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ProfileNomineeDto(
    val name: String? = null,
    val relation: String? = null,
    val dateOfBirth: String? = null,
    val mobileNumber: String? = null,
)

@Serializable
data class ProfileKycDto(
    val status: String? = null,
    val verifiedAt: String? = null,
    val panMasked: String? = null,
)

@Serializable
data class ProfileVpaDto(
    val id: String? = null,
    val vpa: String? = null,
    val isDefault: Boolean? = null,
    val status: String? = null,
    val verified: Boolean? = null,
)

@Serializable
data class ProfileUserDto(
    val id: String? = null,
    val name: String? = null,
    val email: String? = null,
    val mobileNumber: String? = null,
    val role: String? = null,
    val status: String? = null,
    val dateOfBirth: String? = null,
    val gender: String? = null,
    val location: String? = null,
    val pinCode: String? = null,
    val nominee: ProfileNomineeDto? = null,
    val kycStatus: String? = null,
    val panVerifiedAt: String? = null,
    val kyc: ProfileKycDto? = null,
    val providerUserId: String? = null,
    val payoutVpa: String? = null,
    val payoutVpaVerified: Boolean? = null,
    val vpas: List<ProfileVpaDto> = emptyList(),
    val referralId: String? = null,
    val referrerId: String? = null,
    val streakFreezes: Int? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

@Serializable
data class ProfilePortfolioDashboardDto(
    val totalGoldBalanceGrams: String? = null,
)

@Serializable
data class ProfileUpdateRequestDto(
    val name: String,
    val email: String? = null,
    val dateOfBirth: String? = null,
    val gender: String? = null,
    val nominee: ProfileNomineeDto? = null,
    val pinCode: String? = null,
)

@Serializable
data class ProfileKycVerifyRequestDto(
    val pan: String,
    val name: String,
)
