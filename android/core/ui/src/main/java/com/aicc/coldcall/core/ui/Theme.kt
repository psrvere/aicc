package com.aicc.coldcall.core.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.aicc.coldcall.core.model.DealStage

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF90CAF9),
    secondary = Color(0xFFCE93D8),
    tertiary = Color(0xFF80CBC4),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    error = Color(0xFFCF6679),
    onError = Color.Black,
)

@Composable
fun ColdCallTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}

object DealStageColors {
    fun forStage(stage: DealStage): Color = when (stage) {
        DealStage.New -> Color(0xFF90CAF9)
        DealStage.Contacted -> Color(0xFFFFCC80)
        DealStage.Qualified -> Color(0xFFA5D6A7)
        DealStage.Proposal -> Color(0xFFCE93D8)
        DealStage.Negotiation -> Color(0xFFFFAB91)
        DealStage.Won -> Color(0xFF4CAF50)
        DealStage.Lost -> Color(0xFFEF5350)
        DealStage.NotInterested -> Color(0xFF9E9E9E)
    }
}
