package com.example.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtil {
    private val isoFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val displayFormat = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.US)
    private val shortDisplayFormat = SimpleDateFormat("MMM d, yyyy", Locale.US)

    fun getTodayDateString(): String {
        return isoFormat.format(Date())
    }

    fun getYesterdayDateString(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        return isoFormat.format(calendar.time)
    }

    fun getTomorrowDateString(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        return isoFormat.format(calendar.time)
    }

    fun formatToDisplay(dateString: String): String {
        return try {
            val date = isoFormat.parse(dateString) ?: return dateString
            displayFormat.format(date)
        } catch (_: Exception) {
            dateString
        }
    }

    fun formatToShortDisplay(dateString: String): String {
        return try {
            val date = isoFormat.parse(dateString) ?: return dateString
            shortDisplayFormat.format(date)
        } catch (_: Exception) {
            dateString
        }
    }

    fun getGreeting(name: String): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greetingPrefix = when (hour) {
            in 4..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            else -> "Good evening"
        }
        return if (name.isNotBlank()) "$greetingPrefix, $name" else greetingPrefix
    }

    fun formatDurationMmSs(millis: Long): String {
        val totalSeconds = (millis / 1000).coerceAtLeast(0)
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format(Locale.US, "%02d:%02d", minutes, seconds)
    }

    fun isWithinWorkStartGracePeriod(
        workStartHour: Int,
        workStartMinute: Int,
        actualTimeMillis: Long = System.currentTimeMillis()
    ): Boolean {
        val targetCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, workStartHour)
            set(Calendar.MINUTE, workStartMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val targetMillis = targetCal.timeInMillis
        val diffMinutes = (actualTimeMillis - targetMillis) / (60 * 1000)
        // Started up to 30 minutes early or up to 30 minutes after scheduled start time
        return diffMinutes in -30..30
    }
}
