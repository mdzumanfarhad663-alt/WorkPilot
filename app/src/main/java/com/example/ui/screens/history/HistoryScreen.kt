package com.example.ui.screens.history

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
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
import com.example.ui.components.RewardPointsBadge
import com.example.ui.components.ScoreBadge
import com.example.ui.theme.PilotBorder
import com.example.ui.theme.PilotDarkGreen
import com.example.ui.theme.PilotFailure
import com.example.ui.theme.PilotGreenContainer
import com.example.ui.theme.PilotSuccess
import com.example.ui.theme.PilotTextBody
import com.example.ui.theme.PilotTextPrimary
import com.example.ui.theme.PilotTextSecondary
import com.example.ui.theme.PilotWarning
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
            // Header & Backup buttons
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
                                    text = "Workday History",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = PilotTextPrimary
                                )
                                Text(
                                    text = "Discipline track record and past reviews",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = PilotTextSecondary
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

                        // Backup Action Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = onOpenBackupDialog,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("backup_data_button"),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PilotDarkGreen,
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowUpward,
                                    contentDescription = "Export",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Export Backup", fontWeight = FontWeight.SemiBold)
                            }

                            OutlinedButton(
                                onClick = onOpenBackupDialog,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("import_data_button"),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, PilotBorder)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDownward,
                                    contentDescription = "Import",
                                    tint = PilotTextPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Import Backup", color = PilotTextPrimary, fontWeight = FontWeight.SemiBold)
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
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, PilotBorder),
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
                                color = PilotTextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Complete today's focus sessions and end your workday to generate your first score entry.",
                                style = MaterialTheme.typography.bodySmall,
                                color = PilotTextSecondary,
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
                            scoreLabel = scoreLabel
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
    scoreLabel: String
) {
    val displayDate = DateUtil.formatToDisplay(plan.date)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, PilotBorder),
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
                        color = PilotTextPrimary
                    )
                    Text(
                        text = "${plan.completedTasksCount}/3 tasks completed • $completedSessionsCount/$targetSessions focus sessions",
                        style = MaterialTheme.typography.bodySmall,
                        color = PilotTextSecondary
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val pts = plan.rewardPointsEarned
                    Text(
                        text = if (pts >= 0) "+$pts pts" else "$pts pts",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (pts >= 0) PilotSuccess else PilotFailure
                    )
                    ScoreBadge(score = plan.calculatedScore, ratingLabel = scoreLabel)
                }
            }

            if (plan.completedSummaryWhat.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Outcomes:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = PilotDarkGreen
                )
                Text(
                    text = plan.completedSummaryWhat,
                    style = MaterialTheme.typography.bodySmall,
                    color = PilotTextBody
                )
            }

            if (plan.completedSummaryDistraction.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Main distraction",
                        tint = PilotWarning,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Main Distraction: ${plan.completedSummaryDistraction}",
                        style = MaterialTheme.typography.bodySmall,
                        color = PilotWarning
                    )
                }
            }

            if (plan.workdayFinishedWithUnfinishedWork) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "⚠️ Ended with unfinished tasks (-10 pts per task)",
                    style = MaterialTheme.typography.labelSmall,
                    color = PilotFailure
                )
            }
        }
    }
}
