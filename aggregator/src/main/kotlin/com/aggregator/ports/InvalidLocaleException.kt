package com.aggregator.ports

class InvalidLocaleException(locale: String) : IllegalArgumentException("Invalid locale: $locale")