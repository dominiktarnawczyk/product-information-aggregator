package com.aggregator.adapters.api

import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import java.util.Locale

@Component
class LocalizedMessageService(
    private val messageSource: MessageSource
) {
    fun getMessage(key: String, locale: Locale, vararg args: Any?): String {
        return messageSource.getMessage(key, args, locale)
    }

    fun getServiceUnavailableMessage(locale: Locale, cause: String? = null): String {
        return if (cause != null) {
            "${getMessage("error.service.unavailable", locale)}: $cause"
        } else {
            getMessage("error.service.unavailable", locale)
        }
    }

    fun getTimeoutMessage(locale: Locale): String {
        return getMessage("error.timeout", locale)
    }

    fun getCatalogCriticalMessage(locale: Locale): String {
        return getMessage("error.catalog.critical", locale)
    }
}

