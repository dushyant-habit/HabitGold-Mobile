package com.habit.gold.feature.delivery.presentation.components

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

/** Shows gold weight with exactly 4 decimal places, e.g. 0.5000, 1.0000. */
fun formatGrams(value: Double): String {
    val scaled = (value * 10000).let { kotlin.math.round(it).toLong() }
    val whole = scaled / 10000
    val frac = (scaled % 10000).let { if (it < 0) -it else it }
    return "${whole}.${frac.toString().padStart(4, '0')}"
}

/** Shows INR amount with exactly 2 decimal places and US grouping, e.g. 150.00, 1,250.50. */
fun formatAmount(value: Double): String {
    val scaled = (value * 100).let { kotlin.math.round(it).toLong() }
    val whole = scaled / 100
    val frac = (scaled % 100).let { if (it < 0) -it else it }
    
    val wholeStr = whole.toString()
    val withCommas = buildString {
        val len = wholeStr.length
        for (i in wholeStr.indices) {
            append(wholeStr[i])
            val remaining = len - 1 - i
            if (remaining > 0 && remaining % 3 == 0) {
                append(',')
            }
        }
    }
    return "${withCommas}.${frac.toString().padStart(2, '0')}"
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
