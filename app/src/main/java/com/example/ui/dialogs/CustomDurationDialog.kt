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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.TaskType
import com.example.ui.components.GoldenGradientButton
import com.example.ui.theme.BrownIconBorder
import com.example.ui.theme.CardSubtleBorder
import com.example.ui.theme.DarkChocolateHeadings
import com.example.ui.theme.GoldenAmberPrimary
import com.example.ui.theme.SoftCreamCard
import com.example.ui.theme.WarmBrownBody
import com.example.ui.theme.WarmBrownSecondary
import com.example.ui.theme.WarmPillBg

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CustomDurationDialog(
    taskType: TaskType,
    taskTitle: String,
    currentDuration: Int,
    onDismiss: () -> Unit,
    onConfirmDuration: (Int) -> Unit
) {
    var selectedMinutes by remember { mutableIntStateOf(currentDuration) }
    var customText by remember { mutableStateOf(currentDuration.toString()) }

    val presetDurations = listOf(5, 10, 15, 20, 25, 30, 35, 45, 50, 60, 90)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .widthIn(max = 440.dp)
                .fillMaxWidth(0.95f),
            shape = RoundedCornerShape(18.dp),
            color = SoftCreamCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, CardSubtleBorder),
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(WarmPillBg)
                            .border(1.dp, CardSubtleBorder, RoundedCornerShape(10.dp))
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Timer",
                            tint = BrownIconBorder,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Set Timer for ${taskType.displayName}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = DarkChocolateHeadings
                        )
                        if (taskTitle.isNotBlank()) {
                            Text(
                                text = taskTitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = WarmBrownSecondary,
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Custom Minute Stepper & Input
                Text(
                    text = "Custom Duration (Minutes)",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkChocolateHeadings
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = {
                            val newM = (selectedMinutes - 5).coerceAtLeast(1)
                            selectedMinutes = newM
                            customText = newM.toString()
                        },
                        modifier = Modifier
                            .border(1.dp, CardSubtleBorder, RoundedCornerShape(10.dp))
                            .background(WarmPillBg, RoundedCornerShape(10.dp))
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Minus 5m", tint = BrownIconBorder)
                    }

                    OutlinedTextField(
                        value = customText,
                        onValueChange = { input ->
                            val digits = input.filter { it.isDigit() }
                            customText = digits
                            val parsed = digits.toIntOrNull()
                            if (parsed != null && parsed in 1..300) {
                                selectedMinutes = parsed
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("custom_duration_input"),
                        textStyle = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = DarkChocolateHeadings
                        ),
                        suffix = { Text("min", fontWeight = FontWeight.SemiBold, color = WarmBrownSecondary) },
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldenAmberPrimary,
                            unfocusedBorderColor = CardSubtleBorder,
                            focusedContainerColor = SoftCreamCard,
                            unfocusedContainerColor = SoftCreamCard
                        )
                    )

                    IconButton(
                        onClick = {
                            val newM = (selectedMinutes + 5).coerceAtMost(300)
                            selectedMinutes = newM
                            customText = newM.toString()
                        },
                        modifier = Modifier
                            .border(1.dp, CardSubtleBorder, RoundedCornerShape(10.dp))
                            .background(WarmPillBg, RoundedCornerShape(10.dp))
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Plus 5m", tint = BrownIconBorder)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Quick presets
                Text(
                    text = "Quick Presets",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkChocolateHeadings
                )
                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    presetDurations.forEach { dur ->
                        val isSelected = selectedMinutes == dur
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    1.dp,
                                    if (isSelected) GoldenAmberPrimary else CardSubtleBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .background(if (isSelected) WarmPillBg else SoftCreamCard)
                                .clickable {
                                    selectedMinutes = dur
                                    customText = dur.toString()
                                }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = "$dur m",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) DarkChocolateHeadings else WarmBrownBody
                            )
                        }
                    }
                }

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
                        text = "Set $selectedMinutes Min",
                        onClick = {
                            val finalMinutes = selectedMinutes.coerceIn(1, 300)
                            onConfirmDuration(finalMinutes)
                        },
                        height = 48.dp,
                        modifier = Modifier
                            .weight(1.5f)
                            .testTag("apply_duration_button")
                    )
                }
            }
        }
    }
}
