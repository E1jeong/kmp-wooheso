package com.sumas.wooheso.navigation

import kotlinx.serialization.Serializable

sealed interface WoohesoRoute {
    @Serializable
    data object Feed : WoohesoRoute

    @Serializable
    data class ProductDetail(val productId: String) : WoohesoRoute

    @Serializable
    data class CompanyProfile(val companyId: String) : WoohesoRoute

    @Serializable
    data object SavedList : WoohesoRoute

    @Serializable
    data object CompanyRegistration : WoohesoRoute

    @Serializable
    data class ProductRegistration(val companyId: String = "") : WoohesoRoute
}
