package com.sumas.wooheso.data.repository

import com.sumas.wooheso.core.supabase.SupabaseClientProvider
import com.sumas.wooheso.core.supabase.SupabaseConfig
import com.sumas.wooheso.data.mock.MockFeedData
import com.sumas.wooheso.data.model.ProductCardModel
import com.sumas.wooheso.data.model.supabase.ProductDto
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SupabaseProductRepository {
    private val client = SupabaseClientProvider.client
    private val _products = MutableStateFlow<List<ProductCardModel>>(MockFeedData.mockProducts)
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
            val mapped = response.map { it.toDomain() }
            _products.value = mapped
            mapped
        } catch (e: Exception) {
            println("[SupabaseProductRepository] fetchProducts error: ${e.message}, fallback to mock")
            MockFeedData.mockProducts
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
            dto?.toDomain() ?: MockFeedData.mockProducts.find { it.id == productId }
        } catch (e: Exception) {
            println("[SupabaseProductRepository] getProductById error: ${e.message}")
            MockFeedData.mockProducts.find { it.id == productId }
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
            response.map { it.toDomain() }
        } catch (e: Exception) {
            MockFeedData.mockProducts.filter { it.companyId == companyId }
        }
    }

    suspend fun createProduct(product: ProductCardModel): Result<ProductCardModel> {
        return try {
            if (SupabaseConfig.isConfigured) {
                val dto = ProductDto.fromDomain(product)
                val inserted = client.from("products").insert(dto) {
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
        val currentCount = ConversionTracker.trackInquiryClick(productId)
        if (SupabaseConfig.isConfigured) {
            try {
                // Postgrest increment
                client.from("products").update({
                    set("inquiry_click_count", currentCount)
                }) {
                    filter {
                        eq("id", productId)
                    }
                }
            } catch (e: Exception) {
                println("[SupabaseProductRepository] incrementInquiryClick remote update error: ${e.message}")
            }
        }
        return currentCount
    }
}
