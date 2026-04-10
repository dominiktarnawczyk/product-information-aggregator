package com.mocks.pricing.interceptors

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import kotlin.system.measureTimeMillis

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(
    properties = [
        "pricing.interceptor.enabled=true",
        "pricing.interceptor.latency.min-millis=100",
        "pricing.interceptor.latency.max-millis=200"
    ]
)
class PricingInterceptorIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun `should apply latency to pricing endpoint`() {
        val duration = measureTimeMillis {
            mockMvc.get("/api/pricing") {
                param("product-id", "TEST-123")
                param("market-code", "en-GB")
            }.andExpect {
                status { isOk() }
            }
        }

        // Should take at least 100ms due to interceptor
        assert(duration >= 100) { "Expected at least 100ms latency, got ${duration}ms" }
    }

    @Test
    fun `should apply latency to pricing endpoint with customer-id`() {
        val duration = measureTimeMillis {
            mockMvc.get("/api/pricing") {
                param("product-id", "TEST-123")
                param("market-code", "en-GB")
                param("customer-id", "CUSTOMER-456")
            }.andExpect {
                status { isOk() }
            }
        }

        // Should take at least 100ms due to interceptor
        assert(duration >= 100) { "Expected at least 100ms latency, got ${duration}ms" }
    }
}


