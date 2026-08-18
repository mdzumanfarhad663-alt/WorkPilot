package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "focus_sessions")
data class FocusSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // YYYY-MM-DD
    val taskType: String, // "MONEY", "GROWTH", "MAINTENANCE"
    val taskTitle: String,
    val plannedDurationMinutes: Int, // 25, 50, 90
    val startTimeEpochMillis: Long,
    val endTimeEpochMillis: Long? = null,
    val targetEndTimeEpochMillis: Long,
    val isCompleted: Boolean = false,
    val isAbandoned: Boolean = false,
    val isPaused: Boolean = false,
    val pauseTimestampEpochMillis: Long? = null,
    val totalPausedDurationMillis: Long = 0L,
    val pauseReason: String? = null,
    val abandonReason: String? = null,
    val actualDurationSeconds: Long = 0L
)
