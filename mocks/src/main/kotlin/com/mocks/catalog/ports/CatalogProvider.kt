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

    private fun getProductSpecs(locale: Locale): Specification {
        return Specification(
            dimension = "30cm x 20cm x 10cm",
            weight = "2.5kg",
            material = getLocalizedMaterial(locale),
            color = getLocalizedColor(locale)
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
    val specs: Specification,
    val images: List<String>
)

data class Specification(
    val dimension: String,
    val weight: String,
    val material: String,
    val color: String
)

@JvmInline
value class ProductId(val productId: String)

