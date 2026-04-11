package com.aggregator.ports.providers

import com.aggregator.adapters.clients.AvailabilityClient
import com.aggregator.ports.InformationProvider
import com.aggregator.ports.ProviderResults
import com.aggregator.ports.models.Availability
import com.aggregator.ports.models.AvailabilityResponse
import mu.KLogging

class AvailabilityProvider(
    private val availabilityClient: AvailabilityClient
) : InformationProvider<AvailabilityResponse> {

    override suspend fun fetchData(productId: String, marketCode: String, customerId: String?): AvailabilityResponse? {
        logger.info { "Fetching availability data for productId: $productId, market: $marketCode" }
        return try {
            availabilityClient.getAvailability(productId, marketCode)
        } catch (e: Exception) {
            logger.warn { "Availability service failed - will return null" }
            null
        }
    }

    override fun processResult(result: AvailabilityResponse?, currentResults: ProviderResults): ProviderResults {
        return result?.let {
            currentResults.copy(availability = it.toAvailability())
        } ?: currentResults
    }

    private fun AvailabilityResponse.toAvailability(): Availability = Availability(
        stockLevel = stockLevel,
        warehouseLocation = warehouseLocation,
        expectedDelivery = expectedDelivery
    )

    companion object: KLogging()
}

