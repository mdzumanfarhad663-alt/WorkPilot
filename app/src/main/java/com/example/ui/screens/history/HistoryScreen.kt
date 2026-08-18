package com.example.ui.screens.history

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.unit.dp
import com.example.data.local.DailyPlanEntity
import com.example.data.local.FocusSessionEntity
import com.example.data.model.UserSettings
import com.example.ui.components.ScoreBadge
import com.example.ui.theme.FocusBorder
import com.example.ui.theme.FocusDarkGreen
import com.example.ui.theme.FocusGreenContainer
import com.example.ui.theme.FocusTextMuted
import com.example.ui.theme.FocusTextPrimary
import com.example.ui.theme.FocusTextSecondary
import com.example.util.DateUtil

@Composable
fun HistoryScreen(
    userSettings: UserSettings,
    allPlans: List<DailyPlanEntity>,
    allSessions: List<FocusSessionEntity>,
    onOpenBackupDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val completedWorkdays = allPlans.filter { it.isWorkdayFinished || it.completedTasksCount > 0 }

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
            // Header & Backup buttons
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
                            Column {
                                Text(
                                    text = "Workday History",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = FocusTextPrimary
                                )
                                Text(
                                    text = "Discipline track record and past reviews",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = FocusTextSecondary
                                )
                            }

                            // Current Streak
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(FocusGreenContainer)
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "🔥 ${userSettings.currentStreak} days",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = FocusDarkGreen
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Backup Action Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = onOpenBackupDialog,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("backup_data_button"),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = FocusDarkGreen,
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowUpward,
                                    contentDescription = "Export",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Export Backup")
                            }

                            OutlinedButton(
                                onClick = onOpenBackupDialog,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("import_data_button"),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDownward,
                                    contentDescription = "Import",
                                    tint = FocusTextPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Import Backup", color = FocusTextPrimary)
                            }
                        }
                    }
                }
            }

            if (completedWorkdays.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, FocusBorder)
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
                                color = FocusTextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Complete today's focus sessions and end your workday to generate your first score entry.",
                                style = MaterialTheme.typography.bodySmall,
                                color = FocusTextSecondary,
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

                    HistoryDayCard(
                        plan = plan,
                        completedSessionsCount = completedSessions,
                        targetSessions = userSettings.dailyFocusTargetSessions,
                        scoreLabel = scoreLabel
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun HistoryDayCard(
    plan: DailyPlanEntity,
    completedSessionsCount: Int,
    targetSessions: Int,
    scoreLabel: String
) {
    val displayDate = DateUtil.formatToDisplay(plan.date)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, FocusBorder),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
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
                        color = FocusTextPrimary
                    )
                    Text(
                        text = "${plan.completedTasksCount}/3 tasks completed • $completedSessionsCount/$targetSessions focus sessions",
                        style = MaterialTheme.typography.bodySmall,
                        color = FocusTextSecondary
                    )
                }

                ScoreBadge(score = plan.calculatedScore, ratingLabel = scoreLabel)
            }

            if (plan.completedSummaryWhat.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Outcomes:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = FocusDarkGreen
                )
                Text(
                    text = plan.completedSummaryWhat,
                    style = MaterialTheme.typography.bodySmall,
                    color = FocusTextPrimary
                )
            }

            if (plan.completedSummaryDistraction.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Main distraction",
                        tint = Color(0xFFB45309),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Main Distraction: ${plan.completedSummaryDistraction}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF92400E)
                    )
                }
            }

            if (plan.workdayFinishedWithUnfinishedWork) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "⚠️ Ended with unfinished tasks",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFDC2626)
                )
            }
        }
    }
}
