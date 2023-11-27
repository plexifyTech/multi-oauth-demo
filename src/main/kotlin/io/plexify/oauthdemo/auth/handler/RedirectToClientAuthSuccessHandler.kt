package io.plexify.oauthdemo.auth.handler

import io.plexify.oauthdemo.auth.configuration.AuthConfig
import io.plexify.oauthdemo.auth.configuration.Config.AZURE_ACTIVE_DIRECTORY
import io.plexify.oauthdemo.auth.configuration.Config.GITHUB
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler
import reactor.core.publisher.Mono
import java.net.URI

class RedirectToClientAuthSuccessHandler(
    private val authConfig: AuthConfig
) : RedirectServerAuthenticationSuccessHandler() {
    override fun onAuthenticationSuccess(
        webFilterExchange: WebFilterExchange,
        authentication: Authentication
    ): Mono<Void> {
        setRedirectLocation(webFilterExchange)
        return super.onAuthenticationSuccess(webFilterExchange, authentication)
    }

    private fun setRedirectLocation(webFilterExchange: WebFilterExchange) {
        val path = webFilterExchange.exchange.request.path.value()
        try {
            val clientRegistrationId = path.split("/").last()
            val clientRedirectUrl = clientRedirectUrl(clientRegistrationId)
            this.setLocation(URI.create(clientRedirectUrl))
        } catch (e: Exception) {
            this.setLocation(URI.create(authConfig.authFailureUrl))
        }
    }

    private fun clientRedirectUrl(clientRegistrationId: String): String {
        return when (clientRegistrationId) {
            GITHUB -> authConfig.githubRedirectUrl
            AZURE_ACTIVE_DIRECTORY -> authConfig.aadRedirectUrl
            else -> throw IllegalStateException("onAuthenticationSuccess triggered with unsupported Client-Registration")
        }
    }

}