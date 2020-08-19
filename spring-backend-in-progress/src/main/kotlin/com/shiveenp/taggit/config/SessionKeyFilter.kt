package com.shiveenp.taggit.config

import com.shiveenp.taggit.service.TokenHandlerService
import com.shiveenp.taggit.util.toUUID
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.HandlerFilterFunction
import org.springframework.web.reactive.function.server.HandlerFunction
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
class SessionKeyFilter(val tokenHandlerService: TokenHandlerService) : HandlerFilterFunction<ServerResponse?, ServerResponse?> {
    private val logger = KotlinLogging.logger { }
    override fun filter(request: ServerRequest,
                        handlerFunction: HandlerFunction<ServerResponse?>): Mono<ServerResponse?> {
        return if (!request.path().contains("/signin")) {
            val userId = request.pathVariable("userId")
            val sessionTokenInHeader = request.headers().firstHeader(SESSION_KEY_HEADER)
            val sessionTokenInMemory = tokenHandlerService.getSessionTokenFromUserIdOrNull(userId.toUUID())
            if (doSessionTokenMatch(sessionTokenInHeader, sessionTokenInMemory)) {
                handlerFunction.handle(request)
            } else {
                logger.debug { "Request denied since tokens don't match"}
                ServerResponse.status(HttpStatus.FORBIDDEN).build()
            }
        } else {
            handlerFunction.handle(request)
        }
    }

    fun doSessionTokenMatch(sessionTokenInHeader: String?, sessionTokenInMemory: String?): Boolean {
        return if (sessionTokenInHeader != null && sessionTokenInMemory != null) {
            sessionTokenInHeader == sessionTokenInMemory
        } else {
            false
        }
    }

    companion object {
        const val SESSION_KEY_HEADER = "x-taggit-session-key"
    }
}
