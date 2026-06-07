package com.pushk.gatewallpaper

import android.content.Context
import java.time.Clock
import java.time.LocalDate
import java.time.temporal.ChronoUnit

object CountdownCalculator {
    fun daysRemaining(context: Context, clock: Clock = Clock.systemDefaultZone()): Int {
        return daysRemaining(context, LocalDate.now(clock))
    }

    fun daysRemaining(context: Context, today: LocalDate): Int {
        val examDate = ExamRepository.getExamDate(context)
        val days = ChronoUnit.DAYS.between(today, examDate)
        return days.coerceAtLeast(0).toInt()
    }
}
