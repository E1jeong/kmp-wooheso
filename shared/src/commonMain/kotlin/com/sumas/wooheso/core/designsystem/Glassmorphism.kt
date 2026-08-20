package com.sumas.wooheso.core.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Reusable Frosted Glassmorphism background modifier with fallback for cross-platform CMP.
 */
fun Modifier.frostedGlass(
    shape: Shape = RoundedCornerShape(24.dp),
    backgroundColor: Color = AppColors.GlassBg,
    borderColor: Color = AppColors.BorderGlass,
    borderWidth: Dp = 1.dp
): Modifier = this
    .clip(shape)
    .background(backgroundColor)
    .border(borderWidth, borderColor, shape)

/**
 * Top Protection Gradient overlay for readable headers over bright/white video backgrounds.
 */
fun Modifier.topProtectionGradient(): Modifier = this.background(
    brush = Brush.verticalGradient(
        colors = listOf(
            Color.Black.copy(alpha = 0.75f),
            Color.Black.copy(alpha = 0.35f),
            Color.Transparent
        )
    )
)

/**
 * Bottom Protection Gradient overlay for metadata and action pills.
 */
fun Modifier.bottomProtectionGradient(): Modifier = this.background(
    brush = Brush.verticalGradient(
        colors = listOf(
            Color.Transparent,
            Color.Black.copy(alpha = 0.40f),
            Color.Black.copy(alpha = 0.85f)
        )
    )
)
