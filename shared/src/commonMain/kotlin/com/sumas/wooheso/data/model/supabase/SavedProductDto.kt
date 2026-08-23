package com.sumas.wooheso.data.model.supabase

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SavedProductDto(
    @SerialName("id") val id: String? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("product_id") val productId: String,
    @SerialName("created_at") val createdAt: String? = null
)
