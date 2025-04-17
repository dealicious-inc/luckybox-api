package com.dealicious.luckybox.security

import com.dealicious.luckybox.domain.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.core.user.OAuth2User
import java.util.Collections

class CustomOAuth2User(
    private val user: User,
    private val attributes: Map<String, Any>
) : OAuth2User {
    override fun getAuthorities(): Collection<GrantedAuthority> {
        return Collections.singletonList(SimpleGrantedAuthority("ROLE_USER"))
    }

    override fun getAttributes(): Map<String, Any> {
        return attributes
    }

    override fun getName(): String {
        return user.name
    }

    fun getEmail(): String {
        return user.email
    }

    fun getId(): Long? {
        return user.id
    }
} 