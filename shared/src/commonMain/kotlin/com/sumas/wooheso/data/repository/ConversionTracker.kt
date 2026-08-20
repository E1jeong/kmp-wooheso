package com.sumas.wooheso.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ConversionTracker {
    private val _inquiryClicks = MutableStateFlow<Map<String, Long>>(emptyMap())
    val inquiryClicks: StateFlow<Map<String, Long>> = _inquiryClicks.asStateFlow()

    fun trackInquiryClick(productId: String): Long {
        val currentClicks = _inquiryClicks.value[productId] ?: 0L
        val updated = currentClicks + 1L
        _inquiryClicks.value = _inquiryClicks.value + (productId to updated)
        println("[ConversionTracker] Product $productId inquiry clicked! Total count: $updated")
        return updated
    }

    fun getInquiryClickCount(productId: String): Long {
        return _inquiryClicks.value[productId] ?: 0L
    }
}
