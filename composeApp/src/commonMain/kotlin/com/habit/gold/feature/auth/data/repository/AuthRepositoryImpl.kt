package com.habit.gold.feature.auth.data.repository

import com.habit.gold.core.network.ApiResult
import com.habit.gold.core.session.SessionStore
import com.habit.gold.feature.auth.data.model.UserProfileDto
import com.habit.gold.feature.auth.data.remote.AuthRemoteDataSource
import com.habit.gold.feature.auth.domain.AuthRepository
import com.habit.gold.feature.auth.domain.AuthValidators
import com.habit.gold.feature.auth.domain.AuthenticatedUser
import com.habit.gold.feature.auth.domain.OtpRequestResult
import com.habit.gold.feature.auth.domain.VerifyOtpResult

class AuthRepositoryImpl(
    private val remoteDataSource: AuthRemoteDataSource,
    private val sessionStore: SessionStore,
) : AuthRepository {

    override suspend fun requestOtp(phoneNumber: String): ApiResult<OtpRequestResult> {
        return when (val result = remoteDataSource.requestOtp(phoneNumber)) {
            is ApiResult.Success -> ApiResult.Success(
                OtpRequestResult(
                    refId = result.value.refId,
                    message = result.value.message,
                )
            )
            is ApiResult.Failure -> result
        }
    }

    override suspend fun verifyOtp(phoneNumber: String, otp: String): ApiResult<VerifyOtpResult> {
        return when (val result = remoteDataSource.verifyOtp(phoneNumber, otp)) {
            is ApiResult.Failure -> result
            is ApiResult.Success -> {
                val response = result.value
                val baseUser = AuthenticatedUser(
                    id = response.user?.id,
                    phoneNumber = response.user?.mobileNumber ?: phoneNumber,
                )

                if (response.newUser) {
                    sessionStore.saveAuthenticatedUser(
                        accessToken = response.accessToken,
                        refreshToken = response.refreshToken,
                        user = baseUser,
                        isProfileComplete = false,
                    )
                    ApiResult.Success(
                        VerifyOtpResult(
                            user = baseUser,
                            requiresBasicInfo = true,
                        )
                    )
                } else {
                    val profileResult = remoteDataSource.getProfile()
                    when (profileResult) {
                        is ApiResult.Success -> {
                            val enrichedUser = profileResult.value.toAuthenticatedUser(phoneNumber)
                            val isProfileComplete = AuthValidators.isBasicInfoComplete(enrichedUser)
                            sessionStore.saveAuthenticatedUser(
                                accessToken = response.accessToken,
                                refreshToken = response.refreshToken,
                                user = enrichedUser,
                                isProfileComplete = isProfileComplete,
                            )
                            ApiResult.Success(
                                VerifyOtpResult(
                                    user = enrichedUser,
                                    requiresBasicInfo = !isProfileComplete,
                                )
                            )
                        }
                        is ApiResult.Failure -> {
                            sessionStore.saveAuthenticatedUser(
                                accessToken = response.accessToken,
                                refreshToken = response.refreshToken,
                                user = baseUser,
                                isProfileComplete = false,
                            )
                            ApiResult.Success(
                                VerifyOtpResult(
                                    user = baseUser,
                                    requiresBasicInfo = true,
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    override suspend fun submitBasicInfo(
        name: String,
        email: String,
        pinCode: String,
    ): ApiResult<AuthenticatedUser> {
        return when (val result = remoteDataSource.updateBasicInfo(name, email, pinCode)) {
            is ApiResult.Failure -> result
            is ApiResult.Success -> {
                val currentPhoneNumber = sessionStore.state.value.user?.phoneNumber.orEmpty()
                val updatedUser = result.value.toAuthenticatedUser(currentPhoneNumber).copy(
                    name = name.trim(),
                    email = email.trim(),
                    pinCode = pinCode.trim(),
                )
                sessionStore.updateProfile(
                    user = updatedUser,
                    isProfileComplete = true,
                )
                ApiResult.Success(updatedUser)
            }
        }
    }
}

private fun UserProfileDto.toAuthenticatedUser(
    fallbackPhoneNumber: String,
): AuthenticatedUser {
    return AuthenticatedUser(
        id = id,
        phoneNumber = mobileNumber ?: fallbackPhoneNumber,
        name = name.orEmpty(),
        email = email.orEmpty(),
        pinCode = pinCode.orEmpty(),
    )
}
