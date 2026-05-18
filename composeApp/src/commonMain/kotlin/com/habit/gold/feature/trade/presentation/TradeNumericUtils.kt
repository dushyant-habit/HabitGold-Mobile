package com.habit.gold.feature.trade.presentation

import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.roundToLong

internal fun roundToMoney(value: Double): Double = ((value * 100).roundToInt() / 100.0)

internal fun roundToGoldScale(value: Double): Double = ((value * 10_000).roundToInt() / 10_000.0)

internal fun formatMoney(value: Double): String {
    return com.habit.gold.core.util.formatMoneyCeil(value)
}

internal fun formatPercent(value: Double): String {
    val scaled = (value * 100).roundToInt()
    val whole = scaled / 100
    val fraction = (scaled % 100).toString().removePrefix("-").padStart(2, '0')
    return "$whole.$fraction%"
}

internal fun formatCountdown(remainingSeconds: Int): String {
    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    return minutes.toString().padStart(2, '0') + ":" + seconds.toString().padStart(2, '0')
}

internal fun sanitizeGramInput(raw: String, fractionDigits: Int): String {
    val filtered = raw.filter { it.isDigit() || it == '.' }
    val firstDotIndex = filtered.indexOf('.')
    if (firstDotIndex == -1) return filtered.take(3)

    val beforeDot = filtered.substring(0, firstDotIndex).take(3)
    val afterDot = filtered.substring(firstDotIndex + 1).filter(Char::isDigit).take(fractionDigits)
    return buildString {
        append(beforeDot)
        append('.')
        append(afterDot)
    }.trimEnd('.')
}

internal fun formatGoldQuantity(
    value: Double,
    unitsPerGram: Long = 10_000L, // Kept for compatibility, but ignored in favor of global standard.
): String {
    return com.habit.gold.core.util.formatGramsTruncatePlain(value)
}
