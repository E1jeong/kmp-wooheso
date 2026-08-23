package com.sumas.wooheso.data.model.supabase

import com.sumas.wooheso.data.model.PriceType
import com.sumas.wooheso.data.model.ProductCardModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
    @SerialName("id") val id: String? = null,
    @SerialName("company_id") val companyId: String,
    @SerialName("title") val title: String,
    @SerialName("short_description") val shortDescription: String,
    @SerialName("full_description") val fullDescription: String = "",
    @SerialName("price_type") val priceType: String = "inquiry",
    @SerialName("price") val price: Long? = null,
    @SerialName("highlight_features") val highlightFeatures: List<String> = emptyList(),
    @SerialName("image_urls") val imageUrls: List<String> = emptyList(),
    @SerialName("video_url") val videoUrl: String? = null,
    @SerialName("thumbnail_url") val thumbnailUrl: String? = null,
    @SerialName("inquiry_url") val inquiryUrl: String = "",
    @SerialName("inquiry_click_count") val inquiryClickCount: Long = 0,
    @SerialName("category") val category: String = "All",
    @SerialName("created_at") val createdAt: String? = null
) {
    fun toDomain(companyName: String = ""): ProductCardModel {
        return ProductCardModel(
            id = id ?: "",
            companyId = companyId,
            companyName = companyName,
            title = title,
            description = shortDescription,
            imageUrls = imageUrls,
            videoUrl = videoUrl,
            category = category,
            keyFeatures = highlightFeatures,
            priceType = if (priceType.lowercase() == "fixed") PriceType.FIXED else PriceType.INQUIRY,
            price = price,
            inquiryUrl = inquiryUrl,
            inquiryClickCount = inquiryClickCount
        )
    }

    companion object {
        fun fromDomain(model: ProductCardModel): ProductDto {
            return ProductDto(
                id = model.id.ifBlank { null },
                companyId = model.companyId,
                title = model.title,
                shortDescription = model.description,
                fullDescription = model.description,
                priceType = if (model.priceType == PriceType.FIXED) "fixed" else "inquiry",
                price = model.price,
                highlightFeatures = model.keyFeatures,
                imageUrls = model.imageUrls,
                videoUrl = model.videoUrl,
                inquiryUrl = model.inquiryUrl ?: "",
                inquiryClickCount = model.inquiryClickCount,
                category = model.category
            )
        }
    }
}
