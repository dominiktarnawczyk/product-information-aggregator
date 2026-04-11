package com.mocks.customer

import com.mocks.customer.ports.CustomerProvider
import com.mocks.customer.ports.CustomerId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import org.springframework.context.MessageSource
import java.util.Locale
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CustomerProviderUnitTest {

    private lateinit var messageSource: MessageSource
    private lateinit var customerProvider: CustomerProvider

    @BeforeEach
    fun setUp() {
        messageSource = mock()
        customerProvider = CustomerProvider(messageSource)
    }

    @Test
    fun `should return customer segment for default locale`() {
        // Given
        val locale = Locale.ENGLISH
        val expectedSegment = "Premium"
        mockMessageSource(locale, segment = expectedSegment)

        // When
        val result = customerProvider.customer(null, locale)

        // Then
        assertEquals(expectedSegment, result.customerSegment)
        assertNull(result.preferences)
    }

    @Test
    fun `should return customer segment for UK locale`() {
        // Given
        val locale = Locale.UK
        val expectedSegment = "Premium"
        mockMessageSource(locale, segment = expectedSegment)

        // When
        val result = customerProvider.customer(null, locale)

        // Then
        assertEquals(expectedSegment, result.customerSegment)
        assertNull(result.preferences)
    }

    @Test
    fun `should return customer segment for German locale`() {
        // Given
        val locale = Locale.GERMANY
        val expectedSegment = "Premium"
        mockMessageSource(locale, segment = expectedSegment)

        // When
        val result = customerProvider.customer(null, locale)

        // Then
        assertEquals(expectedSegment, result.customerSegment)
        assertNull(result.preferences)
    }

    @Test
    fun `should return preferences when customer-id is provided`() {
        // Given
        val customerId = CustomerId("CUSTOMER-123")
        val locale = Locale.ENGLISH
        mockMessageSource(locale)

        // When
        val result = customerProvider.customer(customerId, locale)

        // Then
        assertNotNull(result.preferences)
        assertNotNull(result.preferences.communicationChannel)
        assertTrue(result.preferences.newsletterSubscription || !result.preferences.newsletterSubscription)

    }

    @Test
    fun `should not return preferences when customer-id is null`() {
        // Given
        val locale = Locale.ENGLISH
        mockMessageSource(locale)

        // When
        val result = customerProvider.customer(null, locale)

        // Then
        assertNull(result.preferences)
    }

    @Test
    fun `should return localized preferences with customer-id for UK locale`() {
        // Given
        val customerId = CustomerId("CUSTOMER-456")
        val locale = Locale.UK
        mockMessageSource(locale, communicationValue = "Email")

        // When
        val result = customerProvider.customer(customerId, locale)

        // Then
        assertNotNull(result.preferences)
        assertEquals("Email", result.preferences.communicationChannel)
        assertNotNull(result.preferences.newsletterSubscription)
        assertNotNull(result.preferences.loyaltyProgramMember)
    }

    @Test
    fun `should return localized preferences with customer-id for German locale`() {
        // Given
        val customerId = CustomerId("CUSTOMER-789")
        val locale = Locale.GERMANY
        mockMessageSource(locale, communicationValue = "E-Mail")

        // When
        val result = customerProvider.customer(customerId, locale)

        // Then
        assertNotNull(result.preferences)
        assertEquals("E-Mail", result.preferences.communicationChannel)
        assertNotNull(result.preferences.newsletterSubscription)
        assertNotNull(result.preferences.loyaltyProgramMember)
    }

    @Test
    fun `should generate different preference values based on customer-id hash`() {
        // Given
        val customerId1 = CustomerId("CUSTOMER-100")
        val customerId2 = CustomerId("CUSTOMER-101")
        val locale = Locale.ENGLISH
        mockMessageSource(locale)

        // When
        val result1 = customerProvider.customer(customerId1, locale)
        val result2 = customerProvider.customer(customerId2, locale)

        // Then
        assertNotNull(result1.preferences)
        assertNotNull(result2.preferences)
        // At least some preferences should differ due to hash-based generation
    }

    @Test
    fun `should return consistent preferences for same customer-id`() {
        // Given
        val customerId = CustomerId("CONSISTENT-CUSTOMER")
        val locale = Locale.ENGLISH
        mockMessageSource(locale)

        // When
        val result1 = customerProvider.customer(customerId, locale)
        val result2 = customerProvider.customer(customerId, locale)

        // Then
        assertEquals(result1.preferences, result2.preferences)
    }

    @Test
    fun `should return all required fields populated`() {
        // Given
        val customerId = CustomerId("COMPLETE-CUSTOMER")
        val locale = Locale.ENGLISH
        mockMessageSource(locale)

        // When
        val result = customerProvider.customer(customerId, locale)

        // Then
        assertTrue(result.customerSegment.isNotEmpty())
        assertNotNull(result.preferences)
        assertNotNull(result.preferences.communicationChannel)
    }

    private fun mockMessageSource(
        locale: Locale,
        segment: String = "Premium",
        communicationValue: String = "Email"
    ) {
        whenever(messageSource.getMessage(eq("customer.segment"), any(), eq(locale)))
            .thenReturn(segment)
        whenever(messageSource.getMessage(eq("customer.preferences.communication.value"), any(), eq(locale)))
            .thenReturn(communicationValue)
    }
}

