package com.aggregator.config

import com.aggregator.adapters.clients.AvailabilityClient
import com.aggregator.adapters.clients.CatalogClient
import com.aggregator.adapters.clients.CustomerClient
import com.aggregator.adapters.clients.PricingClient
import com.aggregator.ports.providers.AvailabilityProvider
import com.aggregator.ports.providers.CatalogProvider
import com.aggregator.ports.providers.CustomerProvider
import com.aggregator.ports.providers.PricingProvider
import com.aggregator.ports.ProvidersCatalog
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class InformationProviderConfig {

    @Bean
    fun providersCatalog(
        catalogProvider: CatalogProvider,
        pricingProvider: PricingProvider,
        availabilityProvider: AvailabilityProvider,
        customerProvider: CustomerProvider
    ): ProvidersCatalog {
        return ProvidersCatalog(
            catalogProvider,
            pricingProvider,
            availabilityProvider,
            customerProvider
        )
    }

    @Bean
    fun catalogProvider(catalogClient: CatalogClient): CatalogProvider {
        return CatalogProvider(catalogClient)
    }

    @Bean
    fun availabilityProvider(availabilityClient: AvailabilityClient): AvailabilityProvider {
        return AvailabilityProvider(availabilityClient)
    }

    @Bean
    fun pricingProvider(pricingClient: PricingClient): PricingProvider {
        return PricingProvider(pricingClient)
    }

    @Bean
    fun customerProvider(customerClient: CustomerClient): CustomerProvider {
        return CustomerProvider(customerClient)
    }
}
