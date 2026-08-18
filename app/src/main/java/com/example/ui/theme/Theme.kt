package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val WorkPilotColorScheme = lightColorScheme(
    primary = PilotDarkGreen,
    onPrimary = Color.White,
    primaryContainer = PilotGreenContainer,
    onPrimaryContainer = PilotOnGreenContainer,
    secondary = PilotDarkGreen,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF1F5F9),
    onSecondaryContainer = PilotTextPrimary,
    background = PilotBackground,
    onBackground = PilotTextBody,
    surface = PilotSurface,
    onSurface = PilotTextPrimary,
    surfaceVariant = Color(0xFFF8FAFC),
    onSurfaceVariant = PilotTextSecondary,
    outline = PilotBorder,
    outlineVariant = Color(0xFFF1F5F9),
    error = PilotFailure,
    onError = Color.White,
    errorContainer = PilotFailureBg,
    onErrorContainer = PilotFailure
)

@Composable
fun WorkPilotTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = WorkPilotColorScheme,
        typography = Typography,
        content = content
    )
}

// Backwards compatibility alias
@Composable
fun FocusLockTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    WorkPilotTheme(content = content)
}
