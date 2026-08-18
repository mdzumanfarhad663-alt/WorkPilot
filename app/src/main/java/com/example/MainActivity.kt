package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.dialogs.BackupDialog
import com.example.ui.dialogs.EndOfDayReviewDialog
import com.example.ui.dialogs.GiveUpReasonDialog
import com.example.ui.dialogs.PauseReasonDialog
import com.example.ui.dialogs.ReviewSummaryDialog
import com.example.ui.dialogs.TaskCompletionDialog
import com.example.ui.dialogs.UnfinishedWorkDisciplineDialog
import com.example.ui.screens.focus.FocusScreen
import com.example.ui.screens.history.HistoryScreen
import com.example.ui.screens.setup.FirstTimeSetupScreen
import com.example.ui.screens.today.TodayScreen
import com.example.ui.theme.FocusDarkGreen
import com.example.ui.theme.FocusGreenContainer
import com.example.ui.theme.FocusLockTheme
import com.example.ui.theme.FocusTextMuted
import com.example.ui.theme.FocusTextPrimary
import com.example.ui.viewmodel.FocusLockUiState
import com.example.ui.viewmodel.FocusLockViewModel
import com.example.ui.viewmodel.NavigationTab

class MainActivity : ComponentActivity() {
    private val viewModel: FocusLockViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            FocusLockTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val context = LocalContext.current

                LaunchedEffect(uiState.messageToast) {
                    uiState.messageToast?.let { msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        viewModel.clearToast()
                    }
                }

