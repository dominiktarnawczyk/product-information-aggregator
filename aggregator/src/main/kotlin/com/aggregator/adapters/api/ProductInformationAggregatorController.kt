package com.aggregator.adapters.api

import com.aggregator.ports.ProductInformationAggregator
import kotlinx.coroutines.withTimeout
import mu.KLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/products")
class ProductInformationAggregatorController(
    private val productInformationAggregator: ProductInformationAggregator,
    @param:Value("\${aggregator.endpoint.timeout-ms}")
    private val timeout: Long = 500,
) {

    @GetMapping
    suspend fun getProductInformation(
        @RequestParam("product-id") productId: String,
        @RequestParam("market-language") marketLanguage: String,
        @RequestParam("customer-id", required = false) customerId: String?
    ): ProductInformationResponse {
        logger.info { "Received request for product: $productId, market: $marketLanguage, customer: $customerId" }
        return withTimeout(timeout) {
            productInformationAggregator.aggregate(productId, marketLanguage, customerId)
        }.toResponse()
    }

    companion object : KLogging()
}

