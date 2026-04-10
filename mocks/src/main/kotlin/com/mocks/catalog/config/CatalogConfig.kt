package com.mocks.catalog.config

import com.mocks.catalog.ports.CatalogProvider
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CatalogConfig {

    @Bean
    fun catalogProvider(messageSource: MessageSource) = CatalogProvider(messageSource)
}

