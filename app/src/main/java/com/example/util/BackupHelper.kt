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
        root.put("app", "WorkPilot")
        root.put("version", 2)
        root.put("exportedAt", backupData.exportedAt)

        val settingsObj = JSONObject().apply {
            put("name", backupData.userSettings.name)
            put("normalWorkStartHour", backupData.userSettings.normalWorkStartHour)
            put("normalWorkStartMinute", backupData.userSettings.normalWorkStartMinute)
            put("dailyFocusTargetSessions", backupData.userSettings.dailyFocusTargetSessions)
            put("defaultFocusDurationMinutes", backupData.userSettings.defaultFocusDurationMinutes)
            put("isFirstTimeSetupCompleted", backupData.userSettings.isFirstTimeSetupCompleted)
            put("currentStreak", backupData.userSettings.currentStreak)
            put("totalRewardPoints", backupData.userSettings.totalRewardPoints)
            put("lastEvaluatedDate", backupData.userSettings.lastEvaluatedDate)
        }
        root.put("userSettings", settingsObj)

        val plansArray = JSONArray()
        for (plan in backupData.dailyPlans) {
            val p = JSONObject().apply {
                put("date", plan.date)
                put("task1Title", plan.task1Title)
                put("task1Completed", plan.task1Completed)
                put("task1DurationMinutes", plan.task1DurationMinutes)
                put("task2Title", plan.task2Title)
                put("task2Completed", plan.task2Completed)
                put("task2DurationMinutes", plan.task2DurationMinutes)
                put("task3Title", plan.task3Title)
                put("task3Completed", plan.task3Completed)
                put("task3DurationMinutes", plan.task3DurationMinutes)
                put("isWorkdayStarted", plan.isWorkdayStarted)
                put("workdayStartTimeMillis", plan.workdayStartTimeMillis ?: 0L)
                put("isStartedOnTime", plan.isStartedOnTime)
                put("isWorkdayFinished", plan.isWorkdayFinished)
                put("workdayFinishedWithUnfinishedWork", plan.workdayFinishedWithUnfinishedWork)
                put("completedReview", plan.completedReview)
                put("completedSummaryWhat", plan.completedSummaryWhat)
                put("completedSummaryDistraction", plan.completedSummaryDistraction)
                put("tomorrowTask1", plan.tomorrowTask1)
                put("tomorrowTask2", plan.tomorrowTask2)
                put("tomorrowTask3", plan.tomorrowTask3)
                put("calculatedScore", plan.calculatedScore)
                put("rewardPointsEarned", plan.rewardPointsEarned)
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
            totalRewardPoints = settingsObj.optInt("totalRewardPoints", 0),
            lastEvaluatedDate = settingsObj.optString("lastEvaluatedDate", "")
        )

        val dailyPlans = mutableListOf<DailyPlanEntity>()
        val plansArray = root.optJSONArray("dailyPlans") ?: JSONArray()
        for (i in 0 until plansArray.length()) {
            val p = plansArray.getJSONObject(i)
            // Support backward compatibility if older keys existed
            val t1 = if (p.has("task1Title")) p.optString("task1Title", "") else p.optString("moneyTaskTitle", "")
            val t1Done = if (p.has("task1Completed")) p.optBoolean("task1Completed", false) else p.optBoolean("moneyTaskCompleted", false)
            val t1Dur = p.optInt("task1DurationMinutes", 25)

            val t2 = if (p.has("task2Title")) p.optString("task2Title", "") else p.optString("growthTaskTitle", "")
            val t2Done = if (p.has("task2Completed")) p.optBoolean("task2Completed", false) else p.optBoolean("growthTaskCompleted", false)
            val t2Dur = p.optInt("task2DurationMinutes", 25)

            val t3 = if (p.has("task3Title")) p.optString("task3Title", "") else p.optString("maintenanceTaskTitle", "")
            val t3Done = if (p.has("task3Completed")) p.optBoolean("task3Completed", false) else p.optBoolean("maintenanceTaskCompleted", false)
            val t3Dur = p.optInt("task3DurationMinutes", 25)

            val tm1 = if (p.has("tomorrowTask1")) p.optString("tomorrowTask1", "") else p.optString("tomorrowMoneyTask", "")
            val tm2 = if (p.has("tomorrowTask2")) p.optString("tomorrowTask2", "") else p.optString("tomorrowGrowthTask", "")
            val tm3 = if (p.has("tomorrowTask3")) p.optString("tomorrowTask3", "") else p.optString("tomorrowMaintenanceTask", "")

            dailyPlans.add(
                DailyPlanEntity(
                    date = p.optString("date", ""),
                    task1Title = t1,
                    task1Completed = t1Done,
                    task1DurationMinutes = t1Dur,
                    task2Title = t2,
                    task2Completed = t2Done,
                    task2DurationMinutes = t2Dur,
                    task3Title = t3,
                    task3Completed = t3Done,
                    task3DurationMinutes = t3Dur,
                    isWorkdayStarted = p.optBoolean("isWorkdayStarted", false),
                    workdayStartTimeMillis = if (p.has("workdayStartTimeMillis")) p.optLong("workdayStartTimeMillis") else null,
                    isStartedOnTime = p.optBoolean("isStartedOnTime", false),
                    isWorkdayFinished = p.optBoolean("isWorkdayFinished", false),
                    workdayFinishedWithUnfinishedWork = p.optBoolean("workdayFinishedWithUnfinishedWork", false),
                    completedReview = p.optBoolean("completedReview", false),
                    completedSummaryWhat = p.optString("completedSummaryWhat", ""),
                    completedSummaryDistraction = p.optString("completedSummaryDistraction", ""),
                    tomorrowTask1 = tm1,
                    tomorrowTask2 = tm2,
                    tomorrowTask3 = tm3,
                    calculatedScore = p.optInt("calculatedScore", 0),
                    rewardPointsEarned = p.optInt("rewardPointsEarned", 0),
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
                    taskType = s.optString("taskType", "TASK_1"),
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
