package com.sumas.wooheso.core.media

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sumas.wooheso.core.designsystem.AppColors

@Composable
actual fun VideoPlayer(
    url: String,
    isPlaying: Boolean,
    modifier: Modifier,
    isMuted: Boolean,
    onPlaybackReady: () -> Unit
) {
    LaunchedEffect(url) {
        onPlaybackReady()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PlayCircleOutline,
                contentDescription = "Desktop Video Simulation",
                tint = if (isPlaying) AppColors.ElectricCyan else AppColors.TextSecondary,
                modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isPlaying) "Playing Video..." else "Paused",
                color = if (isPlaying) AppColors.ElectricCyan else AppColors.TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
