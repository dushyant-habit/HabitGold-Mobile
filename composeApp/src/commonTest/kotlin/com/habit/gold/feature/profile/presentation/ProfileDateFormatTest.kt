package com.habit.gold.feature.profile.presentation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ProfileDateFormatTest {

    @Test
    fun `formatDateOfBirthForDisplay handles utc timestamp with millis`() {
        assertEquals("03/07/1994", formatDateOfBirthForDisplay("1994-07-03T00:00:00.000Z"))
    }

    @Test
    fun `formatDateOfBirthForDisplay handles utc timestamp without millis`() {
        assertEquals("03/07/1994", formatDateOfBirthForDisplay("1994-07-03T00:00:00Z"))
    }

    @Test
    fun `formatDateOfBirthForDisplay handles iso date and keeps formatted value stable`() {
        assertEquals("03/07/1994", formatDateOfBirthForDisplay("1994-07-03"))
        assertEquals("03/07/1994", formatDateOfBirthForDisplay("03/07/1994"))
    }

    @Test
    fun `toIsoDateOrNull converts display date back to iso`() {
        assertEquals("1994-07-03", toIsoDateOrNull("03/07/1994"))
    }

    @Test
    fun `toIsoDateOrNull returns null for invalid input`() {
        assertNull(toIsoDateOrNull("03-07-1994"))
    }
}
