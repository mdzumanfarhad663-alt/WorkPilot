package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = FocusLightGreen,
    onPrimary = FocusDarkGreen,
    primaryContainer = FocusDarkGreen,
    onPrimaryContainer = FocusLightGreen,
    secondary = FocusLightGreen,
    onSecondary = Color.Black,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkBorder,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorder,
    error = FocusFailure,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = FocusDarkGreen,
    onPrimary = Color.White,
    primaryContainer = FocusGreenContainer,
    onPrimaryContainer = FocusOnGreenContainer,
    secondary = FocusLightGreen,
    onSecondary = FocusDarkGreen,
    background = FocusBackground,
    onBackground = FocusTextPrimary,
    surface = FocusSurface,
    onSurface = FocusTextPrimary,
    surfaceVariant = FocusBackground,
    onSurfaceVariant = FocusTextSecondary,
    outline = FocusBorder,
    error = FocusFailure,
    onError = Color.White
)

@Composable
fun FocusLockTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
