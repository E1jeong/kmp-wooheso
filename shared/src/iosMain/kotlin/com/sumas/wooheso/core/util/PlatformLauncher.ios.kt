package com.sumas.wooheso.core.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSURL
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

@Composable
actual fun rememberUrlLauncher(): (String) -> Boolean {
    return remember {
        { urlString ->
            try {
                val nsUrl = NSURL.URLWithString(urlString)
                if (nsUrl != null && UIApplication.sharedApplication.canOpenURL(nsUrl)) {
                    UIApplication.sharedApplication.openURL(nsUrl)
                    true
                } else {
                    false
                }
            } catch (e: Exception) {
                false
            }
        }
    }
}

@Composable
actual fun rememberShareLauncher(): (String, String?) -> Unit {
    return remember {
        { text, _ ->
            try {
                val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
                val activityController = UIActivityViewController(
                    activityItems = listOf(text),
                    applicationActivities = null
                )
                rootViewController?.presentViewController(
                    activityController,
                    animated = true,
                    completion = null
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
