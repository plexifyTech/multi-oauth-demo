package io.plexify.oauthdemo.auth.configuration

import io.plexify.oauthdemo.auth.filter.AttachCsrfTokenFilter
import io.plexify.oauthdemo.auth.handler.RedirectToClientAuthSuccessHandler
import io.plexify.oauthdemo.auth.repository.MultiActiveAccountSecurityContextRepository
import org.springframework.boot.web.server.Cookie
import org.springframework.http.HttpStatus
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationFailureHandler
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository
import org.springframework.security.web.server.csrf.XorServerCsrfTokenRequestAttributeHandler
import java.net.URI

/**
 * Require authentication globally on any endpoint except /login or /logout paths
 * */
fun ServerHttpSecurity.configureAuthorizeExchanges(): ServerHttpSecurity {
    return this.authorizeExchange { exchanges ->
        exchanges
            .pathMatchers("/login**", "/logout**")
            .permitAll()
            .anyExchange()
            .authenticated()
    }
}

/**
 * Define OAuth as the auth method and customize default Spring Boot OAuth2-client behavior
 * */
fun ServerHttpSecurity.configureOAuth2Login(authConfig: AuthConfig): ServerHttpSecurity {
    return this
        .oauth2Login { oauth2Login: ServerHttpSecurity.OAuth2LoginSpec ->
            oauth2Login
                .authenticationSuccessHandler(RedirectToClientAuthSuccessHandler(authConfig))
                .authenticationFailureHandler(RedirectServerAuthenticationFailureHandler(authConfig.authFailureUrl))
        }.exceptionHandling { exceptions ->
            exceptions
                .authenticationEntryPoint(HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED))
        }
}

/**
 * Use a custom SecurityContextRepository that is capable of holding multiple SecurityContexts per WebSession
 * */
fun ServerHttpSecurity.configureSecurityContextRepository(): ServerHttpSecurity {
    return this
        .securityContextRepository(MultiActiveAccountSecurityContextRepository())
}

/**
 * Store csrf-token in a httponly same-site cookie. Will be compared to the token-value provided by the client in a POST-request header.
 * */
fun ServerHttpSecurity.configureCsrf(): ServerHttpSecurity {
    return this.csrf { csrf: ServerHttpSecurity.CsrfSpec ->
        val delegate = XorServerCsrfTokenRequestAttributeHandler()
        val repository = CookieServerCsrfTokenRepository()
        repository.setCookieCustomizer {
            it.sameSite(Cookie.SameSite.STRICT.attributeValue())
        }
        csrf
            .csrfTokenRepository(repository)
            .csrfTokenRequestHandler(delegate::handle)
    }
}

/**
 * Subscribe to the CsrfToken created in the SecurityWebFilter before.
 * */
fun ServerHttpSecurity.configureCsrfTokenFilter(): ServerHttpSecurity {
    return this
        .addFilterAfter(AttachCsrfTokenFilter(), SecurityWebFiltersOrder.CSRF)
}

/**
 * Customize logout flow, so it redirects to a page in the frontend
 * */
fun ServerHttpSecurity.configureLogout(authConfig: AuthConfig): ServerHttpSecurity {
    return this.logout { logout: ServerHttpSecurity.LogoutSpec ->
        val handler = RedirectServerLogoutSuccessHandler()
        val logoutSuccessUrl = authConfig.logoutSuccessUrl
        handler.setLogoutSuccessUrl(URI.create(logoutSuccessUrl))
        logout.logoutSuccessHandler(handler)
    }
}