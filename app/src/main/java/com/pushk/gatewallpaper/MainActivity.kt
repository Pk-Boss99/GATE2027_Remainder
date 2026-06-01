package com.pushk.gatewallpaper

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MainActivity : Activity() {
    private val scope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val days = CountdownCalculator.daysRemaining()
        findViewById<TextView>(R.id.tvDays).text = "$days days to GATE"

        WallpaperScheduler.scheduleDaily(this)

        val button = findViewById<Button>(R.id.btnSetNow)
        button.setOnClickListener {
            button.isEnabled = false
            Toast.makeText(this, "Setting a fresh wallpaper...", Toast.LENGTH_SHORT).show()

            scope.launch {
                val success = WallpaperSetter.setFreshWallpaper(applicationContext)
                button.isEnabled = true
                Toast.makeText(
                    this@MainActivity,
                    if (success) "Wallpaper updated" else "Could not set wallpaper",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}
