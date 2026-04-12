package com.aggregator.ports.providers

import com.aggregator.adapters.clients.CatalogClient
import com.aggregator.ports.InformationProvider
import com.aggregator.ports.ProviderResults
import com.aggregator.ports.models.Catalog
import com.aggregator.ports.models.CatalogResponse
import com.aggregator.ports.models.Specification
import mu.KLogging

class CatalogProvider(
    private val catalogClient: CatalogClient
) : InformationProvider<CatalogResponse> {

    override suspend fun fetchData(productId: String, marketCode: String, customerId: String?): CatalogResponse? {
        logger.info { "Fetching catalog data for productId: $productId, market: $marketCode" }
        return try {
            catalogClient.getCatalog(productId, marketCode)
        } catch (e: Exception) {
            logger.error { "Catalog service failed - this is critical" }
            throw CatalogServiceUnrespondingException("Catalog service is not responding", e)
        }
    }

    override fun processResult(result: CatalogResponse?, currentResults: ProviderResults): ProviderResults {
        return result?.let {
            currentResults.copy(catalog = it.toCatalog())
        } ?: currentResults
    }

    private fun CatalogResponse.toCatalog(): Catalog = Catalog(
        name = name,
        description = description,
        specs = Specification(
            dimension = specs.dimension,
            weight = specs.weight,
            material = specs.material,
            color = specs.color
        ),
        images = images
    )

    companion object: KLogging()
}

