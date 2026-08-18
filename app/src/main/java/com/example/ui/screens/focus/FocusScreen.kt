package com.example.ui.screens.focus

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.ui.components.TaskCategoryBadge
import com.example.ui.screens.setup.SelectionChip
import com.example.ui.theme.FocusBorder
import com.example.ui.theme.FocusDarkGreen
import com.example.ui.theme.FocusFailure
import com.example.ui.theme.FocusGreenContainer
import com.example.ui.theme.FocusLightGreen
import com.example.ui.theme.FocusTextMuted
import com.example.ui.theme.FocusTextPrimary
import com.example.ui.theme.FocusTextSecondary
import com.example.ui.theme.FocusWarning
import com.example.util.DateUtil

@Composable
fun FocusScreen(
    userSettings: UserSettings,
    todayPlan: DailyPlanEntity?,
    activeSession: ActiveSessionState,
    remainingMillis: Long,
    isEligibleToFinishEarly: Boolean,
    onStartSession: (TaskType, String, Int) -> Unit,
    onPauseClick: () -> Unit,
    onResumeClick: () -> Unit,
    onGiveUpClick: () -> Unit,
    onFinishSessionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 20.dp),
        contentAlignment = Alignment.TopCenter
    ) {
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
                userSettings = userSettings,
                todayPlan = todayPlan,
                onStartSession = onStartSession
            )
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
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, FocusBorder),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Task info
            TaskCategoryBadge(taskType = session.taskType)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = session.taskTitle,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = FocusTextPrimary,
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
                        color = Color(0xFFE5E7EB),
                        style = Stroke(width = strokeWidth)
                    )
                    // Progress arc
                    drawArc(
                        color = if (session.isPaused) FocusWarning else FocusDarkGreen,
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
                        color = FocusTextPrimary
                    )
                    Text(
                        text = if (session.isPaused) "PAUSED: ${session.pauseReason ?: ""}" else "${session.durationMinutes} MIN SESSION",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (session.isPaused) FocusWarning else FocusTextSecondary
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
                        .background(Color(0xFFF3F4F6))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Discipline rule: Complete at least 80% ($eightyPercentMin min) before marking session finished.",
                        style = MaterialTheme.typography.bodySmall,
                        color = FocusTextSecondary,
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
                            containerColor = FocusDarkGreen,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Resume")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Resume")
                    }
                } else {
                    OutlinedButton(
                        onClick = onPauseClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("pause_session_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Pause, contentDescription = "Pause", tint = FocusTextPrimary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Pause", color = FocusTextPrimary)
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
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = FocusFailure
                    )
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Give Up", tint = FocusFailure)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Give Up", color = FocusFailure)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

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
                    containerColor = FocusDarkGreen,
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFFE5E7EB),
                    disabledContentColor = FocusTextMuted
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
    userSettings: UserSettings,
    todayPlan: DailyPlanEntity?,
    onStartSession: (TaskType, String, Int) -> Unit
) {
    val plan = todayPlan ?: DailyPlanEntity(date = DateUtil.getTodayDateString())
    var selectedDuration by remember { mutableIntStateOf(userSettings.defaultFocusDurationMinutes) }

    // List all three tasks with their status
    val taskEntries = listOf(
        Triple(
            TaskType.MONEY,
            plan.moneyTaskTitle.ifBlank { "Money Task (Direct Revenue)" },
            plan.moneyTaskCompleted
        ),
        Triple(
            TaskType.GROWTH,
            plan.growthTaskTitle.ifBlank { "Growth Task (Capability & Pipeline)" },
            plan.growthTaskCompleted
        ),
        Triple(
            TaskType.MAINTENANCE,
            plan.maintenanceTaskTitle.ifBlank { "Maintenance Task (Admin & Ops)" },
            plan.maintenanceTaskCompleted
        )
    )

    Surface(
        modifier = Modifier
            .widthIn(max = 560.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, FocusBorder),
        shadowElevation = 2.dp
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
                    .background(FocusGreenContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Focus",
                    tint = FocusDarkGreen,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Launch Focus Session",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = FocusTextPrimary
            )
            Text(
                text = "Pick a task and duration to enter undivided focus mode.",
                style = MaterialTheme.typography.bodySmall,
                color = FocusTextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Duration selector
            Text(
                text = "Session Duration",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = FocusTextPrimary,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf(25, 50, 90).forEach { dur ->
                    val isSelected = selectedDuration == dur
                    SelectionChip(
                        text = "$dur min",
                        isSelected = isSelected,
                        onClick = { selectedDuration = dur },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Select Task to Focus On",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = FocusTextPrimary,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                taskEntries.forEach { (type, title, isDone) ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onStartSession(type, title, selectedDuration) }
                            .testTag("select_focus_${type.name.lowercase()}"),
                        shape = RoundedCornerShape(12.dp),
                        color = if (isDone) Color(0xFFF9FAFB) else MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isDone) FocusGreenContainer else FocusBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                TaskCategoryBadge(taskType = type)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isDone) FocusTextMuted else FocusTextPrimary
                                )
                                if (isDone) {
                                    Text(
                                        text = "✓ Completed today",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = FocusDarkGreen
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = { onStartSession(type, title, selectedDuration) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = FocusDarkGreen,
                                    contentColor = Color.White
                                )
                            ) {
                                Text("Start ($selectedDuration m)")
                            }
                        }
                    }
                }
            }
        }
    }
}
