package com.aggregator.ports.providers

import com.aggregator.ports.locale.MessageLocalizer
import com.aggregator.adapters.clients.CustomerClient
import com.aggregator.ports.InformationProvider
import com.aggregator.ports.ProviderResults
import com.aggregator.ports.models.Customer
import com.aggregator.ports.models.CustomerResponse
import com.aggregator.ports.models.Preferences
import mu.KLogging
import java.util.Locale

class CustomerProvider(
    private val customerClient: CustomerClient,
    private val messageLocalizer: MessageLocalizer
) : InformationProvider<CustomerResponse> {

    override suspend fun fetchData(productId: String, marketCode: String, customerId: String?): CustomerResponse? {
        return if (customerId != null) {
            logger.info { "Fetching customer data for market: $marketCode, customer: $customerId" }
            try {
                customerClient.getCustomer(marketCode, customerId)
            } catch (e: Exception) {
                logger.error("Error fetching customer data", e)
                customerInformationNotProvided(marketCode)
            }
        } else {
            logger.info { "No customer ID provided - returning unknown" }
            customerInformationNotProvided(marketCode)
        }
    }

    override fun processResult(result: CustomerResponse?, currentResults: ProviderResults): ProviderResults {
        return result?.let {
            currentResults.copy(customer = it.toCustomer())
        } ?: currentResults
    }

    private fun CustomerResponse.toCustomer(): Customer = Customer(
        customerSegment = customerSegment,
        preferences = preferences?.let { pref ->
            Preferences(
                communicationChannel = pref.communicationChannel,
                newsletterSubscription = pref.newsletterSubscription,
                loyaltyProgramMember = pref.loyaltyProgramMember
            )
        }
    )

    private fun customerInformationNotProvided(marketCode: String): CustomerResponse {
        val locale = Locale.forLanguageTag(marketCode)
        return CustomerResponse(
            customerSegment = messageLocalizer.getUnknownMessage(locale),
            preferences = null
        )
    }

    companion object: KLogging()
}

