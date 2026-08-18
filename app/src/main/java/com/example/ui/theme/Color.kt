package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Warm Ivory & Golden-Amber Color System
val WarmIvoryBg = Color(0xFFF8F1E3)          // Canvas Background #F8F1E3
val SoftCreamCard = Color(0xFFFFF9ED)        // Cards #FFF9ED
val CardSubtleBorder = Color(0xFFEBDDC6)     // Soft delicate card border

// Typography Colors
val DarkChocolateHeadings = Color(0xFF5A2A08)// Headings #5A2A08
val WarmBrownBody = Color(0xFF704018)        // Body text #704018
val WarmBrownSecondary = Color(0xFF8E5832)   // Secondary / Subtitle text
val WarmBrownMuted = Color(0xFFA87E5E)       // Placeholder / Hint text
val BrownIconBorder = Color(0xFF6B3816)      // Line icons & primary outlines #6B3816

// Golden-Orange Primary Action Gradient
val GoldenOrangeStart = Color(0xFFD96600)    // #D96600
val GoldenOrangeEnd = Color(0xFFE9A12A)      // #E9A12A
val GoldenAmberPrimary = Color(0xFFD96600)

// Progress Bar Colors
val ProgressTrackPaleGold = Color(0xFFF4D66D)// Pale Gold track #F4D66D
val ProgressFillDeepAmber = Color(0xFFA96B00)// Deep Amber fill #A96B00

// Functional Status Colors
val WarmPillBg = Color(0xFFF4E8D3)           // Warm badge container
val WarmOliveSuccess = Color(0xFF2D5A27)     // Success text / icons
val WarmSuccessBg = Color(0xFFEAF5E7)        // Success soft container
val WarmAmberWarning = Color(0xFFA96B00)     // Warning text
val WarmWarningBg = Color(0xFFFDF3DF)        // Warning container
val WarmCrimsonFailure = Color(0xFFA82A2A)   // Failure text
val WarmFailureBg = Color(0xFFFCEAEA)        // Failure container

// Golden Gradient Brush Helper
val PrimaryGoldenGradient = Brush.horizontalGradient(
    colors = listOf(GoldenOrangeStart, GoldenOrangeEnd)
)

val PrimaryVerticalGradient = Brush.verticalGradient(
    colors = listOf(GoldenOrangeStart, GoldenOrangeEnd)
)

// Legacy Aliases mapped to the new warm ivory palette
val PilotDarkGreen = GoldenAmberPrimary
val PilotLightGreen = ProgressTrackPaleGold
val PilotGreenContainer = WarmPillBg
val PilotOnGreenContainer = DarkChocolateHeadings

val PilotBackground = WarmIvoryBg
val PilotSurface = SoftCreamCard
val PilotBorder = CardSubtleBorder
val PilotTextPrimary = DarkChocolateHeadings
val PilotTextBody = WarmBrownBody
val PilotTextSecondary = WarmBrownSecondary
val PilotTextMuted = WarmBrownMuted

val PilotSuccess = WarmOliveSuccess
val PilotSuccessBg = WarmSuccessBg
val PilotWarning = WarmAmberWarning
val PilotWarningBg = WarmWarningBg
val PilotFailure = WarmCrimsonFailure
val PilotFailureBg = WarmFailureBg

val CategoryMoneyBg = WarmSuccessBg
val CategoryMoneyText = WarmOliveSuccess
val CategoryGrowthBg = WarmWarningBg
val CategoryGrowthText = WarmAmberWarning
val CategoryMaintenanceBg = WarmPillBg
val CategoryMaintenanceText = WarmBrownSecondary

val FocusDarkGreen = PilotDarkGreen
val FocusLightGreen = PilotLightGreen
val FocusGreenContainer = PilotGreenContainer
val FocusOnGreenContainer = PilotOnGreenContainer
val FocusBackground = PilotBackground
val FocusSurface = PilotSurface
val FocusBorder = PilotBorder
val FocusTextPrimary = PilotTextPrimary
val FocusTextSecondary = PilotTextSecondary
val FocusTextMuted = PilotTextMuted
val FocusWarning = PilotWarning
val FocusWarningBg = PilotWarningBg
val FocusFailure = PilotFailure
val FocusFailureBg = PilotFailureBg
val FocusSuccess = PilotSuccess
