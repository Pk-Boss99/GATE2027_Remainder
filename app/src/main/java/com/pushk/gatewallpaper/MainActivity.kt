package com.pushk.gatewallpaper

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainActivity : Activity() {
    private val scope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        updateUI()

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

        findViewById<Button>(R.id.btnChangeExam).setOnClickListener {
            showExamSelectionDialog()
        }

        findViewById<Button>(R.id.btnChangeDate).setOnClickListener {
            showDatePicker(ExamRepository.getExamName(this), ExamRepository.getExamDate(this))
        }

        findViewById<TextView>(R.id.btnMenu).setOnClickListener { view ->
            val popup = PopupMenu(this, view)
            popup.menu.add("Check for Updates")
            popup.setOnMenuItemClickListener {
                if (it.title == "Check for Updates") {
                    UpdateManager.checkForUpdates(this@MainActivity)
                }
                true
            }
            popup.show()
        }
    }

    private fun updateUI() {
        val days = CountdownCalculator.daysRemaining(this)
        val examName = ExamRepository.getExamName(this)
        findViewById<TextView>(R.id.tvDays).text = "$days days to $examName"
    }

    private fun showExamSelectionDialog() {
        val exams = arrayOf("GATE 2027", "UPSC CSE 2027", "NEET UG 2027", "IIT-JEE Advanced 2027", "Custom Exam")
        val defaultDates = arrayOf(
            LocalDate.of(2027, 2, 6),
            LocalDate.of(2027, 5, 30),
            LocalDate.of(2027, 5, 2),
            LocalDate.of(2027, 6, 6)
        )

        AlertDialog.Builder(this)
            .setTitle("Select Exam")
            .setItems(exams) { _, which ->
                if (which == exams.size - 1) {
                    showCustomExamDialog()
                } else {
                    ExamRepository.setExam(this, exams[which], defaultDates[which])
                    updateUI()
                }
            }
            .show()
    }

    private fun showCustomExamDialog() {
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 40, 50, 10)

        val nameInput = EditText(this)
        nameInput.hint = "Exam Name (e.g. CAT)"
        layout.addView(nameInput)

        val yearInput = EditText(this)
        yearInput.hint = "Year (e.g. 2027)"
        layout.addView(yearInput)

        AlertDialog.Builder(this)
            .setTitle("Custom Exam")
            .setView(layout)
            .setPositiveButton("OK") { _, _ ->
                val name = nameInput.text.toString().trim()
                val year = yearInput.text.toString().trim()
                if (name.isNotEmpty() && year.isNotEmpty()) {
                    performWebSearch(name, year)
                } else {
                    Toast.makeText(this, "Please enter both fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun performWebSearch(name: String, year: String) {
        val dialog = ProgressDialog.show(this, "Searching...", "Finding exam date...", true)
        scope.launch {
            val date = WebSearchHelper.searchExamDate(name, year)
            dialog.dismiss()
            val fullName = "$name $year"
            if (date != null) {
                showFoundDateDialog(fullName, date)
            } else {
                Toast.makeText(this@MainActivity, "Could not find date automatically.", Toast.LENGTH_LONG).show()
                showDatePicker(fullName, LocalDate.of(year.toIntOrNull() ?: 2027, 1, 1))
            }
        }
    }

    private fun showFoundDateDialog(examName: String, foundDate: LocalDate) {
        AlertDialog.Builder(this)
            .setTitle("Confirm Exam Date")
            .setMessage("Exam: $examName\nFound Date: $foundDate\n\nIs this correct?")
            .setPositiveButton("Confirm") { _, _ ->
                ExamRepository.setExam(this, examName, foundDate)
                updateUI()
            }
            .setNegativeButton("Edit Date") { _, _ ->
                showDatePicker(examName, foundDate)
            }
            .show()
    }

    private fun showDatePicker(examName: String, currentDate: LocalDate) {
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val newDate = LocalDate.of(year, month + 1, dayOfMonth)
                ExamRepository.setExam(this, examName, newDate)
                updateUI()
            },
            currentDate.year,
            currentDate.monthValue - 1,
            currentDate.dayOfMonth
        ).show()
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}
