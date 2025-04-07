package com.dealicious.luckybox

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LuckyboxApplication

fun main(args: Array<String>) {
	runApplication<LuckyboxApplication>(*args)
}
