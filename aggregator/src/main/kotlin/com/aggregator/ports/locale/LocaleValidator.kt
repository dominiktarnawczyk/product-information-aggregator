package com.aggregator.ports.locale

import java.util.Locale

class LocaleValidator {
    companion object {
        fun validateLocale(marketLanguage: String) {
            if (marketLanguage.isBlank()) {
                throw InvalidLocaleException(marketLanguage)
            }

            val locale = Locale.forLanguageTag(marketLanguage)
            if (locale.language.isEmpty()) {
                throw InvalidLocaleException(marketLanguage)
            }

            val languagePattern = Regex("^[a-z]{2,3}$")
            if (!languagePattern.matches(locale.language)) {
                throw InvalidLocaleException(marketLanguage)
            }
        }
    }
}