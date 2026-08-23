package com.sumas.wooheso.data.repository

import com.sumas.wooheso.core.supabase.SupabaseClientProvider
import com.sumas.wooheso.core.supabase.SupabaseConfig
import io.github.jan.supabase.storage.storage

class SupabaseStorageRepository {
    private val client = SupabaseClientProvider.client

    suspend fun uploadProductMedia(
        companyId: String,
        productId: String,
        fileName: String,
        bytes: ByteArray
    ): Result<String> {
        if (!SupabaseConfig.isConfigured) {
            // Return placeholder or mock URL
            return Result.success("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
        }

        return try {
            val bucket = client.storage.from("product-media")
            val path = "$companyId/$productId/$fileName"
            bucket.upload(path, bytes) {
                upsert = true
            }
            val publicUrl = bucket.publicUrl(path)
            Result.success(publicUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadCompanyAsset(
        companyId: String,
        fileName: String,
        bytes: ByteArray
    ): Result<String> {
        if (!SupabaseConfig.isConfigured) {
            return Result.success("https://images.unsplash.com/photo-1579546929518-9e396f3cc809")
        }

        return try {
            val bucket = client.storage.from("company-assets")
            val path = "$companyId/$fileName"
            bucket.upload(path, bytes) {
                upsert = true
            }
            val publicUrl = bucket.publicUrl(path)
            Result.success(publicUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
