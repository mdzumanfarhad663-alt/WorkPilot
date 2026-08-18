package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.DailyPlanEntity
import com.example.data.local.FocusLockPreferences
import com.example.data.local.FocusSessionEntity
import com.example.data.model.ActiveSessionState
import com.example.data.model.DailyScoreResult
import com.example.data.model.TaskType
import com.example.data.model.UserSettings
import com.example.data.repository.FocusLockRepository
import com.example.util.DateUtil
import com.example.util.NotificationHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class NavigationTab(val label: String) {
    TODAY("Today"),
    FOCUS("Focus"),
    HISTORY("History")
}

data class FocusLockUiState(
    val userSettings: UserSettings = UserSettings(),
    val todayPlan: DailyPlanEntity? = null,
    val todaySessions: List<FocusSessionEntity> = emptyList(),
    val allPlans: List<DailyPlanEntity> = emptyList(),
    val allSessions: List<FocusSessionEntity> = emptyList(),
    val activeSession: ActiveSessionState = ActiveSessionState(),
    val remainingMillis: Long = 0L,
    val isEligibleToFinishEarly: Boolean = false,
    val selectedTab: NavigationTab = NavigationTab.TODAY,
    val showFirstTimeSetup: Boolean = false,
    val showEndOfDayReviewDialog: Boolean = false,
    val showUnfinishedWorkDisciplineDialog: Boolean = false,
    val showPauseDialog: Boolean = false,
    val showGiveUpDialog: Boolean = false,
    val showTaskCompletionConfirmDialog: Boolean = false,
    val showBackupDialog: Boolean = false,
    val lastReviewSummaryResult: DailyScoreResult? = null,
    val messageToast: String? = null
) {
    val liveDailyScore: DailyScoreResult
        get() {
            val plan = todayPlan ?: DailyPlanEntity(date = DateUtil.getTodayDateString())
            val completedSessionsCount = todaySessions.count { it.isCompleted }
            return DailyScoreResult.evaluate(
                plannedThreeTasks = plan.areAllThreeTasksPlanned,
                startedOnTime = plan.isStartedOnTime,
                completedTargetSessions = completedSessionsCount >= userSettings.dailyFocusTargetSessions,
                completedMoneyTask = plan.moneyTaskCompleted,
                plannedTomorrow = plan.tomorrowMoneyTask.isNotBlank() || plan.tomorrowGrowthTask.isNotBlank() || plan.tomorrowMaintenanceTask.isNotBlank() || plan.completedReview
            )
        }
}

class FocusLockViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val preferences = FocusLockPreferences(application)
    val repository = FocusLockRepository(database.focusLockDao(), preferences)

    private val _selectedTab = MutableStateFlow(NavigationTab.TODAY)
    private val _dialogState = MutableStateFlow(DialogsState())
    private val _remainingMillis = MutableStateFlow(0L)
    private val _isEligibleToFinishEarly = MutableStateFlow(false)
    private val _messageToast = MutableStateFlow<String?>(null)
    private val _lastReviewResult = MutableStateFlow<DailyScoreResult?>(null)

    private var timerTickerJob: Job? = null

    private data class DialogsState(
        val showFirstTimeSetup: Boolean = false,
        val showEndOfDayReviewDialog: Boolean = false,
        val showUnfinishedWorkDisciplineDialog: Boolean = false,
        val showPauseDialog: Boolean = false,
        val showGiveUpDialog: Boolean = false,
        val showTaskCompletionConfirmDialog: Boolean = false,
        val showBackupDialog: Boolean = false
    )

    val todayDate: String = DateUtil.getTodayDateString()

    val uiState: StateFlow<FocusLockUiState> = combine(
        repository.userSettings,
        repository.getDailyPlan(todayDate),
        repository.getFocusSessionsForDate(todayDate),
        repository.getAllDailyPlans(),
        repository.getAllFocusSessions(),
        repository.activeSessionState,
        _selectedTab,
        _dialogState,
        _remainingMillis,
        _isEligibleToFinishEarly,
        _messageToast,
        _lastReviewResult
    ) { args ->
        val userSettings = args[0] as UserSettings
        val todayPlan = args[1] as DailyPlanEntity?
        @Suppress("UNCHECKED_CAST")
        val todaySessions = args[2] as List<FocusSessionEntity>
        @Suppress("UNCHECKED_CAST")
        val allPlans = args[3] as List<DailyPlanEntity>
        @Suppress("UNCHECKED_CAST")
        val allSessions = args[4] as List<FocusSessionEntity>
        val activeSession = args[5] as ActiveSessionState
        val selectedTab = args[6] as NavigationTab
        val dialogs = args[7] as DialogsState
        val remainingMillis = args[8] as Long
        val isEligible = args[9] as Boolean
        val messageToast = args[10] as String?
        val lastReview = args[11] as DailyScoreResult?

        FocusLockUiState(
            userSettings = userSettings,
            todayPlan = todayPlan,
            todaySessions = todaySessions,
            allPlans = allPlans,
            allSessions = allSessions,
            activeSession = activeSession,
            remainingMillis = remainingMillis,
            isEligibleToFinishEarly = isEligible,
            selectedTab = selectedTab,
            showFirstTimeSetup = !userSettings.isFirstTimeSetupCompleted,
            showEndOfDayReviewDialog = dialogs.showEndOfDayReviewDialog,
            showUnfinishedWorkDisciplineDialog = dialogs.showUnfinishedWorkDisciplineDialog,
            showPauseDialog = dialogs.showPauseDialog,
            showGiveUpDialog = dialogs.showGiveUpDialog,
            showTaskCompletionConfirmDialog = dialogs.showTaskCompletionConfirmDialog,
            showBackupDialog = dialogs.showBackupDialog,
            lastReviewSummaryResult = lastReview,
            messageToast = messageToast
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FocusLockUiState()
    )

    init {
        NotificationHelper.createNotificationChannel(application)
        viewModelScope.launch {
            repository.getOrCreateDailyPlan(todayDate)
            startOrSyncTimerTicker()
        }
    }

    private fun startOrSyncTimerTicker() {
        timerTickerJob?.cancel()
        timerTickerJob = viewModelScope.launch {
            while (true) {
                val current = repository.activeSessionState.value
                if (current.isActive) {
                    val now = System.currentTimeMillis()
                    val remaining = current.calculateRemainingMillis(now)
                    _remainingMillis.value = remaining
                    _isEligibleToFinishEarly.value = current.isEligibleToFinishEarly(now)

                    // If remaining is 0 and not paused, trigger completion prompt
                    if (remaining <= 0 && !current.isPaused) {
                        NotificationHelper.sendSessionCompletedNotification(
                            getApplication(),
                            current.taskTitle
                        )
                        _dialogState.value = _dialogState.value.copy(showTaskCompletionConfirmDialog = true)
                    }
                } else {
                    _remainingMillis.value = 0L
                    _isEligibleToFinishEarly.value = false
                }
                delay(1000)
            }
        }
    }

    fun completeFirstTimeSetup(
        name: String,
        workStartHour: Int,
        workStartMinute: Int,
        dailyTarget: Int,
        durationMinutes: Int
    ) {
        viewModelScope.launch {
            val updated = UserSettings(
                name = name.trim(),
                normalWorkStartHour = workStartHour,
                normalWorkStartMinute = workStartMinute,
                dailyFocusTargetSessions = dailyTarget,
                defaultFocusDurationMinutes = durationMinutes,
                isFirstTimeSetupCompleted = true,
                currentStreak = 0
            )
            repository.saveUserSettings(updated)
            _messageToast.value = "Setup completed. Let's make today count!"
        }
    }

    fun updateTask(taskType: TaskType, title: String) {
        viewModelScope.launch {
            val currentPlan = repository.getOrCreateDailyPlan(todayDate)
            val updatedPlan = when (taskType) {
                TaskType.MONEY -> currentPlan.copy(moneyTaskTitle = title.trim())
                TaskType.GROWTH -> currentPlan.copy(growthTaskTitle = title.trim())
                TaskType.MAINTENANCE -> currentPlan.copy(maintenanceTaskTitle = title.trim())
            }
            repository.updateDailyPlan(updatedPlan)
        }
    }

    fun toggleTaskComplete(taskType: TaskType) {
        viewModelScope.launch {
            val currentPlan = repository.getOrCreateDailyPlan(todayDate)
            val updatedPlan = when (taskType) {
                TaskType.MONEY -> currentPlan.copy(moneyTaskCompleted = !currentPlan.moneyTaskCompleted)
                TaskType.GROWTH -> currentPlan.copy(growthTaskCompleted = !currentPlan.growthTaskCompleted)
                TaskType.MAINTENANCE -> currentPlan.copy(maintenanceTaskCompleted = !currentPlan.maintenanceTaskCompleted)
            }
            repository.updateDailyPlan(updatedPlan)
        }
    }

    fun startFocusSession(taskType: TaskType, taskTitle: String, durationMinutes: Int? = null) {
        val cleanTitle = if (taskTitle.isBlank()) {
            when (taskType) {
                TaskType.MONEY -> "Money Task: Revenue Generation"
                TaskType.GROWTH -> "Growth Task: System & Skill Upgrade"
                TaskType.MAINTENANCE -> "Maintenance Task: Admin & Operations"
            }
        } else {
            taskTitle.trim()
        }
        viewModelScope.launch {
            // Ensure the daily plan stores this task title as well
            val currentPlan = repository.getOrCreateDailyPlan(todayDate)
            val updatedPlan = when (taskType) {
                TaskType.MONEY -> currentPlan.copy(moneyTaskTitle = cleanTitle)
                TaskType.GROWTH -> currentPlan.copy(growthTaskTitle = cleanTitle)
                TaskType.MAINTENANCE -> currentPlan.copy(maintenanceTaskTitle = cleanTitle)
            }
            repository.updateDailyPlan(updatedPlan)

            val duration = durationMinutes ?: repository.userSettings.value.defaultFocusDurationMinutes
            repository.startFocusSession(todayDate, taskType, cleanTitle, duration)
            _selectedTab.value = NavigationTab.FOCUS
            startOrSyncTimerTicker()
            _messageToast.value = "Focus session started: $cleanTitle"
        }
    }

    fun pauseFocusSession(reason: String) {
        viewModelScope.launch {
            repository.pauseFocusSession(reason)
            _dialogState.value = _dialogState.value.copy(showPauseDialog = false)
            _messageToast.value = "Session paused: $reason"
        }
    }

    fun resumeFocusSession() {
        viewModelScope.launch {
            repository.resumeFocusSession()
            _messageToast.value = "Focus session resumed!"
        }
    }

    fun abandonFocusSession(reason: String) {
        viewModelScope.launch {
            repository.abandonFocusSession(reason)
            _dialogState.value = _dialogState.value.copy(showGiveUpDialog = false)
            _messageToast.value = "Session ended. Reset and start again."
        }
    }

    fun completeFocusSession(isTaskCompleted: Boolean) {
        viewModelScope.launch {
            repository.completeFocusSession(isTaskCompleted)
            _dialogState.value = _dialogState.value.copy(showTaskCompletionConfirmDialog = false)
            _selectedTab.value = NavigationTab.TODAY
            _messageToast.value = if (isTaskCompleted) "Great job! Task marked complete." else "Session recorded! Task kept active."
        }
    }

    fun requestFinishWorkday() {
        val plan = uiState.value.todayPlan
        val hasIncompleteTasks = plan != null && plan.areAllThreeTasksPlanned && !plan.areAllTasksCompleted
        if (hasIncompleteTasks) {
            _dialogState.value = _dialogState.value.copy(showUnfinishedWorkDisciplineDialog = true)
        } else {
            _dialogState.value = _dialogState.value.copy(showEndOfDayReviewDialog = true)
        }
    }

    fun proceedToReviewWithUnfinishedWork() {
        _dialogState.value = _dialogState.value.copy(
            showUnfinishedWorkDisciplineDialog = false,
            showEndOfDayReviewDialog = true
        )
    }

    fun submitEndOfDayReview(
        whatCompleted: String,
        distraction: String,
        tomorrowMoney: String,
        tomorrowGrowth: String,
        tomorrowMaintenance: String
    ) {
        viewModelScope.launch {
            val hasUnfinished = uiState.value.todayPlan?.areAllTasksCompleted == false
            val result = repository.finishWorkdayAndReview(
                date = todayDate,
                completedWhat = whatCompleted.trim(),
                distraction = distraction.trim(),
                tomorrowMoney = tomorrowMoney.trim(),
                tomorrowGrowth = tomorrowGrowth.trim(),
                tomorrowMaintenance = tomorrowMaintenance.trim(),
                hasUnfinishedWork = hasUnfinished
            )
            _lastReviewResult.value = result
            _dialogState.value = _dialogState.value.copy(showEndOfDayReviewDialog = false)
        }
    }

    fun selectTab(tab: NavigationTab) {
        _selectedTab.value = tab
    }

    fun openPauseDialog() {
        _dialogState.value = _dialogState.value.copy(showPauseDialog = true)
    }

    fun closePauseDialog() {
        _dialogState.value = _dialogState.value.copy(showPauseDialog = false)
    }

    fun openGiveUpDialog() {
        _dialogState.value = _dialogState.value.copy(showGiveUpDialog = true)
    }

    fun closeGiveUpDialog() {
        _dialogState.value = _dialogState.value.copy(showGiveUpDialog = false)
    }

    fun openTaskCompletionConfirmDialog() {
        _dialogState.value = _dialogState.value.copy(showTaskCompletionConfirmDialog = true)
    }

    fun closeTaskCompletionConfirmDialog() {
        _dialogState.value = _dialogState.value.copy(showTaskCompletionConfirmDialog = false)
    }

    fun closeUnfinishedWorkDialog() {
        _dialogState.value = _dialogState.value.copy(showUnfinishedWorkDisciplineDialog = false)
    }

    fun closeEndOfDayReviewDialog() {
        _dialogState.value = _dialogState.value.copy(showEndOfDayReviewDialog = false)
    }

    fun dismissLastReviewSummary() {
        _lastReviewResult.value = null
    }

    fun openBackupDialog() {
        _dialogState.value = _dialogState.value.copy(showBackupDialog = true)
    }

    fun closeBackupDialog() {
        _dialogState.value = _dialogState.value.copy(showBackupDialog = false)
    }

    fun clearToast() {
        _messageToast.value = null
    }

    suspend fun getExportJson(): String {
        return repository.exportBackupJson()
    }

    fun importBackup(jsonString: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                repository.importBackupJson(jsonString)
                _messageToast.value = "Backup imported successfully!"
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Failed to import backup")
            }
        }
    }
}
