package com.example.data.model

data class UserSettings(
    val name: String = "",
    val normalWorkStartHour: Int = 9,
    val normalWorkStartMinute: Int = 0,
    val dailyFocusTargetSessions: Int = 3, // 2, 3, or 4
    val defaultFocusDurationMinutes: Int = 25, // 25, 50, or 90
    val isFirstTimeSetupCompleted: Boolean = false,
    val currentStreak: Int = 0,
    val totalRewardPoints: Int = 0, // Cumulative reward balance (+10 per task done, -10 per task missed)
    val lastEvaluatedDate: String = "",
    val restDays: List<Int> = listOf(6, 7) // 6=Saturday, 7=Sunday
) {
    val formattedWorkStartTime: String
        get() {
            val period = if (normalWorkStartHour >= 12) "PM" else "AM"
            val hour12 = when {
                normalWorkStartHour == 0 -> 12
                normalWorkStartHour > 12 -> normalWorkStartHour - 12
                else -> normalWorkStartHour
            }
            val minStr = normalWorkStartMinute.toString().padStart(2, '0')
            return "$hour12:$minStr $period"
        }
}
