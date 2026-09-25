package com.sumas.wooheso.core.media

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CompletableDeferred
import java.io.ByteArrayOutputStream
import android.media.MediaMetadataRetriever

@Composable
actual fun rememberImagePicker(): suspend () -> List<SelectedImage> {
    val context = LocalContext.current
    var pending by remember { mutableStateOf<CompletableDeferred<List<SelectedImage>>?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        try {
            require(uris.size <= 5) { "이미지는 최대 5개까지 선택할 수 있습니다." }
            val images = uris.mapIndexed { index, uri ->
                val mime = context.contentResolver.getType(uri)
                require(mime == "image/jpeg" || mime == "image/png" || mime == "image/webp") {
                    "JPG, PNG 또는 WebP 이미지만 선택할 수 있습니다."
                }
                val extension = when (mime) {
                    "image/png" -> "png"
                    "image/webp" -> "webp"
                    else -> "jpg"
                }
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val output = ByteArrayOutputStream()
                    val buffer = ByteArray(8192)
                    while (true) {
                        val read = stream.read(buffer)
                        if (read < 0) break
                        require(output.size() + read <= 10 * 1024 * 1024) { "이미지는 각 10MB 이하여야 합니다." }
                        output.write(buffer, 0, read)
                    }
                    SelectedImage("image-$index.$extension", output.toByteArray())
                } ?: error("이미지를 읽지 못했습니다.")
            }
            pending?.complete(images)
        } catch (e: Exception) {
            pending?.completeExceptionally(e)
        } finally {
            pending = null
        }
    }
    return remember(launcher) {
        suspend {
            val result = CompletableDeferred<List<SelectedImage>>()
            pending = result
            launcher.launch("image/*")
            result.await()
        }
    }
}

@Composable
actual fun rememberVideoPicker(): suspend () -> SelectedVideo? {
    val context = LocalContext.current
    var pending by remember { mutableStateOf<CompletableDeferred<SelectedVideo?>?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        try {
            val video = if (uri == null) null else {
                require(context.contentResolver.getType(uri) == "video/mp4") { "MP4 동영상을 선택해 주세요." }
                val retriever = MediaMetadataRetriever()
                val durationMs = try {
                    retriever.setDataSource(context, uri)
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull()
                } finally {
                    retriever.release()
                }
                require(durationMs != null && durationMs <= 30_000L) { "30초 이하 동영상만 업로드할 수 있습니다." }
                val bytes = context.contentResolver.openInputStream(uri)?.use { stream ->
                    val output = ByteArrayOutputStream()
                    val buffer = ByteArray(8192)
                    while (true) {
                        val read = stream.read(buffer)
                        if (read < 0) break
                        require(output.size() + read <= 30 * 1024 * 1024) { "30MB 이하 동영상만 업로드할 수 있습니다." }
                        output.write(buffer, 0, read)
                    }
                    output.toByteArray()
                } ?: error("동영상을 읽지 못했습니다.")
                SelectedVideo("video.mp4", bytes)
            }
            pending?.complete(video)
        } catch (e: Exception) {
            pending?.completeExceptionally(e)
        } finally {
            pending = null
        }
    }
    return remember(launcher) {
        suspend {
            val result = CompletableDeferred<SelectedVideo?>()
            pending = result
            launcher.launch("video/mp4")
            result.await()
        }
    }
}
