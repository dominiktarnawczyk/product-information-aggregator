package com.mocks.availability.ports

import org.springframework.context.MessageSource
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class AvailabilityProvider(
    private val messageSource: MessageSource
) {
    fun availability(productId: ProductId, locale: Locale): Availability {
        return Availability(
            stockLevel = productId.hashCode() % 1000,
            warehouseLocation = getWarehouseLocation(locale),
            expectedDelivery = getExpectedDelivery(locale)
        )
    }

    private fun getWarehouseLocation(locale: Locale): String {
        return localizedWarehouseLocation(locale)
    }

    private fun getExpectedDelivery(locale: Locale): String {
        return localizedDeliveryDate(locale)
            .toLong()
            .let { LocalDate.now().plusDays(it) }
            .format(localizedDateFormat(locale))
    }

    private fun localizedWarehouseLocation(locale: Locale): String {
        return messageSource.getMessage(
            "availability.warehouse.location",
            null,
            locale
        )
    }

    private fun localizedDeliveryDate(locale: Locale): String {
        return messageSource.getMessage(
            "availability.delivery.days",
            null,
            "3",
            locale
        ) ?: "3"
    }

    private fun localizedDateFormat(locale: Locale): DateTimeFormatter {
        val dateFormat = messageSource.getMessage(
            "availability.date.format",
            null,
            DEFAULT_DATE_FORMAT,
            locale
        ) ?: DEFAULT_DATE_FORMAT
        return DateTimeFormatter.ofPattern(dateFormat, locale)
    }

    companion object {
        private const val DEFAULT_DATE_FORMAT = "yyyy-MM-dd"
    }
}

data class Availability(
    val stockLevel: Int,
    val warehouseLocation: String,
    val expectedDelivery: String
)

data class ProductId(val productId: String)
