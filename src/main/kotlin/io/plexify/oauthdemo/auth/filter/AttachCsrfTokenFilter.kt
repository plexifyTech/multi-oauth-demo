package io.plexify.oauthdemo.auth.filter

import org.springframework.security.web.server.csrf.CsrfToken
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

class AttachCsrfTokenFilter : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        @Suppress("UNCHECKED_CAST")
        val token: Mono<CsrfToken>? = exchange.attributes[CsrfToken::class.java.getName()] as Mono<CsrfToken>?
        return token?.flatMap { chain.filter(exchange) } ?: chain.filter(exchange)
    }

}