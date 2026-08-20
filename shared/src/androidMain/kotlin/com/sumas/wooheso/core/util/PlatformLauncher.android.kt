package com.sumas.wooheso.core.util

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberUrlLauncher(): (String) -> Boolean {
    val context = LocalContext.current
    return remember(context) {
        { url ->
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}

@Composable
actual fun rememberShareLauncher(): (String, String?) -> Unit {
    val context = LocalContext.current
    return remember(context) {
        { text, title ->
            try {
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, text)
                    if (title != null) {
                        putExtra(Intent.EXTRA_TITLE, title)
                    }
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, title).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(shareIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
