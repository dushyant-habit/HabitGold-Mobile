package com.habit.gold.feature.delivery.presentation.components

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

/** Shows gold weight with exactly 4 decimal places, e.g. 0.5000, 1.0000, truncated. */
fun formatGrams(value: Double): String {
    return com.habit.gold.core.util.formatGramsTruncate(value)
}

/** Shows INR amount with exactly 2 decimal places and Indian grouping, e.g. 150.00, 1,250.50, 1,00,000.00. */
fun formatAmount(value: Double): String {
    val formatted = com.habit.gold.core.util.formatMoneyCeil(value)
    val isNegative = formatted.startsWith("-")
    val absoluteFormatted = formatted.removePrefix("-")
    val parts = absoluteFormatted.split(".")
    val wholeStr = parts[0]
    val fracStr = parts.getOrNull(1) ?: "00"
    
    val whole = wholeStr.toLongOrNull() ?: 0L
    val groupedWhole = formatIndianWhole(whole)
    val sign = if (isNegative) "-" else ""
    return "$sign$groupedWhole.$fracStr"
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

/**
 * Parses an ISO-8601 timestamp or epoch milliseconds and returns
 * a human-readable string like "18 May, 01:20 AM".
 * Falls back to the raw string if parsing fails.
 */
fun formatExpiresAt(raw: String, timeZone: TimeZone = TimeZone.currentSystemDefault()): String {
    return try {
        val asLong = raw.toLongOrNull()
        val instant = if (asLong != null) {
            val ms = if (raw.length <= 10) asLong * 1000 else asLong
            Instant.fromEpochMilliseconds(ms)
        } else {
            Instant.parse(raw)
        }
        val local = instant.toLocalDateTime(timeZone)
        val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        val monthName = monthNames.getOrNull(local.month.ordinal) ?: "M"
        val amPm = if (local.hour < 12) "AM" else "PM"
        val displayHour = when {
            local.hour == 0 -> 12
            local.hour > 12 -> local.hour - 12
            else -> local.hour
        }
        val minuteStr = local.minute.toString().padStart(2, '0')
        val dayStr = local.day.toString().padStart(2, '0')
        val hourStr = displayHour.toString().padStart(2, '0')
        "$dayStr $monthName, $hourStr:$minuteStr $amPm"
    } catch (e: Exception) {
        raw
    }
}
