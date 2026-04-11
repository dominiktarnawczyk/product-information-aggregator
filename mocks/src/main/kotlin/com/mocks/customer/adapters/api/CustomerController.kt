package com.mocks.customer.adapters.api

import com.mocks.customer.ports.Customer
import com.mocks.customer.ports.CustomerId
import com.mocks.customer.ports.CustomerProvider
import com.mocks.customer.ports.Preferences
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.Locale

@RestController
@RequestMapping("/api/customer")
class CustomerController(
    private val customerProvider: CustomerProvider
) {
    @GetMapping
    fun getCustomer(
        @RequestParam("market-code") marketCode: String,
        @RequestParam("customer-id", required = false) customerId: String?
    ): CustomerResponse {
        return customerProvider
            .customer(
                customerId?.let { CustomerId(it) },
                Locale.forLanguageTag(marketCode)
            )
            .toResponse()
    }

    private fun Customer.toResponse() = CustomerResponse(
        customerSegment = customerSegment,
        preferences = preferences?.toResponse()
    )

    private fun Preferences.toResponse() = PreferencesResponse(
        communicationChannel = communicationChannel,
        newsletterSubscription = newsletterSubscription,
        loyaltyProgramMember = loyaltyProgramMember
    )
}

data class CustomerResponse(
    val customerSegment: String,
    val preferences: PreferencesResponse?
)

data class PreferencesResponse(
    val communicationChannel: String,
    val newsletterSubscription: Boolean,
    val loyaltyProgramMember: Boolean
)

