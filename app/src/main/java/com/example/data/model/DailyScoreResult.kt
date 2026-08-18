package com.example.data.model

data class DailyScoreResult(
    val plannedThreeTasksPoint: Int, // 0 or 1
    val startedOnTimePoint: Int, // 0 or 1
    val completedTargetSessionsPoint: Int, // 0 or 1
    val completedMoneyTaskPoint: Int, // 0 or 1
    val plannedTomorrowPoint: Int, // 0 or 1
    val totalScore: Int, // 0..5
    val ratingLabel: String, // "Excellent", "Successful", "Needs improvement", "Failed day"
    val isSuccessful: Boolean // totalScore >= 4
) {
    companion object {
        fun evaluate(
            plannedThreeTasks: Boolean,
            startedOnTime: Boolean,
            completedTargetSessions: Boolean,
            completedMoneyTask: Boolean,
            plannedTomorrow: Boolean
        ): DailyScoreResult {
            val p1 = if (plannedThreeTasks) 1 else 0
            val p2 = if (startedOnTime) 1 else 0
            val p3 = if (completedTargetSessions) 1 else 0
            val p4 = if (completedMoneyTask) 1 else 0
            val p5 = if (plannedTomorrow) 1 else 0
            val total = p1 + p2 + p3 + p4 + p5

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
                completedMoneyTaskPoint = p4,
                plannedTomorrowPoint = p5,
                totalScore = total,
                ratingLabel = label,
                isSuccessful = success
            )
        }
    }
}
