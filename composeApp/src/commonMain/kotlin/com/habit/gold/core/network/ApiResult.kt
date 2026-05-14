package com.habit.gold.core.network

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.io.IOException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

sealed interface ApiResult<out T> {
    data class Success<T>(val value: T) : ApiResult<T>
    data class Failure(val error: NetworkError) : ApiResult<Nothing>
}

enum class NetworkErrorKind {
    Connectivity,
    Timeout,
    Unauthorized,
    Forbidden,
    NotFound,
    Validation,
    RateLimited,
    Server,
    Unknown,
}

data class NetworkError(
    val kind: NetworkErrorKind,
    val message: String,
    val statusCode: Int? = null,
    val backendCode: String? = null,
    val isRetryable: Boolean = false,
    val cause: Throwable? = null,
)

suspend fun <T> safeApiCall(
    block: suspend () -> T,
): ApiResult<T> {
    return try {
        ApiResult.Success(block())
    } catch (error: CancellationException) {
        throw error
    } catch (error: HttpRequestTimeoutException) {
        ApiResult.Failure(
            NetworkError(
                kind = NetworkErrorKind.Timeout,
                message = "The request timed out. Please try again.",
                isRetryable = true,
                cause = error,
            )
        )
    } catch (error: TimeoutCancellationException) {
        ApiResult.Failure(
            NetworkError(
                kind = NetworkErrorKind.Timeout,
                message = "The request timed out. Please try again.",
                isRetryable = true,
                cause = error,
            )
        )
    } catch (error: RedirectResponseException) {
        ApiResult.Failure(error.toNetworkError())
    } catch (error: ClientRequestException) {
        ApiResult.Failure(error.toNetworkError())
    } catch (error: ServerResponseException) {
        ApiResult.Failure(error.toNetworkError())
    } catch (error: ResponseException) {
        ApiResult.Failure(error.toNetworkError())
    } catch (error: IOException) {
        ApiResult.Failure(
            NetworkError(
                kind = NetworkErrorKind.Connectivity,
                message = "Please check your internet connection and try again.",
                isRetryable = true,
                cause = error,
            )
        )
    } catch (error: Throwable) {
        ApiResult.Failure(
            NetworkError(
                kind = NetworkErrorKind.Unknown,
                message = error.message ?: "Something went wrong. Please try again.",
                cause = error,
            )
        )
    }
}

private suspend fun ResponseException.toNetworkError(): NetworkError {
    val responseBody = runCatching { response.bodyAsText() }.getOrNull()
    return mapHttpFailure(
        statusCode = response.status.value,
        rawBody = responseBody,
        fallbackMessage = message ?: "Request failed.",
        cause = this,
    )
}

internal fun mapHttpFailure(
    statusCode: Int?,
    rawBody: String?,
    fallbackMessage: String,
    cause: Throwable? = null,
): NetworkError {
    val parsedBody = rawBody?.let(::parseErrorPayload)
    val parsedMessage = parsedBody?.message
    val kind = statusCode.toNetworkErrorKind()
    val message = when {
        !parsedMessage.isNullOrBlank() -> parsedMessage
        else -> defaultMessageFor(kind, fallbackMessage)
    }

    return NetworkError(
        kind = kind,
        message = message,
        statusCode = statusCode,
        backendCode = parsedBody?.code,
        isRetryable = kind == NetworkErrorKind.Connectivity ||
            kind == NetworkErrorKind.Timeout ||
            kind == NetworkErrorKind.RateLimited ||
            kind == NetworkErrorKind.Server,
        cause = cause,
    )
}

internal fun parseErrorPayload(rawBody: String): ParsedErrorPayload? {
    if (rawBody.isBlank()) return null

    val trimmedBody = rawBody.trim()
    val parsedElement = if (looksLikeJsonPayload(trimmedBody)) {
        runCatching {
            Json.parseToJsonElement(trimmedBody)
        }.getOrNull()
    } else {
        null
    }

    if (parsedElement != null) {
        return extractParsedErrorPayload(parsedElement)
    }

    val plainTextMessage = trimmedBody
        .takeIf(::looksLikeReadablePlainTextError)

    return if (plainTextMessage != null) {
        ParsedErrorPayload(message = plainTextMessage, code = null)
    } else {
        null
    }
}

