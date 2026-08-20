package com.sumas.wooheso.data.model

data class UserModel(
    val uid: String,
    val name: String,
    val email: String,
    val photoUrl: String? = null,
    val createdAt: Long = 0
)

data class SavedProductModel(
    val productId: String,
    val savedAt: Long = 0
)
