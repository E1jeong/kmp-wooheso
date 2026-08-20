package com.sumas.wooheso.data.model

data class CompanyModel(
    val companyId: String,
    val ownerUid: String,
    val name: String,
    val tagline: String,
    val category: String,
    val logoUrl: String? = null,
    val description: String? = null,
    val location: String? = null,
    val websiteUrl: String? = null,
    val snsLinks: Map<String, String> = emptyMap(),
    val createdAt: Long = 0
)
