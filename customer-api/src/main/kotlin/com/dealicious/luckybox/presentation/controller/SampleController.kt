package com.dealicious.luckybox.presentation.controller

import com.dealicious.luckybox.domain.User
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/sample")
class SampleController {
    @GetMapping("/hello")
    fun hello(authentication: Authentication): ResponseEntity<Map<String, String>> {
        val user = authentication.principal as User
        return ResponseEntity.ok(mapOf("message" to "Hello, ${user.name}!"))
    }
} 