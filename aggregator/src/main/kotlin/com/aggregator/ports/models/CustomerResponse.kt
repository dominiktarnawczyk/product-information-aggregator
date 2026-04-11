package com.aggregator.ports.models

data class CustomerResponse(
    val customerSegment: String,
    val preferences: PreferencesResponse?
)

data class PreferencesResponse(
    val communicationChannel: String,
    val newsletterSubscription: Boolean,
    val loyaltyProgramMember: Boolean
)