package com.shiveenp.taggit.api

import com.shiveenp.taggit.config.ExternalProperties
import com.shiveenp.taggit.security.TokenHandlerService
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.HandlerFilterFunction
import org.springframework.web.reactive.function.server.HandlerFunction
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
class SessionKeyFilter(val externalProperties: ExternalProperties,
                       val tokenHandlerService: TokenHandlerService
) : HandlerFilterFunction<ServerResponse?, ServerResponse?> {
    private val logger = KotlinLogging.logger { }
    override fun filter(request: ServerRequest,
                        handlerFunction: HandlerFunction<ServerResponse?>): Mono<ServerResponse?> {
        val turnOffSessionTokenCheckFlag = externalProperties.turnOffSessionTokenCheck?.toBoolean() ?: false
        val shouldCheckSessionToken = !request.path().contains("/signin") && !turnOffSessionTokenCheckFlag
        return if (shouldCheckSessionToken) {
            val userId = request.pathVariable("userId")
            val sessionTokenInHeader = request.headers().firstHeader(SESSION_KEY_HEADER)
            val verifiedUserId = tokenHandlerService.getUserIdFromJwtClaims(sessionTokenInHeader ?: "")
            if (verifiedUserId != null && verifiedUserId == userId) {
                handlerFunction.handle(request)
            } else {
                logger.debug { "Request denied since tokens don't match" }
                ServerResponse.status(HttpStatus.FORBIDDEN).build()
            }
        } else {
            handlerFunction.handle(request)
        }
    }

    companion object {
        const val SESSION_KEY_HEADER = "x-taggit-session-key"
    }
}
