package com.shiveenp.taggit

import com.shiveenp.taggit.service.TaggitService
import com.shiveenp.taggit.util.toUUID
import kotlinx.coroutines.*
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.ok
import java.util.*
import kotlin.coroutines.coroutineContext


@Component
class TaggitHandler(private val service: TaggitService) {

    suspend fun loginOrSignup(req: ServerRequest): ServerResponse {
        return ok().bodyValueAndAwait(service.loginOrRegister().also {
            saveUserIdInRequestSession(req, it.id)
        })
    }

    suspend fun getUser(req: ServerRequest): ServerResponse {
        return ok().bodyValueAndAwait(service.getUser(getUserIdFromRequestSession(req)!!)!!)
    }

    suspend fun updateUser(req: ServerRequest): ServerResponse {
        return ok().bodyValueAndAwait(service.updateUser(getUserIdFromRequestSession(req)!!, req.awaitBody()))
    }

    suspend fun getRepos(req: ServerRequest): ServerResponse {
        val loggedInUser = getUserIdFromRequestSession(req)
        val page = req.queryParamOrNull("page")
        val size = req.queryParamOrNull("size")
        return ok().bodyAndAwait(service.getUserStarredRepos(loggedInUser!!, page?.toIntOrNull(), size?.toIntOrNull()))
    }

    suspend fun syncUserRepos(req: ServerRequest): ServerResponse {
        return ok().bodyValueAndAwait(service.syncUserRepos())
    }

    private suspend fun saveUserIdInRequestSession(req: ServerRequest, userId: UUID) {
        req.awaitSession().attributes.putIfAbsent(USER_ID_SESSION_KEY, userId)
    }

    private suspend fun getUserIdFromRequestSession(req: ServerRequest): UUID? {
        return req.awaitSession().attributes[USER_ID_SESSION_KEY]?.toString()?.toUUID()
    }

    companion object {
        const val USER_ID_SESSION_KEY = "userId"
    }
}

