package com.sumas.wooheso.desktop

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.sumas.wooheso.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "우회소 (wooheso) - Showroom Preview",
        state = WindowState(width = 420.dp, height = 860.dp),
        resizable = true
    ) {
        App()
    }
}
