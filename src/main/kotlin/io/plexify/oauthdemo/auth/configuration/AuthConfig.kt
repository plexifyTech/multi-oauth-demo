package io.plexify.oauthdemo.auth.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
data class AuthConfig(
    @Value("\${cors.allowed.origin}")
    val corsAllowedOrigin: String,
    @Value("\${github.client.id}")
    val githubClientId: String,
    @Value("\${github.client.secret}")
    val githubClientSecret: String,
    @Value("\${github.auth.redirect-url}")
    val githubRedirectUrl: String,
    @Value("\${aad.client.id}")
    val aadClientId: String,
    @Value("\${aad.client.secret}")
    val aadClientSecret: String,
    @Value("\${aad.client.tenant-id}")
    val aadTenantId: String,
    @Value("\${aad.auth.redirect-url}")
    val aadRedirectUrl: String,
    @Value("\${auth.failure-url}")
    val authFailureUrl: String,
    @Value("\${auth.logout-success-url}")
    val logoutSuccessUrl: String
)
