package com.sumas.wooheso.features.auth

import androidx.compose.runtime.Composable

@Composable
actual fun rememberGoogleIdTokenRequester(): suspend (String) -> GoogleIdToken = {
    error("Google 로그인은 현재 Android에서만 지원합니다.")
}
