package com.mocks.pricing.ports

import org.springframework.context.MessageSource
import java.math.BigDecimal
import java.math.RoundingMode
import java.math.RoundingMode.HALF_UP
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.absoluteValue

class PricingProvider(
    private val messageSource: MessageSource
) {
    fun pricing(productId: ProductId, customerId: CustomerId?, locale: Locale): Pricing {
        val basePrice = calculateBasePrice(productId)
        val customerDiscount = calculateCustomerDiscount(customerId)
        val finalPrice = basePrice
            .multiply(BigDecimal.ONE.subtract(customerDiscount))
            .setScale(2, HALF_UP)

        return Pricing(
            bestPrice = formatPrice(basePrice, locale),
            customerDiscount = formatPercentage(customerDiscount, locale),
            finalPrice = formatPrice(finalPrice, locale)
        )
    }

    private fun calculateBasePrice(productId: ProductId): BigDecimal {
        // Generate price based on product ID hash
        val hash = productId.productId.hashCode().toLong()
        val priceValue = 50 + (hash.absoluteValue % 950)
        return BigDecimal(priceValue).setScale(2, HALF_UP)
    }

    private fun calculateCustomerDiscount(customerId: CustomerId?): BigDecimal {
        if (customerId == null) return BigDecimal.ZERO

        val hash = customerId.customerId.hashCode().toLong()
        val discountPercentage = hash.absoluteValue % 21
        return BigDecimal(discountPercentage).divide(BigDecimal(100), 4, HALF_UP)
    }

    private fun formatPrice(price: BigDecimal, locale: Locale): String {
        val currencyCode = getLocalizedCurrencyCode(locale)
        val numberFormat = NumberFormat.getCurrencyInstance(locale)

        if (currencyCode.isNotEmpty()) {
            try {
                val currency = java.util.Currency.getInstance(currencyCode)
                numberFormat.currency = currency
            } catch (e: IllegalArgumentException) { }
        }
        
        return numberFormat.format(price)
    }

    private fun formatPercentage(discount: BigDecimal, locale: Locale): String {
        val percentage = discount.multiply(BigDecimal(100)).setScale(0, HALF_UP)
        return messageSource.getMessage(
            "pricing.discount.format",
            arrayOf(percentage.toString()),
            "$percentage%",
            locale
        ) ?: "$percentage%"
    }

    private fun getLocalizedCurrencyCode(locale: Locale): String {
        return messageSource.getMessage(
            "pricing.currency.code",
            null,
            "EUR",
            locale
        ) ?: "EUR"
    }
}

data class Pricing(
    val bestPrice: String,
    val customerDiscount: String,
    val finalPrice: String
)

@JvmInline
value class ProductId(val productId: String)

@JvmInline
value class CustomerId(val customerId: String)


