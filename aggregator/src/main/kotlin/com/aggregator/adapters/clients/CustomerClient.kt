package com.aggregator.adapters.clients

import com.aggregator.ports.models.CustomerResponse
import mu.KLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import org.springframework.web.util.UriComponentsBuilder

@Component
class CustomerClient(
    private val restClient: RestClient,
    @param:Value("\${endpoints.customer.url}") private val customerUrl: String
) {

    fun getCustomer(marketCode: String, customerId: String?): CustomerResponse {
        logger.info("Fetching customer data API endpoint $customerUrl for market: $marketCode, customer: $customerId")
        return restClient.get()
            .uri(uriStringBuilder(marketCode, customerId))
            .retrieve()
            .body<CustomerResponse>()
            ?: throw RuntimeException("Customer service returned null")
    }

    private fun uriStringBuilder(marketCode: String, customerId: String?): String {
        val uriBuilder = UriComponentsBuilder
            .fromUriString(customerUrl)
            .queryParam("market-code", marketCode)
        customerId?.let { uriBuilder.queryParam("customer-id", it) }
        return uriBuilder.toUriString()
    }

    companion object: KLogging()
}

