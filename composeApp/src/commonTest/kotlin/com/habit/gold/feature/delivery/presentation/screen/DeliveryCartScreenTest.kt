package com.habit.gold.feature.delivery.presentation.screen

import com.habit.gold.feature.delivery.presentation.components.formatExpiresAt
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals

class DeliveryCartScreenTest {

    @Test
    fun testFormatExpiresAt_ISO8601() {
        // Assert correct parsing and formatting under UTC timezone
        // ISO-8601 string: 2026-05-18T12:00:00Z
        val rawIso = "2026-05-18T12:00:00Z"
        val formatted = formatExpiresAt(rawIso, TimeZone.UTC)
        assertEquals("18 May, 12:00 PM", formatted)
    }

    @Test
    fun testFormatExpiresAt_UnixEpochSeconds() {
        // Unix epoch seconds (10 digits): 1779192000 corresponds to 2026-05-19 12:00:00 UTC
        val rawSeconds = "1779192000"
        val formatted = formatExpiresAt(rawSeconds, TimeZone.UTC)
        assertEquals("19 May, 12:00 PM", formatted)
    }

    @Test
    fun testFormatExpiresAt_UnixEpochMilliseconds() {
        // Unix epoch milliseconds (13 digits): 1779192000000 corresponds to 2026-05-19 12:00:00 UTC
        val rawMillis = "1779192000000"
        val formatted = formatExpiresAt(rawMillis, TimeZone.UTC)
        assertEquals("19 May, 12:00 PM", formatted)
    }

    @Test
    fun testFormatExpiresAt_InvalidInputFallback() {
        // Any corrupted or non-numeric/non-date input should fallback gracefully to the original string
        val rawInvalid = "invalid-date-string"
        val formatted = formatExpiresAt(rawInvalid, TimeZone.UTC)
        assertEquals(rawInvalid, formatted)
        
        val emptyString = ""
        assertEquals("", formatExpiresAt(emptyString, TimeZone.UTC))
    }

    @Test
    fun testFormatExpiresAt_TimezoneConversions() {
        // ISO-8601 string in Asia/Kolkata (+5:30)
        // 2026-05-18T12:00:00Z -> 2026-05-18T17:30:00+05:30
        val rawIso = "2026-05-18T12:00:00Z"
        val formattedKolkata = formatExpiresAt(rawIso, TimeZone.of("Asia/Kolkata"))
        assertEquals("18 May, 05:30 PM", formattedKolkata)

        // ISO-8601 string in America/New_York (EDT is UTC-4:00 in May)
        // 2026-05-18T12:00:00Z -> 2026-05-18T08:00:00-04:00
        val formattedNewYork = formatExpiresAt(rawIso, TimeZone.of("America/New_York"))
        assertEquals("18 May, 08:00 AM", formattedNewYork)
    }
}
