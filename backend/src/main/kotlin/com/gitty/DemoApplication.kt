package com.gitty

import io.github.cdimascio.dotenv.Dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableAsync
@EnableFeignClients
class DemoApplication

fun main(args: Array<String>) {
	val dotenv = Dotenv.configure()
		.directory(".")
		.ignoreIfMissing()
		.load()

	dotenv.entries().forEach { entry ->
		System.setProperty(entry.key, entry.value)
	}

	runApplication<DemoApplication>(*args)
}
