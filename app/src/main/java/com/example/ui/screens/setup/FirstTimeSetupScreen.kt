package com.example.ui.screens.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.ui.theme.WarmIvoryBg
import com.example.ui.theme.WarmPillBg

@Composable
fun FirstTimeSetupScreen(
    onCompleteSetup: (name: String, hour: Int, minute: Int, dailyTarget: Int, duration: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var selectedHour by remember { mutableIntStateOf(9) } // 9 AM
    var selectedMinute by remember { mutableIntStateOf(0) }
    var dailyTarget by remember { mutableIntStateOf(3) } // 2, 3, 4
    var focusDuration by remember { mutableIntStateOf(25) } // 25, 50, 90

    val timeOptions = listOf(
        Triple("7:00 AM", 7, 0),
        Triple("8:00 AM", 8, 0),
        Triple("9:00 AM", 9, 0),
        Triple("10:00 AM", 10, 0),
        Triple("1:00 PM", 13, 0)
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(WarmIvoryBg),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 24.dp,
            bottom = 64.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Surface(
                modifier = Modifier
                    .widthIn(max = 560.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = SoftCreamCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, CardSubtleBorder),
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    // Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(WarmPillBg)
                                .border(1.dp, CardSubtleBorder, RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "WorkPilot",
                                tint = BrownIconBorder
                            )
                        }
                        Column {
                            Text(
                                text = "Welcome to WorkPilot",
                                style = MaterialTheme.typography.headlineSmall,
                                color = DarkChocolateHeadings,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Plan. Focus. Finish.",
                                style = MaterialTheme.typography.bodySmall,
                                color = WarmBrownSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Question 1: Name
                    Text(
                        text = "1. What is your name?",
                        style = MaterialTheme.typography.titleMedium,
                        color = DarkChocolateHeadings,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = { Text("Your name (e.g. Alex)", color = WarmBrownMuted) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("setup_name_input"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldenAmberPrimary,
                            unfocusedBorderColor = CardSubtleBorder,
                            focusedContainerColor = SoftCreamCard,
                            unfocusedContainerColor = SoftCreamCard
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Question 2: Starting time
                    Text(
                        text = "2. Normal work starting time",
                        style = MaterialTheme.typography.titleMedium,
                        color = DarkChocolateHeadings,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "You get full discipline credit if you begin within 30 minutes of this time.",
                        style = MaterialTheme.typography.bodySmall,
                        color = WarmBrownSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        timeOptions.forEach { (label, h, m) ->
                            val isSelected = selectedHour == h && selectedMinute == m
                            SelectionChip(
                                text = label,
                                isSelected = isSelected,
                                onClick = {
                                    selectedHour = h
                                    selectedMinute = m
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Question 3: Daily focus target
                    Text(
                        text = "3. Daily focus target (sessions)",
                        style = MaterialTheme.typography.titleMedium,
                        color = DarkChocolateHeadings,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listOf(2, 3, 4).forEach { target ->
                            val isSelected = dailyTarget == target
                            SelectionChip(
                                text = "$target sessions",
                                isSelected = isSelected,
                                onClick = { dailyTarget = target },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Question 4: Focus duration
                    Text(
                        text = "4. Default focus session duration",
                        style = MaterialTheme.typography.titleMedium,
                        color = DarkChocolateHeadings,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listOf(25, 50, 90).forEach { dur ->
                            val isSelected = focusDuration == dur
                            SelectionChip(
                                text = "$dur min",
                                isSelected = isSelected,
                                onClick = { focusDuration = dur },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Start button (Golden Orange Gradient)
                    GoldenGradientButton(
                        text = "Begin Workday",
                        onClick = {
                            val finalName = if (name.isBlank()) "Freelancer" else name.trim()
                            onCompleteSetup(finalName, selectedHour, selectedMinute, dailyTarget, focusDuration)
                        },
                        height = 52.dp,
                        fontSize = 15.sp,
                        modifier = Modifier.testTag("complete_setup_button")
                    )
                }
            }
        }
    }
}

@Composable
fun SelectionChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .border(
                width = 1.dp,
                color = if (isSelected) GoldenAmberPrimary else CardSubtleBorder,
                shape = RoundedCornerShape(10.dp)
            )
            .background(if (isSelected) WarmPillBg else SoftCreamCard)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) DarkChocolateHeadings else WarmBrownBody
        )
    }
}
