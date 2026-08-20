package com.sumas.wooheso

import com.sumas.wooheso.data.mock.MockFeedData
import com.sumas.wooheso.data.model.PriceType
import com.sumas.wooheso.data.repository.ConversionTracker
import com.sumas.wooheso.data.repository.SavedProductRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SharedCommonTest {

    @Test
    fun testMockFeedDataIntegrity() {
        assertEquals(3, MockFeedData.mockProducts.size)
        assertEquals(3, MockFeedData.mockCompanies.size)

        val prod1 = MockFeedData.mockProducts.first()
        assertEquals("prod-001", prod1.id)
        assertEquals("₩ 3,500,000", prod1.formattedPrice)
        assertEquals(3, prod1.keyFeatures.size)
        assertTrue(prod1.isVideo)

        val prod2 = MockFeedData.mockProducts[1]
        assertEquals("prod-002", prod2.id)
        assertEquals("가격 문의 필요", prod2.formattedPrice)
        assertEquals(PriceType.INQUIRY, prod2.priceType)

        val comp1 = MockFeedData.mockCompanies.first()
        assertEquals("comp-001", comp1.companyId)
        assertEquals("우회소 스튜디오", comp1.name)
        assertNotNull(comp1.websiteUrl)
    }

    @Test
    fun testSavedProductRepository() {
        val testProductId = "test-prod-101"
        assertFalse(SavedProductRepository.isSaved(testProductId))

        val saved = SavedProductRepository.toggleSave(testProductId)
        assertTrue(saved)
        assertTrue(SavedProductRepository.isSaved(testProductId))

        val unsaved = SavedProductRepository.toggleSave(testProductId)
        assertFalse(unsaved)
        assertFalse(SavedProductRepository.isSaved(testProductId))
    }

    @Test
    fun testConversionTracker() {
        val testProductId = "test-prod-202"
        assertEquals(0L, ConversionTracker.getInquiryClickCount(testProductId))

        val count1 = ConversionTracker.trackInquiryClick(testProductId)
        assertEquals(1L, count1)
        assertEquals(1L, ConversionTracker.getInquiryClickCount(testProductId))

        val count2 = ConversionTracker.trackInquiryClick(testProductId)
        assertEquals(2L, count2)
        assertEquals(2L, ConversionTracker.getInquiryClickCount(testProductId))
    }
}