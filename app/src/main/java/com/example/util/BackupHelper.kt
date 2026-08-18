package com.example.util

import com.example.data.local.DailyPlanEntity
import com.example.data.local.FocusSessionEntity
import com.example.data.model.AppBackupData
import com.example.data.model.UserSettings
import org.json.JSONArray
import org.json.JSONObject

object BackupHelper {
    fun serializeToJson(backupData: AppBackupData): String {
        val root = JSONObject()
        root.put("app", "FocusLock")
        root.put("version", 1)
        root.put("exportedAt", backupData.exportedAt)

        val settingsObj = JSONObject().apply {
            put("name", backupData.userSettings.name)
            put("normalWorkStartHour", backupData.userSettings.normalWorkStartHour)
            put("normalWorkStartMinute", backupData.userSettings.normalWorkStartMinute)
            put("dailyFocusTargetSessions", backupData.userSettings.dailyFocusTargetSessions)
            put("defaultFocusDurationMinutes", backupData.userSettings.defaultFocusDurationMinutes)
            put("isFirstTimeSetupCompleted", backupData.userSettings.isFirstTimeSetupCompleted)
            put("currentStreak", backupData.userSettings.currentStreak)
            put("lastEvaluatedDate", backupData.userSettings.lastEvaluatedDate)
        }
        root.put("userSettings", settingsObj)

        val plansArray = JSONArray()
        for (plan in backupData.dailyPlans) {
            val p = JSONObject().apply {
                put("date", plan.date)
                put("moneyTaskTitle", plan.moneyTaskTitle)
                put("moneyTaskCompleted", plan.moneyTaskCompleted)
                put("growthTaskTitle", plan.growthTaskTitle)
                put("growthTaskCompleted", plan.growthTaskCompleted)
                put("maintenanceTaskTitle", plan.maintenanceTaskTitle)
                put("maintenanceTaskCompleted", plan.maintenanceTaskCompleted)
                put("isWorkdayStarted", plan.isWorkdayStarted)
                put("workdayStartTimeMillis", plan.workdayStartTimeMillis ?: 0L)
                put("isStartedOnTime", plan.isStartedOnTime)
                put("isWorkdayFinished", plan.isWorkdayFinished)
                put("workdayFinishedWithUnfinishedWork", plan.workdayFinishedWithUnfinishedWork)
                put("completedReview", plan.completedReview)
                put("completedSummaryWhat", plan.completedSummaryWhat)
                put("completedSummaryDistraction", plan.completedSummaryDistraction)
                put("tomorrowMoneyTask", plan.tomorrowMoneyTask)
                put("tomorrowGrowthTask", plan.tomorrowGrowthTask)
                put("tomorrowMaintenanceTask", plan.tomorrowMaintenanceTask)
                put("calculatedScore", plan.calculatedScore)
                put("isScoreFinalized", plan.isScoreFinalized)
            }
            plansArray.put(p)
        }
        root.put("dailyPlans", plansArray)

        val sessionsArray = JSONArray()
        for (session in backupData.focusSessions) {
            val s = JSONObject().apply {
                put("id", session.id)
                put("date", session.date)
                put("taskType", session.taskType)
                put("taskTitle", session.taskTitle)
                put("plannedDurationMinutes", session.plannedDurationMinutes)
                put("startTimeEpochMillis", session.startTimeEpochMillis)
                put("endTimeEpochMillis", session.endTimeEpochMillis ?: 0L)
                put("targetEndTimeEpochMillis", session.targetEndTimeEpochMillis)
                put("isCompleted", session.isCompleted)
                put("isAbandoned", session.isAbandoned)
                put("isPaused", session.isPaused)
                put("totalPausedDurationMillis", session.totalPausedDurationMillis)
                put("pauseReason", session.pauseReason ?: "")
                put("abandonReason", session.abandonReason ?: "")
                put("actualDurationSeconds", session.actualDurationSeconds)
            }
            sessionsArray.put(s)
        }
        root.put("focusSessions", sessionsArray)

        return root.toString(2)
    }

