package com.pushk.gatewallpaper

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

object WallpaperScheduler {
    private const val uniqueWorkName = "gate_wallpaper_daily"

    fun scheduleDaily(context: Context) {
        val request = PeriodicWorkRequestBuilder<WallpaperWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(delayUntilNextMidnightMillis(), TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            uniqueWorkName,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    private fun delayUntilNextMidnightMillis(): Long {
        val now = ZonedDateTime.now()
        val midnight = now.toLocalDate().plusDays(1).atStartOfDay(now.zone)
        return Duration.between(now, midnight).toMillis().coerceAtLeast(0)
    }
}
