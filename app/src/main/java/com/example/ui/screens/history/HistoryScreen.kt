package com.example.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.DailyPlanEntity
import com.example.data.local.FocusSessionEntity
import com.example.data.model.TaskType
import com.example.data.model.UserSettings
import com.example.ui.components.GoldenGradientButton
import com.example.ui.components.RewardPointsBadge
import com.example.ui.components.ScoreBadge
import com.example.ui.components.TaskNumberBadge
import com.example.ui.theme.BrownIconBorder
import com.example.ui.theme.CardSubtleBorder
import com.example.ui.theme.DarkChocolateHeadings
import com.example.ui.theme.GoldenAmberPrimary
import com.example.ui.theme.SoftCreamCard
import com.example.ui.theme.WarmAmberWarning
import com.example.ui.theme.WarmBrownBody
import com.example.ui.theme.WarmBrownMuted
import com.example.ui.theme.WarmBrownSecondary
import com.example.ui.theme.WarmCrimsonFailure
import com.example.ui.theme.WarmIvoryBg
import com.example.ui.theme.WarmOliveSuccess
import com.example.ui.theme.WarmPillBg
import com.example.ui.theme.WarmSuccessBg
import com.example.util.DateUtil

@Composable
fun HistoryScreen(
    userSettings: UserSettings,
    allPlans: List<DailyPlanEntity>,
    allSessions: List<FocusSessionEntity>,
    onToggleTaskCompleteForDate: (date: String, taskType: TaskType) -> Unit,
    onOpenBackupDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val completedWorkdays = allPlans.filter { it.isWorkdayFinished || it.completedTasksCount > 0 || it.areAllThreeTasksPlanned }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(WarmIvoryBg)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = 80.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header & Backup buttons
            item {
                Surface(
                    modifier = Modifier
                        .widthIn(max = 720.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = SoftCreamCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardSubtleBorder),
                    shadowElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Workday History",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkChocolateHeadings
                                )
                                Text(
                                    text = "View past added tasks and mark them complete",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = WarmBrownSecondary
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RewardPointsBadge(points = userSettings.totalRewardPoints)

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(WarmPillBg)
                                        .border(1.dp, CardSubtleBorder, RoundedCornerShape(20.dp))
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "🔥 ${userSettings.currentStreak}d",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldenAmberPrimary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Backup Action Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            GoldenGradientButton(
                                text = "Export Backup",
                                onClick = onOpenBackupDialog,
                                leadingIcon = Icons.Default.ArrowUpward,
                                height = 44.dp,
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("backup_data_button")
                            )

                            OutlinedButton(
                                onClick = onOpenBackupDialog,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("import_data_button"),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CardSubtleBorder)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDownward,
                                    contentDescription = "Import",
                                    tint = BrownIconBorder,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Import Backup", color = WarmBrownBody, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            if (completedWorkdays.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier
                            .widthIn(max = 720.dp)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = SoftCreamCard,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardSubtleBorder),
                        shadowElevation = 1.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No past records yet",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = DarkChocolateHeadings
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tasks planned and completed will appear here with history task completion toggles and discipline scores.",
                                style = MaterialTheme.typography.bodySmall,
                                color = WarmBrownSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(completedWorkdays, key = { it.date }) { plan ->
                    val daySessions = allSessions.filter { it.date == plan.date }
                    val completedSessions = daySessions.count { it.isCompleted }
                    val scoreLabel = when (plan.calculatedScore) {
                        5 -> "Excellent"
                        4 -> "Successful"
                        3 -> "Needs improvement"
                        else -> "Failed day"
                    }

                    Box(
                        modifier = Modifier
                            .widthIn(max = 720.dp)
                            .fillMaxWidth()
                    ) {
                        HistoryDayCard(
                            plan = plan,
                            completedSessionsCount = completedSessions,
                            targetSessions = userSettings.dailyFocusTargetSessions,
                            scoreLabel = scoreLabel,
                            onToggleTask = { taskType -> onToggleTaskCompleteForDate(plan.date, taskType) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryDayCard(
    plan: DailyPlanEntity,
    completedSessionsCount: Int,
    targetSessions: Int,
    scoreLabel: String,
    onToggleTask: (TaskType) -> Unit
) {
    val displayDate = DateUtil.formatToDisplay(plan.date)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = SoftCreamCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, CardSubtleBorder),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row: Date, Focus Sessions, Reward Points, Score Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = displayDate,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DarkChocolateHeadings
                    )
                    Text(
                        text = "${plan.completedTasksCount}/3 tasks completed • $completedSessionsCount/$targetSessions focus sessions",
                        style = MaterialTheme.typography.bodySmall,
                        color = WarmBrownSecondary
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val pts = plan.livePointsDelta
                    Text(
                        text = if (pts >= 0) "+$pts pts" else "$pts pts",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (pts >= 0) WarmOliveSuccess else WarmCrimsonFailure
                    )
                    ScoreBadge(score = plan.calculatedScore, ratingLabel = scoreLabel)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tasks Section
            Text(
                text = "Tasks for this day (tap to toggle completion):",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = DarkChocolateHeadings
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Task 1
                HistoryTaskRow(
                    taskType = TaskType.TASK_1,
                    taskTitle = plan.task1Title.ifBlank { "Task 1 (No title)" },
                    durationMinutes = plan.task1DurationMinutes,
                    isCompleted = plan.task1Completed,
                    onToggle = { onToggleTask(TaskType.TASK_1) }
                )

                // Task 2
                HistoryTaskRow(
                    taskType = TaskType.TASK_2,
                    taskTitle = plan.task2Title.ifBlank { "Task 2 (No title)" },
                    durationMinutes = plan.task2DurationMinutes,
                    isCompleted = plan.task2Completed,
                    onToggle = { onToggleTask(TaskType.TASK_2) }
                )

                // Task 3
                HistoryTaskRow(
                    taskType = TaskType.TASK_3,
                    taskTitle = plan.task3Title.ifBlank { "Task 3 (No title)" },
                    durationMinutes = plan.task3DurationMinutes,
                    isCompleted = plan.task3Completed,
                    onToggle = { onToggleTask(TaskType.TASK_3) }
                )
            }

            if (plan.completedSummaryWhat.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Outcomes:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkChocolateHeadings
                )
                Text(
                    text = plan.completedSummaryWhat,
                    style = MaterialTheme.typography.bodySmall,
                    color = WarmBrownBody
                )
            }

            if (plan.completedSummaryDistraction.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Main distraction",
                        tint = WarmAmberWarning,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Main Distraction: ${plan.completedSummaryDistraction}",
                        style = MaterialTheme.typography.bodySmall,
                        color = WarmAmberWarning
                    )
                }
            }

            if (plan.workdayFinishedWithUnfinishedWork) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "⚠️ Ended with unfinished tasks (-10 pts per task)",
                    style = MaterialTheme.typography.labelSmall,
                    color = WarmCrimsonFailure
                )
            }
        }
    }
}

@Composable
fun HistoryTaskRow(
    taskType: TaskType,
    taskTitle: String,
    durationMinutes: Int,
    isCompleted: Boolean,
    onToggle: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle),
        shape = RoundedCornerShape(10.dp),
        color = if (isCompleted) WarmSuccessBg else WarmPillBg.copy(alpha = 0.4f),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isCompleted) WarmOliveSuccess.copy(alpha = 0.35f) else CardSubtleBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                TaskNumberBadge(taskType = taskType)

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = taskTitle,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isCompleted) FontWeight.Normal else FontWeight.SemiBold,
                        color = if (isCompleted) WarmBrownMuted else DarkChocolateHeadings,
                        textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    )
                    Text(
                        text = "$durationMinutes min timer",
                        style = MaterialTheme.typography.labelSmall,
                        color = WarmBrownSecondary
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = if (isCompleted) "+10 pts" else "Mark Done",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isCompleted) WarmOliveSuccess else WarmBrownSecondary
                )

                Checkbox(
                    checked = isCompleted,
                    onCheckedChange = { onToggle() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = GoldenAmberPrimary,
                        checkmarkColor = Color.White,
                        uncheckedColor = BrownIconBorder
                    )
                )
            }
        }
    }
}
