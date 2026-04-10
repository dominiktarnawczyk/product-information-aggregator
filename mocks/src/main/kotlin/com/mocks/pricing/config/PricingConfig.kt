package com.mocks.pricing.config

import com.mocks.pricing.ports.PricingProvider
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PricingConfig {

    @Bean
    fun pricingProvider(messageSource: MessageSource) = PricingProvider(messageSource)
}


