package com.mocks.config

import com.mocks.availability.config.AvailabilityInterceptor
import com.mocks.catalog.config.CatalogInterceptor
import com.mocks.customer.config.CustomerInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val availabilityInterceptor: AvailabilityInterceptor,
    private val catalogInterceptor: CatalogInterceptor,
    private val customerInterceptor: CustomerInterceptor
) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(availabilityInterceptor)
            .addPathPatterns("/api/availability/**")

        registry.addInterceptor(catalogInterceptor)
            .addPathPatterns("/api/catalog/**")

        registry.addInterceptor(customerInterceptor)
            .addPathPatterns("/api/customer/**")
    }
}