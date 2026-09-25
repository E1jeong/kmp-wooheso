package com.sumas.wooheso.data.repository

import com.sumas.wooheso.core.supabase.SupabaseClientProvider
import com.sumas.wooheso.data.model.supabase.SavedProductDto
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

object SavedProductRepository {
    private val mutex = Mutex()
    private val _savedProductIds = MutableStateFlow<Set<String>>(emptySet())
    val savedProductIds: StateFlow<Set<String>> = _savedProductIds.asStateFlow()

    fun isSaved(productId: String): Boolean {
        return _savedProductIds.value.contains(productId)
    }

    suspend fun loadSaved() = mutex.withLock {
        _savedProductIds.value = emptySet()
        val userId = SupabaseClientProvider.client.auth.currentUserOrNull()?.id
        if (userId == null) {
            return@withLock
        }
        val rows = SupabaseClientProvider.client.from("saved_products").select {
            filter { eq("user_id", userId) }
        }.decodeList<SavedProductDto>()
        if (SupabaseClientProvider.client.auth.currentUserOrNull()?.id == userId) {
            _savedProductIds.value = rows.mapTo(mutableSetOf()) { it.productId }
        }
    }

    suspend fun toggleSavePersisted(productId: String): Boolean = mutex.withLock {
        val userId = SupabaseClientProvider.client.auth.currentUserOrNull()?.id
            ?: error("저장하려면 로그인해 주세요.")
        val saved = _savedProductIds.value.contains(productId)
        if (saved) {
            SupabaseClientProvider.client.from("saved_products").delete {
                filter {
                    eq("user_id", userId)
                    eq("product_id", productId)
                }
            }
        } else {
            SupabaseClientProvider.client.from("saved_products").insert(
                SavedProductDto(userId = userId, productId = productId)
            ) { defaultToNull = false }
        }
        if (SupabaseClientProvider.client.auth.currentUserOrNull()?.id != userId) {
            _savedProductIds.value = emptySet()
            error("계정이 변경되었습니다. 다시 시도해 주세요.")
        }
        toggleSave(productId)
    }

    fun toggleSave(productId: String): Boolean {
        val current = _savedProductIds.value
        val newState = if (current.contains(productId)) {
            _savedProductIds.value = current - productId
            false
        } else {
            _savedProductIds.value = current + productId
            true
        }
        return newState
    }
}
