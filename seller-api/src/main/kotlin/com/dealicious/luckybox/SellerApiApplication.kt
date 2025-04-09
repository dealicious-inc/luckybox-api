package com.dealicious.luckybox

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EnableJpaAuditing
@SpringBootApplication
//@EntityScan(basePackages = ["com.dealicious.luckybox"])
//@EnableJpaRepositories(basePackages = ["com.dealicious.luckybox"])
class SellerApiApplication

fun main(args: Array<String>) {
	runApplication<SellerApiApplication>(*args)
}
