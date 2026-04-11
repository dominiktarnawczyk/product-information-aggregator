package com.aggregator.ports

import com.aggregator.ports.providers.AvailabilityProvider
import com.aggregator.ports.providers.CatalogProvider
import com.aggregator.ports.providers.CustomerProvider
import com.aggregator.ports.providers.PricingProvider

class ProvidersCatalog(
    private val catalogProvider: CatalogProvider,
    private val pricingProvider: PricingProvider,
    private val availabilityProvider: AvailabilityProvider,
    private val customerProvider: CustomerProvider
) {
    fun providers(): Collection<InformationProvider<*>> = listOf(
        catalogProvider,
        pricingProvider,
        availabilityProvider,
        customerProvider
    )
}
