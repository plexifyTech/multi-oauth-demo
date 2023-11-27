package io.plexify.oauthdemo.api.controller

import io.plexify.oauthdemo.api.dto.FooResponse
import io.plexify.oauthdemo.auth.configuration.Config.AZURE_ACTIVE_DIRECTORY
import io.plexify.oauthdemo.auth.configuration.Config.GITHUB
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class FooController {

    @GetMapping("/github/foo")
    fun githubFoo(@RegisteredOAuth2AuthorizedClient(GITHUB) github: OAuth2AuthorizedClient): Mono<FooResponse> {
        val ghAccessToken = github.accessToken.tokenValue
        // ... call a service and fetch data using a web-client of your choice
        return Mono.just(FooResponse("Got something from github using access token: $ghAccessToken"))
    }

    @GetMapping("/aad/foo")
    fun msFoo(@RegisteredOAuth2AuthorizedClient(AZURE_ACTIVE_DIRECTORY) ms: OAuth2AuthorizedClient): Mono<FooResponse> {
        val msAccessToken = ms.accessToken.tokenValue
        // ... call a service and fetch data using a web-client of your choice
        return Mono.just(FooResponse("Got something from graph api using access token: $msAccessToken"))
    }

}