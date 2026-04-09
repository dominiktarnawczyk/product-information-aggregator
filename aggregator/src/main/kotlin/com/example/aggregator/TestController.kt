package com.example.aggregator

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController {

	@GetMapping("/test")
	fun test(): Map<String, String> = mapOf(
		"application" to "aggregator",
		"message" to "Aggregator app is running",
	)
}

