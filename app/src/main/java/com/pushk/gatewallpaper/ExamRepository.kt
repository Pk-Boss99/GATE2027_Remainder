package com.pushk.gatewallpaper

import android.content.Context
import java.time.LocalDate

object ExamRepository {
    private const val PREFS_NAME = "exam_preferences"
    private const val KEY_EXAM_NAME = "exam_name"
    private const val KEY_EXAM_DATE = "exam_date"

    private const val DEFAULT_EXAM_NAME = "GATE 2027"
    private const val DEFAULT_EXAM_DATE = "2027-02-06"

    fun getExamName(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_EXAM_NAME, DEFAULT_EXAM_NAME) ?: DEFAULT_EXAM_NAME
    }

    fun getExamDate(context: Context): LocalDate {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val dateString = prefs.getString(KEY_EXAM_DATE, DEFAULT_EXAM_DATE) ?: DEFAULT_EXAM_DATE
        return try {
            LocalDate.parse(dateString)
        } catch (e: Exception) {
            LocalDate.parse(DEFAULT_EXAM_DATE)
        }
    }

    fun setExam(context: Context, name: String, date: LocalDate) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_EXAM_NAME, name)
            .putString(KEY_EXAM_DATE, date.toString())
            .apply()
    }
}
