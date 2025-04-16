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
        val token = getToken(request.code)
        val userInfo = getUserInfo(token)
        val user = saveOrUpdateUser(userInfo)
        return LoginResponse(jwtTokenProvider.createToken(user.email))
    }

    private fun getToken(code: String): String {
        val params = LinkedMultiValueMap<String, String>().apply {
            add("grant_type", "authorization_code")
            add("client_id", clientId)
            add("client_secret", clientSecret)
            add("code", code)
            add("state", "RANDOM_STATE")
            add("redirect_uri", redirectUri)
        }

        val response = restTemplate.postForObject(
            "https://nid.naver.com/oauth2.0/token",
            HttpEntity(params, HttpHeaders()),
            Map::class.java
        ) as Map<*, *>

        return response["access_token"] as String
    }

    private fun getUserInfo(accessToken: String): Map<String, String> {
        val headers = HttpHeaders().apply {
            set("Authorization", "Bearer $accessToken")
        }

        val response = restTemplate.exchange(
            "https://openapi.naver.com/v1/nid/me",
            org.springframework.http.HttpMethod.GET,
            HttpEntity(null, headers),
            Map::class.java
        ).body as Map<*, *>

        val userInfo = response["response"] as Map<*, *>

        return mapOf(
            "email" to (userInfo["email"] as String),
            "name" to (userInfo["name"] as String),
            "providerId" to (userInfo["id"] as String)
        )
    }

    private fun saveOrUpdateUser(userInfo: Map<String, String>): User {
        val user = userRepository.findByProviderAndProviderId(Provider.NAVER, userInfo["providerId"]!!)
            ?: User(
                email = userInfo["email"]!!,
                name = userInfo["name"]!!,
                provider = Provider.NAVER,
                providerId = userInfo["providerId"]!!
            )

        return userRepository.save(user)
    }
} 