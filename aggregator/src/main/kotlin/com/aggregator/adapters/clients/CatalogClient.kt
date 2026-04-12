package com.aggregator.adapters.clients

import com.aggregator.ports.models.CatalogResponse
import mu.KLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import org.springframework.web.util.UriComponentsBuilder

@Component
class CatalogClient(
    private val restClient: RestClient,
    @param:Value("\${endpoints.catalog.url}") private val catalogUrl: String
) {
    fun getCatalog(productId: String, marketCode: String): CatalogResponse {
        logger.info("Fetching catalog API endpoint $catalogUrl for product: $productId, market: $marketCode")
        return restClient.get()
            .uri(uriStringBuilder(productId, marketCode))
            .retrieve()
            .body<CatalogResponse>()
            ?: throw RuntimeException("Catalog service returned null")
    }

    private fun uriStringBuilder(productId: String, marketCode: String): String {
        return UriComponentsBuilder.fromUriString(catalogUrl)
            .queryParam("product-id", productId)
            .queryParam("market-code", marketCode)
            .toUriString()
    }

    companion object: KLogging()
}

