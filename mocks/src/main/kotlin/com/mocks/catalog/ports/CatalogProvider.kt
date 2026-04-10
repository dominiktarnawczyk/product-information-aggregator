package com.mocks.catalog.ports

import org.springframework.context.MessageSource
import java.util.Locale

class CatalogProvider(
    private val messageSource: MessageSource
) {
    fun catalog(productId: ProductId, locale: Locale): Catalog {
        return Catalog(
            name = getProductName(productId, locale),
            description = getProductDescription(locale),
            specs = getProductSpecs(locale),
            images = getProductImages(productId)
        )
    }

    private fun getProductName(productId: ProductId, locale: Locale): String {
        return messageSource.getMessage(
            "catalog.product.name",
            arrayOf(productId.productId),
            locale
        )
    }

    private fun getProductDescription(locale: Locale): String {
        return messageSource.getMessage(
            "catalog.product.description",
            null,
            locale
        )
    }

    private fun getProductSpecs(locale: Locale): Map<String, String> {
        return mapOf(
            messageSource.getMessage("catalog.specs.dimensions", null, locale) to "30cm x 20cm x 10cm",
            messageSource.getMessage("catalog.specs.weight", null, locale) to "2.5kg",
            messageSource.getMessage("catalog.specs.material", null, locale) to getLocalizedMaterial(locale),
            messageSource.getMessage("catalog.specs.color", null, locale) to getLocalizedColor(locale)
        )
    }

    private fun getLocalizedMaterial(locale: Locale): String {
        return messageSource.getMessage(
            "catalog.specs.material.value",
            null,
            locale
        )
    }

    private fun getLocalizedColor(locale: Locale): String {
        return messageSource.getMessage(
            "catalog.specs.color.value",
            null,
            locale
        )
    }

    private fun getProductImages(productId: ProductId): List<String> {
        val hash = productId.productId.hashCode()
        return listOf(
            "https://images.example.com/${productId.productId}/image-${hash % 5}.jpg",
            "https://images.example.com/${productId.productId}/image-${(hash + 1) % 5}.jpg",
            "https://images.example.com/${productId.productId}/image-${(hash + 2) % 5}.jpg"
        )
    }
}

data class Catalog(
    val name: String,
    val description: String,
    val specs: Map<String, String>,
    val images: List<String>
)

data class ProductId(val productId: String)

