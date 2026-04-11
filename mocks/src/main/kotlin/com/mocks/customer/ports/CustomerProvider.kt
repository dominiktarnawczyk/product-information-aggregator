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

    private fun getCustomerPreferences(customerId: CustomerId, locale: Locale): Preferences {
        val hash = customerId.customerId.hashCode().toLong()
        return Preferences(
            communicationChannel = getLocalizedCommunicationPreference(locale),
            newsletterSubscription = hash % 2 == 0L,
            loyaltyProgramMember = hash % 3 == 0L
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
    val preferences: Preferences?
)

data class Preferences(
    val communicationChannel: String,
    val newsletterSubscription: Boolean,
    val loyaltyProgramMember: Boolean
)

@JvmInline
value class CustomerId(val customerId: String)

