package com.aggregator.ports.locale

class InvalidLocaleException(locale: String) : IllegalArgumentException("Invalid locale: $locale")