internal data class ParsedErrorPayload(
    val message: String?,
    val code: String?,
)

private fun extractParsedErrorPayload(element: JsonElement): ParsedErrorPayload {
    return when (element) {
        is JsonPrimitive -> ParsedErrorPayload(
            message = element.contentOrNull?.takeIf { it.isNotBlank() },
            code = null,
        )
        is JsonArray -> {
            val firstMessage = element.firstNotNullOfOrNull { item ->
                extractParsedErrorPayload(item).message
            }
            val firstCode = element.firstNotNullOfOrNull { item ->
                extractParsedErrorPayload(item).code
            }
            ParsedErrorPayload(message = firstMessage, code = firstCode)
        }
        is JsonObject -> {
            val message = listOf("message", "error", "detail", "reason", "title")
                .firstNotNullOfOrNull { key -> element[key]?.let(::extractMessage) }
            val code = element["code"]?.let(::extractMessage)
            ParsedErrorPayload(message = message, code = code)
        }
    }
}

private fun looksLikeJsonPayload(rawBody: String): Boolean {
    val firstCharacter = rawBody.firstOrNull() ?: return false
    return firstCharacter == '{' ||
        firstCharacter == '[' ||
        firstCharacter == '"' ||
        firstCharacter == '-' ||
        firstCharacter.isDigit() ||
        rawBody.startsWith("true") ||
        rawBody.startsWith("false") ||
        rawBody.startsWith("null")
}

private fun extractMessage(element: JsonElement): String? {
    return when (element) {
        is JsonPrimitive -> element.contentOrNull?.takeIf { it.isNotBlank() }
        is JsonArray -> element.firstNotNullOfOrNull(::extractMessage)
        is JsonObject -> {
            listOf("message", "error", "detail", "reason", "title", "code")
                .firstNotNullOfOrNull { key -> element[key]?.let(::extractMessage) }
        }
    }
}

private fun looksLikeReadablePlainTextError(rawBody: String): Boolean {
    if (rawBody.isBlank()) return false
    if (rawBody.length > 200) return false

    return rawBody.all { character ->
        character.isLetterOrDigit() ||
            character.isWhitespace() ||
            character in setOf('.', ',', ':', ';', '\'', '"', '!', '?', '-', '_', '(', ')', '/', '&')
    }
}

private fun Int?.toNetworkErrorKind(): NetworkErrorKind {
    return when (this) {
        400, 409, 422 -> NetworkErrorKind.Validation
        401 -> NetworkErrorKind.Unauthorized
        403 -> NetworkErrorKind.Forbidden
        404 -> NetworkErrorKind.NotFound
        429 -> NetworkErrorKind.RateLimited
        in 500..599 -> NetworkErrorKind.Server
        else -> NetworkErrorKind.Unknown
    }
}

private fun defaultMessageFor(
    kind: NetworkErrorKind,
    fallbackMessage: String,
): String {
    return when (kind) {
        NetworkErrorKind.Connectivity -> "Please check your internet connection and try again."
        NetworkErrorKind.Timeout -> "The request timed out. Please try again."
        NetworkErrorKind.Unauthorized -> "Your session has expired. Please log in again."
        NetworkErrorKind.Forbidden -> "You are not allowed to perform this action."
        NetworkErrorKind.NotFound -> "The requested item could not be found."
        NetworkErrorKind.Validation -> fallbackMessage
        NetworkErrorKind.RateLimited -> "Too many attempts. Please try again in a moment."
        NetworkErrorKind.Server -> "Something went wrong on our side. Please try again."
        NetworkErrorKind.Unknown -> fallbackMessage
    }
}
