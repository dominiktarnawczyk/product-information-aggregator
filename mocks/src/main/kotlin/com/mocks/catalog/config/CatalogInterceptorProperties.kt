package com.mocks.catalog.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "catalog.interceptor")
data class CatalogInterceptorProperties(
    var enabled: Boolean = true,
    var latency: LatencyConfig = LatencyConfig(),
    var reliability: ReliabilityConfig = ReliabilityConfig()
) {
    data class LatencyConfig(
        var minMillis: Long = 50,
        var maxMillis: Long = 50
    )

    data class ReliabilityConfig(
        var failureRate: Double = 0.001,
        var errorStatusCode: Int = 500
    )
}

