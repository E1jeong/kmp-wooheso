package com.sumas.wooheso.features.auth

import androidx.compose.runtime.Composable
import com.sumas.wooheso.core.supabase.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken

data class GoogleIdToken(val value: String, val nonce: String)

@Composable
expect fun rememberGoogleIdTokenRequester(): suspend (String) -> GoogleIdToken

suspend fun signInWithGoogle(token: GoogleIdToken) {
    SupabaseClientProvider.client.auth.signInWith(IDToken) {
        provider = Google
        idToken = token.value
        nonce = token.nonce
    }
}
