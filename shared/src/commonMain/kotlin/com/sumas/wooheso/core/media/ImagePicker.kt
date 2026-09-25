package com.sumas.wooheso.core.media

import androidx.compose.runtime.Composable

data class SelectedImage(val fileName: String, val bytes: ByteArray)
data class SelectedVideo(val fileName: String, val bytes: ByteArray)

@Composable
expect fun rememberImagePicker(): suspend () -> List<SelectedImage>

@Composable
expect fun rememberVideoPicker(): suspend () -> SelectedVideo?
