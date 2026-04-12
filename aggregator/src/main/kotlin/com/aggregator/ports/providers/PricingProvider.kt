package com.aggregator.ports.providers

import com.aggregator.adapters.api.LocalizedMessageService
import com.aggregator.adapters.clients.PricingClient
import com.aggregator.ports.InformationProvider
import com.aggregator.ports.ProviderResults
import com.aggregator.ports.models.Pricing
import com.aggregator.ports.models.PricingResponse
import mu.KLogging
import java.util.Locale
import kotlin.String

class PricingProvider(
    private val pricingClient: PricingClient,
    private val localizedMessageService: LocalizedMessageService
) : InformationProvider<PricingResponse> {

    override suspend fun fetchData(productId: String, marketCode: String, customerId: String?): PricingResponse? {
        logger.info { "Fetching pricing data for productId: $productId, market: $marketCode, customer: $customerId" }
        return try {
            pricingClient.getPricing(productId, marketCode, customerId)
        } catch (_: Exception) {
            unavailablePriceResponse(marketCode)
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

    private fun unavailablePriceResponse(marketCode: String): PricingResponse {
        val locale = Locale.forLanguageTag(marketCode)
        val unavailable = localizedMessageService.getMessage("fallback.unavailable", locale)
        return PricingResponse(
            bestPrice = unavailable,
            customerDiscount = unavailable,
            finalPrice = unavailable
        )
    }

    companion object: KLogging()
}

