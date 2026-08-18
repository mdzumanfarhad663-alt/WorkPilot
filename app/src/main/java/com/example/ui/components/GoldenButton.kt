package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldenOrangeEnd
import com.example.ui.theme.GoldenOrangeStart
import com.example.ui.theme.WarmBrownMuted
import com.example.ui.theme.WarmPillBg

@Composable
fun GoldenGradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    height: Dp = 50.dp,
    fontSize: TextUnit = 15.sp,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null
) {
    val gradientBrush = if (enabled) {
        Brush.horizontalGradient(
            colors = listOf(GoldenOrangeStart, GoldenOrangeEnd)
        )
    } else {
        Brush.horizontalGradient(
            colors = listOf(WarmPillBg, WarmPillBg)
        )
    }

    val textColor = if (enabled) Color.White else WarmBrownMuted

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .shadow(
                elevation = if (enabled) 2.dp else 0.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = GoldenOrangeStart.copy(alpha = 0.35f)
            )
            .clip(RoundedCornerShape(12.dp))
            .background(gradientBrush)
            .clickable(
                enabled = enabled,
                role = Role.Button,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            leadingIcon?.let { icon ->
                androidx.compose.material3.Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }

            Text(
                text = text,
                color = textColor,
                style = MaterialTheme.typography.labelLarge,
                fontSize = fontSize,
                fontWeight = FontWeight.Bold
            )

            trailingIcon?.let { icon ->
                Spacer(modifier = Modifier.width(8.dp))
                androidx.compose.material3.Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = textColor
                )
            }
        }
    }
}
