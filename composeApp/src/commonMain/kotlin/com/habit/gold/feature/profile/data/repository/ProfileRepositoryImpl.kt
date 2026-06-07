package com.habit.gold.feature.profile.data.repository

import com.habit.gold.app.AuthenticatedSessionResetManager
import com.habit.gold.core.network.ApiResult
import com.habit.gold.core.session.SessionStore
import com.habit.gold.feature.auth.domain.AuthenticatedUser
import com.habit.gold.feature.profile.data.model.ProfileKycVerifyRequestDto
import com.habit.gold.feature.profile.data.model.ProfileKycDto
import com.habit.gold.feature.profile.data.model.ProfileNomineeDto
import com.habit.gold.feature.profile.data.model.ProfileUpdateRequestDto
import com.habit.gold.feature.profile.data.model.ProfileUserDto
import com.habit.gold.feature.profile.data.model.ProfileVpaDto
import com.habit.gold.feature.profile.data.remote.ProfileRemoteDataSource
import com.habit.gold.feature.profile.domain.ProfileRepository
import com.habit.gold.feature.profile.domain.model.ProfileKyc
import com.habit.gold.feature.profile.domain.model.ProfileNominee
import com.habit.gold.feature.profile.domain.model.ProfileSummary
import com.habit.gold.feature.profile.domain.model.ProfileUser
import com.habit.gold.feature.profile.domain.model.ProfileVpa

class ProfileRepositoryImpl(
    private val remoteDataSource: ProfileRemoteDataSource,
    private val sessionStore: SessionStore,
    private val sessionResetManager: AuthenticatedSessionResetManager,
) : ProfileRepository {

    override suspend fun getProfileSummary(): ApiResult<ProfileSummary> {
        val profileResult = remoteDataSource.getProfile()
        val profile = when (profileResult) {
            is ApiResult.Success -> profileResult.value
            is ApiResult.Failure -> return profileResult
        }

        syncSession(profile)

        val totalGoldBalanceGrams = when (val dashboardResult = remoteDataSource.getPortfolioDashboard()) {
            is ApiResult.Success -> dashboardResult.value.totalGoldBalanceGrams
                ?.toDoubleOrNull()
                ?.coerceAtLeast(0.0)
                ?: 0.0
            is ApiResult.Failure -> 0.0
        }

        return ApiResult.Success(
            ProfileSummary(
                user = profile.toDomain(),
                totalGoldBalanceGrams = totalGoldBalanceGrams,
            )
        )
    }

    override suspend fun updateProfile(
        name: String,
        email: String,
        dateOfBirth: String?,
        gender: String?,
        nominee: ProfileNominee?,
    ): ApiResult<ProfileUser> {
        return when (
            val result = remoteDataSource.updateProfile(
                ProfileUpdateRequestDto(
                    name = name.trim(),
                    email = email.trim().takeIf { it.isNotBlank() },
                    dateOfBirth = normalizeIsoDateOrNull(dateOfBirth),
                    gender = normalizeGenderOrNull(gender),
                    nominee = nominee?.toDto(),
                    pinCode = sessionStore.state.value.user?.pinCode?.takeIf { it.isNotBlank() },
                )
            )
        ) {
            is ApiResult.Success -> {
                syncSession(result.value)
                ApiResult.Success(result.value.toDomain())
            }
            is ApiResult.Failure -> result
        }
    }

    override suspend fun verifyKyc(
        pan: String,
        name: String,
    ): ApiResult<Unit> {
        return remoteDataSource.verifyKyc(
            ProfileKycVerifyRequestDto(
                pan = pan.trim().uppercase(),
                name = name.trim(),
            )
        )
    }

    override suspend fun logout(): ApiResult<Unit> {
        remoteDataSource.logout()
        sessionResetManager.reset()
        return ApiResult.Success(Unit)
    }

    override suspend fun requestDeleteAccount(): ApiResult<Unit> {
        return when (val result = remoteDataSource.requestDeleteAccount()) {
            is ApiResult.Success -> {
                sessionResetManager.reset()
                ApiResult.Success(Unit)
            }
            is ApiResult.Failure -> result
        }
    }

    private suspend fun syncSession(profile: ProfileUserDto) {
        val currentSession = sessionStore.state.value
        val currentUser = currentSession.user
        val phoneNumber = profile.mobileNumber
            ?.takeIf { it.isNotBlank() }
            ?: currentUser?.phoneNumber
            ?: return

        sessionStore.updateProfile(
            user = AuthenticatedUser(
                id = profile.id ?: currentUser?.id,
                phoneNumber = phoneNumber,
                name = profile.name?.takeIf { it.isNotBlank() } ?: currentUser?.name.orEmpty(),
                email = profile.email?.takeIf { it.isNotBlank() } ?: currentUser?.email.orEmpty(),
                pinCode = profile.pinCode?.takeIf { it.isNotBlank() } ?: currentUser?.pinCode.orEmpty(),
            ),
            isProfileComplete = currentSession.isProfileComplete,
            isPinCodeRequired = currentSession.isPinCodeRequired,
        )
    }
}

