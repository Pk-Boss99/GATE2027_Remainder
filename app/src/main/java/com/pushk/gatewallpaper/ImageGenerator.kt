package com.pushk.gatewallpaper

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.min
import kotlin.random.Random

object ImageGenerator {
    suspend fun generate(days: Int, seed: Long): Bitmap? = withContext(Dispatchers.Default) {
        val random = Random(seed xor days.toLong())
        val palette = palettes.random(random)
        val bitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        drawBackground(canvas, palette)
        drawSoftLight(canvas, random, palette)
        when (random.nextInt(5)) {
            0 -> drawDeskScene(canvas, random, palette)
            1 -> drawSunriseWindowScene(canvas, random, palette)
            2 -> drawGoalPathScene(canvas, random, palette)
            3 -> drawMinimalShelfScene(canvas, random, palette)
            else -> drawFocusCircleScene(canvas, random, palette)
        }
        drawMinimalMotivationShapes(canvas, random, palette)

        bitmap
    }

    private fun drawBackground(canvas: Canvas, palette: Palette) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f,
                0f,
                0f,
                1920f,
                palette.top,
                palette.bottom,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, 1080f, 1920f, paint)
    }

    private fun drawSoftLight(canvas: Canvas, random: Random, palette: Palette) {
        val x = random.nextInt(180, 900).toFloat()
        val y = random.nextInt(180, 620).toFloat()
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                x,
                y,
                random.nextInt(360, 620).toFloat(),
                withAlpha(palette.glow, 120),
                Color.TRANSPARENT,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(x, y, 620f, paint)
    }

    private fun drawDeskScene(canvas: Canvas, random: Random, palette: Palette) {
        val deskTop = random.nextInt(1270, 1410).toFloat()
        val deskPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f,
                deskTop,
                0f,
                1920f,
                withAlpha(palette.surface, 235),
                darken(palette.surface, 0.56f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRoundRect(RectF(-40f, deskTop, 1120f, 1960f), 70f, 70f, deskPaint)

        drawNotebook(canvas, random, palette, deskTop)
        drawLampGlow(canvas, random, palette, deskTop)
    }

    private fun drawSunriseWindowScene(canvas: Canvas, random: Random, palette: Palette) {
        val windowLeft = random.nextInt(140, 250).toFloat()
        val windowTop = random.nextInt(150, 250).toFloat()
        val windowRight = windowLeft + random.nextInt(560, 720)
        val windowBottom = windowTop + random.nextInt(620, 780)

        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = withAlpha(Color.WHITE, 20)
        }.also { canvas.drawRoundRect(RectF(windowLeft, windowTop, windowRight, windowBottom), 38f, 38f, it) }

        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f,
                windowTop,
                0f,
                windowBottom,
                withAlpha(palette.glow, 115),
                withAlpha(palette.accent, 35),
                Shader.TileMode.CLAMP
            )
        }.also { canvas.drawRoundRect(RectF(windowLeft + 18f, windowTop + 18f, windowRight - 18f, windowBottom - 18f), 30f, 30f, it) }

        val frame = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = withAlpha(palette.surface, 190)
            strokeWidth = 18f
            style = Paint.Style.STROKE
        }
        canvas.drawRoundRect(RectF(windowLeft, windowTop, windowRight, windowBottom), 38f, 38f, frame)
        canvas.drawLine((windowLeft + windowRight) / 2f, windowTop + 16f, (windowLeft + windowRight) / 2f, windowBottom - 16f, frame)
        canvas.drawLine(windowLeft + 16f, (windowTop + windowBottom) / 2f, windowRight - 16f, (windowTop + windowBottom) / 2f, frame)

        val deskTop = random.nextInt(1320, 1420).toFloat()
        drawDeskSurface(canvas, palette, deskTop)
        drawNotebook(canvas, random, palette, deskTop)
    }

    private fun drawGoalPathScene(canvas: Canvas, random: Random, palette: Palette) {
        val pathPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                540f,
                780f,
                540f,
                1920f,
                withAlpha(palette.accent, 40),
                withAlpha(palette.glow, 125),
                Shader.TileMode.CLAMP
            )
        }
        val path = Path().apply {
            moveTo(450f, 760f)
            cubicTo(320f, 1040f, 240f, 1340f, 90f, 1920f)
            lineTo(990f, 1920f)
            cubicTo(820f, 1340f, 760f, 1040f, 630f, 760f)
            close()
        }
        canvas.drawPath(path, pathPaint)

        val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = withAlpha(Color.WHITE, 52)
            strokeWidth = 5f
        }
        repeat(7) { index ->
            val y = 900f + index * 130f
            val half = 55f + index * 42f
            canvas.drawLine(540f - half, y, 540f + half, y, linePaint)
        }

        val sunPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(540f, 590f, 260f, withAlpha(palette.glow, 145), Color.TRANSPARENT, Shader.TileMode.CLAMP)
        }
        canvas.drawCircle(540f, 590f, 260f, sunPaint)
    }

    private fun drawMinimalShelfScene(canvas: Canvas, random: Random, palette: Palette) {
        val shelfPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = withAlpha(palette.surface, 210) }
        val accentPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = withAlpha(palette.accent, 145) }
        val startY = random.nextInt(260, 380).toFloat()

        repeat(3) { shelf ->
            val y = startY + shelf * 250f
            canvas.drawRoundRect(RectF(120f, y, 960f, y + 22f), 12f, 12f, shelfPaint)
            repeat(random.nextInt(3, 6)) {
                val x = random.nextInt(150, 860).toFloat()
                val h = random.nextInt(80, 150).toFloat()
                val paint = if (random.nextBoolean()) shelfPaint else accentPaint
                canvas.drawRoundRect(RectF(x, y - h, x + random.nextInt(28, 48), y), 8f, 8f, paint)
            }
        }

        val deskTop = 1340f
        drawDeskSurface(canvas, palette, deskTop)
        drawLampGlow(canvas, random, palette, deskTop)
    }

    private fun drawFocusCircleScene(canvas: Canvas, random: Random, palette: Palette) {
        val centerX = random.nextInt(390, 690).toFloat()
        val centerY = random.nextInt(300, 540).toFloat()
        repeat(5) { index ->
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = withAlpha(palette.accent, 22 + index * 12)
                style = Paint.Style.STROKE
                strokeWidth = 7f
            }.also {
                val radius = 150f + index * 70f
                canvas.drawCircle(centerX, centerY, radius, it)
            }
        }

        val deskTop = random.nextInt(1320, 1440).toFloat()
        drawDeskSurface(canvas, palette, deskTop)
        drawNotebook(canvas, random, palette, deskTop)
    }

    private fun drawDeskSurface(canvas: Canvas, palette: Palette, deskTop: Float) {
        val deskPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f,
                deskTop,
                0f,
                1920f,
                withAlpha(palette.surface, 235),
                darken(palette.surface, 0.56f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRoundRect(RectF(-40f, deskTop, 1120f, 1960f), 70f, 70f, deskPaint)
    }

    private fun drawNotebook(canvas: Canvas, random: Random, palette: Palette, deskTop: Float) {
        val cx = random.nextInt(420, 660).toFloat()
        val cy = deskTop + random.nextInt(130, 230)
        val width = random.nextInt(390, 500).toFloat()
        val height = random.nextInt(250, 330).toFloat()
        val angle = random.nextInt(-7, 8).toFloat()

        canvas.save()
        canvas.rotate(angle, cx, cy)

        val shadow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(80, 0, 0, 0)
        }
        canvas.drawRoundRect(
            RectF(cx - width / 2f + 16f, cy - height / 2f + 22f, cx + width / 2f + 16f, cy + height / 2f + 22f),
            26f,
            26f,
            shadow
        )

        val page = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = palette.paper }
        canvas.drawRoundRect(RectF(cx - width / 2f, cy - height / 2f, cx + width / 2f, cy + height / 2f), 26f, 26f, page)

        val spine = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = withAlpha(palette.accent, 95) }
        canvas.drawRoundRect(RectF(cx - 8f, cy - height / 2f + 18f, cx + 8f, cy + height / 2f - 18f), 10f, 10f, spine)

        val line = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = withAlpha(palette.line, 65)
            strokeWidth = 3f
        }
        repeat(5) { index ->
            val y = cy - height / 2f + 62f + index * 34f
            canvas.drawLine(cx - width / 2f + 48f, y, cx - 34f, y, line)
            canvas.drawLine(cx + 34f, y, cx + width / 2f - 48f, y, line)
        }

        canvas.restore()
    }

    private fun drawLampGlow(canvas: Canvas, random: Random, palette: Palette, deskTop: Float) {
        val lampX = random.nextInt(150, 300).toFloat()
        val lampY = deskTop + random.nextInt(110, 180)
        val glow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                lampX,
                lampY,
                300f,
                withAlpha(palette.glow, 95),
                Color.TRANSPARENT,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(lampX, lampY, 300f, glow)

        val lamp = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = withAlpha(palette.accent, 210) }
        canvas.drawRoundRect(RectF(lampX - 46f, lampY - 20f, lampX + 46f, lampY + 18f), 22f, 22f, lamp)
        canvas.drawRoundRect(RectF(lampX - 10f, lampY + 14f, lampX + 10f, lampY + 140f), 10f, 10f, lamp)
    }

    private fun drawMinimalMotivationShapes(canvas: Canvas, random: Random, palette: Palette) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = withAlpha(palette.accent, 52)
            style = Paint.Style.STROKE
            strokeWidth = 5f
        }

        repeat(4) {
            val size = random.nextInt(80, 160).toFloat()
            val x = random.nextInt(90, 990).toFloat()
            val y = random.nextInt(150, 950).toFloat()
            canvas.drawRoundRect(RectF(x, y, x + size, y + size), 28f, 28f, paint)
        }

        val mountain = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = withAlpha(palette.accent, 42)
            style = Paint.Style.FILL
        }
        val baseY = random.nextInt(920, 1080).toFloat()
        val path = Path().apply {
            moveTo(100f, baseY)
            lineTo(330f, baseY - random.nextInt(130, 230))
            lineTo(520f, baseY)
            lineTo(700f, baseY - random.nextInt(120, 210))
            lineTo(980f, baseY)
            close()
        }
        canvas.drawPath(path, mountain)
    }

    private fun withAlpha(color: Int, alpha: Int): Int {
        return Color.argb(alpha.coerceIn(0, 255), Color.red(color), Color.green(color), Color.blue(color))
    }

    private fun darken(color: Int, factor: Float): Int {
        fun channel(value: Int) = min(255, (value * factor).toInt()).coerceAtLeast(0)
        return Color.rgb(channel(Color.red(color)), channel(Color.green(color)), channel(Color.blue(color)))
    }

    private data class Palette(
        val top: Int,
        val bottom: Int,
        val surface: Int,
        val paper: Int,
        val accent: Int,
        val glow: Int,
        val line: Int
    )

    private val palettes = listOf(
        Palette(Color.rgb(13, 24, 40), Color.rgb(5, 9, 18), Color.rgb(23, 35, 52), Color.rgb(235, 239, 232), Color.rgb(0, 212, 255), Color.rgb(255, 204, 112), Color.rgb(80, 105, 130)),
        Palette(Color.rgb(20, 30, 42), Color.rgb(8, 13, 24), Color.rgb(36, 43, 58), Color.rgb(238, 232, 216), Color.rgb(112, 210, 180), Color.rgb(255, 226, 150), Color.rgb(92, 111, 122)),
        Palette(Color.rgb(17, 25, 37), Color.rgb(4, 8, 16), Color.rgb(31, 38, 52), Color.rgb(230, 235, 240), Color.rgb(130, 170, 255), Color.rgb(145, 210, 255), Color.rgb(85, 100, 125)),
        Palette(Color.rgb(29, 28, 45), Color.rgb(9, 10, 20), Color.rgb(43, 39, 56), Color.rgb(236, 229, 218), Color.rgb(255, 183, 77), Color.rgb(255, 210, 140), Color.rgb(120, 100, 90))
    )
}
