package com.mocks.customer.interceptors

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
        "customer.interceptor.enabled=true",
        "customer.interceptor.latency.min-millis=100",
        "customer.interceptor.latency.max-millis=200"
    ]
)
class CustomerInterceptorIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun `should apply latency to customer endpoint`() {
        val duration = measureTimeMillis {
            mockMvc.get("/api/customer") {
                param("market-code", "en-GB")
            }.andExpect {
                status { isOk() }
            }
        }

        // Should take at least 100ms due to interceptor
        assert(duration >= 100) { "Expected at least 100ms latency, got ${duration}ms" }
    }

    @Test
    fun `should apply latency to customer endpoint with customer-id`() {
        val duration = measureTimeMillis {
            mockMvc.get("/api/customer") {
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

