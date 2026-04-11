package com.aggregator.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestClient
import java.time.Duration

@Configuration
class RestClientConfig(
    @param:Value("\${rest-client.connect-timeout-ms}")
    private val connectTimeout: Long = 500,

    @param:Value("\${rest-client.read-timeout-ms}")
    private val readTimeout: Long = 500
) {
    @Bean
    fun restClient(): RestClient {
        val factory = SimpleClientHttpRequestFactory()
        factory.setConnectTimeout(Duration.ofMillis(connectTimeout))
        factory.setReadTimeout(Duration.ofMillis(readTimeout))
        return RestClient.builder()
            .requestFactory(factory)
            .build()
    }
}
