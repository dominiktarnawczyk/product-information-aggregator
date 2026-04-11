package com.aggregator.ports

import com.aggregator.ports.models.Availability
import com.aggregator.ports.models.Catalog
import com.aggregator.ports.models.Customer
import com.aggregator.ports.models.Pricing

/**
 * Interface for information providers that fetch information from external services.
 * Each provider type is explicitly defined to preserve type information.
 */
interface InformationProvider<T> {
    suspend fun fetchData(productId: String, marketCode: String, customerId: String?): T?
    fun processResult(result: T?, currentResults: ProviderResults): ProviderResults
}

data class ProviderResults(
    val catalog: Catalog?,
    val pricing: Pricing?,
    val availability: Availability?,
    val customer: Customer?
) {
    companion object {
        fun initialProvidersResults() = ProviderResults(null, null, null, null)
    }
}
