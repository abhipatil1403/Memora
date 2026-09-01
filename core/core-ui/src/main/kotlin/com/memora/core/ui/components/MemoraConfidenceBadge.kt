package com.memora.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.memora.core.common.constant.ConfidenceThresholds
import com.memora.core.ui.theme.MemoraTheme
import kotlin.math.roundToInt

@Composable
fun MemoraConfidenceBadge(
    confidence: Double,
    modifier: Modifier = Modifier
) {
    val (color, icon) = when {
        confidence >= ConfidenceThresholds.HIGH -> MemoraTheme.colors.confidenceHigh to Icons.Outlined.Check
        confidence >= ConfidenceThresholds.MEDIUM -> MemoraTheme.colors.confidenceMedium to Icons.Outlined.Warning
        else -> MemoraTheme.colors.confidenceLow to Icons.Outlined.HelpOutline
    }

    val percentage = (confidence * 100).roundToInt()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(color = color.copy(alpha = 0.1f), shape = MemoraTheme.shapes.small)
            .padding(horizontal = MemoraTheme.spacing.space2, vertical = MemoraTheme.spacing.space1)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(MemoraTheme.spacing.space1))
        Text(
            text = "$percentage%",
            style = MemoraTheme.typography.caption,
            color = color
        )
    }
}
