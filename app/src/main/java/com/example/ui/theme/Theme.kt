package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val WarmIvoryColorScheme = lightColorScheme(
    primary = GoldenAmberPrimary,
    onPrimary = Color.White,
    primaryContainer = WarmPillBg,
    onPrimaryContainer = DarkChocolateHeadings,
    secondary = GoldenAmberPrimary,
    onSecondary = Color.White,
    secondaryContainer = WarmPillBg,
    onSecondaryContainer = DarkChocolateHeadings,
    background = WarmIvoryBg,
    onBackground = WarmBrownBody,
    surface = SoftCreamCard,
    onSurface = DarkChocolateHeadings,
    surfaceVariant = WarmIvoryBg,
    onSurfaceVariant = WarmBrownSecondary,
    outline = CardSubtleBorder,
    outlineVariant = WarmPillBg,
    error = WarmCrimsonFailure,
    onError = Color.White,
    errorContainer = WarmFailureBg,
    onErrorContainer = WarmCrimsonFailure
)

@Composable
fun WorkPilotTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = WarmIvoryColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun FocusLockTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    WorkPilotTheme(content = content)
}
