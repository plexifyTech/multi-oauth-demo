package io.plexify.oauthdemo.auth.repository

import io.plexify.oauthdemo.auth.configuration.Config.CLIENT_REGISTRATION_ID_SET
import io.plexify.oauthdemo.auth.configuration.Config.LOGOUT_URL
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository
import org.springframework.util.Assert
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebSession
import reactor.core.publisher.Mono

class MultiActiveAccountSecurityContextRepository : WebSessionServerSecurityContextRepository() {

    private var cacheSecurityContext = true

    override fun setCacheSecurityContext(cacheSecurityContext: Boolean) {
        this.cacheSecurityContext = cacheSecurityContext
    }

    override fun save(exchange: ServerWebExchange, context: SecurityContext?): Mono<Void> {
        val clientSecurityContextKey = clientSecurityContextKey(exchange)
        return exchange.session.flatMap { session: WebSession ->
            if (context == null) {
                session.attributes.remove(clientSecurityContextKey)
            } else {
                session.attributes[clientSecurityContextKey] = context
            }
            session.changeSessionId()
        }
    }

    override fun load(exchange: ServerWebExchange): Mono<SecurityContext> {
        val clientSecurityContextKey = clientSecurityContextKey(exchange)
        val result = exchange.session.flatMap { session: WebSession ->
            val context = session.attributes[clientSecurityContextKey] as SecurityContext?
            Mono.justOrEmpty(context)
        }
        return if (cacheSecurityContext) result.cache() else result
    }

    private fun clientSecurityContextKey(exchange: ServerWebExchange): String {
        val path = exchange.request.path.value()
        return if (path.contains(LOGOUT_URL)) {
            val key = extractFromQueryParam(exchange)
            clientSecurityContextKey(key)
        } else {
            val key = extractClientKeyFromPath(exchange)
            clientSecurityContextKey(key)
        }
    }

    private fun extractClientKeyFromPath(exchange: ServerWebExchange): String? {
        val path = exchange.request.path
        return path.elements()
            .map { it.value() }
            .firstOrNull { CLIENT_REGISTRATION_ID_SET.contains(it) }
    }

    private fun extractFromQueryParam(exchange: ServerWebExchange): String {
        val query = exchange.request.uri.query.split("=")
        Assert.isTrue(query.size == 2, "Malformed Logout request. Single client key param expected.")
        return query.last()
    }

    private fun clientSecurityContextKey(key: String?) =
        "${DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME}_${key?.uppercase()}"

}