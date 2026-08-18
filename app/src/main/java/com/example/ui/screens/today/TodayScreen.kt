package com.example.ui.screens.today

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.data.model.DailyScoreResult
import com.example.data.model.TaskType
import com.example.data.model.UserSettings
import com.example.ui.components.RewardPointsBadge
import com.example.ui.components.ScoreBadge
import com.example.ui.components.TaskDurationChip
import com.example.ui.components.TaskNumberBadge
import com.example.ui.dialogs.CustomDurationDialog
import com.example.ui.theme.PilotBorder
import com.example.ui.theme.PilotDarkGreen
import com.example.ui.theme.PilotFailure
import com.example.ui.theme.PilotGreenContainer
import com.example.ui.theme.PilotSuccess
import com.example.ui.theme.PilotTextMuted
import com.example.ui.theme.PilotTextPrimary
import com.example.ui.theme.PilotTextSecondary
import com.example.ui.theme.PilotWarning
import com.example.ui.theme.PilotWarningBg
import com.example.util.DateUtil
import com.example.util.SpecificityCoach

@Composable
fun TodayScreen(
    userSettings: UserSettings,
    todayPlan: DailyPlanEntity?,
    liveScore: DailyScoreResult,
    completedSessionsCount: Int,
    onUpdateTask: (TaskType, String) -> Unit,
    onUpdateTaskDuration: (TaskType, Int) -> Unit,
    onToggleTaskComplete: (TaskType) -> Unit,
    onStartFocus: (TaskType, String, Int) -> Unit,
    onFinishWorkday: () -> Unit,
    modifier: Modifier = Modifier
) {
    val plan = todayPlan ?: DailyPlanEntity(date = DateUtil.getTodayDateString())
    val completedCount = plan.completedTasksCount
    val greeting = DateUtil.getGreeting(userSettings.name)
    val todayDisplayDate = DateUtil.formatToDisplay(DateUtil.getTodayDateString())

    var durationDialogTaskType by remember { mutableStateOf<TaskType?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
            // 1. Top Header Card
            item {
                Surface(
                    modifier = Modifier
                        .widthIn(max = 720.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, PilotBorder),
                    shadowElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = greeting,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = PilotTextPrimary
                                )
                                Text(
                                    text = "$todayDisplayDate • Plan. Focus. Finish.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = PilotTextSecondary
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Reward Points Badge
                                RewardPointsBadge(
                                    points = userSettings.totalRewardPoints,
                                    isLarge = true,
                                    modifier = Modifier.testTag("reward_points_badge")
                                )

                                // Streak Pill
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(PilotGreenContainer)
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "🔥 ${userSettings.currentStreak}d",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = PilotSuccess
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Progress: "X of 3 tasks completed"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$completedCount of 3 tasks completed",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = PilotDarkGreen
                            )
                            Text(
                                text = "${completedSessionsCount}/${userSettings.dailyFocusTargetSessions} focus sessions",
                                style = MaterialTheme.typography.bodySmall,
                                color = PilotTextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { (completedCount / 3f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = PilotDarkGreen,
                            trackColor = PilotBorder
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Reward rule: +10 pts per task completed • -10 pts per task missed",
                                style = MaterialTheme.typography.labelSmall,
                                color = PilotTextSecondary
                            )
                            val livePoints = plan.livePointsDelta
                            Text(
                                text = if (livePoints >= 0) "Today: +$livePoints pts" else "Today: $livePoints pts",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (livePoints >= 0) PilotSuccess else PilotFailure
                            )
                        }
                    }
                }
            }

            // 2. Three Task Cards Header
            item {
                Column(
                    modifier = Modifier
                        .widthIn(max = 720.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Today's Priorities",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = PilotTextPrimary
                    )
                    Text(
                        text = "Set custom timer durations for each task. Complete tasks to earn +10 reward points.",
                        style = MaterialTheme.typography.bodySmall,
                        color = PilotTextSecondary
                    )
                }
            }

            // Task 1 Card
            item {
                TaskSlotCard(
                    taskType = TaskType.TASK_1,
                    taskTitle = plan.task1Title,
                    durationMinutes = plan.task1DurationMinutes,
                    isCompleted = plan.task1Completed,
                    onTitleChange = { onUpdateTask(TaskType.TASK_1, it) },
                    onDurationClick = { durationDialogTaskType = TaskType.TASK_1 },
                    onToggleComplete = { onToggleTaskComplete(TaskType.TASK_1) },
                    onStartFocus = { title -> onStartFocus(TaskType.TASK_1, title, plan.task1DurationMinutes) },
                    modifier = Modifier.widthIn(max = 720.dp)
                )
            }

            // Task 2 Card
            item {
                TaskSlotCard(
                    taskType = TaskType.TASK_2,
                    taskTitle = plan.task2Title,
                    durationMinutes = plan.task2DurationMinutes,
                    isCompleted = plan.task2Completed,
                    onTitleChange = { onUpdateTask(TaskType.TASK_2, it) },
                    onDurationClick = { durationDialogTaskType = TaskType.TASK_2 },
                    onToggleComplete = { onToggleTaskComplete(TaskType.TASK_2) },
                    onStartFocus = { title -> onStartFocus(TaskType.TASK_2, title, plan.task2DurationMinutes) },
                    modifier = Modifier.widthIn(max = 720.dp)
                )
            }

            // Task 3 Card
            item {
                TaskSlotCard(
                    taskType = TaskType.TASK_3,
                    taskTitle = plan.task3Title,
                    durationMinutes = plan.task3DurationMinutes,
                    isCompleted = plan.task3Completed,
                    onTitleChange = { onUpdateTask(TaskType.TASK_3, it) },
                    onDurationClick = { durationDialogTaskType = TaskType.TASK_3 },
                    onToggleComplete = { onToggleTaskComplete(TaskType.TASK_3) },
                    onStartFocus = { title -> onStartFocus(TaskType.TASK_3, title, plan.task3DurationMinutes) },
                    modifier = Modifier.widthIn(max = 720.dp)
                )
            }

            // 3. Live Daily Score Tracker
            item {
                Surface(
                    modifier = Modifier
                        .widthIn(max = 720.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, PilotBorder),
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
                                    text = "Daily Discipline Score",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = PilotTextPrimary
                                )
                                Text(
                                    text = "Score 4+ to maintain discipline streak",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = PilotTextSecondary
                                )
                            }
                            ScoreBadge(
                                score = liveScore.totalScore,
                                ratingLabel = liveScore.ratingLabel
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        ScoreItemRow("1. Planned three specific tasks", liveScore.plannedThreeTasksPoint == 1)
                        ScoreItemRow("2. Started work within 30 min of ${userSettings.formattedWorkStartTime}", liveScore.startedOnTimePoint == 1)
                        ScoreItemRow("3. Completed daily target (${userSettings.dailyFocusTargetSessions} focus sessions)", liveScore.completedTargetSessionsPoint == 1)
                        ScoreItemRow("4. Completed at least one priority task (+10 pts)", liveScore.completedAllTasksPoint == 1)
                        ScoreItemRow("5. Plan tomorrow before ending workday", liveScore.plannedTomorrowPoint == 1)
                    }
                }
            }

            // 4. Finish Workday CTA
            item {
                Box(
                    modifier = Modifier
                        .widthIn(max = 720.dp)
                        .fillMaxWidth()
                ) {
                    Button(
                        onClick = onFinishWorkday,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("finish_workday_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PilotDarkGreen,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Finish My Workday",
                            style = MaterialTheme.typography.labelLarge,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Custom Duration Dialog
        durationDialogTaskType?.let { taskType ->
            val curDur = plan.getTaskDuration(taskType)
            val curTitle = plan.getTaskTitle(taskType)
            CustomDurationDialog(
                taskType = taskType,
                taskTitle = curTitle,
                currentDuration = curDur,
                onDismiss = { durationDialogTaskType = null },
                onConfirmDuration = { newMinutes ->
                    onUpdateTaskDuration(taskType, newMinutes)
                    durationDialogTaskType = null
                }
            )
        }
    }
}

@Composable
fun TaskSlotCard(
    taskType: TaskType,
    taskTitle: String,
    durationMinutes: Int,
    isCompleted: Boolean,
    onTitleChange: (String) -> Unit,
    onDurationClick: () -> Unit,
    onToggleComplete: () -> Unit,
    onStartFocus: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var textValue by remember(taskTitle) { mutableStateOf(taskTitle) }
    val isVague = SpecificityCoach.isVague(textValue)

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isCompleted) Color(0xFFBBF7D0) else PilotBorder
        ),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TaskNumberBadge(taskType = taskType)
                    TaskDurationChip(
                        durationMinutes = durationMinutes,
                        onClick = onDurationClick,
                        modifier = Modifier.testTag("duration_chip_${taskType.name.lowercase()}")
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onToggleComplete() }
                ) {
                    Text(
                        text = if (isCompleted) "+10 pts" else "Mark Done (+10)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isCompleted) PilotSuccess else PilotTextSecondary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Checkbox(
                        checked = isCompleted,
                        onCheckedChange = { onToggleComplete() },
                        colors = CheckboxDefaults.colors(
                            checkedColor = PilotDarkGreen,
                            checkmarkColor = Color.White,
                            uncheckedColor = PilotBorder
                        ),
                        modifier = Modifier.testTag("checkbox_${taskType.name.lowercase()}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Task title input
            OutlinedTextField(
                value = textValue,
                onValueChange = {
                    textValue = it
                    onTitleChange(it)
                },
                placeholder = {
                    Text(
                        text = taskType.placeholder,
                        color = PilotTextMuted,
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_${taskType.name.lowercase()}"),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    color = if (isCompleted) PilotTextMuted else PilotTextPrimary
                ),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PilotDarkGreen,
                    unfocusedBorderColor = PilotBorder,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            // Inline Specificity Coaching Warning
            AnimatedVisibility(visible = isVague && textValue.isNotBlank()) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(PilotWarningBg)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Specificity warning",
                                tint = PilotWarning,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = SpecificityCoach.getSuggestion(textValue),
                                style = MaterialTheme.typography.bodySmall,
                                color = PilotWarning,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Prominent Start Focus button if incomplete
            if (!isCompleted) {
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = { onStartFocus(textValue) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("start_focus_${taskType.name.lowercase()}"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PilotDarkGreen,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Start Focus",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Start Focus (${durationMinutes} min)",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ScoreItemRow(label: String, earned: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(if (earned) PilotGreenContainer else Color.Transparent)
                    .border(
                        1.dp,
                        if (earned) PilotSuccess else PilotBorder,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (earned) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Earned point",
                        tint = PilotSuccess,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = if (earned) PilotTextPrimary else PilotTextSecondary
            )
        }
        Text(
            text = if (earned) "+1" else "0",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = if (earned) PilotSuccess else PilotTextMuted
        )
    }
}
