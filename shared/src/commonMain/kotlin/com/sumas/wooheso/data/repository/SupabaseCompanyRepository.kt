package com.sumas.wooheso.data.repository

import com.sumas.wooheso.core.supabase.SupabaseClientProvider
import com.sumas.wooheso.core.supabase.SupabaseConfig
import com.sumas.wooheso.data.mock.MockFeedData
import com.sumas.wooheso.data.model.CompanyModel
import com.sumas.wooheso.data.model.supabase.CompanyDto
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SupabaseCompanyRepository {
    private val client = SupabaseClientProvider.client
    private val _companies = MutableStateFlow<Map<String, CompanyModel>>(
        MockFeedData.mockCompanies.associateBy { it.companyId }
    )
    val companies: StateFlow<Map<String, CompanyModel>> = _companies.asStateFlow()

    suspend fun getCompanyById(companyId: String): CompanyModel? {
        val fallback = MockFeedData.mockCompanies.find { it.companyId == companyId }
            ?: MockFeedData.mockCompanies.first()

        if (!SupabaseConfig.isConfigured) {
            return _companies.value[companyId] ?: fallback
        }

        return try {
            val dto = client.from("companies").select {
                filter {
                    eq("id", companyId)
                }
            }.decodeSingleOrNull<CompanyDto>()
            val domain = dto?.toDomain() ?: _companies.value[companyId] ?: fallback
            _companies.value = _companies.value + (domain.companyId to domain)
            domain
        } catch (e: Exception) {
            println("[SupabaseCompanyRepository] getCompanyById error: ${e.message}")
            _companies.value[companyId] ?: fallback
        }
    }

    suspend fun createCompany(company: CompanyModel): Result<CompanyModel> {
        return try {
            if (SupabaseConfig.isConfigured) {
                val dto = CompanyDto.fromDomain(company)
                val inserted = client.from("companies").insert(dto) {
                    select()
                }.decodeSingle<CompanyDto>()
                val result = inserted.toDomain()
                _companies.value = _companies.value + (result.companyId to result)
                Result.success(result)
            } else {
                _companies.value = _companies.value + (company.companyId to company)
                Result.success(company)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
