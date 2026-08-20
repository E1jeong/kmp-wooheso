package com.sumas.wooheso.core.designsystem

import androidx.compose.ui.graphics.Color

object AppColors {
    // Primary brand colors (Showroom exhibition aesthetic)
    val Primary = Color(0xFF1E293B)      // Slate 800
    val PrimaryLight = Color(0xFF334155) // Slate 700
    val PrimaryDark = Color(0xFF0F172A)  // Slate 900
    val Accent = Color(0xFF6366F1)       // Indigo 500
    val AccentHover = Color(0xFF4F46E5)  // Indigo 600
    val AccentGradientStart = Color(0xFF6366F1)
    val AccentGradientEnd = Color(0xFF6C5CE7)

    // Neutral / Background
    val Background = Color(0xFFF8FAFC)   // Slate 50
    val Surface = Color(0xFFFFFFFF)      // White
    val CardBg = Color(0xFFFFFFFF)
    val DarkBackground = Color(0xFF0A0F1D) // Dark Feed Background

    // Text colors
    val TextPrimary = Color(0xFF0F172A)   // Slate 900
    val TextSecondary = Color(0xFF64748B) // Slate 500
    val TextMuted = Color(0xFF94A3B8)     // Slate 400
    val TextWhite = Color(0xFFFFFFFF)
    val TextWhiteDim = Color(0xB3FFFFFF)  // 70% White

    // Functional / Status
    val Success = Color(0xFF10B981)      // Emerald 500
    val Warning = Color(0xFFF59E0B)      // Amber 500
    val Error = Color(0xFFEF4444)        // Red 500
    val Border = Color(0xFFE2E8F0)       // Slate 200
    val BorderGlass = Color(0x33FFFFFF)  // 20% White border for glassmorphism
    val GlassBg = Color(0x4D000000)      // 30% Black glass
}
