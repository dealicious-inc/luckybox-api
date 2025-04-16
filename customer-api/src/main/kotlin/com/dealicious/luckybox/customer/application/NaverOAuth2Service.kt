package com.dealicious.luckybox.customer.application

import com.dealicious.luckybox.domain.User
import com.dealicious.luckybox.domain.UserRepository
import com.dealicious.luckybox.domain.Provider
import com.dealicious.luckybox.customer.application.dto.LoginResponse
import com.dealicious.luckybox.customer.application.dto.OAuth2LoginRequest
import com.dealicious.luckybox.customer.security.JwtTokenProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestTemplate

@Service
class NaverOAuth2Service(
    private val userRepository: UserRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val restTemplate: RestTemplate
) : OAuth2Service {
    @Value("\${oauth2.naver.client-id}")
    private lateinit var clientId: String

    @Value("\${oauth2.naver.client-secret}")
    private lateinit var clientSecret: String

    @Value("\${oauth2.naver.redirect-uri}")
    private lateinit var redirectUri: String

    @Transactional
    override fun login(request: OAuth2LoginRequest): LoginResponse {
        val accessToken = getToken(request.code)
        val userInfo = getUserInfo(accessToken)
        val user = saveOrUpdateUser(userInfo)
        val token = jwtTokenProvider.createToken(user.email)
        return LoginResponse(token)
    }

    private fun getToken(code: String): String {
        val headers = HttpHeaders()
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8")

        val params: LinkedMultiValueMap<String, String> = LinkedMultiValueMap()
        params.add("grant_type", "authorization_code")
        params.add("client_id", clientId)
        params.add("client_secret", clientSecret)
        params.add("code", code)
        params.add("state", "RANDOM_STATE")

        val request = HttpEntity(params, headers)
        val response = restTemplate.exchange(
            "https://nid.naver.com/oauth2.0/token",
            org.springframework.http.HttpMethod.POST,
            request,
            Map::class.java
        )

        return response.body?.get("access_token") as String
    }

    private fun getUserInfo(accessToken: String): Map<String, String> {
        val headers = HttpHeaders()
        headers.add("Authorization", "Bearer $accessToken")
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8")

        val response = restTemplate.exchange(
            "https://openapi.naver.com/v1/nid/me",
            HttpMethod.GET,
            HttpEntity(null, headers),
            Map::class.java
        )

        val responseBody = response.body!!
        val profile = responseBody["response"] as Map<*, *>

        return mapOf(
            "id" to (profile["id"] as Long).toString(),
            "name" to (profile["name"] as String),
            "email" to (profile["email"] as? String ?: ""),
            "provider" to Provider.NAVER.name
        )
    }

    private fun saveOrUpdateUser(userInfo: Map<String, String>): User {
        val user = userRepository.findByProviderAndProviderId(Provider.NAVER, userInfo["id"]!!)
            ?: User(
                email = userInfo["email"]!!,
                name = userInfo["name"]!!,
                provider = Provider.NAVER,
                providerId = userInfo["id"]!!
            )

        return userRepository.save(user)
    }
} 