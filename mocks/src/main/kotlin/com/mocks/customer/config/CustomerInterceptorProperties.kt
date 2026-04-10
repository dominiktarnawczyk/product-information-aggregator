package com.mocks.customer.config
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "customer.interceptor")
data class CustomerInterceptorProperties(
    var enabled: Boolean = true,
    var latency: LatencyConfig = LatencyConfig(),
    var reliability: ReliabilityConfig = ReliabilityConfig()
) {
    data class LatencyConfig(
        var minMillis: Long = 60,
        var maxMillis: Long = 60
    )
    data class ReliabilityConfig(
        var failureRate: Double = 0.01,
        var errorStatusCode: Int = 500
    )
}
