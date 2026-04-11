package com.aggregator.ports.models

data class ProductInformation(
    val catalog: Catalog,
    val pricing: Pricing?,
    val availability: Availability?,
    val customer: Customer?
)

data class Catalog(
    val name: String,
    val description: String,
    val specs: Specification,
    val images: List<String>
)

data class Specification(
    val dimension: String,
    val weight: String,
    val material: String,
    val color: String
)

data class Pricing(
    val bestPrice: String,
    val customerDiscount: String,
    val finalPrice: String,
)

data class Availability(
    val stockLevel: Int?,
    val warehouseLocation: String?,
    val expectedDelivery: String?,
)

data class Customer(
    val customerSegment: String,
    val preferences: Preferences?
)

data class Preferences(
    val communicationChannel: String,
    val newsletterSubscription: Boolean,
    val loyaltyProgramMember: Boolean
)