package io.plexify.oauthdemo.auth.configuration

import io.plexify.oauthdemo.auth.configuration.Config.AZURE_ACTIVE_DIRECTORY
import io.plexify.oauthdemo.auth.configuration.Config.GITHUB
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.oauth2.core.AuthorizationGrantType.AUTHORIZATION_CODE
import org.springframework.security.oauth2.core.ClientAuthenticationMethod.CLIENT_SECRET_BASIC
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsConfigurationSource
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource


@Configuration
@EnableWebFluxSecurity
class OAuth2LoginConfig(
    private val authConfig: AuthConfig
) {

    @Bean
    fun httpSecurity(http: ServerHttpSecurity): SecurityWebFilterChain {
        http
            .configureAuthorizeExchanges()
            .configureOAuth2Login(authConfig)
            .configureSecurityContextRepository()
            .configureCsrf()
            .configureCsrfTokenFilter()
            .configureLogout(authConfig)
        return http.build()
    }

    @Bean
    fun clientRegistrationRepository(): ReactiveClientRegistrationRepository {
        return InMemoryReactiveClientRegistrationRepository(githubClientRegistration(), aadClientRegistration())
    }

    @Bean
    fun corsConfigurationSource(config: AuthConfig): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = mutableListOf(config.corsAllowedOrigin)
        configuration.allowedHeaders = mutableListOf("*")
        configuration.setAllowedMethods(mutableListOf("GET", "PUT", "POST"))
        configuration.exposedHeaders = mutableListOf("Set-Cookie")
        configuration.allowCredentials = true
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    private fun githubClientRegistration(): ClientRegistration {
        return CommonOAuth2Provider.GITHUB.getBuilder(GITHUB)
            .clientId(authConfig.githubClientId)
            .clientSecret(authConfig.githubClientSecret)
            .userNameAttributeName("id")
            .scope("read:user")
            .build()
    }


    private fun aadClientRegistration(): ClientRegistration {
        val tenantId = authConfig.aadTenantId
        return ClientRegistration.withRegistrationId(AZURE_ACTIVE_DIRECTORY)
            .clientId(authConfig.aadClientId)
            .clientSecret(authConfig.aadClientSecret)
            .clientAuthenticationMethod(CLIENT_SECRET_BASIC)
            .authorizationGrantType(AUTHORIZATION_CODE)
            .authorizationUri("https://login.microsoftonline.com/$tenantId/oauth2/v2.0/authorize")
            .tokenUri("https://login.microsoftonline.com/$tenantId/oauth2/v2.0/token")
            .issuerUri("https://login.microsoftonline.com/$tenantId/v2.0")
            .jwkSetUri("https://login.microsoftonline.com/$tenantId/discovery/keys")
            .userInfoUri("https://graph.microsoft.com/oidc/userinfo")
            .userNameAttributeName("sub")
            .clientName("Microsoft")
            .redirectUri("{baseUrl}/{action}/oauth2/code/{registrationId}")
            .scope("openid", "offline_access", "user.read")
            .build()
    }

}