package com.aggregator.ports

import com.aggregator.adapters.clients.AvailabilityClient
import com.aggregator.adapters.clients.CatalogClient
import com.aggregator.adapters.clients.CustomerClient
import com.aggregator.adapters.clients.PricingClient
import com.aggregator.ports.models.AvailabilityResponse
import com.aggregator.ports.models.CatalogResponse
import com.aggregator.ports.models.CustomerResponse
import com.aggregator.ports.models.PreferencesResponse
import com.aggregator.ports.models.PricingResponse
import com.aggregator.ports.models.SpecificationResponse
import com.aggregator.ports.providers.AvailabilityProvider
import com.aggregator.ports.providers.CatalogProvider
import com.aggregator.ports.providers.CustomerProvider
import com.aggregator.ports.providers.PricingProvider
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ProductInformationAggregatorTest {

    private val catalogClient = mock(CatalogClient::class.java)
    private val pricingClient = mock(PricingClient::class.java)
    private val availabilityClient = mock(AvailabilityClient::class.java)
    private val customerClient = mock(CustomerClient::class.java)

    private val catalogProvider = CatalogProvider(catalogClient)
    private val pricingProvider = PricingProvider(pricingClient)
    private val availabilityProvider = AvailabilityProvider(availabilityClient)
    private val customerProvider = CustomerProvider(customerClient)

    private val providersCatalog = ProvidersCatalog(
        catalogProvider,
        pricingProvider,
        availabilityProvider,
        customerProvider
    )

    private val aggregator = ProductInformationAggregator(providersCatalog)

    @Test
    fun `should aggregate all services successfully`() = runTest {
        // Given
        val productId = "PROD001"
        val marketCode = "en-GB"
        val customerId = "CUST123"

        `when`(catalogClient.getCatalog(productId, marketCode)).thenReturn(
            CatalogResponse(
                name = "Test Product",
                description = "Test Description",
                specs = SpecificationResponse("10x10x10", "1kg", "Plastic", "Blue"),
                images = listOf("img1.jpg")
            )
        )

        `when`(pricingClient.getPricing(productId, marketCode, customerId)).thenReturn(
            PricingResponse("100.00 EUR", "10%", "90.00 EUR")
        )

        `when`(availabilityClient.getAvailability(productId, marketCode)).thenReturn(
            AvailabilityResponse(50, "Warehouse A", "2024-04-15")
        )

        `when`(customerClient.getCustomer(marketCode, customerId)).thenReturn(
            CustomerResponse(
                "Premium",
                PreferencesResponse("Email", true, true)
            )
        )

        // When
        val productInfo = aggregator.aggregate(productId, marketCode, customerId)

        // Then
        assertNotNull(productInfo.catalog)
        assertEquals("Test Product", productInfo.catalog.name)

        assertNotNull(productInfo.pricing)
        assertEquals("90.00 EUR", productInfo.pricing?.finalPrice)

        assertNotNull(productInfo.availability)
        assertEquals(50, productInfo.availability?.stockLevel)

        assertNotNull(productInfo.customer)
        assertEquals("Premium", productInfo.customer?.customerSegment)
    }

    @Test
    fun `should fail when catalog service fails`() = runTest {
        // Given
        val productId = "PROD001"
        val marketCode = "en-GB"
        val customerId = "CUST123"

        `when`(catalogClient.getCatalog(productId, marketCode))
            .thenThrow(RuntimeException("Catalog service failed"))

        `when`(pricingClient.getPricing(productId, marketCode, customerId))
            .thenReturn(PricingResponse("100.00 EUR", "10%", "90.00 EUR"))

        `when`(availabilityClient.getAvailability(productId, marketCode))
            .thenReturn(AvailabilityResponse(50, "Warehouse A", "2024-04-15"))

        `when`(customerClient.getCustomer(marketCode, customerId))
            .thenReturn(CustomerResponse("Premium", null))

        // When & Then
        assertThrows<RuntimeException> {
            aggregator.aggregate(productId, marketCode, customerId)
        }
    }

    @Test
    fun `should return product without pricing when pricing service fails`() = runTest {
        // Given
        val productId = "PROD001"
        val marketCode = "en-GB"
        val customerId = "CUST123"

        `when`(catalogClient.getCatalog(productId, marketCode)).thenReturn(
            CatalogResponse(
                name = "Test Product",
                description = "Test Description",
                specs = SpecificationResponse("10x10x10", "1kg", "Plastic", "Blue"),
                images = listOf("img1.jpg")
            )
        )

        `when`(pricingClient.getPricing(productId, marketCode, customerId))
            .thenThrow(RuntimeException("Pricing service failed"))

        `when`(availabilityClient.getAvailability(productId, marketCode)).thenReturn(
            AvailabilityResponse(50, "Warehouse A", "2024-04-15")
        )

        `when`(customerClient.getCustomer(marketCode, customerId)).thenReturn(
            CustomerResponse("Premium", null)
        )

        // When
        val productInfo = aggregator.aggregate(productId, marketCode, customerId)

        // Then
        assertNotNull(productInfo.catalog)
        assertNull(productInfo.pricing)
        assertNotNull(productInfo.availability)
        assertNotNull(productInfo.customer)
    }

    @Test
    fun `should return product without availability when availability service fails`() = runTest {
        // Given
        val productId = "PROD001"
        val marketCode = "en-GB"
        val customerId = "CUST123"

        `when`(catalogClient.getCatalog(productId, marketCode)).thenReturn(
            CatalogResponse(
                name = "Test Product",
                description = "Test Description",
                specs = SpecificationResponse("10x10x10", "1kg", "Plastic", "Blue"),
                images = listOf("img1.jpg")
            )
        )

        `when`(pricingClient.getPricing(productId, marketCode, customerId)).thenReturn(
            PricingResponse("100.00 EUR", "10%", "90.00 EUR")
        )

        `when`(availabilityClient.getAvailability(productId, marketCode))
            .thenThrow(RuntimeException("Availability service failed"))

        `when`(customerClient.getCustomer(marketCode, customerId)).thenReturn(
            CustomerResponse("Premium", null)
        )

        // When
        val productInfo = aggregator.aggregate(productId, marketCode, customerId)

        // Then
        assertNotNull(productInfo.catalog)
        assertNotNull(productInfo.pricing)
        assertNull(productInfo.availability)
        assertNotNull(productInfo.customer)
    }

    @Test
    fun `should return standard response when customer service fails`() = runTest {
        // Given
        val productId = "PROD001"
        val marketCode = "en-GB"
        val customerId = "CUST123"

        `when`(catalogClient.getCatalog(productId, marketCode)).thenReturn(
            CatalogResponse(
                name = "Test Product",
                description = "Test Description",
                specs = SpecificationResponse("10x10x10", "1kg", "Plastic", "Blue"),
                images = listOf("img1.jpg")
            )
        )

        `when`(pricingClient.getPricing(productId, marketCode, customerId)).thenReturn(
            PricingResponse("100.00 EUR", "10%", "90.00 EUR")
        )

        `when`(availabilityClient.getAvailability(productId, marketCode)).thenReturn(
            AvailabilityResponse(50, "Warehouse A", "2024-04-15")
        )

        `when`(customerClient.getCustomer(marketCode, customerId))
            .thenThrow(RuntimeException("Customer service failed"))

        // When
        val productInfo = aggregator.aggregate(productId, marketCode, customerId)

        // Then
        assertNotNull(productInfo.catalog)
        assertNotNull(productInfo.pricing)
        assertNotNull(productInfo.availability)
        assertNull(productInfo.customer) // Customer failed, should be null (standard response)
    }

    @Test
    fun `should return standard response when no customer ID provided`() = runTest {
        // Given
        val productId = "PROD001"
        val marketCode = "en-GB"
        val customerId: String? = null

        `when`(catalogClient.getCatalog(productId, marketCode)).thenReturn(
            CatalogResponse(
                name = "Test Product",
                description = "Test Description",
                specs = SpecificationResponse("10x10x10", "1kg", "Plastic", "Blue"),
                images = listOf("img1.jpg")
            )
        )

        `when`(pricingClient.getPricing(productId, marketCode, customerId)).thenReturn(
            PricingResponse("100.00 EUR", "0%", "100.00 EUR")
        )

        `when`(availabilityClient.getAvailability(productId, marketCode)).thenReturn(
            AvailabilityResponse(50, "Warehouse A", "2024-04-15")
        )


        // When
        val productInfo = aggregator.aggregate(productId, marketCode, customerId)

        // Then
        assertNotNull(productInfo.catalog)
        assertNotNull(productInfo.pricing)
        assertNotNull(productInfo.availability)
        assertNull(productInfo.customer)
    }

    @Test
    fun `should throw InvalidLocaleException when market code is blank`() = runTest {
        // Given
        val productId = "PROD001"
        val marketCode = ""
        val customerId = "CUST123"

        // When & Then
        val exception = assertThrows<InvalidLocaleException> {
            aggregator.aggregate(productId, marketCode, customerId)
        }
        assertEquals("Invalid locale: ", exception.message)
    }

    @Test
    fun `should throw InvalidLocaleException when market code is whitespace`() = runTest {
        // Given
        val productId = "PROD001"
        val marketCode = "   "
        val customerId = "CUST123"

        // When & Then
        val exception = assertThrows<InvalidLocaleException> {
            aggregator.aggregate(productId, marketCode, customerId)
        }
        assertEquals("Invalid locale:    ", exception.message)
    }

    @Test
    fun `should throw InvalidLocaleException when market code is invalid`() = runTest {
        // Given
        val productId = "PROD001"
        val marketCode = "invalid-locale-@@"
        val customerId = "CUST123"

        // When & Then
        assertThrows<InvalidLocaleException> {
            aggregator.aggregate(productId, marketCode, customerId)
        }
    }

    @Test
    fun `should accept valid locale with language and region`() = runTest {
        // Given
        val productId = "PROD001"
        val marketCode = "en-GB"
        val customerId = "CUST123"

        `when`(catalogClient.getCatalog(productId, marketCode)).thenReturn(
            CatalogResponse(
                name = "Test Product",
                description = "Test Description",
                specs = SpecificationResponse("10x10x10", "1kg", "Plastic", "Blue"),
                images = listOf("img1.jpg")
            )
        )

        `when`(pricingClient.getPricing(productId, marketCode, customerId)).thenReturn(
            PricingResponse("100.00 EUR", "10%", "90.00 EUR")
        )

        `when`(availabilityClient.getAvailability(productId, marketCode)).thenReturn(
            AvailabilityResponse(50, "Warehouse A", "2024-04-15")
        )

        `when`(customerClient.getCustomer(marketCode, customerId)).thenReturn(
            CustomerResponse("Premium", PreferencesResponse("Email", true, true))
        )

        // When
        val productInfo = aggregator.aggregate(productId, marketCode, customerId)

        // Then - should not throw and return valid data
        assertNotNull(productInfo.catalog)
    }
}

