package com.habit.gold.feature.delivery.domain

interface PostalPincodeRepository {
    /**
     * @return district (suitable for city field) and state, or failure if not found / error status.
     */
    suspend fun lookupDistrictAndState(pincode: String): Result<Pair<String, String>>
}
