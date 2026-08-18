package com.example.ui.dialogs

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.ui.theme.FocusBorder
import com.example.ui.theme.FocusDarkGreen
import com.example.ui.theme.FocusFailure
import com.example.ui.theme.FocusTextPrimary
import com.example.ui.theme.FocusTextSecondary
import kotlinx.coroutines.launch

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
    val scope = rememberCoroutineScope()

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
                    text = "Backup & Data",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = FocusTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Export or restore your FocusLock settings, tasks, and history.",
                    style = MaterialTheme.typography.bodySmall,
                    color = FocusTextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = FocusDarkGreen
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Export Backup") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Import Backup") }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (selectedTab == 0) {
                    // Export
                    Text(
                        text = "JSON Data Backup:",
                        style = MaterialTheme.typography.labelMedium,
                        color = FocusTextPrimary
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
                        textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontSize = 11.sp),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = FocusBorder,
                            focusedBorderColor = FocusDarkGreen
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (copyStatus.isNotEmpty()) {
                        Text(
                            text = copyStatus,
                            style = MaterialTheme.typography.labelSmall,
                            color = FocusDarkGreen
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Close")
                        }
                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("FocusLock Backup", exportJson)
                                clipboard.setPrimaryClip(clip)
                                copyStatus = "Copied to clipboard!"
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("copy_export_button"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = FocusDarkGreen,
                                contentColor = Color.White
                            )
                        ) {
                            Text("Copy JSON")
                        }
                    }
                } else {
                    // Import
                    Text(
                        text = "Paste Backup JSON:",
                        style = MaterialTheme.typography.labelMedium,
                        color = FocusTextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = importJson,
                        onValueChange = {
                            importJson = it
                            importError = null
                        },
                        placeholder = { Text("Paste valid FocusLock JSON here...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .testTag("import_json_field"),
                        textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontSize = 11.sp),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = FocusBorder,
                            focusedBorderColor = FocusDarkGreen
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    importError?.let { err ->
                        Text(
                            text = err,
                            style = MaterialTheme.typography.labelSmall,
                            color = FocusFailure
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

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
                                if (importJson.isBlank()) {
                                    importError = "Please paste JSON before restoring."
                                    return@Button
                                }
                                onImportBackup(
                                    importJson,
                                    { onDismiss() },
                                    { err -> importError = err }
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("restore_backup_button"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = FocusDarkGreen,
                                contentColor = Color.White
                            )
                        ) {
                            Text("Restore Data")
                        }
                    }
                }
            }
        }
    }
}
