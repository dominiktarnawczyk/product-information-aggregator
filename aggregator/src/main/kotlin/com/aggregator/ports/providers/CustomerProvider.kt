package com.aggregator.ports.providers

import com.aggregator.adapters.clients.CustomerClient
import com.aggregator.ports.InformationProvider
import com.aggregator.ports.ProviderResults
import com.aggregator.ports.models.Customer
import com.aggregator.ports.models.CustomerResponse
import com.aggregator.ports.models.Preferences
import mu.KLogging

class CustomerProvider(
    private val customerClient: CustomerClient
) : InformationProvider<CustomerResponse> {

    override suspend fun fetchData(productId: String, marketCode: String, customerId: String?): CustomerResponse? {
        return if (customerId != null) {
            logger.info { "Fetching customer data for market: $marketCode, customer: $customerId" }
            try {
                customerClient.getCustomer(marketCode, customerId)
            } catch (e: Exception) {
                logger.warn { "Customer service failed - will return null" }
                null
            }
        } else {
            logger.info { "No customer ID provided - returning null" }
            null
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

    companion object: KLogging()
}

