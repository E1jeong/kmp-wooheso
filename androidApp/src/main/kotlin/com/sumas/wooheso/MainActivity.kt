package com.sumas.wooheso

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            App(googleWebClientId = BuildConfig.GOOGLE_WEB_CLIENT_ID)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
