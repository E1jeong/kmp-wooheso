package com.sumas.wooheso.features.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

@Composable
actual fun rememberGoogleIdTokenRequester(): suspend (String) -> GoogleIdToken {
    val context = LocalContext.current
    return remember(context) {
        { webClientId ->
            require(webClientId.isNotBlank()) { "Google 웹 클라이언트 ID가 설정되지 않았습니다." }
            val random = ByteArray(32).also { SecureRandom().nextBytes(it) }
            val nonce = Base64.getUrlEncoder().withoutPadding().encodeToString(random)
            val hashedNonce = MessageDigest.getInstance("SHA-256")
                .digest(nonce.toByteArray(Charsets.UTF_8))
                .joinToString("") { "%02x".format(it) }
            val option = GetSignInWithGoogleOption.Builder(webClientId)
                .setNonce(hashedNonce)
                .build()
            val credential = CredentialManager.create(context).getCredential(
                context,
                GetCredentialRequest.Builder().addCredentialOption(option).build()
            ).credential
            require(credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                "Google ID 토큰을 받지 못했습니다."
            }
            GoogleIdToken(GoogleIdTokenCredential.createFrom(credential.data).idToken, nonce)
        }
    }
}
