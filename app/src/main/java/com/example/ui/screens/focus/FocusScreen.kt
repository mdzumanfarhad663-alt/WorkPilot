package com.example.ui.screens.focus

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.DailyPlanEntity
import com.example.data.model.ActiveSessionState
import com.example.data.model.TaskType
import com.example.data.model.UserSettings
import com.example.ui.components.TaskDurationChip
import com.example.ui.components.TaskNumberBadge
import com.example.ui.dialogs.CustomDurationDialog
import com.example.ui.theme.PilotBorder
import com.example.ui.theme.PilotDarkGreen
import com.example.ui.theme.PilotFailure
import com.example.ui.theme.PilotFailureBg
import com.example.ui.theme.PilotGreenContainer
import com.example.ui.theme.PilotSuccess
import com.example.ui.theme.PilotTextMuted
import com.example.ui.theme.PilotTextPrimary
import com.example.ui.theme.PilotTextSecondary
import com.example.ui.theme.PilotWarning
import com.example.ui.theme.PilotWarningBg
import com.example.util.DateUtil

@Composable
fun FocusScreen(
    userSettings: UserSettings,
    todayPlan: DailyPlanEntity?,
    activeSession: ActiveSessionState,
    remainingMillis: Long,
    isEligibleToFinishEarly: Boolean,
    onStartSession: (TaskType, String, Int) -> Unit,
    onUpdateTaskDuration: (TaskType, Int) -> Unit,
    onPauseClick: () -> Unit,
    onResumeClick: () -> Unit,
    onGiveUpClick: () -> Unit,
    onFinishSessionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 20.dp,
            bottom = 80.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            if (activeSession.isActive) {
                ActiveFocusView(
                    session = activeSession,
                    remainingMillis = remainingMillis,
                    isEligibleToFinishEarly = isEligibleToFinishEarly,
                    onPauseClick = onPauseClick,
                    onResumeClick = onResumeClick,
                    onGiveUpClick = onGiveUpClick,
                    onFinishSessionClick = onFinishSessionClick
                )
            } else {
                IdleFocusLauncherView(
                    todayPlan = todayPlan,
                    onStartSession = onStartSession,
                    onUpdateTaskDuration = onUpdateTaskDuration
                )
            }
        }
    }
}

