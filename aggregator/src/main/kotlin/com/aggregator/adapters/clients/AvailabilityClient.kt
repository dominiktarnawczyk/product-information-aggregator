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
        val url = UriComponentsBuilder.fromUriString(availabilityUrl)
            .queryParam("product-id", productId)
            .queryParam("market-code", marketCode)
            .toUriString()

        logger.info("Fetching availability for product: $productId, market: $marketCode")
        return restClient.get()
            .uri(url)
            .retrieve()
            .body<AvailabilityResponse>()
            ?: throw RuntimeException("Availability service returned null")
    }

    companion object: KLogging()
}