private fun ProfileNominee.toDto(): ProfileNomineeDto {
    return ProfileNomineeDto(
        name = name.trim(),
        relation = relation.trim(),
        dateOfBirth = null,
        mobileNumber = mobileNumber.trim(),
    )
}

private fun ProfileUserDto.toDomain(): ProfileUser {
    return ProfileUser(
        id = id.orEmpty(),
        name = name.orEmpty(),
        email = email.orEmpty(),
        mobileNumber = mobileNumber.orEmpty(),
        dateOfBirth = dateOfBirth.orEmpty(),
        gender = gender.orEmpty(),
        pinCode = pinCode.orEmpty(),
        kycStatus = kycStatus.orEmpty(),
        nominee = nominee?.toDomain(),
        kyc = kyc?.toDomain(),
        payoutVpa = payoutVpa.orEmpty(),
        payoutVpaVerified = payoutVpaVerified == true,
        vpas = vpas.map(ProfileVpaDto::toDomain),
    )
}

private fun ProfileNomineeDto.toDomain(): ProfileNominee {
    return ProfileNominee(
        name = name.orEmpty(),
        relation = relation.orEmpty(),
        dateOfBirth = dateOfBirth.orEmpty(),
        mobileNumber = mobileNumber.orEmpty(),
    )
}

private fun ProfileKycDto.toDomain(): ProfileKyc {
    return ProfileKyc(
        status = status.orEmpty(),
        verifiedAt = verifiedAt.orEmpty(),
        panMasked = panMasked.orEmpty(),
    )
}

private fun ProfileVpaDto.toDomain(): ProfileVpa {
    return ProfileVpa(
        id = id.orEmpty(),
        value = vpa.orEmpty(),
        isDefault = isDefault == true,
        status = status.orEmpty(),
        verified = verified == true,
    )
}

private fun normalizeGenderOrNull(gender: String?): String? {
    return when (gender?.trim()?.uppercase()) {
        "MALE" -> "MALE"
        "FEMALE" -> "FEMALE"
        "OTHER" -> "OTHER"
        else -> null
    }
}

private fun normalizeIsoDateOrNull(raw: String?): String? {
    val value = raw?.trim()?.takeIf { it.isNotEmpty() } ?: return null
    if (ISO_DATE_REGEX.matches(value)) return value

    val parts = value.split("/")
    if (parts.size == 3) {
        val day = parts[0].padStart(2, '0')
        val month = parts[1].padStart(2, '0')
        val year = parts[2]
        if (year.length == 4) {
            val isoCandidate = "$year-$month-$day"
            if (ISO_DATE_REGEX.matches(isoCandidate)) return isoCandidate
        }
    }

    return value.take(10).takeIf { ISO_DATE_REGEX.matches(it) }
}

private val ISO_DATE_REGEX = Regex("""\d{4}-\d{2}-\d{2}""")
