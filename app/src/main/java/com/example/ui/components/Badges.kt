package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.TaskType
import com.example.ui.theme.CategoryGrowthBg
import com.example.ui.theme.CategoryGrowthText
import com.example.ui.theme.CategoryMaintenanceBg
import com.example.ui.theme.CategoryMaintenanceText
import com.example.ui.theme.CategoryMoneyBg
import com.example.ui.theme.CategoryMoneyText
import com.example.ui.theme.PilotFailure
import com.example.ui.theme.PilotFailureBg
import com.example.ui.theme.PilotSuccess
import com.example.ui.theme.PilotSuccessBg
import com.example.ui.theme.PilotWarning
import com.example.ui.theme.PilotWarningBg

@Composable
fun TaskCategoryBadge(
    taskType: TaskType,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (taskType) {
        TaskType.MONEY -> CategoryMoneyBg to CategoryMoneyText
        TaskType.GROWTH -> CategoryGrowthBg to CategoryGrowthText
        TaskType.MAINTENANCE -> CategoryMaintenanceBg to CategoryMaintenanceText
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = taskType.displayName.uppercase(),
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ScoreBadge(
    score: Int,
    ratingLabel: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when {
        score >= 4 -> PilotSuccessBg to PilotSuccess
        score == 3 -> PilotWarningBg to PilotWarning
        else -> PilotFailureBg to PilotFailure
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
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

