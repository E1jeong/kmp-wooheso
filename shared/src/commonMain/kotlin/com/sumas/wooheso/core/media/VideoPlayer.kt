package com.sumas.wooheso.core.media

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Multiplatform video player component.
 * Plays the video from [url] when [isPlaying] is true.
 */
@Composable
expect fun VideoPlayer(
    url: String,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    isMuted: Boolean = false,
    onPlaybackReady: () -> Unit = {}
)
