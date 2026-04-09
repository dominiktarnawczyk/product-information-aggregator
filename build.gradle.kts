import org.gradle.api.plugins.JavaPluginExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "2.1.0" apply false
	kotlin("plugin.spring") version "2.1.0" apply false
	id("org.springframework.boot") version "3.5.13" apply false
	id("io.spring.dependency-management") version "1.1.7" apply false
}

group = "com.example"
version = "0.0.1"

repositories {
	mavenCentral()
}

subprojects {
	group = rootProject.group
	version = rootProject.version

	apply(plugin = "org.jetbrains.kotlin.jvm")
	apply(plugin = "org.jetbrains.kotlin.plugin.spring")
	apply(plugin = "io.spring.dependency-management")

	repositories {
		mavenCentral()
	}

	extensions.configure<JavaPluginExtension> {
		toolchain {
			languageVersion.set(JavaLanguageVersion.of(21))
		}
	}

	tasks.withType<KotlinCompile>().configureEach {
		compilerOptions {
			freeCompilerArgs.add("-Xjsr305=strict")
		}
	}

	tasks.withType<Test> {
		useJUnitPlatform()
	}
}
