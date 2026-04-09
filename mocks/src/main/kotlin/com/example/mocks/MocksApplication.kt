package com.example.mocks

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MocksApplication

fun main(args: Array<String>) {
	runApplication<MocksApplication>(*args)
}

