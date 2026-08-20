package com.sumas.wooheso

import androidx.compose.runtime.Composable
import com.sumas.wooheso.core.designsystem.WoohesoTheme
import com.sumas.wooheso.features.feed.presentation.FeedScreen

@Composable
fun App() {
    WoohesoTheme(darkTheme = true) {
        FeedScreen()
    }
}
