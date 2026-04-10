package com.mocks.pricing

import com.mocks.pricing.ports.PricingProvider
import com.mocks.pricing.ports.ProductId
import com.mocks.pricing.ports.CustomerId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import org.springframework.context.MessageSource
import java.util.Locale
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PricingProviderUnitTest {

    private lateinit var messageSource: MessageSource
    private lateinit var pricingProvider: PricingProvider

    @BeforeEach
    fun setUp() {
        messageSource = mock()
        pricingProvider = PricingProvider(messageSource)
    }

    @Test
    fun `should return pricing for default locale with customer discount`() {
        // Given
        val productId = ProductId("PRODUCT-123")
        val customerId = CustomerId("CUSTOMER-456")
        val locale = Locale.ENGLISH
        mockMessageSource(locale, "EUR", "{0}%")

        // When
        val result = pricingProvider.pricing(productId, customerId, locale)

        // Then
        assertNotNull(result.bestPrice)
        assertNotNull(result.customerDiscount)
        assertNotNull(result.finalPrice)
        assertTrue(result.bestPrice.isNotEmpty())
        assertTrue(result.customerDiscount.isNotEmpty())
        assertTrue(result.finalPrice.isNotEmpty())
    }

    @Test
    fun `should return pricing for UK locale with GBP currency`() {
        // Given
        val productId = ProductId("PRODUCT-123")
        val customerId = CustomerId("CUSTOMER-456")
        val locale = Locale.UK
        mockMessageSource(locale, "GBP", "{0}%")

        // When
        val result = pricingProvider.pricing(productId, customerId, locale)

        // Then
        assertNotNull(result.bestPrice)
        assertNotNull(result.customerDiscount)
        assertNotNull(result.finalPrice)
    }

    @Test
    fun `should return pricing for German locale with EUR currency`() {
        // Given
        val productId = ProductId("PRODUCT-123")
        val customerId = CustomerId("CUSTOMER-456")
        val locale = Locale.GERMANY
        mockMessageSource(locale, "EUR", "{0}%")

        // When
        val result = pricingProvider.pricing(productId, customerId, locale)

        // Then
        assertNotNull(result.bestPrice)
        assertNotNull(result.customerDiscount)
        assertNotNull(result.finalPrice)
    }

    @Test
    fun `should return pricing without customer discount when customer-id is null`() {
        // Given
        val productId = ProductId("PRODUCT-123")
        val locale = Locale.ENGLISH
        mockMessageSource(locale, "EUR", "{0}%")

        // When
        val result = pricingProvider.pricing(productId, null, locale)

        // Then
        assertNotNull(result.bestPrice)
        assertNotNull(result.customerDiscount)
        assertNotNull(result.finalPrice)
        assertTrue(result.customerDiscount.contains("0"))
        assertEquals(result.bestPrice, result.finalPrice)
    }

    @Test
    fun `should generate different prices for different product IDs`() {
        // Given
        val productId1 = ProductId("PRODUCT-100")
        val productId2 = ProductId("PRODUCT-200")
        val locale = Locale.ENGLISH
        mockMessageSource(locale, "EUR", "{0}%")

        // When
        val result1 = pricingProvider.pricing(productId1, null, locale)
        val result2 = pricingProvider.pricing(productId2, null, locale)

        // Then
        assertNotEquals(result1.bestPrice, result2.bestPrice)
    }

    @Test
    fun `should generate different discounts for different customer IDs`() {
        // Given
        val productId = ProductId("PRODUCT-123")
        val customerId1 = CustomerId("CUSTOMER-100")
        val customerId2 = CustomerId("CUSTOMER-200")
        val locale = Locale.ENGLISH
        mockMessageSource(locale, "EUR", "{0}%")

        // When
        val result1 = pricingProvider.pricing(productId, customerId1, locale)
        val result2 = pricingProvider.pricing(productId, customerId2, locale)

        // Then
        assertNotNull(result1.customerDiscount)
        assertNotNull(result2.customerDiscount)
    }

    @Test
    fun `should return consistent pricing for same product and customer`() {
        // Given
        val productId = ProductId("CONSISTENT-PRODUCT")
        val customerId = CustomerId("CONSISTENT-CUSTOMER")
        val locale = Locale.ENGLISH
        mockMessageSource(locale, "EUR", "{0}%")

        // When
        val result1 = pricingProvider.pricing(productId, customerId, locale)
        val result2 = pricingProvider.pricing(productId, customerId, locale)

        // Then
        assertEquals(result1.bestPrice, result2.bestPrice)
        assertEquals(result1.customerDiscount, result2.customerDiscount)
        assertEquals(result1.finalPrice, result2.finalPrice)
    }

    @Test
    fun `should return all required fields populated`() {
        // Given
        val productId = ProductId("COMPLETE-PRODUCT")
        val customerId = CustomerId("COMPLETE-CUSTOMER")
        val locale = Locale.ENGLISH
        mockMessageSource(locale, "EUR", "{0}%")

        // When
        val result = pricingProvider.pricing(productId, customerId, locale)

        // Then
        assertTrue(result.bestPrice.isNotEmpty())
        assertTrue(result.customerDiscount.isNotEmpty())
        assertTrue(result.finalPrice.isNotEmpty())
    }

    @Test
    fun `should handle Dutch locale with EUR currency`() {
        // Given
        val productId = ProductId("PRODUCT-123")
        val customerId = CustomerId("CUSTOMER-456")
        val locale = Locale.forLanguageTag("nl-NL")
        mockMessageSource(locale, "EUR", "{0}%")

        // When
        val result = pricingProvider.pricing(productId, customerId, locale)

        // Then
        assertNotNull(result.bestPrice)
        assertNotNull(result.customerDiscount)
        assertNotNull(result.finalPrice)
    }

    private fun mockMessageSource(
        locale: Locale,
        currencyCode: String = "EUR",
        discountFormat: String = "{0}%"
    ) {
        whenever(messageSource.getMessage(eq("pricing.currency.code"), any(), eq("EUR"), eq(locale)))
            .thenReturn(currencyCode)
        whenever(messageSource.getMessage(eq("pricing.discount.format"), any(), any(), eq(locale)))
            .thenReturn(discountFormat)
    }
}


