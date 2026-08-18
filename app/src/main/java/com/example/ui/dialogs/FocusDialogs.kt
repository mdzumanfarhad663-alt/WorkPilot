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
import com.example.ui.theme.PilotBorder
import com.example.ui.theme.PilotDarkGreen
import com.example.ui.theme.PilotFailure
import com.example.ui.theme.PilotGreenContainer
import com.example.ui.theme.PilotTextBody
import com.example.ui.theme.PilotTextMuted
import com.example.ui.theme.PilotTextPrimary
import com.example.ui.theme.PilotTextSecondary

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
                color = PilotTextPrimary
            )
        },
        text = {
            Column {
                Text(
                    text = "Discipline rule: Record why you paused to stay conscious of interruptions.",
                    style = MaterialTheme.typography.bodySmall,
                    color = PilotTextSecondary
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
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    1.dp,
                                    if (isSelected) PilotDarkGreen else PilotBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .background(if (isSelected) PilotGreenContainer else Color.Transparent)
                                .clickable { reason = item }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = item,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) PilotDarkGreen else PilotTextPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    placeholder = { Text("Or type custom reason...", color = PilotTextMuted) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pause_reason_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PilotDarkGreen,
                        unfocusedBorderColor = PilotBorder,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalReason = if (reason.isBlank()) "Quick pause" else reason.trim()
                    onConfirmPause(finalReason)
                },
                modifier = Modifier.testTag("confirm_pause_button"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PilotDarkGreen,
                    contentColor = Color.White
                )
            ) {
                Text("Pause Timer", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, PilotBorder)
            ) {
                Text("Keep Working", color = PilotTextPrimary, fontWeight = FontWeight.SemiBold)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp)
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
                color = PilotFailure
            )
        },
        text = {
            Column {
                Text(
                    text = "Giving up will record an abandoned session in your history. State your reason honestly to learn from it:",
                    style = MaterialTheme.typography.bodySmall,
                    color = PilotTextSecondary
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
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    1.dp,
                                    if (isSelected) PilotFailure else PilotBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .background(if (isSelected) Color(0xFFFEE2E2) else Color.Transparent)
                                .clickable { reason = item }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = item,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) PilotFailure else PilotTextPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    placeholder = { Text("Short honest reason...", color = PilotTextMuted) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("abandon_reason_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PilotFailure,
                        unfocusedBorderColor = PilotBorder,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
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
                    containerColor = PilotFailure,
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
                border = androidx.compose.foundation.BorderStroke(1.dp, PilotBorder)
            ) {
                Text("Resume Focus", color = PilotTextPrimary, fontWeight = FontWeight.SemiBold)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp)
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
                color = PilotDarkGreen
            )
        },
        text = {
            Column {
                Text(
                    text = "Focused on: \"$taskTitle\"",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = PilotTextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Is the task fully completed?",
                    style = MaterialTheme.typography.headlineSmall,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = PilotTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "If you need more time, select 'No' to keep it active for another focus session.",
                    style = MaterialTheme.typography.bodySmall,
                    color = PilotTextSecondary
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirmCompleted(true) },
                modifier = Modifier.testTag("task_completed_yes_button"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PilotDarkGreen,
                    contentColor = Color.White
                )
            ) {
                Text("Yes, Task Completed!", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = { onConfirmCompleted(false) },
                modifier = Modifier.testTag("task_completed_no_button"),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, PilotBorder)
            ) {
                Text("No, Need Another Session", color = PilotTextPrimary, fontWeight = FontWeight.SemiBold)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp)
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
                color = PilotDarkGreen
            )
        },
        text = {
            Column {
                Text(
                    text = "“You still have unfinished work. Complete one more focus session before ending today.”",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = PilotTextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "You can still choose to end now, but the day will be recorded with unfinished work.",
                    style = MaterialTheme.typography.bodySmall,
                    color = PilotTextSecondary
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onContinueWork,
                modifier = Modifier.testTag("do_one_more_session_button"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PilotDarkGreen,
                    contentColor = Color.White
                )
            ) {
                Text("Complete 1 More Session", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onFinishAnyway,
                modifier = Modifier.testTag("finish_anyway_button"),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, PilotBorder)
            ) {
                Text("End Workday Anyway", color = PilotTextPrimary, fontWeight = FontWeight.SemiBold)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp)
    )
}

