package com.mocks.config

import com.mocks.availability.config.AvailabilityInterceptor
import com.mocks.catalog.config.CatalogInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val availabilityInterceptor: AvailabilityInterceptor,
    private val catalogInterceptor: CatalogInterceptor
) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(availabilityInterceptor)
            .addPathPatterns("/api/availability/**")

        registry.addInterceptor(catalogInterceptor)
            .addPathPatterns("/api/catalog/**")
    }
}