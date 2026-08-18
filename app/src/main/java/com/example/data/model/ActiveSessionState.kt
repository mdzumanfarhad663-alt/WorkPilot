package com.example.data.model

data class ActiveSessionState(
    val isActive: Boolean = false,
    val sessionId: Long = 0L,
    val taskType: TaskType = TaskType.MONEY,
    val taskTitle: String = "",
    val durationMinutes: Int = 25,
    val startTimeMillis: Long = 0L,
    val targetEndTimeMillis: Long = 0L,
    val isPaused: Boolean = false,
    val pausedAtMillis: Long = 0L,
    val totalPausedDurationMillis: Long = 0L,
    val pauseReason: String? = null
) {
    fun calculateRemainingMillis(currentMillis: Long = System.currentTimeMillis()): Long {
        if (!isActive) return 0L
        return if (isPaused) {
            val totalPause = totalPausedDurationMillis + (currentMillis - pausedAtMillis)
            val effectiveTarget = targetEndTimeMillis + totalPause
            (effectiveTarget - currentMillis).coerceAtLeast(0L)
        } else {
            val effectiveTarget = targetEndTimeMillis + totalPausedDurationMillis
            (effectiveTarget - currentMillis).coerceAtLeast(0L)
        }
    }

    fun calculateElapsedMillis(currentMillis: Long = System.currentTimeMillis()): Long {
        if (!isActive) return 0L
        val totalDuration = durationMinutes * 60 * 1000L
        val remaining = calculateRemainingMillis(currentMillis)
        return (totalDuration - remaining).coerceAtLeast(0L)
    }

    fun calculateProgress(currentMillis: Long = System.currentTimeMillis()): Float {
        if (!isActive || durationMinutes <= 0) return 0f
        val totalMillis = durationMinutes * 60 * 1000L
        val elapsed = calculateElapsedMillis(currentMillis)
        return (elapsed.toFloat() / totalMillis.toFloat()).coerceIn(0f, 1f)
    }

    fun isEligibleToFinishEarly(currentMillis: Long = System.currentTimeMillis()): Boolean {
        // Must have completed at least 80% of selected time
        return calculateProgress(currentMillis) >= 0.80f
    }
}
