package com.example.ui.screens.today

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Star
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
import com.example.ui.components.ScoreBadge
import com.example.ui.components.TaskCategoryBadge
import com.example.ui.theme.FocusBorder
import com.example.ui.theme.FocusDarkGreen
import com.example.ui.theme.FocusGreenContainer
import com.example.ui.theme.FocusLightGreen
import com.example.ui.theme.FocusTextMuted
import com.example.ui.theme.FocusTextPrimary
import com.example.ui.theme.FocusTextSecondary
import com.example.ui.theme.FocusWarning
import com.example.ui.theme.FocusWarningBg
import com.example.util.DateUtil
import com.example.util.SpecificityCoach

@Composable
fun TodayScreen(
    userSettings: UserSettings,
    todayPlan: DailyPlanEntity?,
    liveScore: DailyScoreResult,
    completedSessionsCount: Int,
    onUpdateTask: (TaskType, String) -> Unit,
    onToggleTaskComplete: (TaskType) -> Unit,
    onStartFocus: (TaskType, String) -> Unit,
    onFinishWorkday: () -> Unit,
    modifier: Modifier = Modifier
) {
    val plan = todayPlan ?: DailyPlanEntity(date = DateUtil.getTodayDateString())
    val completedCount = plan.completedTasksCount
    val greeting = DateUtil.getGreeting(userSettings.name)
    val todayDisplayDate = DateUtil.formatToDisplay(DateUtil.getTodayDateString())

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = 720.dp)
                .fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = 48.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Top Header Card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, FocusBorder)
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
                                    color = FocusTextPrimary
                                )
                                Text(
                                    text = todayDisplayDate,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = FocusTextSecondary
                                )
                            }

                            // Streak Pill
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(FocusGreenContainer)
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "🔥 ${userSettings.currentStreak} day streak",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = FocusDarkGreen
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Progress: "1 of 3 tasks completed"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$completedCount of 3 tasks completed",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = FocusDarkGreen
                            )
                            Text(
                                text = "${completedSessionsCount}/${userSettings.dailyFocusTargetSessions} focus sessions",
                                style = MaterialTheme.typography.bodySmall,
                                color = FocusTextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { completedCount / 3f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = FocusDarkGreen,
                            trackColor = FocusBorder
                        )
                    }
                }
            }

            // 2. Three Task Cards
            item {
                Text(
                    text = "Today's Three Priorities",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = FocusTextPrimary
                )
                Text(
                    text = "Limit daily work to 3 key outcomes to protect focus and eliminate decision fatigue.",
                    style = MaterialTheme.typography.bodySmall,
                    color = FocusTextSecondary
                )
            }

            // Slot 1: Money Task
            item {
                TaskSlotCard(
                    taskType = TaskType.MONEY,
                    taskTitle = plan.moneyTaskTitle,
                    isCompleted = plan.moneyTaskCompleted,
                    onTitleChange = { onUpdateTask(TaskType.MONEY, it) },
                    onToggleComplete = { onToggleTaskComplete(TaskType.MONEY) },
                    onStartFocus = { title -> onStartFocus(TaskType.MONEY, title) }
                )
            }

            // Slot 2: Growth Task
            item {
                TaskSlotCard(
                    taskType = TaskType.GROWTH,
                    taskTitle = plan.growthTaskTitle,
                    isCompleted = plan.growthTaskCompleted,
                    onTitleChange = { onUpdateTask(TaskType.GROWTH, it) },
                    onToggleComplete = { onToggleTaskComplete(TaskType.GROWTH) },
                    onStartFocus = { title -> onStartFocus(TaskType.GROWTH, title) }
                )
            }

            // Slot 3: Maintenance Task
            item {
                TaskSlotCard(
                    taskType = TaskType.MAINTENANCE,
                    taskTitle = plan.maintenanceTaskTitle,
                    isCompleted = plan.maintenanceTaskCompleted,
                    onTitleChange = { onUpdateTask(TaskType.MAINTENANCE, it) },
                    onToggleComplete = { onToggleTaskComplete(TaskType.MAINTENANCE) },
                    onStartFocus = { title -> onStartFocus(TaskType.MAINTENANCE, title) }
                )
            }

            // 3. Live Daily Score Tracker
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, FocusBorder)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
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
                                    color = FocusTextPrimary
                                )
                                Text(
                                    text = "Calculated automatically • Score 4+ to protect streak",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = FocusTextSecondary
                                )
                            }
                            ScoreBadge(
                                score = liveScore.totalScore,
                                ratingLabel = liveScore.ratingLabel
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        ScoreItemRow("1. Planned three specific tasks", liveScore.plannedThreeTasksPoint == 1)
                        ScoreItemRow("2. Started work within 30 min of ${userSettings.formattedWorkStartTime}", liveScore.startedOnTimePoint == 1)
                        ScoreItemRow("3. Completed daily target (${userSettings.dailyFocusTargetSessions} focus sessions)", liveScore.completedTargetSessionsPoint == 1)
                        ScoreItemRow("4. Completed Money Task", liveScore.completedMoneyTaskPoint == 1)
                        ScoreItemRow("5. Plan tomorrow before ending day", liveScore.plannedTomorrowPoint == 1)
                    }
                }
            }

            // 4. Finish Workday CTA
            item {
                Button(
                    onClick = onFinishWorkday,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("finish_workday_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FocusDarkGreen,
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
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun TaskSlotCard(
    taskType: TaskType,
    taskTitle: String,
    isCompleted: Boolean,
    onTitleChange: (String) -> Unit,
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
            if (isCompleted) FocusGreenContainer else FocusBorder
        ),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TaskCategoryBadge(taskType = taskType)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isCompleted) "Completed" else "Mark Done",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isCompleted) FocusDarkGreen else FocusTextMuted
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Checkbox(
                        checked = isCompleted,
                        onCheckedChange = { onToggleComplete() },
                        colors = CheckboxDefaults.colors(
                            checkedColor = FocusDarkGreen,
                            checkmarkColor = Color.White
                        ),
                        modifier = Modifier.testTag("checkbox_${taskType.name.lowercase()}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Task title input
            OutlinedTextField(
                value = textValue,
                onValueChange = {
                    textValue = it
                    onTitleChange(it)
                },
                placeholder = { Text(taskType.example) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_${taskType.name.lowercase()}"),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    color = if (isCompleted) FocusTextMuted else FocusTextPrimary
                ),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FocusDarkGreen,
                    unfocusedBorderColor = FocusBorder
                )
            )

            // Inline Specificity Coaching Warning
            AnimatedVisibility(visible = isVague && textValue.isNotBlank()) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(FocusWarningBg)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Specificity warning",
                                tint = FocusWarning,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = SpecificityCoach.getSuggestion(textValue),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF92400E)
                            )
                        }
                    }
                }
            }

            // Start Focus button if incomplete
            if (!isCompleted) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { onStartFocus(textValue) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("start_focus_${taskType.name.lowercase()}"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FocusDarkGreen,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Start Focus",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Start Focus",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
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
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(if (earned) FocusGreenContainer else Color.Transparent)
                    .border(
                        1.dp,
                        if (earned) FocusDarkGreen else FocusBorder,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (earned) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Earned point",
                        tint = FocusDarkGreen,
                        modifier = Modifier.size(11.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = if (earned) FocusTextPrimary else FocusTextSecondary
            )
        }
        Text(
            text = if (earned) "+1" else "0",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = if (earned) FocusDarkGreen else FocusTextMuted
        )
    }
}
