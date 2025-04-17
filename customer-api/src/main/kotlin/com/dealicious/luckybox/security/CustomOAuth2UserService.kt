package com.dealicious.luckybox.security

import com.dealicious.luckybox.domain.User
import com.dealicious.luckybox.domain.UserRepository
import com.dealicious.luckybox.domain.Provider
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CustomOAuth2UserService(
    private val userRepository: UserRepository
) : DefaultOAuth2UserService() {

    @Transactional
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2User = super.loadUser(userRequest)
        val provider = Provider.valueOf(userRequest.clientRegistration.registrationId.uppercase())
        val attributes = oAuth2User.attributes

        val userInfo = when (provider) {
            Provider.KAKAO -> {
                val kakaoAccount = attributes["kakao_account"] as Map<*, *>
                val profile = kakaoAccount["profile"] as Map<*, *>
                mapOf(
                    "id" to (attributes["id"] as Number).toString(),
                    "name" to (profile["nickname"] as String),
                    "email" to (kakaoAccount["email"] as? String ?: "")
                )
            }
            Provider.NAVER -> {
                val response = attributes["response"] as Map<*, *>
                mapOf(
                    "id" to (response["id"] as String),
                    "name" to (response["name"] as String),
                    "email" to (response["email"] as String)
                )
            }
        }

        val user = userRepository.findByProviderAndProviderId(provider, userInfo["id"]!!)
            ?: User(
                email = userInfo["email"]!!,
                name = userInfo["name"]!!,
                provider = provider,
                providerId = userInfo["id"]!!
            )

        return CustomOAuth2User(userRepository.save(user), attributes)
    }
} 