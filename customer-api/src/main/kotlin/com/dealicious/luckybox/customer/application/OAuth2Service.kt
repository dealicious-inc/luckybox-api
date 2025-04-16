package com.dealicious.luckybox.customer.application

import com.dealicious.luckybox.customer.application.dto.LoginResponse
import com.dealicious.luckybox.customer.application.dto.OAuth2LoginRequest

interface OAuth2Service {
    fun login(request: OAuth2LoginRequest): LoginResponse
} 