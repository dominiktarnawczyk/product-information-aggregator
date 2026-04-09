package com.example.mocks

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController {

	@GetMapping("/test")
	fun test(): Map<String, String> = mapOf(
		"application" to "mocks",
		"message" to "Mocks app is running",
	)
}

