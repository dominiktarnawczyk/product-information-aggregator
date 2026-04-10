package com.mocks.pricing.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "pricing.interceptor")
data class PricingInterceptorProperties(
    var enabled: Boolean = true,
    var latency: LatencyConfig = LatencyConfig(),
    var reliability: ReliabilityConfig = ReliabilityConfig()
) {
    data class LatencyConfig(
        var minMillis: Long = 80,
        var maxMillis: Long = 80
    )

    data class ReliabilityConfig(
        var failureRate: Double = 0.005,
        var errorStatusCode: Int = 500
    )
}


