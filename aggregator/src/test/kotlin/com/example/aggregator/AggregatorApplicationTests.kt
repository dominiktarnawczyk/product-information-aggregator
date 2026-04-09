package com.example.aggregator

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest
@AutoConfigureMockMvc
class AggregatorApplicationTests {

	@Autowired
	lateinit var mockMvc: MockMvc

	@Test
	fun contextLoads() {
	}

	@Test
	fun `health endpoint returns up`() {
		mockMvc.get("/health")
			.andExpect {
				status { isOk() }
				jsonPath("$.application") { value("aggregator") }
				jsonPath("$.status") { value("UP") }
			}
	}
}

