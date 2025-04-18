package com.dealicious.luckybox

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@SpringBootApplication
class SellerApiApplication

fun main(args: Array<String>) {
	runApplication<SellerApiApplication>(*args)
}
