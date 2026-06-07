package com.pushk.gatewallpaper

import android.app.WallpaperManager
import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random

object WallpaperSetter {
    suspend fun setFreshWallpaper(context: Context): Boolean {
        val days = CountdownCalculator.daysRemaining(context)
        val examName = ExamRepository.getExamName(context)
        val seed = nextSeed(context)
        val generated = ImageGenerator.generate(days, seed) ?: return false
        val wallpaper = WallpaperComposer.compose(generated, days, examName)

        return withContext(Dispatchers.IO) {
            try {
                WallpaperManager.getInstance(context).setBitmap(wallpaper)
                true
            } catch (_: Exception) {
                false
            }
        }
    }

    private fun nextSeed(context: Context): Long {
        val prefs = context.getSharedPreferences("wallpaper_seed", Context.MODE_PRIVATE)
        val lastSeed = prefs.getLong("last_seed", 0L)
        var seed = System.currentTimeMillis() xor System.nanoTime() xor Random.nextLong()
        if (seed == lastSeed) {
            seed += 1L
        }
        prefs.edit().putLong("last_seed", seed).apply()
        return seed
    }
}