@Composable
private fun ActiveFocusView(
    session: ActiveSessionState,
    remainingMillis: Long,
    isEligibleToFinishEarly: Boolean,
    onPauseClick: () -> Unit,
    onResumeClick: () -> Unit,
    onGiveUpClick: () -> Unit,
    onFinishSessionClick: () -> Unit
) {
    val totalMillis = session.durationMinutes * 60 * 1000L
    val progress = if (totalMillis > 0) {
        ((totalMillis - remainingMillis).toFloat() / totalMillis.toFloat()).coerceIn(0f, 1f)
    } else 0f

    val formattedTime = DateUtil.formatDurationMmSs(remainingMillis)
    val eightyPercentMin = (session.durationMinutes * 0.8).toInt()

    Surface(
        modifier = Modifier
            .widthIn(max = 560.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, PilotBorder),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Task number badge
            TaskNumberBadge(taskType = session.taskType)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = session.taskTitle,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = PilotTextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Circular Progress & Remaining Time Ring
            Box(
                modifier = Modifier.size(220.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 14.dp.toPx()
                    // Background track
                    drawCircle(
                        color = Color(0xFFE2E8F0),
                        style = Stroke(width = strokeWidth)
                    )
                    // Progress arc
                    drawArc(
                        color = if (session.isPaused) Color(0xFFF59E0B) else PilotDarkGreen,
                        startAngle = -90f,
                        sweepAngle = progress * 360f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.headlineLarge,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,
                        color = PilotTextPrimary
                    )
                    Text(
                        text = if (session.isPaused) "PAUSED: ${session.pauseReason ?: ""}" else "${session.durationMinutes} MIN SESSION",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (session.isPaused) PilotWarning else PilotTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Discipline 80% Rule Notice
            AnimatedVisibility(visible = !isEligibleToFinishEarly && remainingMillis > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(PilotWarningBg)
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "Discipline Rule: Complete at least 80% ($eightyPercentMin min) before marking session finished.",
                        style = MaterialTheme.typography.bodySmall,
                        color = PilotWarning,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Pause / Resume
                if (session.isPaused) {
                    Button(
                        onClick = onResumeClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("resume_session_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PilotDarkGreen,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Resume")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Resume", fontWeight = FontWeight.SemiBold)
                    }
                } else {
                    OutlinedButton(
                        onClick = onPauseClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("pause_session_button"),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PilotBorder)
                    ) {
                        Icon(Icons.Default.Pause, contentDescription = "Pause", tint = PilotTextPrimary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Pause", color = PilotTextPrimary, fontWeight = FontWeight.SemiBold)
                    }
                }

                // Give Up button
                OutlinedButton(
                    onClick = onGiveUpClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("give_up_session_button"),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PilotFailureBg),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = PilotFailure
                    )
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Give Up", tint = PilotFailure)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Give Up", color = PilotFailure, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Finish Session Button (Active when >= 80% elapsed)
            Button(
                onClick = onFinishSessionClick,
                enabled = isEligibleToFinishEarly || remainingMillis <= 0,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("finish_session_button"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PilotDarkGreen,
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFFE2E8F0),
                    disabledContentColor = PilotTextMuted
                )
            ) {
                Text(
                    text = if (isEligibleToFinishEarly || remainingMillis <= 0) "Finish Session" else "Finish Session (Locked until 80%)",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun IdleFocusLauncherView(
    todayPlan: DailyPlanEntity?,
    onStartSession: (TaskType, String, Int) -> Unit,
    onUpdateTaskDuration: (TaskType, Int) -> Unit
) {
    val plan = todayPlan ?: DailyPlanEntity(date = DateUtil.getTodayDateString())
    var durationDialogTaskType by remember { mutableStateOf<TaskType?>(null) }

    val taskEntries = listOf(
        TaskType.TASK_1 to (plan.task1Title.ifBlank { "Task 1" } to plan.task1Completed),
        TaskType.TASK_2 to (plan.task2Title.ifBlank { "Task 2" } to plan.task2Completed),
        TaskType.TASK_3 to (plan.task3Title.ifBlank { "Task 3" } to plan.task3Completed)
    )

    Surface(
        modifier = Modifier
            .widthIn(max = 560.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, PilotBorder),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(PilotGreenContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Focus",
                    tint = PilotDarkGreen,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Launch Focus Session",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = PilotTextPrimary
            )
            Text(
                text = "Each task has its own customizable timer. Pick any duration (e.g. 5, 20, 35 min) and focus.",
                style = MaterialTheme.typography.bodySmall,
                color = PilotTextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Select Task & Timer",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = PilotTextPrimary,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                taskEntries.forEach { (type, pair) ->
                    val (title, isDone) = pair
                    val duration = plan.getTaskDuration(type)

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("select_focus_${type.name.lowercase()}"),
                        shape = RoundedCornerShape(12.dp),
                        color = if (isDone) Color(0xFFF8FAFC) else MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isDone) Color(0xFFBBF7D0) else PilotBorder
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    TaskNumberBadge(taskType = type)
                                    TaskDurationChip(
                                        durationMinutes = duration,
                                        onClick = { durationDialogTaskType = type }
                                    )
                                }

                                if (isDone) {
                                    Text(
                                        text = "✓ Completed (+10 pts)",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = PilotSuccess,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isDone) PilotTextMuted else PilotTextPrimary
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = { onStartSession(type, title, duration) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PilotDarkGreen,
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Start",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Start Focus ($duration min)",
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
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
