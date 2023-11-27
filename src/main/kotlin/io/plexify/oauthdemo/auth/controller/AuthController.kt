package io.plexify.oauthdemo.auth.controller

import io.plexify.oauthdemo.auth.configuration.Config.AZURE_ACTIVE_DIRECTORY
import io.plexify.oauthdemo.auth.configuration.Config.GITHUB
import io.plexify.oauthdemo.auth.dto.CsrfTokenResponse
import io.plexify.oauthdemo.auth.dto.LoginStateResponse
import io.plexify.oauthdemo.auth.service.CsrfTokenExtractorService
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/auth")
class AuthController(
    private val csrfTokenService: CsrfTokenExtractorService
) {

    @GetMapping("/aad/check")
    fun checkMsLogin(
        @RegisteredOAuth2AuthorizedClient(AZURE_ACTIVE_DIRECTORY) client: OAuth2AuthorizedClient
    ): Mono<LoginStateResponse> {
        return Mono.just(LoginStateResponse(loginActive = true))
    }

    @GetMapping("/github/check")
    fun checkGithubLogin(
        @RegisteredOAuth2AuthorizedClient(GITHUB) client: OAuth2AuthorizedClient
    ): Mono<LoginStateResponse> {
        return Mono.just(LoginStateResponse(loginActive = true))
    }

    @GetMapping("/github/token", "aad/token")
    fun getCsrfToken(request: ServerHttpRequest): Mono<CsrfTokenResponse> {
        return csrfTokenService.extractTokenFromCookie(request)
    }

}