package com.mocks.customer.ports

import org.springframework.context.MessageSource
import java.util.Locale

class CustomerProvider(
    private val messageSource: MessageSource
) {
    fun customer(customerId: CustomerId?, locale: Locale): Customer {
        return Customer(
            customerSegment = getCustomerSegment(locale),
            preferences = customerId?.let { getCustomerPreferences(it, locale) }
        )
    }

    private fun getCustomerSegment(locale: Locale): String {
        return messageSource.getMessage(
            "customer.segment",
            null,
            locale
        )
    }

    private fun getCustomerPreferences(customerId: CustomerId, locale: Locale): Map<String, String> {
        val hash = customerId.customerId.hashCode().toLong()
        return mapOf(
            "communicationChannel" to getLocalizedCommunicationPreference(locale),
            "newsletterSubscription" to (hash % 2 == 0L).toString(),
            "loyaltyProgramMember" to (hash % 3 == 0L).toString()
        )
    }

    private fun getLocalizedCommunicationPreference(locale: Locale): String {
        return messageSource.getMessage(
            "customer.preferences.communication.value",
            null,
            locale
        )
    }
}

data class Customer(
    val customerSegment: String,
    val preferences: Map<String, String>?
)

@JvmInline
value class CustomerId(val customerId: String)

