package com.pushk.gatewallpaper

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate

object WebSearchHelper {
    suspend fun searchExamDate(examName: String, year: String): LocalDate? {
        return withContext(Dispatchers.IO) {
            try {
                val query = "$examName $year exam date".replace(" ", "+")
                val url = URL("https://html.duckduckgo.com/html/?q=$query")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                
                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    var line: String?
                    val response = StringBuilder()
                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }
                    reader.close()
                    
                    return@withContext extractDate(response.toString(), year)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            null
        }
    }

    private fun extractDate(html: String, year: String): LocalDate? {
        val months = listOf(
            "January" to 1, "February" to 2, "March" to 3, "April" to 4, "May" to 5, "June" to 6,
            "July" to 7, "August" to 8, "September" to 9, "October" to 10, "November" to 11, "December" to 12,
            "Jan" to 1, "Feb" to 2, "Mar" to 3, "Apr" to 4, "Jun" to 6, "Jul" to 7, "Aug" to 8, "Sep" to 9, "Oct" to 10, "Nov" to 11, "Dec" to 12
        )
        
        for ((monthName, monthNum) in months) {
            // Pattern: Month DD, YYYY
            val regex1 = Regex("(?i)\\b$monthName\\s+(\\d{1,2})\\s*,?\\s*$year\\b")
            val match1 = regex1.find(html)
            if (match1 != null) {
                return try { LocalDate.of(year.toInt(), monthNum, match1.groupValues[1].toInt()) } catch(e: Exception) { null }
            }
            
            // Pattern: DD Month YYYY
            val regex2 = Regex("(?i)\\b(\\d{1,2})(?:st|nd|rd|th)?\\s+$monthName\\s*,?\\s*$year\\b")
            val match2 = regex2.find(html)
            if (match2 != null) {
                return try { LocalDate.of(year.toInt(), monthNum, match2.groupValues[1].toInt()) } catch(e: Exception) { null }
            }
        }
        return null
    }
}
