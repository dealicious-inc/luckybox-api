package com.dealicious.luckybox.customer.application

import com.dealicious.luckybox.domain.User
import com.dealicious.luckybox.domain.UserRepository
import com.dealicious.luckybox.domain.Provider
import com.dealicious.luckybox.customer.application.dto.LoginResponse
import com.dealicious.luckybox.customer.application.dto.OAuth2LoginRequest
import com.dealicious.luckybox.customer.application.dto.KakaoUserInfoResponse
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
        params.add("redirect_uri", redirectUri)
        params.add("code", code)

        val request = HttpEntity(params, headers)
        val response = restTemplate.exchange(
            "https://kauth.kakao.com/oauth/token",
            HttpMethod.POST,
            request,
            Map::class.java
        )

        return response.body?.get("access_token") as String
    }

    private fun getUserInfo(accessToken: String): Map<String, String> {
        val headers = HttpHeaders()
        headers.add("Authorization", "Bearer $accessToken")
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8")

        val request = HttpEntity(null, headers)
        val response = restTemplate.exchange(
            "https://kapi.kakao.com/v2/user/me",
            HttpMethod.GET,
            request,
            KakaoUserInfoResponse::class.java
        )

        val userInfo = response.body!!
        return mapOf(
            "id" to userInfo.id.toString(),
            "name" to userInfo.properties.nickname,
            "profileImageUrl" to userInfo.properties.profileImage,
            "email" to "",  // 이메일은 별도 동의가 필요하므로 빈 문자열로 처리
            "provider" to Provider.KAKAO.name
        )
    }

    private fun saveOrUpdateUser(userInfo: Map<String, String>): User {
        val user = userRepository.findByProviderAndProviderId(Provider.KAKAO, userInfo["id"]!!)
            ?: User(
                email = userInfo["email"]!!,
                name = userInfo["name"]!!,
                profileImageUrl = userInfo["profileImageUrl"],
                provider = Provider.KAKAO,
                providerId = userInfo["id"]!!
            )

        return userRepository.save(user)
    }
} 