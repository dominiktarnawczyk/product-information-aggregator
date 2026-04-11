package com.aggregator.ports.models

data class PricingResponse(
    val bestPrice: String,
    val customerDiscount: String,
    val finalPrice: String
)
