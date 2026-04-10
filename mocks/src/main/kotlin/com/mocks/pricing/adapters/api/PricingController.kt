package com.mocks.pricing.adapters.api

import com.mocks.pricing.ports.Pricing
import com.mocks.pricing.ports.PricingProvider
import com.mocks.pricing.ports.ProductId
import com.mocks.pricing.ports.CustomerId
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.Locale

@RestController
@RequestMapping("/api/pricing")
class PricingController(
    private val pricingProvider: PricingProvider
) {
    @GetMapping
    fun getPricing(
        @RequestParam("product-id") productId: String,
        @RequestParam("market-code") marketCode: String,
        @RequestParam("customer-id", required = false) customerId: String?
    ): PricingResponse {
        return pricingProvider
            .pricing(
                ProductId(productId),
                customerId?.let { CustomerId(it) },
                Locale.forLanguageTag(marketCode)
            )
            .toResponse()
    }

    private fun Pricing.toResponse() = PricingResponse(
        bestPrice = bestPrice,
        customerDiscount = customerDiscount,
        finalPrice = finalPrice
    )
}

data class PricingResponse(
    val bestPrice: String,
    val customerDiscount: String,
    val finalPrice: String
)


