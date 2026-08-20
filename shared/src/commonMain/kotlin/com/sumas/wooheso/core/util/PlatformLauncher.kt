package com.sumas.wooheso.core.util

import androidx.compose.runtime.Composable

/**
 * Multiplatform URL launcher bridge.
 * Returns a function (url: String) -> Boolean that opens external URLs (Kakao Channel, website, etc.)
 */
@Composable
expect fun rememberUrlLauncher(): (String) -> Boolean

/**
 * Multiplatform Share launcher bridge.
 * Returns a function (text: String, title: String?) -> Unit that triggers the system share sheet.
 */
@Composable
expect fun rememberShareLauncher(): (String, String?) -> Unit
