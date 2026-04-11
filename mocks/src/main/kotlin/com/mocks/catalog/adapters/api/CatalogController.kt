package com.mocks.catalog.adapters.api

import com.mocks.catalog.ports.Catalog
import com.mocks.catalog.ports.CatalogProvider
import com.mocks.catalog.ports.ProductId
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.Locale

@RestController
@RequestMapping("/api/catalog")
class CatalogController(
    private val catalogProvider: CatalogProvider
) {
    @GetMapping
    fun getCatalog(
        @RequestParam("product-id") productId: String,
        @RequestParam("market-code") marketCode: String
    ): CatalogResponse {
        return catalogProvider
            .catalog(ProductId(productId), Locale.forLanguageTag(marketCode))
            .toResponse()
    }

    private fun Catalog.toResponse() = CatalogResponse(
        name = name,
        description = description,
        specs = specs,
        images = images
    )
}

data class CatalogResponse(
    val name: String,
    val description: String,
    val specs: Map<String, String>,
    val images: List<String>
)
