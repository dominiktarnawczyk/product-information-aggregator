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
            val results = providersCatalog.providers()
                .map { provider ->
                    providerCall(provider, productId, marketLanguage, customerId)
                }
                .awaitAll()
                .fold(initialProvidersResults()) { acc, (provider, result) ->
                    fillingProviderResults(provider, result, acc)
                }
            results.catalog?.let {
                ProductInformation(
                    catalog = it,
                    pricing = results.pricing,
                    availability = results.availability,
                    customer = results.customer
                )
            } ?: throw IllegalStateException("Catalog provider must return a result")
        }
    }

    private fun fillingProviderResults(
        provider: InformationProvider<*>,
        result: Any?,
        acc: ProviderResults
    ): ProviderResults = when (provider) {
        is CatalogProvider -> {
            (provider as InformationProvider<CatalogResponse>)
                .processResult(result as CatalogResponse, acc)
        }

        is PricingProvider -> {
            (provider as InformationProvider<PricingResponse>)
                .processResult(result as PricingResponse?, acc)
        }

        is AvailabilityProvider -> {
            (provider as InformationProvider<AvailabilityResponse>)
                .processResult(result as AvailabilityResponse?, acc)
        }

        is CustomerProvider -> {
            (provider as InformationProvider<CustomerResponse>)
                .processResult(result as CustomerResponse?, acc)
        }

        else -> {
            logger.warn { "Unknown provider type: ${provider::class.simpleName}" }
            acc
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

    companion object : KLogging()
}

