package com.sumas.wooheso.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object SavedProductRepository {
    private val _savedProductIds = MutableStateFlow<Set<String>>(emptySet())
    val savedProductIds: StateFlow<Set<String>> = _savedProductIds.asStateFlow()

    fun isSaved(productId: String): Boolean {
        return _savedProductIds.value.contains(productId)
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
