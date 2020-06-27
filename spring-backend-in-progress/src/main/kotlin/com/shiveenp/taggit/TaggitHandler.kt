package com.shiveenp.taggit

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyValueAndAwait


@Component
class TaggitHandler(val taggitService: TaggitService) {

    suspend fun loginOrSignup(req: ServerRequest): ServerResponse {
        return ok().bodyValueAndAwait(taggitService.loginOrRegister())
    }
}

