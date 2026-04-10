package com.mocks.availability.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "availability.interceptor")
data class AvailabilityInterceptorProperties(
    var enabled: Boolean = true,
    var latency: LatencyConfig = LatencyConfig(),
    var reliability: ReliabilityConfig = ReliabilityConfig()
) {
    data class LatencyConfig(
        var minMillis: Long = 0,
        var maxMillis: Long = 0
    )

    data class ReliabilityConfig(
        var failureRate: Double = 0.0,
        var errorStatusCode: Int = 500
    )
}

