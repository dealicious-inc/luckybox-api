package com.dealicious.luckybox.customer.application.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class NaverUserInfoResponse(
    val resultcode: String,
    val message: String,
    val response: Response
) {
    data class Response(
        val id: String,
        @JsonProperty("profile_image")
        val profileImage: String,
        val email: String,
        val mobile: String,
        @JsonProperty("mobile_e164")
        val mobileE164: String,
        val name: String
    )
}
