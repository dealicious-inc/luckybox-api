package com.dealicious.luckybox.presentation.controller

import com.dealicious.luckybox.customer.application.dto.LoginResponse
import com.dealicious.luckybox.customer.application.dto.OAuth2LoginRequest
import com.dealicious.luckybox.customer.application.KakaoOAuth2Service
import com.dealicious.luckybox.customer.application.NaverOAuth2Service
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/api/v1/auth")
class OAuth2Controller(
    private val kakaoOAuth2Service: KakaoOAuth2Service,
    private val naverOAuth2Service: NaverOAuth2Service
) {
    @Value("\${oauth2.kakao.client-id}")
    private lateinit var kakaoClientId: String

    @Value("\${oauth2.kakao.redirect-uri}")
    private lateinit var kakaoRedirectUri: String

    @Value("\${oauth2.naver.client-id}")
    private lateinit var naverClientId: String

    @Value("\${oauth2.naver.redirect-uri}")
    private lateinit var naverRedirectUri: String

    @GetMapping("/kakao")
    fun kakaoLogin(): String {
        val url = "https://kauth.kakao.com/oauth/authorize?" +
                "client_id=$kakaoClientId&" +
                "redirect_uri=$kakaoRedirectUri&" +
                "response_type=code"
        return "redirect:$url"
    }

    @GetMapping("/naver")
    fun naverLogin(): String {
        val url = "https://nid.naver.com/oauth2.0/authorize?" +
                "client_id=$naverClientId&" +
                "redirect_uri=$naverRedirectUri&" +
                "response_type=code&" +
                "state=RANDOM_STATE"
        return "redirect:$url"
    }

    @GetMapping("/kakao/login")
    @ResponseBody
    fun kakaoLoginCallback(@RequestParam code: String): LoginResponse {
        return kakaoOAuth2Service.login(OAuth2LoginRequest(code))
    }

    @GetMapping("/naver/login")
    @ResponseBody
    fun naverLoginCallback(@RequestParam code: String): LoginResponse {
        return naverOAuth2Service.login(OAuth2LoginRequest(code))
    }
} 