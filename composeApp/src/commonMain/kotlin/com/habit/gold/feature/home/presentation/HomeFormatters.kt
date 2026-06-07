package com.habit.gold.feature.home.presentation

import kotlin.math.absoluteValue
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

internal fun formatInr(value: Double): String {
    val formatted = com.habit.gold.core.util.formatMoneyCeil(value)
    val isNegative = formatted.startsWith("-")
    val absoluteFormatted = formatted.removePrefix("-")
    val parts = absoluteFormatted.split(".")
    val wholeStr = parts[0]
    val fracStr = parts.getOrNull(1)
    
    val whole = wholeStr.toLongOrNull() ?: 0L
    val groupedWhole = formatIndianWhole(whole)
    val decimalSuffix = if (fracStr == null || fracStr == "00") "" else ".$fracStr"
    val sign = if (isNegative) "-" else ""
    return "$sign$groupedWhole$decimalSuffix"
}

internal fun formatGoldBalance(value: Double): String {
    return com.habit.gold.core.util.formatGramsTruncate(value)
}

internal fun formatProfitLabel(value: Double): String {
    if (value == 0.0) return "₹0"
    val prefix = if (value > 0) "+" else "-"
    return "$prefix₹${formatInr(value.absoluteValue)}"
}

internal fun formatLiveRate(value: Double): String = "₹${formatInr(value)}/g"

internal fun formatCreatedAt(raw: String): String {
    return runCatching {
        val local = Instant.parse(raw).toLocalDateTime(TimeZone.currentSystemDefault())
        "${local.day.toString().padStart(2, '0')} ${monthAbbreviation(local.month.name)}"
    }.getOrElse { raw.take(10) }
}

internal fun formatCreatedAtWithTime(raw: String): String {
    return runCatching {
        val local = Instant.parse(raw).toLocalDateTime(TimeZone.currentSystemDefault())
        val hour24 = local.hour
        val hour12 = when {
            hour24 == 0 -> 12
            hour24 > 12 -> hour24 - 12
            else -> hour24
        }
        val meridiem = if (hour24 >= 12) "PM" else "AM"
        "${local.day.toString().padStart(2, '0')} ${monthAbbreviation(local.month.name)} ${local.year} • " +
            "${hour12}:${local.minute.toString().padStart(2, '0')} $meridiem"
    }.getOrElse { raw }
}

internal fun String.homeInitials(): String {
    val parts = trim().split(Regex("\\s+")).filter { it.isNotBlank() }
    if (parts.isEmpty()) return "U"
    return when {
        parts.size == 1 -> parts.first().take(2)
        else -> "${parts.first().first()}${parts.last().first()}"
    }.uppercase()
}

internal fun String.homeDisplayName(maxChars: Int = 15): String {
    val firstName = trim().split(Regex("\\s+")).firstOrNull().orEmpty()
    if (firstName.isBlank()) return "User"
    return if (firstName.length > maxChars) "${firstName.take(maxChars)}..." else firstName
}

private fun formatIndianWhole(value: Long): String {
    val digits = value.toString()
    if (digits.length <= 3) return digits

    val lastThree = digits.takeLast(3)
    var prefix = digits.dropLast(3)
    val groups = mutableListOf<String>()
    while (prefix.length > 2) {
        groups += prefix.takeLast(2)
        prefix = prefix.dropLast(2)
    }
    if (prefix.isNotEmpty()) groups += prefix
    return groups.asReversed().joinToString(",") + ",$lastThree"
}

private fun String.trimTrailingZeros(): String {
    return if (!contains('.')) {
        this
    } else {
        trimEnd('0').trimEnd('.')
    }
}

private fun monthAbbreviation(monthName: String): String {
    return monthName.lowercase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        .take(3)
}
