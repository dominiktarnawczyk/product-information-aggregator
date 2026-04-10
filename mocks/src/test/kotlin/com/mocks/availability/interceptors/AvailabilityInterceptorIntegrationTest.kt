package com.mocks.availability.interceptors

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
        "availability.interceptor.enabled=true",
        "availability.interceptor.latency.min-millis=100",
        "availability.interceptor.latency.max-millis=200"
    ]
)
class AvailabilityInterceptorIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun `should apply latency to availability endpoint`() {
        val duration = measureTimeMillis {
            mockMvc.get("/api/availability") {
                param("product-id", "TEST-123")
                param("market-code", "en-GB")
            }.andExpect {
                status { isOk() }
            }
        }

        assert(duration >= 100) {
            "Expected at least 100ms latency, but got ${duration}ms"
        }
    }
}

