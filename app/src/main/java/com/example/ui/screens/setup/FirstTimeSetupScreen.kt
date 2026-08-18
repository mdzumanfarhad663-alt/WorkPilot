package com.example.ui.screens.setup

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.ui.theme.FocusBorder
import com.example.ui.theme.FocusDarkGreen
import com.example.ui.theme.FocusGreenContainer
import com.example.ui.theme.FocusLightGreen
import com.example.ui.theme.FocusTextMuted
import com.example.ui.theme.FocusTextPrimary
import com.example.ui.theme.FocusTextSecondary

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

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 24.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Surface(
            modifier = Modifier
                .widthIn(max = 540.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, FocusBorder),
            shadowElevation = 2.dp
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
                            .background(FocusGreenContainer)
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "FocusLock",
                            tint = FocusDarkGreen
                        )
                    }
                    Column {
                        Text(
                            text = "Welcome to FocusLock",
                            style = MaterialTheme.typography.headlineSmall,
                            color = FocusTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Calm, minimal discipline for freelancers.",
                            style = MaterialTheme.typography.bodySmall,
                            color = FocusTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Question 1: Name
                Text(
                    text = "1. What is your name?",
                    style = MaterialTheme.typography.titleMedium,
                    color = FocusTextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("Your name (e.g. Alex)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("setup_name_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FocusDarkGreen,
                        unfocusedBorderColor = FocusBorder
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Question 2: Starting time
                Text(
                    text = "2. Normal work starting time",
                    style = MaterialTheme.typography.titleMedium,
                    color = FocusTextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "You get full discipline credit if you begin within 30 minutes.",
                    style = MaterialTheme.typography.bodySmall,
                    color = FocusTextMuted
                )
                Spacer(modifier = Modifier.height(8.dp))
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
                    color = FocusTextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
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
                    text = "4. Focus session duration",
                    style = MaterialTheme.typography.titleMedium,
                    color = FocusTextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
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

                // Start button
                Button(
                    onClick = {
                        val finalName = if (name.isBlank()) "Freelancer" else name.trim()
                        onCompleteSetup(finalName, selectedHour, selectedMinute, dailyTarget, focusDuration)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("complete_setup_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FocusDarkGreen,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Begin Workday",
                        style = MaterialTheme.typography.labelLarge,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
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
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) FocusDarkGreen else FocusBorder,
                shape = RoundedCornerShape(8.dp)
            )
            .background(if (isSelected) FocusGreenContainer else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) FocusDarkGreen else FocusTextPrimary
        )
    }
}
