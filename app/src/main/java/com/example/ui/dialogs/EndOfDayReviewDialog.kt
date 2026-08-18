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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.DailyScoreResult
import com.example.ui.components.GoldenGradientButton
import com.example.ui.components.RewardPointsBadge
import com.example.ui.components.ScoreBadge
import com.example.ui.theme.CardSubtleBorder
import com.example.ui.theme.DarkChocolateHeadings
import com.example.ui.theme.GoldenAmberPrimary
import com.example.ui.theme.SoftCreamCard
import com.example.ui.theme.WarmAmberWarning
import com.example.ui.theme.WarmBrownBody
import com.example.ui.theme.WarmBrownMuted
import com.example.ui.theme.WarmBrownSecondary
import com.example.ui.theme.WarmCrimsonFailure
import com.example.ui.theme.WarmOliveSuccess
import com.example.ui.theme.WarmPillBg

@Composable
fun EndOfDayReviewDialog(
    onDismiss: () -> Unit,
    onSubmitReview: (
        completedWhat: String,
        distraction: String,
        tomorrow1: String,
        tomorrow2: String,
        tomorrow3: String
    ) -> Unit
) {
    var completedWhat by remember { mutableStateOf("") }
    var distraction by remember { mutableStateOf("") }
    var tomorrow1 by remember { mutableStateOf("") }
    var tomorrow2 by remember { mutableStateOf("") }
    var tomorrow3 by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxWidth(0.92f),
            shape = RoundedCornerShape(18.dp),
            color = SoftCreamCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, CardSubtleBorder),
            shadowElevation = 2.dp
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
                    color = DarkChocolateHeadings
                )
                Text(
                    text = "Reflect on today, calculate points & score, and plan tomorrow's 3 tasks.",
                    style = MaterialTheme.typography.bodySmall,
                    color = WarmBrownSecondary
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Question 1
                Text(
                    text = "1. What did I complete today?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DarkChocolateHeadings
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = completedWhat,
                    onValueChange = { completedWhat = it },
                    placeholder = { Text("Key outcomes, deliverables, and wins...", color = WarmBrownMuted) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("review_completed_input"),
                    minLines = 2,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldenAmberPrimary,
                        unfocusedBorderColor = CardSubtleBorder,
                        focusedContainerColor = SoftCreamCard,
                        unfocusedContainerColor = SoftCreamCard
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Question 2
                Text(
                    text = "2. What distracted me today?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DarkChocolateHeadings
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = distraction,
                    onValueChange = { distraction = it },
                    placeholder = { Text("Social media, unstructured calls, tab-switching...", color = WarmBrownMuted) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("review_distraction_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldenAmberPrimary,
                        unfocusedBorderColor = CardSubtleBorder,
                        focusedContainerColor = SoftCreamCard,
                        unfocusedContainerColor = SoftCreamCard
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Question 3: Tomorrow's 3 tasks
                Text(
                    text = "3. What are tomorrow’s three tasks?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DarkChocolateHeadings
                )
                Text(
                    text = "Plan 3 clear tasks to be ready tomorrow morning.",
                    style = MaterialTheme.typography.bodySmall,
                    color = WarmBrownSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = tomorrow1,
                    onValueChange = { tomorrow1 = it },
                    label = { Text("Task 1", color = WarmBrownSecondary) },
                    placeholder = { Text("e.g. Finish client proposal deliverable", color = WarmBrownMuted) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("review_tomorrow_1_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldenAmberPrimary,
                        unfocusedBorderColor = CardSubtleBorder,
                        focusedContainerColor = SoftCreamCard,
                        unfocusedContainerColor = SoftCreamCard
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = tomorrow2,
                    onValueChange = { tomorrow2 = it },
                    label = { Text("Task 2", color = WarmBrownSecondary) },
                    placeholder = { Text("e.g. Send five tailored outreach emails", color = WarmBrownMuted) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("review_tomorrow_2_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldenAmberPrimary,
                        unfocusedBorderColor = CardSubtleBorder,
                        focusedContainerColor = SoftCreamCard,
                        unfocusedContainerColor = SoftCreamCard
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = tomorrow3,
                    onValueChange = { tomorrow3 = it },
                    label = { Text("Task 3", color = WarmBrownSecondary) },
                    placeholder = { Text("e.g. Reply to inbox and send weekly invoice", color = WarmBrownMuted) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("review_tomorrow_3_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldenAmberPrimary,
                        unfocusedBorderColor = CardSubtleBorder,
                        focusedContainerColor = SoftCreamCard,
                        unfocusedContainerColor = SoftCreamCard
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardSubtleBorder)
                    ) {
                        Text("Cancel", color = WarmBrownBody, fontWeight = FontWeight.SemiBold)
                    }

                    GoldenGradientButton(
                        text = "Finalize & Score",
                        onClick = {
                            onSubmitReview(
                                completedWhat,
                                distraction,
                                tomorrow1,
                                tomorrow2,
                                tomorrow3
                            )
                        },
                        height = 48.dp,
                        modifier = Modifier
                            .weight(2f)
                            .testTag("submit_review_button")
                    )
                }
            }
        }
    }
}

@Composable
fun ReviewSummaryDialog(
    scoreResult: DailyScoreResult,
    currentStreak: Int,
    totalRewardPoints: Int,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .widthIn(max = 480.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = SoftCreamCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, CardSubtleBorder),
            shadowElevation = 2.dp
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
                    color = DarkChocolateHeadings
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Here is your discipline & reward summary for today:",
                    style = MaterialTheme.typography.bodySmall,
                    color = WarmBrownSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(WarmPillBg)
                        .border(1.dp, CardSubtleBorder, RoundedCornerShape(14.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${scoreResult.totalScore} / 5",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = DarkChocolateHeadings
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        ScoreBadge(score = scoreResult.totalScore, ratingLabel = scoreResult.ratingLabel)
                        Spacer(modifier = Modifier.height(10.dp))

                        // Reward Points Delta
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val ptsDelta = scoreResult.todayRewardPointsDelta
                            Text(
                                text = if (ptsDelta >= 0) "Today: +$ptsDelta pts" else "Today: $ptsDelta pts",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (ptsDelta >= 0) WarmOliveSuccess else WarmCrimsonFailure
                            )
                            Text(text = "•", color = WarmBrownSecondary)
                            RewardPointsBadge(points = totalRewardPoints)
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (scoreResult.isSuccessful) "🔥 Streak: $currentStreak days" else "Streak reset to 0 (Score < 4)",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (scoreResult.isSuccessful) WarmOliveSuccess else WarmBrownMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Breakdown list
                ScoreBreakdownRow("Planned 3 tasks", scoreResult.plannedThreeTasksPoint == 1)
                ScoreBreakdownRow("Started within 30 min of schedule", scoreResult.startedOnTimePoint == 1)
                ScoreBreakdownRow("Completed focus session target", scoreResult.completedTargetSessionsPoint == 1)
                ScoreBreakdownRow("Completed at least 1 task", scoreResult.completedAllTasksPoint == 1)
                ScoreBreakdownRow("Planned tomorrow before ending", scoreResult.plannedTomorrowPoint == 1)

                Spacer(modifier = Modifier.height(10.dp))
                val completedBonus = scoreResult.completedTasksCount * 10
                val missedPenalty = scoreResult.uncompletedTasksCount * 10
                Text(
                    text = "Points Breakdown: +$completedBonus pts (${scoreResult.completedTasksCount} done) - $missedPenalty pts (${scoreResult.uncompletedTasksCount} missed)",
                    style = MaterialTheme.typography.bodySmall,
                    color = WarmBrownSecondary
                )

                Spacer(modifier = Modifier.height(20.dp))

                GoldenGradientButton(
                    text = "Done",
                    onClick = onDismiss,
                    height = 48.dp,
                    modifier = Modifier.testTag("dismiss_summary_button")
                )
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
            color = WarmBrownBody
        )
        Text(
            text = if (earned) "+1 pt" else "0 pt",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = if (earned) WarmOliveSuccess else WarmBrownMuted
        )
    }
}
