plugins {
	id("org.springframework.boot")
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.7.3")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
	testImplementation("org.mockito:mockito-core:5.10.0")
	testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

