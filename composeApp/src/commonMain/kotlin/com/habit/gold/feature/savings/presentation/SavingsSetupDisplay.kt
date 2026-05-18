package com.habit.gold.feature.savings.presentation

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.math.ceil
import kotlin.math.pow

internal data class SavingsCompoundingSummary(
    val monthlyInvestment: Double,
    val projectionYears: Int,
    val totalInvested: Double,
    val totalValue: Double,
    val estimatedEarnings: Double,
    val highlightText: String,
)

internal fun projectionMessages(
    amount: Int,
    goldPrice: Double,
    frequency: SavingsFrequency,
): List<String> {
    if (amount <= 0 || goldPrice <= 0.0) return emptyList()
    val monthlyContribution = monthlyContribution(amount, frequency)
    if (monthlyContribution <= 0.0) return emptyList()
    return listOf(1.0, 2.0, 5.0).map { targetGrams ->
        val months = ceil((targetGrams * goldPrice) / monthlyContribution).toInt().coerceAtLeast(1)
        val monthText = if (months == 1) "1 month" else "$months months"
        "Keep it up, you're on track to hit ${formatTargetGrams(targetGrams)} in $monthText."
    }
}

internal fun monthlyContribution(
    amount: Int,
    frequency: SavingsFrequency,
): Double {
    return when (frequency) {
        SavingsFrequency.Daily -> amount * 30.0
        SavingsFrequency.Weekly -> amount * (52.0 / 12.0)
        SavingsFrequency.Monthly -> amount.toDouble()
    }
}

internal fun calculateCompoundingSummary(monthlyInvestment: Double): SavingsCompoundingSummary? {
    if (monthlyInvestment <= 0.0) return null
    val projectionYears = 10
    val annualRate = 0.12
    val monthlyRate = (1.0 + annualRate).pow(1.0 / 12.0) - 1.0

    fun maturity(months: Int): Double {
        if (monthlyRate <= 0.0) return monthlyInvestment * months
        return monthlyInvestment *
            (((1.0 + monthlyRate).pow(months.toDouble()) - 1.0) / monthlyRate) *
            (1.0 + monthlyRate)
    }

    val totalInvested = monthlyInvestment * 12 * projectionYears
    val totalValue = maturity(projectionYears * 12)
    val estimatedLakhs = totalValue / 100000.0
    return SavingsCompoundingSummary(
        monthlyInvestment = monthlyInvestment,
        projectionYears = projectionYears,
        totalInvested = totalInvested,
        totalValue = totalValue,
        estimatedEarnings = totalValue - totalInvested,
        highlightText = "₹${formatTwoDecimals(estimatedLakhs)}L in $projectionYears years",
    )
}

internal fun SavingsSetupUiState.scheduleSuffixLabel(): String {
    return when (frequency) {
        SavingsFrequency.Daily -> "Every Day"
        SavingsFrequency.Weekly -> selectedExecutionDay?.let(::weeklyExecutionDayShortName)?.let(::weeklyDayFullName)?.let { "Every $it" }
            ?: "Weekly"
        SavingsFrequency.Monthly -> selectedExecutionDay?.let(::toOrdinalDay)?.let { "Every $it" } ?: "Monthly"
    }
}

internal fun SavingsSetupUiState.nextDebitLabel(
    executionDay: Int,
    today: LocalDate = currentSavingsDate(),
): String {
    return nextPaymentLabel(frequency, executionDay, today)
}

internal fun SavingsSetupUiState.nextDebitLabelOrNull(
    today: LocalDate = currentSavingsDate(),
): String? {
    return when (frequency) {
        SavingsFrequency.Daily -> nextPaymentLabel(frequency, 0, today)
        SavingsFrequency.Weekly,
        SavingsFrequency.Monthly -> selectedExecutionDay?.let { nextPaymentLabel(frequency, it, today) }
    }
}

internal fun nextPaymentLabel(
    frequency: SavingsFrequency,
    executionDay: Int,
    today: LocalDate = currentSavingsDate(),
): String {
    return when (frequency) {
        SavingsFrequency.Daily -> "Tomorrow, ${formatSavingsDate(today.plus(DatePeriod(days = 1)))}"
        SavingsFrequency.Weekly -> {
            val target = executionDay.coerceIn(1, 7)
            var cursor = today
            while (true) {
                cursor = cursor.plus(DatePeriod(days = 1))
                if (cursor.dayOfWeek.toMondayFirst() == target) break
            }
            "On ${formatSavingsDate(cursor)}"
        }

        SavingsFrequency.Monthly -> {
            var year = today.year
            var month = today.month.ordinal + 1
            if (today.day >= executionDay) {
                if (month == 12) {
                    year += 1
                    month = 1
                } else {
                    month += 1
                }
            }
            val targetDay = executionDay.coerceAtMost(daysInMonth(year, month))
            "On ${formatSavingsDate(LocalDate(year, month, targetDay))}"
        }
    }
}

