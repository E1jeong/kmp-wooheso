package com.sumas.wooheso.core.util

import androidx.compose.runtime.Composable
import java.awt.Desktop
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.net.URI

@Composable
actual fun rememberUrlLauncher(): (String) -> Boolean {
    return { url ->
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(URI(url))
                true
            } else {
                false
            }
        } catch (e: Exception) {
            println("[Desktop] Failed to open URL: ${e.message}")
            false
        }
    }
}

@Composable
actual fun rememberShareLauncher(): (String, String?) -> Unit {
    return { text, _ ->
        try {
            val selection = StringSelection(text)
            Toolkit.getDefaultToolkit().systemClipboard.setContents(selection, selection)
            println("[Desktop] Shared link copied to clipboard: $text")
        } catch (e: Exception) {
            println("[Desktop] Share failed: ${e.message}")
        }
    }
}
