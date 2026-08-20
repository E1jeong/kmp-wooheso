package com.sumas.wooheso.data.model

enum class PriceType {
    FIXED,
    INQUIRY
}

data class ProductCardModel(
    val id: String,
    val companyId: String,
    val companyName: String,
    val title: String,
    val description: String,
    val imageUrls: List<String>,
    val videoUrl: String? = null,
    val category: String,
    val keyFeatures: List<String>,
    val priceType: PriceType = PriceType.INQUIRY,
    val price: Long? = null,
    val inquiryUrl: String? = null,
    val saveCount: Int = 0,
    val inquiryClickCount: Long = 0,
    val createdAt: Long = 0
) {
    val isVideo: Boolean
        get() = !videoUrl.isNullOrBlank()

    val formattedPrice: String
        get() = when (priceType) {
            PriceType.INQUIRY -> "가격 문의 필요"
            PriceType.FIXED -> {
                val formatted = price?.toString()?.reversed()?.chunked(3)?.joinToString(",")?.reversed() ?: "0"
                "₩ $formatted"
            }
        }
}
