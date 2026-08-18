package com.example.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserSettings
import com.example.ui.components.GoldenGradientButton
import com.example.ui.components.RewardPointsBadge
import com.example.ui.theme.BrownIconBorder
import com.example.ui.theme.CardSubtleBorder
import com.example.ui.theme.DarkChocolateHeadings
import com.example.ui.theme.GoldenAmberPrimary
import com.example.ui.theme.SoftCreamCard
import com.example.ui.theme.WarmAmberWarning
import com.example.ui.theme.WarmBrownBody
import com.example.ui.theme.WarmBrownMuted
import com.example.ui.theme.WarmBrownSecondary
import com.example.ui.theme.WarmCrimsonFailure
import com.example.ui.theme.WarmIvoryBg
import com.example.ui.theme.WarmPillBg

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    userSettings: UserSettings,
    onUpdateName: (String) -> Unit,
    onUpdateWorkStartTime: (hour: Int, minute: Int) -> Unit,
    onUpdateDailyTargetSessions: (Int) -> Unit,
    onUpdateDefaultFocusDuration: (Int) -> Unit,
    onUpdateRewardPoints: (Int) -> Unit,
    onOpenBackupDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    var nameInput by remember(userSettings.name) { mutableStateOf(userSettings.name) }
    var showCustomPointsEditor by remember { mutableStateOf(false) }
    var customPointsInput by remember(userSettings.totalRewardPoints) {
        mutableStateOf(userSettings.totalRewardPoints.toString())
    }

    val timeOptions = listOf(
        Triple("7:00 AM", 7, 0),
        Triple("8:00 AM", 8, 0),
        Triple("8:30 AM", 8, 30),
        Triple("9:00 AM", 9, 0),
        Triple("9:30 AM", 9, 30),
        Triple("10:00 AM", 10, 0),
        Triple("1:00 PM", 13, 0),
        Triple("2:00 PM", 14, 0)
    )

    val sessionTargetOptions = listOf(1, 2, 3, 4, 5, 6)
    val durationOptions = listOf(15, 20, 25, 30, 35, 45, 50, 60, 90)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(WarmIvoryBg),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 16.dp,
            bottom = 80.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header Card
        item {
            Surface(
                modifier = Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = SoftCreamCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, CardSubtleBorder),
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(WarmPillBg)
                            .border(1.dp, CardSubtleBorder, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = BrownIconBorder,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Settings & Preferences",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = DarkChocolateHeadings
                        )
                        Text(
                            text = "Customize your work schedule, daily targets, and profile",
                            style = MaterialTheme.typography.bodySmall,
                            color = WarmBrownSecondary
                        )
                    }
                }
            }
        }

        // 2. Personal Profile Section (Name)
        item {
            SettingsCard(
                title = "Personal Profile",
                icon = Icons.Default.Person,
                description = "Change the name shown on your daily dashboard"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        placeholder = { Text("Your name (e.g. Alex)", color = WarmBrownMuted) },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("settings_name_input"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldenAmberPrimary,
                            unfocusedBorderColor = CardSubtleBorder,
                            focusedContainerColor = SoftCreamCard,
                            unfocusedContainerColor = SoftCreamCard
                        )
                    )

                    GoldenGradientButton(
                        text = "Save",
                        onClick = { onUpdateName(nameInput) },
                        enabled = nameInput.isNotBlank() && nameInput != userSettings.name,
                        height = 52.dp,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .width(90.dp)
                            .testTag("settings_save_name_button")
                    )
                }
            }
        }

        // 3. Normal Work Starting Time
        item {
            SettingsCard(
                title = "Work Starting Time",
                icon = Icons.Default.AccessTime,
                description = "Currently: ${userSettings.formattedWorkStartTime}. Starting within 30 min of this time earns you full on-time discipline credit."
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    timeOptions.forEach { (label, h, m) ->
                        val isSelected = userSettings.normalWorkStartHour == h && userSettings.normalWorkStartMinute == m
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .border(
                                    1.dp,
                                    if (isSelected) GoldenAmberPrimary else CardSubtleBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .background(if (isSelected) WarmPillBg else SoftCreamCard)
                                .clickable { onUpdateWorkStartTime(h, m) }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) DarkChocolateHeadings else WarmBrownBody
                            )
                        }
                    }
                }
            }
        }

        // 4. Daily Focus Target (Sessions)
        item {
            SettingsCard(
                title = "Daily Focus Target (Sessions)",
                icon = Icons.Default.Lock,
                description = "Currently set to ${userSettings.dailyFocusTargetSessions} sessions per day. Change this anytime to fit your workload."
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    sessionTargetOptions.forEach { target ->
                        val isSelected = userSettings.dailyFocusTargetSessions == target
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .border(
                                    1.dp,
                                    if (isSelected) GoldenAmberPrimary else CardSubtleBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .background(if (isSelected) WarmPillBg else SoftCreamCard)
                                .clickable { onUpdateDailyTargetSessions(target) }
                                .padding(horizontal = 16.dp, vertical = 9.dp)
                                .testTag("target_session_chip_$target")
                        ) {
                            Text(
                                text = "$target ${if (target == 1) "session" else "sessions"}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) DarkChocolateHeadings else WarmBrownBody
                            )
                        }
                    }
                }
            }
        }

        // 5. Default Session Duration
        item {
            SettingsCard(
                title = "Default Focus Duration",
                icon = Icons.Default.Timer,
                description = "Default timer for new tasks: ${userSettings.defaultFocusDurationMinutes} min (you can still set custom minutes per task on Today/Focus)."
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    durationOptions.forEach { dur ->
                        val isSelected = userSettings.defaultFocusDurationMinutes == dur
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .border(
                                    1.dp,
                                    if (isSelected) GoldenAmberPrimary else CardSubtleBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .background(if (isSelected) WarmPillBg else SoftCreamCard)
                                .clickable { onUpdateDefaultFocusDuration(dur) }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "$dur min",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) DarkChocolateHeadings else WarmBrownBody
                            )
                        }
                    }
                }
            }
        }

        // 6. Reward Points Manager
        item {
            SettingsCard(
                title = "Reward Points Balance",
                icon = Icons.Default.EmojiEvents,
                description = "Rule: +10 points for completed tasks, -10 points for uncompleted tasks."
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RewardPointsBadge(
                            points = userSettings.totalRewardPoints,
                            isLarge = true
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { onUpdateRewardPoints(userSettings.totalRewardPoints + 10) },
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CardSubtleBorder)
                            ) {
                                Text("+10 pts", fontWeight = FontWeight.Bold, color = GoldenAmberPrimary)
                            }

                            OutlinedButton(
                                onClick = { onUpdateRewardPoints(userSettings.totalRewardPoints - 10) },
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CardSubtleBorder)
                            ) {
                                Text("-10 pts", fontWeight = FontWeight.Bold, color = WarmCrimsonFailure)
                            }

                            OutlinedButton(
                                onClick = { onUpdateRewardPoints(0) },
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CardSubtleBorder)
                            ) {
                                Text("Reset 0", fontWeight = FontWeight.SemiBold, color = WarmBrownSecondary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (!showCustomPointsEditor) {
                        OutlinedButton(
                            onClick = { showCustomPointsEditor = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CardSubtleBorder)
                        ) {
                            Text("Set Custom Points Balance", color = DarkChocolateHeadings)
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = customPointsInput,
                                onValueChange = { customPointsInput = it },
                                placeholder = { Text("Points (e.g. 50 or -20)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldenAmberPrimary,
                                    unfocusedBorderColor = CardSubtleBorder,
                                    focusedContainerColor = SoftCreamCard,
                                    unfocusedContainerColor = SoftCreamCard
                                )
                            )

                            GoldenGradientButton(
                                text = "Apply",
                                onClick = {
                                    val parsed = customPointsInput.toIntOrNull()
                                    if (parsed != null) {
                                        onUpdateRewardPoints(parsed)
                                        showCustomPointsEditor = false
                                    }
                                },
                                height = 50.dp,
                                modifier = Modifier.width(90.dp)
                            )

                            OutlinedButton(
                                onClick = { showCustomPointsEditor = false },
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CardSubtleBorder),
                                modifier = Modifier.height(50.dp)
                            ) {
                                Text("Cancel", color = WarmBrownSecondary)
                            }
                        }
                    }
                }
            }
        }

        // 7. Backup & Restore Data
        item {
            SettingsCard(
                title = "Data & Backups",
                icon = Icons.Default.ArrowUpward,
                description = "Export and import your focus history, daily plans, and discipline streaks anytime."
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GoldenGradientButton(
                        text = "Export Backup",
                        onClick = onOpenBackupDialog,
                        leadingIcon = Icons.Default.ArrowUpward,
                        height = 46.dp,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("settings_export_button")
                    )

                    OutlinedButton(
                        onClick = onOpenBackupDialog,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("settings_import_button"),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardSubtleBorder)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = "Import",
                            tint = BrownIconBorder,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Import Backup", color = WarmBrownBody, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // 8. About WorkPilot
        item {
            Surface(
                modifier = Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = SoftCreamCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, CardSubtleBorder),
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "WorkPilot",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DarkChocolateHeadings
                    )
                    Text(
                        text = "Plan. Focus. Finish.",
                        style = MaterialTheme.typography.bodySmall,
                        color = WarmBrownSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "v1.2 • 100% Offline & Private Local Storage",
                        style = MaterialTheme.typography.labelSmall,
                        color = WarmBrownMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsCard(
    title: String,
    icon: ImageVector,
    description: String,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier
            .widthIn(max = 720.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = SoftCreamCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, CardSubtleBorder),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
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
                        imageVector = icon,
                        contentDescription = title,
                        tint = BrownIconBorder,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DarkChocolateHeadings
                )
            }

            if (description.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = WarmBrownSecondary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}
