package com.example.data.repository

import com.example.data.local.DailyPlanEntity
import com.example.data.local.FocusLockDao
import com.example.data.local.FocusLockPreferences
import com.example.data.local.FocusSessionEntity
import com.example.data.model.ActiveSessionState
import com.example.data.model.AppBackupData
import com.example.data.model.DailyScoreResult
import com.example.data.model.TaskType
import com.example.data.model.UserSettings
import com.example.util.BackupHelper
import com.example.util.DateUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class FocusLockRepository(
    private val dao: FocusLockDao,
    private val preferences: FocusLockPreferences
) {
    val userSettings: StateFlow<UserSettings> = preferences.userSettings
    val activeSessionState: StateFlow<ActiveSessionState> = preferences.activeSession

    fun getDailyPlan(date: String): Flow<DailyPlanEntity?> = dao.getDailyPlan(date)

    suspend fun getOrCreateDailyPlan(date: String): DailyPlanEntity {
        val existing = dao.getDailyPlanSync(date)
        if (existing != null) return existing

        // Check if yesterday had planned tomorrow tasks
        val yesterdayDate = DateUtil.getYesterdayDateString()
        val yesterdayPlan = dao.getDailyPlanSync(yesterdayDate)
        val defaultDuration = userSettings.value.defaultFocusDurationMinutes

        val newPlan = DailyPlanEntity(
            date = date,
            task1Title = yesterdayPlan?.tomorrowTask1 ?: "",
            task1DurationMinutes = defaultDuration,
            task2Title = yesterdayPlan?.tomorrowTask2 ?: "",
            task2DurationMinutes = defaultDuration,
            task3Title = yesterdayPlan?.tomorrowTask3 ?: "",
            task3DurationMinutes = defaultDuration
        )
        dao.insertDailyPlan(newPlan)
        return newPlan
    }

    suspend fun updateDailyPlan(plan: DailyPlanEntity) {
        dao.insertDailyPlan(plan)
    }

    fun getFocusSessionsForDate(date: String): Flow<List<FocusSessionEntity>> =
        dao.getFocusSessionsForDate(date)

    fun getAllDailyPlans(): Flow<List<DailyPlanEntity>> = dao.getAllDailyPlans()

    fun getAllFocusSessions(): Flow<List<FocusSessionEntity>> = dao.getAllFocusSessions()

    fun saveUserSettings(settings: UserSettings) {
        preferences.saveUserSettings(settings)
    }

    fun updateStreak(streak: Int, lastDate: String) {
        preferences.updateStreak(streak, lastDate)
    }

    fun updateRewardPoints(delta: Int) {
        preferences.updateRewardPoints(delta)
    }

    // Active session persistence
    fun saveActiveSession(session: ActiveSessionState) {
        preferences.saveActiveSession(session)
    }

    fun clearActiveSession() {
        preferences.clearActiveSession()
    }

    suspend fun startFocusSession(
        date: String,
        taskType: TaskType,
        taskTitle: String,
        durationMinutes: Int
    ): ActiveSessionState {
        val now = System.currentTimeMillis()
        val targetEndTime = now + (durationMinutes * 60 * 1000L)

        val entity = FocusSessionEntity(
            date = date,
            taskType = taskType.name,
            taskTitle = taskTitle,
            plannedDurationMinutes = durationMinutes,
            startTimeEpochMillis = now,
            targetEndTimeEpochMillis = targetEndTime,
            isCompleted = false,
            isAbandoned = false,
            isPaused = false
        )
        val sessionId = dao.insertFocusSession(entity)

        // If today's workday hasn't been marked started yet, mark it started
        val plan = getOrCreateDailyPlan(date)
        if (!plan.isWorkdayStarted) {
            val settings = userSettings.value
            val startedOnTime = DateUtil.isWithinWorkStartGracePeriod(
                settings.normalWorkStartHour,
                settings.normalWorkStartMinute,
                now
            )
            val updatedPlan = plan.copy(
                isWorkdayStarted = true,
                workdayStartTimeMillis = now,
                isStartedOnTime = startedOnTime
            )
            dao.insertDailyPlan(updatedPlan)
        }

        val activeState = ActiveSessionState(
            isActive = true,
            sessionId = sessionId,
            taskType = taskType,
            taskTitle = taskTitle,
            durationMinutes = durationMinutes,
            startTimeMillis = now,
            targetEndTimeMillis = targetEndTime,
            isPaused = false
        )
        saveActiveSession(activeState)
        return activeState
    }

    suspend fun pauseFocusSession(reason: String) {
        val current = preferences.activeSession.value
        if (!current.isActive || current.isPaused) return

        val now = System.currentTimeMillis()
        val updated = current.copy(
            isPaused = true,
            pausedAtMillis = now,
            pauseReason = reason
        )
        saveActiveSession(updated)

        val entity = dao.getFocusSessionById(current.sessionId)
        if (entity != null) {
            dao.updateFocusSession(
                entity.copy(
                    isPaused = true,
                    pauseTimestampEpochMillis = now,
                    pauseReason = reason
                )
            )
        }
    }

    suspend fun resumeFocusSession() {
        val current = preferences.activeSession.value
        if (!current.isActive || !current.isPaused) return

        val now = System.currentTimeMillis()
        val pauseDuration = (now - current.pausedAtMillis).coerceAtLeast(0L)
        val updated = current.copy(
            isPaused = false,
            pausedAtMillis = 0L,
            totalPausedDurationMillis = current.totalPausedDurationMillis + pauseDuration
        )
        saveActiveSession(updated)

        val entity = dao.getFocusSessionById(current.sessionId)
        if (entity != null) {
            dao.updateFocusSession(
                entity.copy(
                    isPaused = false,
                    pauseTimestampEpochMillis = null,
                    totalPausedDurationMillis = entity.totalPausedDurationMillis + pauseDuration
                )
            )
        }
    }

    suspend fun abandonFocusSession(reason: String) {
        val current = preferences.activeSession.value
        if (!current.isActive) return

        val now = System.currentTimeMillis()
        val totalDurationSeconds = (now - current.startTimeMillis - current.totalPausedDurationMillis) / 1000

        val entity = dao.getFocusSessionById(current.sessionId)
        if (entity != null) {
            dao.updateFocusSession(
                entity.copy(
                    endTimeEpochMillis = now,
                    isAbandoned = true,
                    isCompleted = false,
                    abandonReason = reason,
                    actualDurationSeconds = totalDurationSeconds.coerceAtLeast(0L)
                )
            )
        }
        clearActiveSession()
    }

    suspend fun completeFocusSession(isTaskFinished: Boolean) {
        val current = preferences.activeSession.value
        if (!current.isActive) return

        val now = System.currentTimeMillis()
        val totalDurationSeconds = (now - current.startTimeMillis - current.totalPausedDurationMillis) / 1000

        val entity = dao.getFocusSessionById(current.sessionId)
        if (entity != null) {
            dao.updateFocusSession(
                entity.copy(
                    endTimeEpochMillis = now,
                    isCompleted = true,
                    isAbandoned = false,
                    actualDurationSeconds = totalDurationSeconds.coerceAtLeast(0L)
                )
            )
        }

        if (isTaskFinished) {
            val plan = getOrCreateDailyPlan(entity?.date ?: DateUtil.getTodayDateString())
            val wasAlreadyComplete = plan.isTaskCompleted(current.taskType)
            val updatedPlan = when (current.taskType) {
                TaskType.TASK_1 -> plan.copy(task1Completed = true)
                TaskType.TASK_2 -> plan.copy(task2Completed = true)
                TaskType.TASK_3 -> plan.copy(task3Completed = true)
            }
            dao.insertDailyPlan(updatedPlan)

            if (!wasAlreadyComplete) {
                // Award +10 points
                updateRewardPoints(10)
            }
        }

        clearActiveSession()
    }

    suspend fun finishWorkdayAndReview(
        date: String,
        completedWhat: String,
        distraction: String,
        tomorrow1: String,
        tomorrow2: String,
        tomorrow3: String,
        hasUnfinishedWork: Boolean
    ): DailyScoreResult {
        val plan = getOrCreateDailyPlan(date)
        val sessions = dao.getFocusSessionsForDateSync(date)
        val completedSessionsCount = sessions.count { it.isCompleted }
        val settings = userSettings.value

        val scoreResult = DailyScoreResult.evaluate(
            plannedThreeTasks = plan.areAllThreeTasksPlanned,
            startedOnTime = plan.isStartedOnTime,
            completedTargetSessions = completedSessionsCount >= settings.dailyFocusTargetSessions,
            completedTasksCount = plan.completedTasksCount,
            totalTasksCount = 3,
            plannedTomorrow = tomorrow1.isNotBlank() || tomorrow2.isNotBlank() || tomorrow3.isNotBlank()
        )

        // Deduct 10 points for each uncompleted task at end of day
        val uncompletedCount = plan.uncompletedTasksCount
        if (uncompletedCount > 0) {
            // Apply uncompleted task deduction (-10 per uncompleted task)
            updateRewardPoints(-10 * uncompletedCount)
        }

        val updatedPlan = plan.copy(
            isWorkdayFinished = true,
            workdayFinishedWithUnfinishedWork = hasUnfinishedWork,
            completedReview = true,
            completedSummaryWhat = completedWhat,
            completedSummaryDistraction = distraction,
            tomorrowTask1 = tomorrow1,
            tomorrowTask2 = tomorrow2,
            tomorrowTask3 = tomorrow3,
            calculatedScore = scoreResult.totalScore,
            rewardPointsEarned = scoreResult.todayRewardPointsDelta,
            isScoreFinalized = true
        )
        dao.insertDailyPlan(updatedPlan)

        // Update streak
        val newStreak = if (scoreResult.isSuccessful) {
            settings.currentStreak + 1
        } else {
            0
        }
        updateStreak(newStreak, date)

        // Seed tomorrow's plan if tomorrow tasks provided
        val tomorrowDate = DateUtil.getTomorrowDateString()
        val existingTomorrow = dao.getDailyPlanSync(tomorrowDate)
        if (existingTomorrow == null) {
            dao.insertDailyPlan(
                DailyPlanEntity(
                    date = tomorrowDate,
                    task1Title = tomorrow1,
                    task1DurationMinutes = plan.task1DurationMinutes,
                    task2Title = tomorrow2,
                    task2DurationMinutes = plan.task2DurationMinutes,
                    task3Title = tomorrow3,
                    task3DurationMinutes = plan.task3DurationMinutes
                )
            )
        }

        return scoreResult
    }

    suspend fun exportBackupJson(): String {
        val settings = preferences.userSettings.value
        val plans = dao.getAllDailyPlansSync()
        val sessions = dao.getAllFocusSessionsSync()
        val backupData = AppBackupData(
            userSettings = settings,
            dailyPlans = plans,
            focusSessions = sessions
        )
        return BackupHelper.serializeToJson(backupData)
    }

    suspend fun importBackupJson(jsonString: String) {
        val backupData = BackupHelper.deserializeFromJson(jsonString)
        dao.deleteAllDailyPlans()
        dao.deleteAllFocusSessions()
        dao.insertAllPlans(backupData.dailyPlans)
        dao.insertAllSessions(backupData.focusSessions)
        saveUserSettings(backupData.userSettings)
    }
}
