package com.mocks.availability.interceptors

import com.mocks.availability.config.AvailabilityInterceptor
import com.mocks.availability.config.AvailabilityInterceptorProperties
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.system.measureTimeMillis

class AvailabilityInterceptorUnitTest {

    private lateinit var interceptor: AvailabilityInterceptor
    private lateinit var properties: AvailabilityInterceptorProperties
    private lateinit var request: HttpServletRequest
    private lateinit var response: HttpServletResponse
    private lateinit var handler: Any

    @BeforeEach
    fun setUp() {
        properties = AvailabilityInterceptorProperties()
        interceptor = AvailabilityInterceptor(properties)
        request = mock(HttpServletRequest::class.java)
        response = mock(HttpServletResponse::class.java)
        handler = Any()
    }

    @Test
    fun `should pass through when interceptor is disabled`() {
        properties.enabled = false

        val result = interceptor.preHandle(request, response, handler)

        assertTrue(result)
        verify(response, never()).status = anyInt()
    }

    @Test
    fun `should introduce latency when configured`() {
        properties.enabled = true
        properties.latency.minMillis = 100
        properties.latency.maxMillis = 200

        val duration = measureTimeMillis {
            interceptor.preHandle(request, response, handler)
        }

        assertTrue(duration >= 100, "Expected at least 100ms latency, got ${duration}ms")
    }

    @Test
    fun `should return true when no failure is simulated`() {
        properties.enabled = true
        properties.reliability.failureRate = 0.0

        val result = interceptor.preHandle(request, response, handler)

        assertTrue(result)
    }

    @Test
    fun `should return error response when failure rate is 100 percent`() {
        properties.enabled = true
        properties.reliability.failureRate = 1.0
        properties.reliability.errorStatusCode = 503

        val writer = StringWriter()
        `when`(response.writer).thenReturn(PrintWriter(writer))

        val result = interceptor.preHandle(request, response, handler)

        assertFalse(result)
        verify(response).status = 503
        verify(response).contentType = "application/json"
        assertTrue(writer.toString().contains("Simulated failure"))
    }

    @Test
    fun `should handle edge case when min latency equals max latency`() {
        properties.enabled = true
        properties.latency.minMillis = 100
        properties.latency.maxMillis = 100

        val duration = measureTimeMillis {
            interceptor.preHandle(request, response, handler)
        }

        assertTrue(duration >= 100, "Expected at least 100ms latency, got ${duration}ms")
    }
}

