package com.aggregator.ports

import com.aggregator.ports.locale.MessageLocalizer
import com.aggregator.adapters.clients.AvailabilityClient
import com.aggregator.adapters.clients.CatalogClient
import com.aggregator.adapters.clients.CustomerClient
import com.aggregator.adapters.clients.PricingClient
import com.aggregator.ports.locale.InvalidLocaleException
import com.aggregator.ports.models.AvailabilityResponse
import com.aggregator.ports.models.CatalogResponse
import com.aggregator.ports.models.CustomerResponse
import com.aggregator.ports.models.PreferencesResponse
import com.aggregator.ports.models.PricingResponse
import com.aggregator.ports.models.SpecificationResponse
import com.aggregator.ports.providers.AvailabilityProvider
import com.aggregator.ports.providers.CatalogProvider
import com.aggregator.ports.providers.CatalogServiceUnrespondingException
import com.aggregator.ports.providers.CustomerProvider
import com.aggregator.ports.providers.PricingProvider
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.junit.jupiter.MockitoExtension
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExtendWith(MockitoExtension::class)
class ProductInformationAggregatorUnitTest {

    @Mock
    private lateinit var catalogClient: CatalogClient

    @Mock
    private lateinit var pricingClient: PricingClient

    @Mock
    private lateinit var availabilityClient: AvailabilityClient

    @Mock
    private lateinit var customerClient: CustomerClient

    private lateinit var messageLocalizer: MessageLocalizer

    private lateinit var catalogProvider: CatalogProvider
    private lateinit var pricingProvider: PricingProvider
    private lateinit var availabilityProvider: AvailabilityProvider
    private lateinit var customerProvider: CustomerProvider

    private lateinit var providersCatalog: ProvidersCatalog
    private lateinit var aggregator: ProductInformationAggregator

    @BeforeEach
    fun setUp() {
        messageLocalizer = mock(MessageLocalizer::class.java) { invocation ->
            val method = invocation.method
            if (method.name == "getMessage" && invocation.arguments.isNotEmpty()) {
                when (invocation.arguments[0] as String) {
                    "fallback.unknown" -> "Unknown"
                    "fallback.unavailable" -> "Unavailable"
                    else -> invocation.arguments[0] as String
                }
            } else {
                ""
            }
        }

        catalogProvider = CatalogProvider(catalogClient)
        pricingProvider = PricingProvider(pricingClient, messageLocalizer)
        availabilityProvider = AvailabilityProvider(availabilityClient, messageLocalizer)
        customerProvider = CustomerProvider(customerClient, messageLocalizer)

        providersCatalog = ProvidersCatalog(
            catalogProvider,
            pricingProvider,
            availabilityProvider,
            customerProvider
        )

        aggregator = ProductInformationAggregator(providersCatalog)
    }

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
        assertThrows<CatalogServiceUnrespondingException> {
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
        assertNotNull(productInfo.pricing) // Returns fallback data
        assertEquals("Unavailable", productInfo.pricing?.finalPrice)
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
        assertNotNull(productInfo.availability) // Returns fallback data
        assertEquals(-1, productInfo.availability.stockLevel)
        assertEquals("Unknown", productInfo.availability.warehouseLocation)
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
        assertNotNull(productInfo.customer)
        assertEquals("Unknown", productInfo.customer.customerSegment)
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
        assertNotNull(productInfo.customer)
        assertEquals("Unknown", productInfo.customer.customerSegment)
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
        // Then
        assertNotNull(productInfo.catalog)
    }
}

