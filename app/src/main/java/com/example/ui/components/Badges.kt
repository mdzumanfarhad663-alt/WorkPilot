package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskType
import com.example.ui.theme.BrownIconBorder
import com.example.ui.theme.CardSubtleBorder
import com.example.ui.theme.DarkChocolateHeadings
import com.example.ui.theme.GoldenAmberPrimary
import com.example.ui.theme.SoftCreamCard
import com.example.ui.theme.WarmAmberWarning
import com.example.ui.theme.WarmBrownBody
import com.example.ui.theme.WarmBrownSecondary
import com.example.ui.theme.WarmCrimsonFailure
import com.example.ui.theme.WarmFailureBg
import com.example.ui.theme.WarmOliveSuccess
import com.example.ui.theme.WarmPillBg
import com.example.ui.theme.WarmSuccessBg
import com.example.ui.theme.WarmWarningBg

@Composable
fun TaskNumberBadge(
    taskType: TaskType,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(WarmPillBg)
            .border(1.dp, CardSubtleBorder, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = taskType.displayName.uppercase(),
            color = DarkChocolateHeadings,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun RewardPointsBadge(
    points: Int,
    modifier: Modifier = Modifier,
    isLarge: Boolean = false
) {
    val isPositive = points >= 0
    val (bgColor, textColor, borderColor) = if (isPositive) {
        Triple(WarmPillBg, GoldenAmberPrimary, CardSubtleBorder)
    } else {
        Triple(WarmFailureBg, WarmCrimsonFailure, CardSubtleBorder)
    }

    val displayString = if (points > 0) "+$points pts" else if (points < 0) "$points pts" else "0 pts"

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .padding(horizontal = if (isLarge) 12.dp else 9.dp, vertical = if (isLarge) 6.dp else 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "🏆",
                fontSize = if (isLarge) 14.sp else 12.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = displayString,
                color = textColor,
                style = if (isLarge) MaterialTheme.typography.labelLarge else MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun TaskDurationChip(
    durationMinutes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(SoftCreamCard)
            .border(1.dp, CardSubtleBorder, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Timer,
                contentDescription = "Set duration",
                tint = BrownIconBorder,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = "$durationMinutes min",
                color = WarmBrownBody,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun ScoreBadge(
    score: Int,
    ratingLabel: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when {
        score >= 4 -> WarmSuccessBg to WarmOliveSuccess
        score == 3 -> WarmWarningBg to WarmAmberWarning
        else -> WarmFailureBg to WarmCrimsonFailure
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(1.dp, CardSubtleBorder, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = "$score/5 • $ratingLabel",
            color = textColor,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}
