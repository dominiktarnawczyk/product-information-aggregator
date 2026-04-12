package com.aggregator.ports.providers

class CatalogServiceUnrespondingException(message: String, cause: Throwable? = null) :
    RuntimeException(message, cause)