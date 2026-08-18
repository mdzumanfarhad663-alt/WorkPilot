package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.ui.components.GoldenGradientButton
import com.example.ui.theme.BrownIconBorder
import com.example.ui.theme.CardSubtleBorder
import com.example.ui.theme.DarkChocolateHeadings
import com.example.ui.theme.GoldenAmberPrimary
import com.example.ui.theme.SoftCreamCard
import com.example.ui.theme.WarmBrownBody
import com.example.ui.theme.WarmBrownMuted
import com.example.ui.theme.WarmBrownSecondary
import com.example.ui.theme.WarmCrimsonFailure
import com.example.ui.theme.WarmPillBg

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PauseReasonDialog(
    onDismiss: () -> Unit,
    onConfirmPause: (reason: String) -> Unit
) {
    var reason by remember { mutableStateOf("") }
    val commonReasons = listOf(
        "Urgent client message",
        "Quick bio break",
        "Grab water / coffee",
        "Doorbell / Delivery",
        "Brief phone call"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Pause Focus Session",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = DarkChocolateHeadings
            )
        },
        text = {
            Column {
                Text(
                    text = "Discipline rule: Record why you paused to stay conscious of interruptions.",
                    style = MaterialTheme.typography.bodySmall,
                    color = WarmBrownSecondary
                )
                Spacer(modifier = Modifier.height(14.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    commonReasons.forEach { item ->
                        val isSelected = reason == item
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .border(
                                    1.dp,
                                    if (isSelected) GoldenAmberPrimary else CardSubtleBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .background(if (isSelected) WarmPillBg else SoftCreamCard)
                                .clickable { reason = item }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = item,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) DarkChocolateHeadings else WarmBrownBody
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    placeholder = { Text("Or type custom reason...", color = WarmBrownMuted) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pause_reason_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldenAmberPrimary,
                        unfocusedBorderColor = CardSubtleBorder,
                        focusedContainerColor = SoftCreamCard,
                        unfocusedContainerColor = SoftCreamCard
                    )
                )
            }
        },
        confirmButton = {
            GoldenGradientButton(
                text = "Pause Timer",
                onClick = {
                    val finalReason = if (reason.isBlank()) "Quick pause" else reason.trim()
                    onConfirmPause(finalReason)
                },
                height = 44.dp,
                modifier = Modifier.testTag("confirm_pause_button")
            )
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardSubtleBorder)
            ) {
                Text("Keep Working", color = WarmBrownBody, fontWeight = FontWeight.SemiBold)
            }
        },
        containerColor = SoftCreamCard,
        shape = RoundedCornerShape(18.dp)
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GiveUpReasonDialog(
    onDismiss: () -> Unit,
    onConfirmAbandon: (reason: String) -> Unit
) {
    var reason by remember { mutableStateOf("") }
    val commonReasons = listOf(
        "Lost focus / Distracted",
        "Scope was too big",
        "Need missing client info",
        "Mental fatigue",
        "Technical blocker"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Abandon Focus Session?",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = WarmCrimsonFailure
            )
        },
        text = {
            Column {
                Text(
                    text = "Giving up will record an abandoned session in your history. State your reason honestly to learn from it:",
                    style = MaterialTheme.typography.bodySmall,
                    color = WarmBrownSecondary
                )
                Spacer(modifier = Modifier.height(14.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    commonReasons.forEach { item ->
                        val isSelected = reason == item
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .border(
                                    1.dp,
                                    if (isSelected) WarmCrimsonFailure else CardSubtleBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .background(if (isSelected) Color(0xFFFDE8E8) else SoftCreamCard)
                                .clickable { reason = item }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = item,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) WarmCrimsonFailure else WarmBrownBody
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    placeholder = { Text("Short honest reason...", color = WarmBrownMuted) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("abandon_reason_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = WarmCrimsonFailure,
                        unfocusedBorderColor = CardSubtleBorder,
                        focusedContainerColor = SoftCreamCard,
                        unfocusedContainerColor = SoftCreamCard
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalReason = if (reason.isBlank()) "Abandoned session" else reason.trim()
                    onConfirmAbandon(finalReason)
                },
                modifier = Modifier.testTag("confirm_abandon_button"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = WarmCrimsonFailure,
                    contentColor = Color.White
                )
            ) {
                Text("Give Up Session", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardSubtleBorder)
            ) {
                Text("Resume Focus", color = WarmBrownBody, fontWeight = FontWeight.SemiBold)
            }
        },
        containerColor = SoftCreamCard,
        shape = RoundedCornerShape(18.dp)
    )
}

@Composable
fun TaskCompletionDialog(
    taskTitle: String,
    onConfirmCompleted: (isCompleted: Boolean) -> Unit
) {
    AlertDialog(
        onDismissRequest = { /* Require choice */ },
        title = {
            Text(
                text = "Session Finished",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = DarkChocolateHeadings
            )
        },
        text = {
            Column {
                Text(
                    text = "Focused on: \"$taskTitle\"",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkChocolateHeadings
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Is the task fully completed?",
                    style = MaterialTheme.typography.headlineSmall,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkChocolateHeadings
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "If you need more time, select 'No' to keep it active for another focus session.",
                    style = MaterialTheme.typography.bodySmall,
                    color = WarmBrownSecondary
                )
            }
        },
        confirmButton = {
            GoldenGradientButton(
                text = "Yes, Task Completed!",
                onClick = { onConfirmCompleted(true) },
                height = 44.dp,
                modifier = Modifier.testTag("task_completed_yes_button")
            )
        },
        dismissButton = {
            OutlinedButton(
                onClick = { onConfirmCompleted(false) },
                modifier = Modifier.testTag("task_completed_no_button"),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardSubtleBorder)
            ) {
                Text("No, Need Another Session", color = WarmBrownBody, fontWeight = FontWeight.SemiBold)
            }
        },
        containerColor = SoftCreamCard,
        shape = RoundedCornerShape(18.dp)
    )
}

@Composable
fun UnfinishedWorkDisciplineDialog(
    onContinueWork: () -> Unit,
    onFinishAnyway: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onContinueWork,
        title = {
            Text(
                text = "Unfinished Work",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = DarkChocolateHeadings
            )
        },
        text = {
            Column {
                Text(
                    text = "“You still have unfinished work. Complete one more focus session before ending today.”",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = DarkChocolateHeadings
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "You can still choose to end now, but the day will be recorded with unfinished work.",
                    style = MaterialTheme.typography.bodySmall,
                    color = WarmBrownSecondary
                )
            }
        },
        confirmButton = {
            GoldenGradientButton(
                text = "Complete 1 More Session",
                onClick = onContinueWork,
                height = 44.dp,
                modifier = Modifier.testTag("do_one_more_session_button")
            )
        },
        dismissButton = {
            OutlinedButton(
                onClick = onFinishAnyway,
                modifier = Modifier.testTag("finish_anyway_button"),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardSubtleBorder)
            ) {
                Text("End Workday Anyway", color = WarmBrownBody, fontWeight = FontWeight.SemiBold)
            }
        },
        containerColor = SoftCreamCard,
        shape = RoundedCornerShape(18.dp)
    )
}
