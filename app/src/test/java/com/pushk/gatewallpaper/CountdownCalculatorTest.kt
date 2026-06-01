package com.pushk.gatewallpaper

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class CountdownCalculatorTest {
    @Test
    fun futureDateReturnsPositiveDays() {
        val days = CountdownCalculator.daysRemaining(LocalDate.of(2026, 5, 31))

        assertTrue(days > 0)
    }

    @Test
    fun examDateReturnsZero() {
        val days = CountdownCalculator.daysRemaining(LocalDate.of(2027, 2, 1))

        assertEquals(0, days)
    }

    @Test
    fun pastDateClampsToZero() {
        val days = CountdownCalculator.daysRemaining(LocalDate.of(2027, 2, 2))

        assertEquals(0, days)
    }
}
