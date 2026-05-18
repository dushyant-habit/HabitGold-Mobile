package com.habit.gold.feature.profile.domain.model

data class ProfileNominee(
    val name: String,
    val relation: String,
    val dateOfBirth: String,
    val mobileNumber: String,
)

data class ProfileKyc(
    val status: String,
    val verifiedAt: String,
    val panMasked: String,
)

data class ProfileVpa(
    val id: String,
    val value: String,
    val isDefault: Boolean,
    val status: String,
    val verified: Boolean,
)

data class ProfileUser(
    val id: String,
    val name: String,
    val email: String,
    val mobileNumber: String,
    val dateOfBirth: String,
    val gender: String,
    val pinCode: String,
    val kycStatus: String,
    val nominee: ProfileNominee?,
    val kyc: ProfileKyc?,
    val payoutVpa: String,
    val payoutVpaVerified: Boolean,
    val vpas: List<ProfileVpa>,
)

data class ProfileSummary(
    val user: ProfileUser,
    val totalGoldBalanceGrams: Double,
)

