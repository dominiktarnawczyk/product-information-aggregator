package com.aggregator.config

import com.aggregator.ports.locale.MessageLocalizer
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class LocaleConfig {

    @Bean
    fun messageLocalizer(messageSource: MessageSource): MessageLocalizer {
        return MessageLocalizer(messageSource)
    }
}
