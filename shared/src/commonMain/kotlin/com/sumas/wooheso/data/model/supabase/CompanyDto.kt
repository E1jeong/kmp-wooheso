package com.sumas.wooheso.data.model.supabase

import com.sumas.wooheso.data.model.CompanyModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CompanyDto(
    @SerialName("id") val id: String? = null,
    @SerialName("owner_id") val ownerId: String,
    @SerialName("name") val name: String,
    @SerialName("tagline") val tagline: String,
    @SerialName("category") val category: String,
    @SerialName("description") val description: String = "",
    @SerialName("logo_url") val logoUrl: String? = null,
    @SerialName("banner_url") val bannerUrl: String? = null,
    @SerialName("website_url") val websiteUrl: String? = null,
    @SerialName("kakao_channel_url") val kakaoChannelUrl: String? = null,
    @SerialName("is_verified") val isVerified: Boolean = false,
    @SerialName("created_at") val createdAt: String? = null
) {
    fun toDomain(): CompanyModel {
        return CompanyModel(
            companyId = id ?: "",
            ownerUid = ownerId,
            name = name,
            tagline = tagline,
            category = category,
            logoUrl = logoUrl,
            description = description,
            websiteUrl = websiteUrl,
            snsLinks = if (!kakaoChannelUrl.isNullOrBlank()) mapOf("kakao" to kakaoChannelUrl) else emptyMap()
        )
    }

    companion object {
        fun fromDomain(model: CompanyModel): CompanyDto {
            return CompanyDto(
                id = model.companyId.ifBlank { null },
                ownerId = model.ownerUid,
                name = model.name,
                tagline = model.tagline,
                category = model.category,
                description = model.description ?: "",
                logoUrl = model.logoUrl,
                websiteUrl = model.websiteUrl,
                kakaoChannelUrl = model.snsLinks["kakao"]
            )
        }
    }
}
