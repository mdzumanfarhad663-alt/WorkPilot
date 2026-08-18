package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.ActiveSessionState
import com.example.data.model.TaskType
import com.example.data.model.UserSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FocusLockPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("focuslock_prefs", Context.MODE_PRIVATE)

    private val _userSettings = MutableStateFlow(loadUserSettings())
    val userSettings: StateFlow<UserSettings> = _userSettings.asStateFlow()

    private val _activeSession = MutableStateFlow(loadActiveSession())
    val activeSession: StateFlow<ActiveSessionState> = _activeSession.asStateFlow()

    private fun loadUserSettings(): UserSettings {
        return UserSettings(
            name = prefs.getString("user_name", "") ?: "",
            normalWorkStartHour = prefs.getInt("work_start_hour", 9),
            normalWorkStartMinute = prefs.getInt("work_start_minute", 0),
            dailyFocusTargetSessions = prefs.getInt("daily_focus_target", 3),
            defaultFocusDurationMinutes = prefs.getInt("default_focus_duration", 25),
            isFirstTimeSetupCompleted = prefs.getBoolean("setup_completed", false),
            currentStreak = prefs.getInt("current_streak", 0),
            totalRewardPoints = prefs.getInt("total_reward_points", 0),
            lastEvaluatedDate = prefs.getString("last_evaluated_date", "") ?: ""
        )
    }

    fun saveUserSettings(settings: UserSettings) {
        prefs.edit()
            .putString("user_name", settings.name)
            .putInt("work_start_hour", settings.normalWorkStartHour)
            .putInt("work_start_minute", settings.normalWorkStartMinute)
            .putInt("daily_focus_target", settings.dailyFocusTargetSessions)
            .putInt("default_focus_duration", settings.defaultFocusDurationMinutes)
            .putBoolean("setup_completed", settings.isFirstTimeSetupCompleted)
            .putInt("current_streak", settings.currentStreak)
            .putInt("total_reward_points", settings.totalRewardPoints)
            .putString("last_evaluated_date", settings.lastEvaluatedDate)
            .apply()
        _userSettings.value = settings
    }

    fun updateStreak(streak: Int, lastEvaluatedDate: String) {
        val updated = _userSettings.value.copy(
            currentStreak = streak,
            lastEvaluatedDate = lastEvaluatedDate
        )
        saveUserSettings(updated)
    }

    fun updateRewardPoints(delta: Int) {
        val currentPoints = _userSettings.value.totalRewardPoints
        val updated = _userSettings.value.copy(
            totalRewardPoints = currentPoints + delta
        )
        saveUserSettings(updated)
    }

    fun setTotalRewardPoints(points: Int) {
        val updated = _userSettings.value.copy(
            totalRewardPoints = points
        )
        saveUserSettings(updated)
    }

    private fun loadActiveSession(): ActiveSessionState {
        val isActive = prefs.getBoolean("session_is_active", false)
        if (!isActive) return ActiveSessionState()

        val taskTypeName = prefs.getString("session_task_type", TaskType.TASK_1.name) ?: TaskType.TASK_1.name
        val taskType = TaskType.fromString(taskTypeName)

        return ActiveSessionState(
            isActive = true,
            sessionId = prefs.getLong("session_id", 0L),
            taskType = taskType,
            taskTitle = prefs.getString("session_task_title", "") ?: "",
            durationMinutes = prefs.getInt("session_duration_minutes", 25),
            startTimeMillis = prefs.getLong("session_start_time", 0L),
            targetEndTimeMillis = prefs.getLong("session_target_end_time", 0L),
            isPaused = prefs.getBoolean("session_is_paused", false),
            pausedAtMillis = prefs.getLong("session_paused_at", 0L),
            totalPausedDurationMillis = prefs.getLong("session_total_paused", 0L),
            pauseReason = prefs.getString("session_pause_reason", null)
        )
    }

    fun saveActiveSession(session: ActiveSessionState) {
        prefs.edit()
            .putBoolean("session_is_active", session.isActive)
            .putLong("session_id", session.sessionId)
            .putString("session_task_type", session.taskType.name)
            .putString("session_task_title", session.taskTitle)
            .putInt("session_duration_minutes", session.durationMinutes)
            .putLong("session_start_time", session.startTimeMillis)
            .putLong("session_target_end_time", session.targetEndTimeMillis)
            .putBoolean("session_is_paused", session.isPaused)
            .putLong("session_paused_at", session.pausedAtMillis)
            .putLong("session_total_paused", session.totalPausedDurationMillis)
            .putString("session_pause_reason", session.pauseReason)
            .apply()
        _activeSession.value = session
    }

    fun clearActiveSession() {
        prefs.edit()
            .putBoolean("session_is_active", false)
            .remove("session_id")
            .remove("session_task_type")
            .remove("session_task_title")
            .remove("session_duration_minutes")
            .remove("session_start_time")
            .remove("session_target_end_time")
            .remove("session_is_paused")
            .remove("session_paused_at")
            .remove("session_total_paused")
            .remove("session_pause_reason")
            .apply()
        _activeSession.value = ActiveSessionState()
    }
}
