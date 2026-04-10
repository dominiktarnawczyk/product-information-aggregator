package com.mocks.availability.config

import com.mocks.availability.ports.AvailabilityProvider
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AvailabilityConfig {

    @Bean
    fun availabilityProvider(messageSource: MessageSource) = AvailabilityProvider(messageSource)
}