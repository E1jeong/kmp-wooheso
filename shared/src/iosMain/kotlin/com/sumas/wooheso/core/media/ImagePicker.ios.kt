package com.sumas.wooheso.core.media

import androidx.compose.runtime.Composable

@Composable
actual fun rememberImagePicker(): suspend () -> List<SelectedImage> = {
    error("이미지 선택은 현재 Android에서만 지원합니다.")
}

@Composable
actual fun rememberVideoPicker(): suspend () -> SelectedVideo? = {
    error("동영상 선택은 현재 Android에서만 지원합니다.")
}
