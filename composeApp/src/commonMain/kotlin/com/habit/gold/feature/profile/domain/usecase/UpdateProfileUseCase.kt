package com.habit.gold.feature.profile.domain.usecase

import com.habit.gold.core.network.ApiResult
import com.habit.gold.feature.profile.domain.ProfileRepository
import com.habit.gold.feature.profile.domain.model.ProfileNominee
import com.habit.gold.feature.profile.domain.model.ProfileUser

class UpdateProfileUseCase(
    private val repository: ProfileRepository,
) {
    suspend operator fun invoke(
        name: String,
        email: String,
        dateOfBirth: String?,
        gender: String?,
        nominee: ProfileNominee?,
    ): ApiResult<ProfileUser> {
        return repository.updateProfile(
            name = name,
            email = email,
            dateOfBirth = dateOfBirth,
            gender = gender,
            nominee = nominee,
        )
    }
}

