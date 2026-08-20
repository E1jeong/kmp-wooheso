package com.sumas.wooheso.core.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AppColors.Accent,
    onPrimary = Color.White,
    primaryContainer = AppColors.PrimaryLight,
    onPrimaryContainer = Color.White,
    secondary = AppColors.AccentHover,
    onSecondary = Color.White,
    background = AppColors.DarkBackground,
    onBackground = Color.White,
    surface = AppColors.Primary,
    onSurface = Color.White,
    error = AppColors.Error,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = AppColors.Primary,
    onPrimary = Color.White,
    primaryContainer = AppColors.Background,
    onPrimaryContainer = AppColors.TextPrimary,
    secondary = AppColors.Accent,
    onSecondary = Color.White,
    background = AppColors.Background,
    onBackground = AppColors.TextPrimary,
    surface = AppColors.Surface,
    onSurface = AppColors.TextPrimary,
    error = AppColors.Error,
    onError = Color.White
)

@Composable
fun WoohesoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
