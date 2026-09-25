package com.sumas.wooheso.data.repository

import com.sumas.wooheso.core.supabase.SupabaseClientProvider
import com.sumas.wooheso.core.supabase.SupabaseConfig
import com.sumas.wooheso.data.mock.MockFeedData
import com.sumas.wooheso.data.model.ProductCardModel
import com.sumas.wooheso.data.model.supabase.ProductDto
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SupabaseProductRepository {
    private val client = SupabaseClientProvider.client
    private val _products = MutableStateFlow<List<ProductCardModel>>(emptyList())
    val products: StateFlow<List<ProductCardModel>> = _products.asStateFlow()

    suspend fun fetchProducts(category: String? = null): List<ProductCardModel> {
        if (!SupabaseConfig.isConfigured) {
            val mockList = if (category.isNullOrBlank() || category == "All" || category == "전체") {
                MockFeedData.mockProducts
            } else {
                MockFeedData.mockProducts.filter { it.category.equals(category, ignoreCase = true) }
            }
            _products.value = mockList
            return mockList
        }

        return try {
            val response = if (category.isNullOrBlank() || category == "All" || category == "전체") {
                client.from("products").select().decodeList<ProductDto>()
            } else {
                client.from("products").select {
                    filter {
                        eq("category", category)
                    }
                }.decodeList<ProductDto>()
            }
            val mapped = response.map { dto ->
                dto.toDomain(ShowroomRepositories.companies.getCompanyById(dto.companyId)?.name ?: "")
            }
            _products.value = mapped
            mapped
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getProductById(productId: String): ProductCardModel? {
        if (!SupabaseConfig.isConfigured) {
            return MockFeedData.mockProducts.find { it.id == productId }
        }

        return try {
            val dto = client.from("products").select {
                filter {
                    eq("id", productId)
                }
            }.decodeSingleOrNull<ProductDto>()
            dto?.toDomain(ShowroomRepositories.companies.getCompanyById(dto.companyId)?.name ?: "")
        } catch (e: Exception) {
            println("[SupabaseProductRepository] getProductById error: ${e.message}")
            null
        }
    }

    suspend fun getProductsByCompany(companyId: String): List<ProductCardModel> {
        if (!SupabaseConfig.isConfigured) {
            return MockFeedData.mockProducts.filter { it.companyId == companyId }
        }

        return try {
            val response = client.from("products").select {
                filter {
                    eq("company_id", companyId)
                }
            }.decodeList<ProductDto>()
            response.map { it.toDomain(ShowroomRepositories.companies.getCompanyById(companyId)?.name ?: "") }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun createProduct(product: ProductCardModel): Result<ProductCardModel> {
        return try {
            if (SupabaseConfig.isConfigured) {
                require(product.companyId.isNotBlank()) { "회사를 먼저 등록해 주세요." }
                val dto = ProductDto.fromDomain(product)
                val inserted = client.from("products").insert(dto) {
                    defaultToNull = false
                    select()
                }.decodeSingle<ProductDto>()
                val result = inserted.toDomain(product.companyName)
                _products.value = _products.value + result
                Result.success(result)
            } else {
                _products.value = _products.value + product
                Result.success(product)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun incrementInquiryClick(productId: String): Long {
        if (SupabaseConfig.isConfigured) {
            client.postgrest.rpc("increment_inquiry_click", buildJsonObject { put("p_product_id", productId) })
        }
        return ConversionTracker.trackInquiryClick(productId)
    }
}
