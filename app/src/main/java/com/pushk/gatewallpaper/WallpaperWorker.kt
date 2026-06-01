package com.pushk.gatewallpaper

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class WallpaperWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return if (WallpaperSetter.setFreshWallpaper(context)) Result.success() else Result.retry()
    }
}
