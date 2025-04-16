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
class KakaoOAuth2Service(
    private val userRepository: UserRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val restTemplate: RestTemplate
) : OAuth2Service {
    @Value("\${oauth2.kakao.client-id}")
    private lateinit var clientId: String

    @Value("\${oauth2.kakao.redirect-uri}")
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
            add("redirect_uri", redirectUri)
            add("code", code)
        }

        val response = restTemplate.postForObject(
            "https://kauth.kakao.com/oauth/token",
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
            "https://kapi.kakao.com/v2/user/me",
            org.springframework.http.HttpMethod.GET,
            HttpEntity(null, headers),
            Map::class.java
        ).body as Map<*, *>

        val kakaoAccount = response["kakao_account"] as Map<*, *>
        val profile = kakaoAccount["profile"] as Map<*, *>

        return mapOf(
            "email" to (kakaoAccount["email"] as String),
            "name" to (profile["nickname"] as String),
            "providerId" to (response["id"].toString())
        )
    }

    private fun saveOrUpdateUser(userInfo: Map<String, String>): User {
        val user = userRepository.findByProviderAndProviderId(Provider.KAKAO, userInfo["providerId"]!!)
            ?: User(
                email = userInfo["email"]!!,
                name = userInfo["name"]!!,
                provider = Provider.KAKAO,
                providerId = userInfo["providerId"]!!
            )

        return userRepository.save(user)
    }
} 