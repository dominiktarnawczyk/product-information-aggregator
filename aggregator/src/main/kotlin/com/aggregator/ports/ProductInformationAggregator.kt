package com.aggregator.ports

import com.aggregator.ports.ProviderResults.Companion.initialProvidersResults
import com.aggregator.ports.locale.LocaleValidator.Companion.validateLocale
import com.aggregator.ports.models.AvailabilityResponse
import com.aggregator.ports.models.CatalogResponse
import com.aggregator.ports.models.CustomerResponse
import com.aggregator.ports.models.ProductInformation
import com.aggregator.ports.models.PricingResponse
import com.aggregator.ports.providers.AvailabilityProvider
import com.aggregator.ports.providers.CatalogProvider
import com.aggregator.ports.providers.CustomerProvider
import com.aggregator.ports.providers.PricingProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class ProductInformationAggregator(
    private val providersCatalog: ProvidersCatalog
) {
    suspend fun aggregate(productId: String, marketLanguage: String, customerId: String?): ProductInformation {
        logger.info { "Aggregating product information for productId: $productId, market: $marketLanguage, customer: $customerId" }
        validateLocale(marketLanguage)
        return coroutineScope {
            providersCatalog.providers()
                .map { provider ->
                    providerCall(provider, productId, marketLanguage, customerId)
                }
                .awaitAll()
                .fold(initialProvidersResults()) { resultsAccumulator, (provider, result) ->
                    fillingProviderResults(provider, result, resultsAccumulator)
                }
                .let { results ->
                    aggregatedProductInformation(results)
                }
        }
    }

    private fun fillingProviderResults(
        provider: InformationProvider<*>,
        result: Any?,
        resultsAccumulator: ProviderResults
    ): ProviderResults = when (provider) {
        is CatalogProvider -> {
            (provider as InformationProvider<CatalogResponse>)
                .processResult(result as CatalogResponse, resultsAccumulator)
        }

        is PricingProvider -> {
            (provider as InformationProvider<PricingResponse>)
                .processResult(result as PricingResponse?, resultsAccumulator)
        }

        is AvailabilityProvider -> {
            (provider as InformationProvider<AvailabilityResponse>)
                .processResult(result as AvailabilityResponse?, resultsAccumulator)
        }

        is CustomerProvider -> {
            (provider as InformationProvider<CustomerResponse>)
                .processResult(result as CustomerResponse?, resultsAccumulator)
        }

        else -> {
            logger.warn { "Unknown provider type: ${provider::class.simpleName}" }
            resultsAccumulator
        }
    }

    private fun CoroutineScope.providerCall(
        provider: InformationProvider<*>,
        productId: String,
        marketCode: String,
        customerId: String?
    ): Deferred<Pair<InformationProvider<*>, Any?>> = async(Dispatchers.IO) {
        val result = provider.fetchData(productId, marketCode, customerId)
        provider to result
    }

    private fun aggregatedProductInformation(results: ProviderResults): ProductInformation {
        return ProductInformation(
            catalog = requireNotNull(results.catalog) {" Catalog data is missing - this is critical" },
            pricing = results.pricing,
            availability = results.availability,
            customer = results.customer
        )
    }

    companion object : KLogging()
}

