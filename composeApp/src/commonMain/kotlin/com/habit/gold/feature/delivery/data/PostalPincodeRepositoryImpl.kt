package com.habit.gold.feature.delivery.data

import com.habit.gold.feature.delivery.domain.PostalPincodeRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class PostalPincodeRepositoryImpl(
    private val httpClient: HttpClient
) : PostalPincodeRepository {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    override suspend fun lookupDistrictAndState(pincode: String): Result<Pair<String, String>> {
        return try {
            val response = httpClient.get("https://api.postalpincode.in/pincode/$pincode")
            val responseText = response.bodyAsText()
            
            val jsonArray = json.decodeFromString<JsonArray>(responseText)
            val firstObject = jsonArray.firstOrNull()?.jsonObject
            
            if (firstObject != null) {
                val status = firstObject["Status"]?.jsonPrimitive?.content
                if (status == "Success") {
                    val postOfficeArray = firstObject["PostOffice"] as? JsonArray
                    val firstPostOffice = postOfficeArray?.firstOrNull()?.jsonObject
                    if (firstPostOffice != null) {
                        val district = firstPostOffice["District"]?.jsonPrimitive?.content.orEmpty()
                        val state = firstPostOffice["State"]?.jsonPrimitive?.content.orEmpty()
                        if (district.isNotBlank() && state.isNotBlank()) {
                            return Result.success(Pair(district, state))
                        }
                    }
                }
            }
            Result.failure(Exception("Could not lookup pincode"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
