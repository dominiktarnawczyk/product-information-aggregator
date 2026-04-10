package com.mocks.customer.config

import com.mocks.customer.ports.CustomerProvider
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CustomerConfig {

    @Bean
    fun customerProvider(messageSource: MessageSource) = CustomerProvider(messageSource)
}