internal data class SavingsCalendarMonth(
    val year: Int,
    val month: Int,
    val monthLabel: String,
    val dayCount: Int,
    val firstDayOffset: Int,
)

internal fun currentSavingsMonth(today: LocalDate = currentSavingsDate()): SavingsCalendarMonth {
    val month = today.month.ordinal + 1
    val monthStart = LocalDate(today.year, month, 1)
    return SavingsCalendarMonth(
        year = today.year,
        month = month,
        monthLabel = when (today.month) {
            Month.JANUARY -> "January"
            Month.FEBRUARY -> "February"
            Month.MARCH -> "March"
            Month.APRIL -> "April"
            Month.MAY -> "May"
            Month.JUNE -> "June"
            Month.JULY -> "July"
            Month.AUGUST -> "August"
            Month.SEPTEMBER -> "September"
            Month.OCTOBER -> "October"
            Month.NOVEMBER -> "November"
            Month.DECEMBER -> "December"
        },
        dayCount = daysInMonth(today.year, month),
        firstDayOffset = monthStart.dayOfWeek.toMondayFirst() - 1,
    )
}

internal fun weeklyExecutionDayShortName(day: Int): String = when (day) {
    1 -> "Mon"
    2 -> "Tue"
    3 -> "Wed"
    4 -> "Thu"
    5 -> "Fri"
    6 -> "Sat"
    7 -> "Sun"
    else -> "Mon"
}

internal fun weeklyDayFullName(shortName: String): String = when (shortName) {
    "Mon" -> "Monday"
    "Tue" -> "Tuesday"
    "Wed" -> "Wednesday"
    "Thu" -> "Thursday"
    "Fri" -> "Friday"
    "Sat" -> "Saturday"
    "Sun" -> "Sunday"
    else -> shortName
}

internal fun toOrdinalDay(day: Int): String {
    val suffix = when {
        day % 100 in 11..13 -> "th"
        day % 10 == 1 -> "st"
        day % 10 == 2 -> "nd"
        day % 10 == 3 -> "rd"
        else -> "th"
    }
    return "$day$suffix"
}

private fun formatSavingsDate(date: LocalDate): String {
    val month = when (date.month) {
        Month.JANUARY -> "Jan"
        Month.FEBRUARY -> "Feb"
        Month.MARCH -> "Mar"
        Month.APRIL -> "Apr"
        Month.MAY -> "May"
        Month.JUNE -> "Jun"
        Month.JULY -> "Jul"
        Month.AUGUST -> "Aug"
        Month.SEPTEMBER -> "Sep"
        Month.OCTOBER -> "Oct"
        Month.NOVEMBER -> "Nov"
        Month.DECEMBER -> "Dec"
    }
    return "${date.day.toString().padStart(2, '0')} $month, ${date.year}"
}

private fun formatTargetGrams(targetGrams: Double): String {
    return if (targetGrams % 1.0 == 0.0) {
        "${targetGrams.toInt()}g"
    } else {
        "${formatOneDecimal(targetGrams)}g"
    }
}

internal fun DayOfWeek.toMondayFirst(): Int {
    return when (this) {
        DayOfWeek.MONDAY -> 1
        DayOfWeek.TUESDAY -> 2
        DayOfWeek.WEDNESDAY -> 3
        DayOfWeek.THURSDAY -> 4
        DayOfWeek.FRIDAY -> 5
        DayOfWeek.SATURDAY -> 6
        DayOfWeek.SUNDAY -> 7
    }
}

internal fun daysInMonth(year: Int, month: Int): Int {
    return when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (isLeapYear(year)) 29 else 28
        else -> 30
    }
}

private fun isLeapYear(year: Int): Boolean {
    return (year % 4 == 0 && year % 100 != 0) || year % 400 == 0
}

internal fun currentSavingsDate(): LocalDate {
    return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
}

private fun formatTwoDecimals(value: Double): String {
    return ((value * 100).toInt() / 100.0).toString().let { text ->
        when {
            text.contains('.') && text.substringAfter('.').length == 1 -> "${text}0"
            text.contains('.') -> text
            else -> "$text.00"
        }
    }
}

private fun formatOneDecimal(value: Double): String {
    return ((value * 10).toInt() / 10.0).toString()
}
