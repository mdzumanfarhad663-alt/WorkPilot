package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_plans")
data class DailyPlanEntity(
    @PrimaryKey val date: String, // YYYY-MM-DD
    val moneyTaskTitle: String = "",
    val moneyTaskCompleted: Boolean = false,
    val growthTaskTitle: String = "",
    val growthTaskCompleted: Boolean = false,
    val maintenanceTaskTitle: String = "",
    val maintenanceTaskCompleted: Boolean = false,
    val isWorkdayStarted: Boolean = false,
    val workdayStartTimeMillis: Long? = null,
    val isStartedOnTime: Boolean = false,
    val isWorkdayFinished: Boolean = false,
    val workdayFinishedWithUnfinishedWork: Boolean = false,
    val completedReview: Boolean = false,
    val completedSummaryWhat: String = "",
    val completedSummaryDistraction: String = "",
    val tomorrowMoneyTask: String = "",
    val tomorrowGrowthTask: String = "",
    val tomorrowMaintenanceTask: String = "",
    val calculatedScore: Int = 0,
    val isScoreFinalized: Boolean = false
) {
    val completedTasksCount: Int
        get() = (if (moneyTaskCompleted) 1 else 0) +
                (if (growthTaskCompleted) 1 else 0) +
                (if (maintenanceTaskCompleted) 1 else 0)

    val totalPlannedTasksCount: Int
        get() = (if (moneyTaskTitle.isNotBlank()) 1 else 0) +
                (if (growthTaskTitle.isNotBlank()) 1 else 0) +
                (if (maintenanceTaskTitle.isNotBlank()) 1 else 0)

    val areAllThreeTasksPlanned: Boolean
        get() = moneyTaskTitle.isNotBlank() && growthTaskTitle.isNotBlank() && maintenanceTaskTitle.isNotBlank()

    val areAllTasksCompleted: Boolean
        get() = areAllThreeTasksPlanned && moneyTaskCompleted && growthTaskCompleted && maintenanceTaskCompleted
}
