package com.aggregator.ports.providers

import com.aggregator.adapters.api.LocalizedMessageService
import com.aggregator.adapters.clients.AvailabilityClient
import com.aggregator.ports.InformationProvider
import com.aggregator.ports.ProviderResults
import com.aggregator.ports.models.Availability
import com.aggregator.ports.models.AvailabilityResponse
import mu.KLogging
import java.util.Locale

class AvailabilityProvider(
    private val availabilityClient: AvailabilityClient,
    private val localizedMessageService: LocalizedMessageService
) : InformationProvider<AvailabilityResponse> {

    override suspend fun fetchData(productId: String, marketCode: String, customerId: String?): AvailabilityResponse? {
        logger.info { "Fetching availability data for productId: $productId, market: $marketCode" }
        return try {
            availabilityClient.getAvailability(productId, marketCode)
        } catch (_: Exception) {
            stockUnknown(marketCode)
        }
    }

    override fun processResult(result: AvailabilityResponse?, currentResults: ProviderResults): ProviderResults {
        return result?.let {
            currentResults.copy(availability = it.toAvailability())
        } ?: currentResults
    }

    private fun stockUnknown(marketCode: String): AvailabilityResponse {
        val locale = Locale.forLanguageTag(marketCode)
        return AvailabilityResponse(
            stockLevel = -1,
            warehouseLocation = localizedMessageService.getMessage("fallback.unknown", locale),
            expectedDelivery = localizedMessageService.getMessage("fallback.unknown", locale)
        )
    }

    private fun AvailabilityResponse.toAvailability(): Availability = Availability(
        stockLevel = stockLevel,
        warehouseLocation = warehouseLocation,
        expectedDelivery = expectedDelivery
    )

    companion object: KLogging()
}
