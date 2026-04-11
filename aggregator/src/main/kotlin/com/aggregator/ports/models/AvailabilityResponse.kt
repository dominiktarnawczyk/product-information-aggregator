package com.aggregator.ports.models

data class AvailabilityResponse(
    val stockLevel: Int,
    val warehouseLocation: String,
    val expectedDelivery: String
)

