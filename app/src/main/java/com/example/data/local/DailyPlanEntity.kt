package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.TaskType

@Entity(tableName = "daily_plans")
data class DailyPlanEntity(
    @PrimaryKey val date: String, // YYYY-MM-DD
    val task1Title: String = "",
    val task1Completed: Boolean = false,
    val task1DurationMinutes: Int = 25,
    val task2Title: String = "",
    val task2Completed: Boolean = false,
    val task2DurationMinutes: Int = 25,
    val task3Title: String = "",
    val task3Completed: Boolean = false,
    val task3DurationMinutes: Int = 25,
    val isWorkdayStarted: Boolean = false,
    val workdayStartTimeMillis: Long? = null,
    val isStartedOnTime: Boolean = false,
    val isWorkdayFinished: Boolean = false,
    val workdayFinishedWithUnfinishedWork: Boolean = false,
    val completedReview: Boolean = false,
    val completedSummaryWhat: String = "",
    val completedSummaryDistraction: String = "",
    val tomorrowTask1: String = "",
    val tomorrowTask2: String = "",
    val tomorrowTask3: String = "",
    val calculatedScore: Int = 0,
    val rewardPointsEarned: Int = 0,
    val isScoreFinalized: Boolean = false
) {
    val completedTasksCount: Int
        get() = (if (task1Completed) 1 else 0) +
                (if (task2Completed) 1 else 0) +
                (if (task3Completed) 1 else 0)

    val uncompletedTasksCount: Int
        get() = 3 - completedTasksCount

    val totalPlannedTasksCount: Int
        get() = (if (task1Title.isNotBlank()) 1 else 0) +
                (if (task2Title.isNotBlank()) 1 else 0) +
                (if (task3Title.isNotBlank()) 1 else 0)

    val areAllThreeTasksPlanned: Boolean
        get() = task1Title.isNotBlank() && task2Title.isNotBlank() && task3Title.isNotBlank()

    val areAllTasksCompleted: Boolean
        get() = areAllThreeTasksPlanned && task1Completed && task2Completed && task3Completed

    // Live daily point balance: +10 per completed, -10 per uncompleted
    val livePointsDelta: Int
        get() = (completedTasksCount * 10) - (uncompletedTasksCount * 10)

    fun getTaskTitle(taskType: TaskType): String = when (taskType) {
        TaskType.TASK_1 -> task1Title
        TaskType.TASK_2 -> task2Title
        TaskType.TASK_3 -> task3Title
    }

    fun isTaskCompleted(taskType: TaskType): Boolean = when (taskType) {
        TaskType.TASK_1 -> task1Completed
        TaskType.TASK_2 -> task2Completed
        TaskType.TASK_3 -> task3Completed
    }

    fun getTaskDuration(taskType: TaskType): Int = when (taskType) {
        TaskType.TASK_1 -> task1DurationMinutes
        TaskType.TASK_2 -> task2DurationMinutes
        TaskType.TASK_3 -> task3DurationMinutes
    }
}
