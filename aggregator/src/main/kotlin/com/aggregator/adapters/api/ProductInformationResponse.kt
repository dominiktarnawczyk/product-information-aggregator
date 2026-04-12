package com.aggregator.adapters.api

data class ProductInformationResponse(
    val catalog: CatalogResponse,
    val pricing: PricingResponse?,
    val availability: AvailabilityResponse?,
    val customer: CustomerResponse?
)

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

data class PricingResponse(
    val bestPrice: String,
    val customerDiscount: String,
    val finalPrice: String
)

data class AvailabilityResponse(
    val stockLevel: Int?,
    val warehouseLocation: String?,
    val expectedDelivery: String?
)

data class CustomerResponse(
    val customerSegment: String,
    val preferences: PreferencesResponse?
)

data class PreferencesResponse(
    val communicationChannel: String,
    val newsletterSubscription: Boolean,
    val loyaltyProgramMember: Boolean
)

