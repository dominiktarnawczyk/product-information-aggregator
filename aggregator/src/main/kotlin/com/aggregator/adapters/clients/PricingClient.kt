package com.aggregator.adapters.clients

import com.aggregator.ports.models.PricingResponse
import mu.KLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import org.springframework.web.util.UriComponentsBuilder

@Component
class PricingClient(
    private val restClient: RestClient,
    @param:Value("\${endpoints.pricing.url}") private val pricingUrl: String
) {
    fun getPricing(productId: String, marketCode: String, customerId: String?): PricingResponse {
        logger.info("Fetching pricing for product: $productId, market: $marketCode, customer: $customerId")
        return restClient.get()
            .uri(uriStringBuilder(productId, marketCode, customerId))
            .retrieve()
            .body<PricingResponse>()
            ?: throw RuntimeException("Pricing service returned null")
    }

    private fun uriStringBuilder(productId: String, marketCode: String, customerId: String?): String {
        val uriBuilder = UriComponentsBuilder.fromUriString(pricingUrl)
            .queryParam("product-id", productId)
            .queryParam("market-code", marketCode)
        customerId?.let { uriBuilder.queryParam("customer-id", it) }
        return uriBuilder.toUriString()
    }

    companion object: KLogging()
}

