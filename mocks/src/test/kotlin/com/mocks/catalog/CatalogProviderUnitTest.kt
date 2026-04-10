package com.mocks.catalog

import com.mocks.catalog.ports.CatalogProvider
import com.mocks.catalog.ports.ProductId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import org.springframework.context.MessageSource
import java.util.Locale
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CatalogProviderUnitTest {

    private lateinit var messageSource: MessageSource
    private lateinit var catalogProvider: CatalogProvider

    @BeforeEach
    fun setUp() {
        messageSource = mock()
        catalogProvider = CatalogProvider(messageSource)
    }

    @Test
    fun `should return catalog with product name for default locale`() {
        // Given
        val productId = ProductId("TEST-PRODUCT-123")
        val locale = Locale.ENGLISH
        val expectedName = "Premium Wireless Headphones - TEST-PRODUCT-123"
        mockMessageSource(locale, productName = expectedName)

        // When
        val result = catalogProvider.catalog(productId, locale)

        // Then
        assertEquals(expectedName, result.name)
    }

    @Test
    fun `should return catalog with product description for UK locale`() {
        // Given
        val productId = ProductId("PRODUCT-001")
        val locale = Locale.UK
        val expectedDescription = "High-quality wireless headphones with noise cancellation"
        mockMessageSource(locale, productDescription = expectedDescription)

        // When
        val result = catalogProvider.catalog(productId, locale)

        // Then
        assertEquals(expectedDescription, result.description)
    }

    @Test
    fun `should return catalog with localized specs for German locale`() {
        // Given
        val productId = ProductId("PRODUCT-001")
        val locale = Locale.GERMANY
        mockMessageSource(
            locale,
            dimensions = "Abmessungen",
            weight = "Gewicht",
            material = "Material",
            color = "Farbe",
            materialValue = "Premium-Kunststoff",
            colorValue = "Blau"
        )

        // When
        val result = catalogProvider.catalog(productId, locale)

        // Then
        assertTrue(result.specs.containsKey("Abmessungen"))
        assertTrue(result.specs.containsKey("Gewicht"))
        assertTrue(result.specs.containsKey("Material"))
        assertTrue(result.specs.containsKey("Farbe"))
        assertEquals("Premium-Kunststoff", result.specs["Material"])
        assertEquals("Blau", result.specs["Farbe"])
    }

    @Test
    fun `should return catalog with correct spec values`() {
        // Given
        val productId = ProductId("PRODUCT-001")
        val locale = Locale.ENGLISH
        mockMessageSource(locale)

        // When
        val result = catalogProvider.catalog(productId, locale)

        // Then
        assertEquals(4, result.specs.size)
        assertTrue(result.specs.values.contains("30cm x 20cm x 10cm"))
        assertTrue(result.specs.values.contains("2.5kg"))
    }

    @Test
    fun `should generate three product images based on product id`() {
        // Given
        val productId = ProductId("PRODUCT-001")
        val locale = Locale.ENGLISH
        mockMessageSource(locale)

        // When
        val result = catalogProvider.catalog(productId, locale)

        // Then
        assertEquals(3, result.images.size)
        result.images.forEach { image ->
            assertTrue(image.startsWith("https://images.example.com/${productId.productId}/"))
            assertTrue(image.endsWith(".jpg"))
        }
    }

    @Test
    fun `should return consistent images for same product id`() {
        // Given
        val productId = ProductId("CONSISTENT-PRODUCT")
        val locale = Locale.ENGLISH
        mockMessageSource(locale)

        // When
        val result1 = catalogProvider.catalog(productId, locale)
        val result2 = catalogProvider.catalog(productId, locale)

        // Then
        assertEquals(result1.images, result2.images)
    }

    @Test
    fun `should return different images for different product ids`() {
        // Given
        val productId1 = ProductId("PRODUCT-001")
        val productId2 = ProductId("PRODUCT-002")
        val locale = Locale.ENGLISH
        mockMessageSource(locale)

        // When
        val result1 = catalogProvider.catalog(productId1, locale)
        val result2 = catalogProvider.catalog(productId2, locale)

        // Then
        // Images should be different (at least some of them)
        val imagesMatch = result1.images == result2.images
        assertTrue(!imagesMatch, "Different product IDs should generate different images")
    }

    @Test
    fun `should handle product name with product id interpolation`() {
        // Given
        val productId = ProductId("XYZ-999")
        val locale = Locale.UK
        val expectedName = "Product XYZ-999"
        mockMessageSource(locale, productName = expectedName)

        // When
        val result = catalogProvider.catalog(productId, locale)

        // Then
        assertEquals(expectedName, result.name)
    }

    @Test
    fun `should return catalog with all required fields populated`() {
        // Given
        val productId = ProductId("COMPLETE-PRODUCT")
        val locale = Locale.ENGLISH
        mockMessageSource(locale)

        // When
        val result = catalogProvider.catalog(productId, locale)

        // Then
        assertTrue(result.name.isNotEmpty())
        assertTrue(result.description.isNotEmpty())
        assertTrue(result.specs.isNotEmpty())
        assertTrue(result.images.isNotEmpty())
    }

    private fun mockMessageSource(
        locale: Locale,
        productName: String = "Test Product",
        productDescription: String = "Test Description",
        dimensions: String = "Dimensions",
        weight: String = "Weight",
        material: String = "Material",
        color: String = "Color",
        materialValue: String = "Premium Plastic",
        colorValue: String = "Blue"
    ) {
        whenever(messageSource.getMessage(eq("catalog.product.name"), any(), eq(locale)))
            .thenReturn(productName)
        whenever(messageSource.getMessage(eq("catalog.product.description"), any(), eq(locale)))
            .thenReturn(productDescription)
        whenever(messageSource.getMessage(eq("catalog.specs.dimensions"), any(), eq(locale)))
            .thenReturn(dimensions)
        whenever(messageSource.getMessage(eq("catalog.specs.weight"), any(), eq(locale)))
            .thenReturn(weight)
        whenever(messageSource.getMessage(eq("catalog.specs.material"), any(), eq(locale)))
            .thenReturn(material)
        whenever(messageSource.getMessage(eq("catalog.specs.color"), any(), eq(locale)))
            .thenReturn(color)
        whenever(messageSource.getMessage(eq("catalog.specs.material.value"), any(), eq(locale)))
            .thenReturn(materialValue)
        whenever(messageSource.getMessage(eq("catalog.specs.color.value"), any(), eq(locale)))
            .thenReturn(colorValue)
    }
}

