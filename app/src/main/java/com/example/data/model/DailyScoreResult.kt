package com.example.data.model

data class DailyScoreResult(
    val plannedThreeTasksPoint: Int, // 0 or 1
    val startedOnTimePoint: Int, // 0 or 1
    val completedTargetSessionsPoint: Int, // 0 or 1
    val completedAllTasksPoint: Int, // 0 or 1
    val plannedTomorrowPoint: Int, // 0 or 1
    val totalScore: Int, // 0..5
    val ratingLabel: String, // "Excellent", "Successful", "Needs improvement", "Failed day"
    val isSuccessful: Boolean, // totalScore >= 4
    val completedTasksCount: Int = 0,
    val uncompletedTasksCount: Int = 0,
    val todayRewardPointsDelta: Int = 0 // +10 per completed task, -10 per uncompleted task
) {
    companion object {
        fun evaluate(
            plannedThreeTasks: Boolean,
            startedOnTime: Boolean,
            completedTargetSessions: Boolean,
            completedTasksCount: Int,
            totalTasksCount: Int = 3,
            plannedTomorrow: Boolean
        ): DailyScoreResult {
            val p1 = if (plannedThreeTasks) 1 else 0
            val p2 = if (startedOnTime) 1 else 0
            val p3 = if (completedTargetSessions) 1 else 0
            val p4 = if (completedTasksCount >= 1) 1 else 0
            val p5 = if (plannedTomorrow) 1 else 0
            val total = p1 + p2 + p3 + p4 + p5

            val uncompleted = (totalTasksCount - completedTasksCount).coerceAtLeast(0)
            val pointsDelta = (completedTasksCount * 10) - (uncompleted * 10)

            val (label, success) = when (total) {
                5 -> "Excellent" to true
                4 -> "Successful" to true
                3 -> "Needs improvement" to false
                else -> "Failed day" to false
            }

            return DailyScoreResult(
                plannedThreeTasksPoint = p1,
                startedOnTimePoint = p2,
                completedTargetSessionsPoint = p3,
                completedAllTasksPoint = p4,
                plannedTomorrowPoint = p5,
                totalScore = total,
                ratingLabel = label,
                isSuccessful = success,
                completedTasksCount = completedTasksCount,
                uncompletedTasksCount = uncompleted,
                todayRewardPointsDelta = pointsDelta
            )
        }
    }
}
