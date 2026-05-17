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

    /**
     * Verifies OTP, restores the best available user snapshot, and falls back to profile completion when needed.
     */
    override suspend fun verifyOtp(phoneNumber: String, otp: String): ApiResult<VerifyOtpResult> {
        return when (val result = remoteDataSource.verifyOtp(phoneNumber, otp)) {
            is ApiResult.Failure -> result
            is ApiResult.Success -> {
                val response = result.value
                val requiresBasicDetails = response.newUser || response.showOnboarding
                val baseUser = AuthenticatedUser(
                    id = response.user?.id,
                    phoneNumber = response.user?.mobileNumber ?: phoneNumber,
                )

                if (requiresBasicDetails) {
                    sessionStore.saveAuthenticatedUser(
                        accessToken = response.accessToken,
                        refreshToken = response.refreshToken,
                        user = baseUser,
                        isProfileComplete = false,
                        isPinCodeRequired = response.pincodeRequired,
                    )
                    ApiResult.Success(
                        VerifyOtpResult(
                            user = baseUser,
                            requiresBasicDetails = true,
                            isPinCodeRequired = response.pincodeRequired,
                        )
                    )
                } else {
                    val profileResult = remoteDataSource.getProfile(accessToken = response.accessToken)
                    val resolvedUser = when (profileResult) {
                        is ApiResult.Success -> profileResult.value.toAuthenticatedUser(phoneNumber)
                        is ApiResult.Failure -> baseUser
                    }
                    sessionStore.saveAuthenticatedUser(
                        accessToken = response.accessToken,
                        refreshToken = response.refreshToken,
                        user = resolvedUser,
                        isProfileComplete = true,
                        isPinCodeRequired = response.pincodeRequired,
                    )
                    ApiResult.Success(
                        VerifyOtpResult(
                            user = resolvedUser,
                            requiresBasicDetails = false,
                            isPinCodeRequired = response.pincodeRequired,
                        )
                    )
                }
            }
        }
    }

    override suspend fun submitBasicDetails(
        name: String,
        pinCode: String?,
        referralCode: String?,
    ): ApiResult<AuthenticatedUser> {
        return when (val result = remoteDataSource.updateBasicInfo(name, email = null, pinCode = pinCode)) {
            is ApiResult.Failure -> result
            is ApiResult.Success -> {
                val normalizedReferralCode = referralCode
                    ?.let(AuthValidators::normalizeReferralCode)
                    ?.takeIf { it.isNotBlank() }
                if (normalizedReferralCode != null) {
                    when (val referralResult = remoteDataSource.submitReferralCode(normalizedReferralCode)) {
                        is ApiResult.Failure -> return referralResult
                        is ApiResult.Success -> Unit
                    }
                }
                val currentPhoneNumber = sessionStore.state.value.user?.phoneNumber.orEmpty()
                val updatedUser = result.value.toAuthenticatedUser(currentPhoneNumber).copy(
                    name = name.trim(),
                    pinCode = pinCode?.trim().orEmpty(),
                )
                sessionStore.updateProfile(
                    user = updatedUser,
                    isProfileComplete = true,
                    isPinCodeRequired = sessionStore.state.value.isPinCodeRequired,
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
