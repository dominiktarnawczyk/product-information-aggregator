package com.aggregator.ports.models

data class CatalogResponse(
    val name: String,
    val description: String,
    val specs: SpecificationResponse,
    val images: List<String>
)

data class SpecificationResponse(
    val dimension: String,
    val weight: String,
    val material: String,
    val color: String
)