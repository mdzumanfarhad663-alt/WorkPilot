package com.example.ui.dialogs

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.components.GoldenGradientButton
import com.example.ui.theme.CardSubtleBorder
import com.example.ui.theme.DarkChocolateHeadings
import com.example.ui.theme.GoldenAmberPrimary
import com.example.ui.theme.SoftCreamCard
import com.example.ui.theme.WarmBrownBody
import com.example.ui.theme.WarmBrownMuted
import com.example.ui.theme.WarmBrownSecondary
import com.example.ui.theme.WarmCrimsonFailure
import com.example.ui.theme.WarmOliveSuccess

@Composable
fun BackupDialog(
    onDismiss: () -> Unit,
    onGetExportJson: suspend () -> String,
    onImportBackup: (jsonString: String, onSuccess: () -> Unit, onError: (String) -> Unit) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Export, 1: Import
    var exportJson by remember { mutableStateOf("") }
    var importJson by remember { mutableStateOf("") }
    var copyStatus by remember { mutableStateOf("") }
    var importError by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    LaunchedEffect(selectedTab) {
        if (selectedTab == 0) {
            exportJson = onGetExportJson()
        }
    }

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
                    text = "Backup & Data",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = DarkChocolateHeadings
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Export or restore your WorkPilot settings, tasks, and history.",
                    style = MaterialTheme.typography.bodySmall,
                    color = WarmBrownSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = GoldenAmberPrimary,
                    indicator = { tabPositions ->
                        SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = GoldenAmberPrimary
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                "Export Backup",
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTab == 0) DarkChocolateHeadings else WarmBrownSecondary
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                "Import Backup",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTab == 1) DarkChocolateHeadings else WarmBrownSecondary
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (selectedTab == 0) {
                    // Export
                    Text(
                        text = "JSON Data Backup:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = DarkChocolateHeadings
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = exportJson,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .testTag("export_json_field"),
                        textStyle = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = DarkChocolateHeadings
                        ),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = CardSubtleBorder,
                            focusedBorderColor = GoldenAmberPrimary,
                            unfocusedContainerColor = SoftCreamCard,
                            focusedContainerColor = SoftCreamCard
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (copyStatus.isNotEmpty()) {
                        Text(
                            text = copyStatus,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = WarmOliveSuccess
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

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
                            Text("Close", color = WarmBrownBody, fontWeight = FontWeight.SemiBold)
                        }
                        GoldenGradientButton(
                            text = "Copy JSON",
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("WorkPilot Backup", exportJson)
                                clipboard.setPrimaryClip(clip)
                                copyStatus = "Copied to clipboard!"
                            },
                            height = 48.dp,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("copy_export_button")
                        )
                    }
                } else {
                    // Import
                    Text(
                        text = "Paste Backup JSON:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = DarkChocolateHeadings
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = importJson,
                        onValueChange = {
                            importJson = it
                            importError = null
                        },
                        placeholder = { Text("Paste valid WorkPilot JSON here...", color = WarmBrownMuted) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .testTag("import_json_field"),
                        textStyle = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = DarkChocolateHeadings
                        ),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = CardSubtleBorder,
                            focusedBorderColor = GoldenAmberPrimary,
                            unfocusedContainerColor = SoftCreamCard,
                            focusedContainerColor = SoftCreamCard
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    importError?.let { err ->
                        Text(
                            text = err,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = WarmCrimsonFailure
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

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
                            text = "Restore Data",
                            onClick = {
                                if (importJson.isBlank()) {
                                    importError = "Please paste JSON before restoring."
                                    return@GoldenGradientButton
                                }
                                onImportBackup(
                                    importJson,
                                    { onDismiss() },
                                    { err -> importError = err }
                                )
                            },
                            height = 48.dp,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("restore_backup_button")
                        )
                    }
                }
            }
        }
    }
}
