package com.aggregator.adapters.api

import com.aggregator.ports.models.Availability
import com.aggregator.ports.models.Catalog
import com.aggregator.ports.models.Customer
import com.aggregator.ports.models.Preferences
import com.aggregator.ports.models.Pricing
import com.aggregator.ports.models.ProductInformation
import com.aggregator.ports.models.Specification

fun ProductInformation.toResponse(): ProductInformationResponse {
    return ProductInformationResponse(
        catalog = catalog.toResponse(),
        pricing = pricing?.toResponse(),
        availability = availability?.toResponse(),
        customer = customer?.toResponse()
    )
}

fun Catalog.toResponse(): CatalogResponse {
    return CatalogResponse(
        name = name,
        description = description,
        specs = specs.toResponse(),
        images = images
    )
}

fun Specification.toResponse(): SpecificationResponse {
    return SpecificationResponse(
        dimension = dimension,
        weight = weight,
        material = material,
        color = color
    )
}

fun Pricing.toResponse(): PricingResponse {
    return PricingResponse(
        bestPrice = bestPrice,
        customerDiscount = customerDiscount,
        finalPrice = finalPrice
    )
}

fun Availability.toResponse(): AvailabilityResponse {
    return AvailabilityResponse(
        stockLevel = stockLevel,
        warehouseLocation = warehouseLocation,
        expectedDelivery = expectedDelivery
    )
}

fun Customer.toResponse(): CustomerResponse {
    return CustomerResponse(
        customerSegment = customerSegment,
        preferences = preferences?.toResponse()
    )
}

fun Preferences.toResponse(): PreferencesResponse {
    return PreferencesResponse(
        communicationChannel = communicationChannel,
        newsletterSubscription = newsletterSubscription,
        loyaltyProgramMember = loyaltyProgramMember
    )
}

