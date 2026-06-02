package com.pushk.gatewallpaper

import java.time.Clock
import java.time.LocalDate
import java.time.temporal.ChronoUnit

object CountdownCalculator {
    val gateDate: LocalDate = LocalDate.of(2027, 2, 6)

    fun daysRemaining(clock: Clock = Clock.systemDefaultZone()): Int {
        return daysRemaining(LocalDate.now(clock))
    }

    fun daysRemaining(today: LocalDate): Int {
        val days = ChronoUnit.DAYS.between(today, gateDate)
        return days.coerceAtLeast(0).toInt()
    }
}
