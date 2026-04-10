package com.mocks.availability.adapters.api

import com.mocks.availability.ports.Availability
import com.mocks.availability.ports.AvailabilityProvider
import com.mocks.availability.ports.ProductId
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.Locale

@RestController
@RequestMapping("/api/availability")
class AvailabilityController(
    private val availabilityProvider: AvailabilityProvider
) {
    @GetMapping
    fun getAvailability(
        @RequestParam("product-id") productId: String,
        @RequestParam("market-code") marketCode: String
    ): AvailabilityResponse {
        return availabilityProvider
            .availability(ProductId(productId), Locale.forLanguageTag(marketCode))
            .toResponse()
    }

    private fun Availability.toResponse() = AvailabilityResponse(
        stockLevel = stockLevel,
        warehouseLocation = warehouseLocation,
        expectedDelivery = expectedDelivery
    )
}

data class AvailabilityResponse(
    val stockLevel: Int,
    val warehouseLocation: String,
    val expectedDelivery: String
)