    fun deserializeFromJson(jsonString: String): AppBackupData {
        val root = JSONObject(jsonString)
        val settingsObj = root.optJSONObject("userSettings") ?: JSONObject()

        val settings = UserSettings(
            name = settingsObj.optString("name", ""),
            normalWorkStartHour = settingsObj.optInt("normalWorkStartHour", 9),
            normalWorkStartMinute = settingsObj.optInt("normalWorkStartMinute", 0),
            dailyFocusTargetSessions = settingsObj.optInt("dailyFocusTargetSessions", 3),
            defaultFocusDurationMinutes = settingsObj.optInt("defaultFocusDurationMinutes", 25),
            isFirstTimeSetupCompleted = settingsObj.optBoolean("isFirstTimeSetupCompleted", true),
            currentStreak = settingsObj.optInt("currentStreak", 0),
            lastEvaluatedDate = settingsObj.optString("lastEvaluatedDate", "")
        )

        val dailyPlans = mutableListOf<DailyPlanEntity>()
        val plansArray = root.optJSONArray("dailyPlans") ?: JSONArray()
        for (i in 0 until plansArray.length()) {
            val p = plansArray.getJSONObject(i)
            dailyPlans.add(
                DailyPlanEntity(
                    date = p.optString("date", ""),
                    moneyTaskTitle = p.optString("moneyTaskTitle", ""),
                    moneyTaskCompleted = p.optBoolean("moneyTaskCompleted", false),
                    growthTaskTitle = p.optString("growthTaskTitle", ""),
                    growthTaskCompleted = p.optBoolean("growthTaskCompleted", false),
                    maintenanceTaskTitle = p.optString("maintenanceTaskTitle", ""),
                    maintenanceTaskCompleted = p.optBoolean("maintenanceTaskCompleted", false),
                    isWorkdayStarted = p.optBoolean("isWorkdayStarted", false),
                    workdayStartTimeMillis = if (p.has("workdayStartTimeMillis")) p.optLong("workdayStartTimeMillis") else null,
                    isStartedOnTime = p.optBoolean("isStartedOnTime", false),
                    isWorkdayFinished = p.optBoolean("isWorkdayFinished", false),
                    workdayFinishedWithUnfinishedWork = p.optBoolean("workdayFinishedWithUnfinishedWork", false),
                    completedReview = p.optBoolean("completedReview", false),
                    completedSummaryWhat = p.optString("completedSummaryWhat", ""),
                    completedSummaryDistraction = p.optString("completedSummaryDistraction", ""),
                    tomorrowMoneyTask = p.optString("tomorrowMoneyTask", ""),
                    tomorrowGrowthTask = p.optString("tomorrowGrowthTask", ""),
                    tomorrowMaintenanceTask = p.optString("tomorrowMaintenanceTask", ""),
                    calculatedScore = p.optInt("calculatedScore", 0),
                    isScoreFinalized = p.optBoolean("isScoreFinalized", false)
                )
            )
        }

        val focusSessions = mutableListOf<FocusSessionEntity>()
        val sessionsArray = root.optJSONArray("focusSessions") ?: JSONArray()
        for (i in 0 until sessionsArray.length()) {
            val s = sessionsArray.getJSONObject(i)
            focusSessions.add(
                FocusSessionEntity(
                    id = s.optLong("id", 0L),
                    date = s.optString("date", ""),
                    taskType = s.optString("taskType", "MONEY"),
                    taskTitle = s.optString("taskTitle", ""),
                    plannedDurationMinutes = s.optInt("plannedDurationMinutes", 25),
                    startTimeEpochMillis = s.optLong("startTimeEpochMillis", 0L),
                    endTimeEpochMillis = if (s.has("endTimeEpochMillis")) s.optLong("endTimeEpochMillis") else null,
                    targetEndTimeEpochMillis = s.optLong("targetEndTimeEpochMillis", 0L),
                    isCompleted = s.optBoolean("isCompleted", false),
                    isAbandoned = s.optBoolean("isAbandoned", false),
                    isPaused = s.optBoolean("isPaused", false),
                    totalPausedDurationMillis = s.optLong("totalPausedDurationMillis", 0L),
                    pauseReason = s.optString("pauseReason").takeIf { it.isNotBlank() },
                    abandonReason = s.optString("abandonReason").takeIf { it.isNotBlank() },
                    actualDurationSeconds = s.optLong("actualDurationSeconds", 0L)
                )
            )
        }

        return AppBackupData(
            userSettings = settings,
            dailyPlans = dailyPlans,
            focusSessions = focusSessions
        )
    }
}
