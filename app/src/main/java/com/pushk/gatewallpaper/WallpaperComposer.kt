package com.pushk.gatewallpaper

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface

object WallpaperComposer {
    fun compose(base: Bitmap, days: Int, examName: String): Bitmap {
        val scaledBase = Bitmap.createScaledBitmap(base, 1080, 1920, true)
        val result = scaledBase.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(result)
        val w = result.width.toFloat()
        val h = result.height.toFloat()

        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(170, 0, 0, 0)
        }.also { canvas.drawRoundRect(72f, h * 0.50f, w - 72f, h * 0.80f, 36f, 36f, it) }

        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 240f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            setShadowLayer(30f, 0f, 0f, Color.parseColor("#00D4FF"))
        }.also { canvas.drawText("$days", w / 2f, h * 0.66f, it) }

        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#00D4FF")
            textSize = 52f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            letterSpacing = 0.15f
        }.also { canvas.drawText("DAYS REMAINING", w / 2f, h * 0.73f, it) }

        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(220, 255, 255, 255)
            textSize = 36f
            textAlign = Paint.Align.CENTER
            letterSpacing = 0.2f
        }.also { canvas.drawText("TO ${examName.uppercase()} EXAM", w / 2f, h * 0.77f, it) }

        return result
    }
}
