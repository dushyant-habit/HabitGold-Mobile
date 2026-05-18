package com.habit.gold.core.util

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.round

/**
 * Common formatter rules for HabitGold apps:
 * - INR (Money): Always ROUND UP (ceil) if there is any value other than 0 after the second decimal.
 * - Gold (Grams): Always REMOVE (truncate) any value after the 4th decimal. Never round up.
 */

/**
 * Formats money to 2 decimal places, always rounding up (ceiling).
 * e.g., 12.341 -> 12.35
 */
fun formatMoneyCeil(value: Double): String {
    if (value.isNaN()) return "0.00"
    val isNegative = value < 0
    val absVal = if (isNegative) -value else value

    // Round to 6 decimal places to eliminate IEEE 754 precision noise before ceiling
    val smoothed = round(absVal * 1_000_000.0) / 1_000_000.0
    val scaled = ceil(smoothed * 100.0).toLong()

    val whole = scaled / 100
    val frac = (scaled % 100).toString().padStart(2, '0')
    val prefix = if (isNegative && scaled != 0L) "-" else ""
    return "$prefix$whole.$frac"
}

/**
 * Formats money to 0 decimal places, rounding to nearest int (used for coupons usually).
 */
fun formatMoney0(value: Double): String {
    if (value.isNaN()) return "0"
    return round(value).toLong().toString()
}

/**
 * Formats gold to 4 decimal places, always truncating (flooring for positive numbers) any value after the 4th decimal.
 * e.g., 1.96289 -> 1.9628
 */
fun formatGramsTruncate(value: Double): String {
    if (value.isNaN()) return "0.0000"
    val isNegative = value < 0
    val absVal = if (isNegative) -value else value

    // Round to 8 decimal places to eliminate IEEE 754 precision noise before truncating
    val smoothed = round(absVal * 100_000_000.0) / 100_000_000.0
    val scaled = floor(smoothed * 10000.0).toLong()

    val whole = scaled / 10000
    val frac = (scaled % 10000).toString().padStart(4, '0')
    val prefix = if (isNegative && scaled != 0L) "-" else ""
    return "$prefix$whole.$frac"
}

/**
 * Formats gold to exactly 4 decimal places but removes trailing zeros.
 * Useful when the UI design asks for compact display but strictly limits to 4 decimals max.
 */
fun formatGramsTruncatePlain(value: Double): String {
    val formatted = formatGramsTruncate(value)
    if (!formatted.contains(".")) return formatted
    return formatted.trimEnd('0').trimEnd('.')
}
