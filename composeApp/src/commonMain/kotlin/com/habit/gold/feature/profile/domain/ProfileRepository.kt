package com.habit.gold.feature.profile.domain

import com.habit.gold.core.network.ApiResult
import com.habit.gold.feature.profile.domain.model.ProfileNominee
import com.habit.gold.feature.profile.domain.model.ProfileSummary
import com.habit.gold.feature.profile.domain.model.ProfileUser

interface ProfileRepository {
    suspend fun getProfileSummary(): ApiResult<ProfileSummary>

    suspend fun updateProfile(
        name: String,
        email: String,
        dateOfBirth: String?,
        gender: String?,
        nominee: ProfileNominee?,
    ): ApiResult<ProfileUser>

    suspend fun verifyKyc(
        pan: String,
        name: String,
    ): ApiResult<Unit>

    suspend fun logout(): ApiResult<Unit>

    suspend fun requestDeleteAccount(): ApiResult<Unit>
}
