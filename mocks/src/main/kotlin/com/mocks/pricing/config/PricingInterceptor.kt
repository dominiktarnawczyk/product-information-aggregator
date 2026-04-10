package com.mocks.pricing.config

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import mu.KLogging
import kotlin.random.Random

@Component
class PricingInterceptor(
    private val properties: PricingInterceptorProperties
) : HandlerInterceptor {


    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        if (!properties.enabled) {
            return true
        }

        simulateLatency()

        return simulateReliability(response)
    }

    private fun simulateLatency() {
        val minLatency = properties.latency.minMillis
        val maxLatency = properties.latency.maxMillis

        if (maxLatency > 0) {
            val delay = if (minLatency >= maxLatency) {
                maxLatency
            } else {
                Random.nextLong(minLatency, maxLatency)
            }

            if (delay > 0) {
                logger.debug { "Simulating latency: ${delay}ms" }
                Thread.sleep(delay)
            }
        }
    }

    private fun simulateReliability(response: HttpServletResponse): Boolean {
        val failureRate = properties.reliability.failureRate

        if (failureRate > 0 && Random.nextDouble() < failureRate) {
            val statusCode = properties.reliability.errorStatusCode
            logger.warn { "Simulating failure with status code: $statusCode" }

            response.status = statusCode
            response.contentType = "application/json"
            response.writer.write("""{"error": "Simulated failure", "statusCode": $statusCode}""")
            response.writer.flush()

            return false
        }

        return true
    }

    companion object : KLogging()
}


