package com.mocks.availability

import com.mocks.availability.ports.AvailabilityProvider
import com.mocks.availability.ports.ProductId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import org.springframework.context.MessageSource
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.test.assertEquals

class AvailabilityProviderUnitTest {

    private lateinit var messageSource: MessageSource
    private lateinit var availabilityProvider: AvailabilityProvider

    @BeforeEach
    fun setUp() {
        messageSource = mock()
        availabilityProvider = AvailabilityProvider(messageSource)
    }

    @Test
    fun `should return availability with correct stock level based on product id hash`() {
        // Given
        val productId = ProductId("TEST-PRODUCT-123")
        val locale = Locale.ENGLISH
        val expectedStockLevel = productId.hashCode() % 1000
        mockMessageSource(locale)

        // When
        val result = availabilityProvider.availability(productId, locale)

        // Then
        assertEquals(expectedStockLevel, result.stockLevel)
    }

    @Test
    fun `should return availability with warehouse location for default locale`() {
        // Given
        val productId = ProductId("PRODUCT-001")
        val locale = Locale.ENGLISH
        val expectedWarehouse = "European Central Distribution Center - Frankfurt"
        mockMessageSource(locale, warehouseLocation = expectedWarehouse)

        // When
        val result = availabilityProvider.availability(productId, locale)

        // Then
        assertEquals(expectedWarehouse, result.warehouseLocation)
    }

    @Test
    fun `should return availability with warehouse location for UK locale`() {
        // Given
        val productId = ProductId("PRODUCT-001")
        val locale = Locale.UK
        val expectedWarehouse = "UK Distribution Center - Manchester"
        mockMessageSource(locale, warehouseLocation = expectedWarehouse, dateFormat = "dd/MM/yyyy")

        // When
        val result = availabilityProvider.availability(productId, locale)

        // Then
        assertEquals(expectedWarehouse, result.warehouseLocation)
    }

    @Test
    fun `should return availability with warehouse location for German locale`() {
        // Given
        val productId = ProductId("PRODUCT-001")
        val locale = Locale.GERMANY
        val expectedWarehouse = "Deutsches Vertriebszentrum - Hamburg"
        mockMessageSource(locale, warehouseLocation = expectedWarehouse, dateFormat = "dd.MM.yyyy")

        // When
        val result = availabilityProvider.availability(productId, locale)

        // Then
        assertEquals(expectedWarehouse, result.warehouseLocation)
    }

    @Test
    fun `should calculate expected delivery date correctly with default format`() {
        // Given
        val productId = ProductId("PRODUCT-001")
        val locale = Locale.ENGLISH
        val deliveryDays = 3
        val expectedDate = LocalDate.now().plusDays(deliveryDays.toLong())
        val expectedDateString = expectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd", locale))
        mockMessageSource(locale, warehouseLocation = "Test Warehouse", deliveryDays = deliveryDays.toString())

        // When
        val result = availabilityProvider.availability(productId, locale)

        // Then
        assertEquals(expectedDateString, result.expectedDelivery)
    }

    @Test
    fun `should calculate expected delivery date with UK date format`() {
        // Given
        val productId = ProductId("PRODUCT-001")
        val locale = Locale.UK
        val deliveryDays = 3
        val expectedDate = LocalDate.now().plusDays(deliveryDays.toLong())
        val expectedDateString = expectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy", locale))
        mockMessageSource(locale, warehouseLocation = "UK Distribution Center", deliveryDays = deliveryDays.toString(), dateFormat = "dd/MM/yyyy")

        // When
        val result = availabilityProvider.availability(productId, locale)

        // Then
        assertEquals(expectedDateString, result.expectedDelivery)
    }

    @Test
    fun `should calculate expected delivery date with German date format`() {
        // Given
        val productId = ProductId("PRODUCT-001")
        val locale = Locale.GERMANY
        val deliveryDays = 3
        val expectedDate = LocalDate.now().plusDays(deliveryDays.toLong())
        val expectedDateString = expectedDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy", locale))
        mockMessageSource(locale, warehouseLocation = "German Distribution Center", deliveryDays = deliveryDays.toString(), dateFormat = "dd.MM.yyyy")

        // When
        val result = availabilityProvider.availability(productId, locale)

        // Then
        assertEquals(expectedDateString, result.expectedDelivery)
    }

    @Test
    fun `should handle custom delivery days`() {
        // Given
        val productId = ProductId("PRODUCT-001")
        val locale = Locale.ENGLISH
        val customDeliveryDays = 7
        val expectedDate = LocalDate.now().plusDays(customDeliveryDays.toLong())
        val expectedDateString = expectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd", locale))
        mockMessageSource(locale, warehouseLocation = "Test Warehouse", deliveryDays = customDeliveryDays.toString())

        // When
        val result = availabilityProvider.availability(productId, locale)

        // Then
        assertEquals(expectedDateString, result.expectedDelivery)
    }

    @Test
    fun `should return consistent stock level for same product id`() {
        // Given
        val productId = ProductId("CONSISTENT-PRODUCT")
        val locale = Locale.ENGLISH
        mockMessageSource(locale)

        // When
        val result1 = availabilityProvider.availability(productId, locale)
        val result2 = availabilityProvider.availability(productId, locale)

        // Then
        assertEquals(result1.stockLevel, result2.stockLevel)
    }

    private fun mockMessageSource(
        locale: Locale,
        warehouseLocation: String = "Test Warehouse",
        deliveryDays: String = "3",
        dateFormat: String = "yyyy-MM-dd"
    ) {
        whenever(messageSource.getMessage(eq("availability.warehouse.location"), any(), eq(locale)))
            .thenReturn(warehouseLocation)
        whenever(messageSource.getMessage(eq("availability.delivery.days"), any(), eq("3"), eq(locale)))
            .thenReturn(deliveryDays)
        whenever(messageSource.getMessage(eq("availability.date.format"), any(), eq("yyyy-MM-dd"), eq(locale)))
            .thenReturn(dateFormat)
    }
}