                if (uiState.showFirstTimeSetup) {
                    FirstTimeSetupScreen(
                        onCompleteSetup = { name, hour, minute, dailyTarget, duration ->
                            viewModel.completeFirstTimeSetup(name, hour, minute, dailyTarget, duration)
                        },
                        modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)
                    )
                } else {
                    FocusLockMainApp(
                        uiState = uiState,
                        viewModel = viewModel
                    )
                }

                // Global Dialogs
                if (uiState.showPauseDialog) {
                    PauseReasonDialog(
                        onDismiss = { viewModel.closePauseDialog() },
                        onConfirmPause = { reason -> viewModel.pauseFocusSession(reason) }
                    )
                }

                if (uiState.showGiveUpDialog) {
                    GiveUpReasonDialog(
                        onDismiss = { viewModel.closeGiveUpDialog() },
                        onConfirmAbandon = { reason -> viewModel.abandonFocusSession(reason) }
                    )
                }

                if (uiState.showTaskCompletionConfirmDialog) {
                    TaskCompletionDialog(
                        taskTitle = uiState.activeSession.taskTitle,
                        onConfirmCompleted = { isDone -> viewModel.completeFocusSession(isDone) }
                    )
                }

                if (uiState.showUnfinishedWorkDisciplineDialog) {
                    UnfinishedWorkDisciplineDialog(
                        onContinueWork = {
                            viewModel.closeUnfinishedWorkDialog()
                            viewModel.selectTab(NavigationTab.FOCUS)
                        },
                        onFinishAnyway = {
                            viewModel.proceedToReviewWithUnfinishedWork()
                        }
                    )
                }

                if (uiState.showEndOfDayReviewDialog) {
                    EndOfDayReviewDialog(
                        onDismiss = { viewModel.closeEndOfDayReviewDialog() },
                        onSubmitReview = { what, dist, mTask, gTask, maintTask ->
                            viewModel.submitEndOfDayReview(what, dist, mTask, gTask, maintTask)
                        }
                    )
                }

                uiState.lastReviewSummaryResult?.let { result ->
                    ReviewSummaryDialog(
                        scoreResult = result,
                        currentStreak = uiState.userSettings.currentStreak,
                        onDismiss = { viewModel.dismissLastReviewSummary() }
                    )
                }

                if (uiState.showBackupDialog) {
                    BackupDialog(
                        onDismiss = { viewModel.closeBackupDialog() },
                        onGetExportJson = { viewModel.getExportJson() },
                        onImportBackup = { json, onSuccess, onError ->
                            viewModel.importBackup(json, onSuccess, onError)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FocusLockMainApp(
    uiState: FocusLockUiState,
    viewModel: FocusLockViewModel
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = FocusDarkGreen,
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("bottom_nav_bar")
            ) {
                // Today Tab
                NavigationBarItem(
                    selected = uiState.selectedTab == NavigationTab.TODAY,
                    onClick = { viewModel.selectTab(NavigationTab.TODAY) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.selectedTab == NavigationTab.TODAY) Icons.Filled.DateRange else Icons.Outlined.DateRange,
                            contentDescription = "Today"
                        )
                    },
                    label = {
                        Text(
                            text = "Today",
                            fontWeight = if (uiState.selectedTab == NavigationTab.TODAY) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = FocusDarkGreen,
                        selectedTextColor = FocusDarkGreen,
                        indicatorColor = FocusGreenContainer,
                        unselectedIconColor = FocusTextMuted,
                        unselectedTextColor = FocusTextMuted
                    ),
                    modifier = Modifier.testTag("tab_today")
                )

                // Focus Tab
                NavigationBarItem(
                    selected = uiState.selectedTab == NavigationTab.FOCUS,
                    onClick = { viewModel.selectTab(NavigationTab.FOCUS) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.selectedTab == NavigationTab.FOCUS) Icons.Filled.Lock else Icons.Outlined.Lock,
                            contentDescription = "Focus"
                        )
                    },
                    label = {
                        Text(
                            text = "Focus",
                            fontWeight = if (uiState.selectedTab == NavigationTab.FOCUS) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = FocusDarkGreen,
                        selectedTextColor = FocusDarkGreen,
                        indicatorColor = FocusGreenContainer,
                        unselectedIconColor = FocusTextMuted,
                        unselectedTextColor = FocusTextMuted
                    ),
                    modifier = Modifier.testTag("tab_focus")
                )

                // History Tab
                NavigationBarItem(
                    selected = uiState.selectedTab == NavigationTab.HISTORY,
                    onClick = { viewModel.selectTab(NavigationTab.HISTORY) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.selectedTab == NavigationTab.HISTORY) Icons.Filled.History else Icons.Outlined.History,
                            contentDescription = "History"
                        )
                    },
                    label = {
                        Text(
                            text = "History",
                            fontWeight = if (uiState.selectedTab == NavigationTab.HISTORY) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = FocusDarkGreen,
                        selectedTextColor = FocusDarkGreen,
                        indicatorColor = FocusGreenContainer,
                        unselectedIconColor = FocusTextMuted,
                        unselectedTextColor = FocusTextMuted
                    ),
                    modifier = Modifier.testTag("tab_history")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.selectedTab) {
                NavigationTab.TODAY -> {
                    TodayScreen(
                        userSettings = uiState.userSettings,
                        todayPlan = uiState.todayPlan,
                        liveScore = uiState.liveDailyScore,
                        completedSessionsCount = uiState.todaySessions.count { it.isCompleted },
                        onUpdateTask = { type, title -> viewModel.updateTask(type, title) },
                        onToggleTaskComplete = { type -> viewModel.toggleTaskComplete(type) },
                        onStartFocus = { type, title -> viewModel.startFocusSession(type, title) },
                        onFinishWorkday = { viewModel.requestFinishWorkday() }
                    )
                }

                NavigationTab.FOCUS -> {
                    FocusScreen(
                        userSettings = uiState.userSettings,
                        todayPlan = uiState.todayPlan,
                        activeSession = uiState.activeSession,
                        remainingMillis = uiState.remainingMillis,
                        isEligibleToFinishEarly = uiState.isEligibleToFinishEarly,
                        onStartSession = { type, title, duration -> viewModel.startFocusSession(type, title, duration) },
                        onPauseClick = { viewModel.openPauseDialog() },
                        onResumeClick = { viewModel.resumeFocusSession() },
                        onGiveUpClick = { viewModel.openGiveUpDialog() },
                        onFinishSessionClick = { viewModel.openTaskCompletionConfirmDialog() }
                    )
                }

                NavigationTab.HISTORY -> {
                    HistoryScreen(
                        userSettings = uiState.userSettings,
                        allPlans = uiState.allPlans,
                        allSessions = uiState.allSessions,
                        onOpenBackupDialog = { viewModel.openBackupDialog() }
                    )
                }
            }
        }
    }
}
