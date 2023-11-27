package io.plexify.oauthdemo.auth.service

import io.plexify.oauthdemo.auth.dto.CsrfTokenResponse
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CsrfTokenExtractorService {
    fun extractTokenFromCookie(request: ServerHttpRequest): Mono<CsrfTokenResponse> {
        val csrfToken = request.cookies.getFirst("XSRF-TOKEN")?.value
        return when {
            csrfToken.isNotNullOrEmpty() -> Mono.just(CsrfTokenResponse(csrfToken!!))
            else -> Mono.empty()
        }
    }
}

fun String?.isNotNullOrEmpty() = !this.isNullOrEmpty()