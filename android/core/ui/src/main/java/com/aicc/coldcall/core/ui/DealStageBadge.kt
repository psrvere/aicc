package com.aicc.coldcall.core.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aicc.coldcall.core.model.DealStage

@Composable
fun DealStageBadge(stage: DealStage, modifier: Modifier = Modifier) {
    val color = DealStageColors.forStage(stage)
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = color.copy(alpha = 0.2f),
        contentColor = color,
    ) {
        Text(
            text = stage.name,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}
