package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.DailyScoreResult
import com.example.ui.components.ScoreBadge
import com.example.ui.theme.FocusBorder
import com.example.ui.theme.FocusDarkGreen
import com.example.ui.theme.FocusGreenContainer
import com.example.ui.theme.FocusTextMuted
import com.example.ui.theme.FocusTextPrimary
import com.example.ui.theme.FocusTextSecondary

@Composable
fun EndOfDayReviewDialog(
    onDismiss: () -> Unit,
    onSubmitReview: (
        completedWhat: String,
        distraction: String,
        tomorrowMoney: String,
        tomorrowGrowth: String,
        tomorrowMaintenance: String
    ) -> Unit
) {
    var completedWhat by remember { mutableStateOf("") }
    var distraction by remember { mutableStateOf("") }
    var tomorrowMoney by remember { mutableStateOf("") }
    var tomorrowGrowth by remember { mutableStateOf("") }
    var tomorrowMaintenance by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxWidth(0.92f),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, FocusBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                Text(
                    text = "End-of-Day Review",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = FocusTextPrimary
                )
                Text(
                    text = "Reflect with precision, calculate your daily score, and set up tomorrow.",
                    style = MaterialTheme.typography.bodySmall,
                    color = FocusTextSecondary
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Question 1
                Text(
                    text = "1. What did I complete today?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = FocusTextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = completedWhat,
                    onValueChange = { completedWhat = it },
                    placeholder = { Text("Key outcomes, deliverables, and wins...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("review_completed_input"),
                    minLines = 2,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FocusDarkGreen,
                        unfocusedBorderColor = FocusBorder
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Question 2
                Text(
                    text = "2. What distracted me today?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = FocusTextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = distraction,
                    onValueChange = { distraction = it },
                    placeholder = { Text("Social media, unstructured client calls, tab-switching...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("review_distraction_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FocusDarkGreen,
                        unfocusedBorderColor = FocusBorder
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Question 3: Tomorrow's 3 tasks
                Text(
                    text = "3. What are tomorrow’s three tasks?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = FocusTextPrimary
                )
                Text(
                    text = "Planning tomorrow gives you +1 discipline point for today.",
                    style = MaterialTheme.typography.bodySmall,
                    color = FocusTextMuted
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = tomorrowMoney,
                    onValueChange = { tomorrowMoney = it },
                    label = { Text("Money Task (Direct Revenue)") },
                    placeholder = { Text("e.g. Finish Stripe checkout integration") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("review_tomorrow_money_input"),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FocusDarkGreen,
                        unfocusedBorderColor = FocusBorder
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = tomorrowGrowth,
                    onValueChange = { tomorrowGrowth = it },
                    label = { Text("Growth Task (Outreach/Proposals)") },
                    placeholder = { Text("e.g. Send 5 client pitches") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("review_tomorrow_growth_input"),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FocusDarkGreen,
                        unfocusedBorderColor = FocusBorder
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = tomorrowMaintenance,
                    onValueChange = { tomorrowMaintenance = it },
                    label = { Text("Maintenance Task (Admin/Emails)") },
                    placeholder = { Text("e.g. Reply to backlog emails & send invoice") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("review_tomorrow_maint_input"),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FocusDarkGreen,
                        unfocusedBorderColor = FocusBorder
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            onSubmitReview(
                                completedWhat,
                                distraction,
                                tomorrowMoney,
                                tomorrowGrowth,
                                tomorrowMaintenance
                            )
                        },
                        modifier = Modifier
                            .weight(2f)
                            .testTag("submit_review_button"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FocusDarkGreen,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Finalize & Calculate Score")
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewSummaryDialog(
    scoreResult: DailyScoreResult,
    currentStreak: Int,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .widthIn(max = 480.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, FocusBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Workday Complete",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = FocusTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Here is your discipline score for today:",
                    style = MaterialTheme.typography.bodySmall,
                    color = FocusTextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(FocusGreenContainer)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${scoreResult.totalScore} / 5",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = FocusDarkGreen
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        ScoreBadge(score = scoreResult.totalScore, ratingLabel = scoreResult.ratingLabel)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (scoreResult.isSuccessful) "🔥 Streak: $currentStreak days" else "Streak reset to 0 (Score < 4)",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = FocusDarkGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Breakdown list
                ScoreBreakdownRow("Planned 3 tasks", scoreResult.plannedThreeTasksPoint == 1)
                ScoreBreakdownRow("Started within 30 min of schedule", scoreResult.startedOnTimePoint == 1)
                ScoreBreakdownRow("Completed focus session target", scoreResult.completedTargetSessionsPoint == 1)
                ScoreBreakdownRow("Completed Money Task", scoreResult.completedMoneyTaskPoint == 1)
                ScoreBreakdownRow("Planned tomorrow before ending", scoreResult.plannedTomorrowPoint == 1)

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dismiss_summary_button"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FocusDarkGreen,
                        contentColor = Color.White
                    )
                ) {
                    Text("Done")
                }
            }
        }
    }
}

@Composable
private fun ScoreBreakdownRow(label: String, earned: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = FocusTextPrimary
        )
        Text(
            text = if (earned) "+1 pt" else "0 pt",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = if (earned) FocusDarkGreen else FocusTextMuted
        )
    }
}
