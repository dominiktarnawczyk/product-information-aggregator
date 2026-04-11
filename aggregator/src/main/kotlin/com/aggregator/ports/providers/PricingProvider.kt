package com.aggregator.ports.providers

import com.aggregator.adapters.clients.PricingClient
import com.aggregator.ports.InformationProvider
import com.aggregator.ports.ProviderResults
import com.aggregator.ports.models.Pricing
import com.aggregator.ports.models.PricingResponse
import mu.KLogging

class PricingProvider(
    private val pricingClient: PricingClient
) : InformationProvider<PricingResponse> {

    override suspend fun fetchData(productId: String, marketCode: String, customerId: String?): PricingResponse? {
        logger.info { "Fetching pricing data for productId: $productId, market: $marketCode, customer: $customerId" }
        return try {
            pricingClient.getPricing(productId, marketCode, customerId)
        } catch (e: Exception) {
            logger.warn { "Pricing service failed - will return null" }
            null
        }
    }

    override fun processResult(result: PricingResponse?, currentResults: ProviderResults): ProviderResults {
        return result?.let {
            currentResults.copy(pricing = it.toPricing())
        } ?: currentResults
    }

    private fun PricingResponse.toPricing(): Pricing = Pricing(
        bestPrice = bestPrice,
        customerDiscount = customerDiscount,
        finalPrice = finalPrice
    )

    companion object: KLogging()
}

