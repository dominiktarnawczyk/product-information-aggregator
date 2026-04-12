package com.aggregator.adapters.clients

import com.aggregator.ports.models.AvailabilityResponse
import mu.KLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import org.springframework.web.util.UriComponentsBuilder

@Component
class AvailabilityClient(
    private val restClient: RestClient,
    @param:Value("\${endpoints.availability.url}") private val availabilityUrl: String
) {
    fun getAvailability(productId: String, marketCode: String): AvailabilityResponse {
        logger.info("Fetching availability API endpoint $availabilityUrl for product: $productId, market: $marketCode")
        return restClient.get()
            .uri(uriStringBuilder(productId, marketCode))
            .retrieve()
            .body<AvailabilityResponse>()
            ?: throw RuntimeException("Availability service returned null")
    }

    private fun uriStringBuilder(productId: String, marketCode: String): String {
        return UriComponentsBuilder.fromUriString(availabilityUrl)
            .queryParam("product-id", productId)
            .queryParam("market-code", marketCode)
            .toUriString()
    }

    companion object: KLogging()
}

