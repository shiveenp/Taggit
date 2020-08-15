package com.shiveenp.taggit.api

import com.aventrix.jnanoid.jnanoid.NanoIdUtils
import com.shiveenp.taggit.config.ExternalProperties
import com.shiveenp.taggit.models.TagInput
import com.shiveenp.taggit.service.RedisService
import com.shiveenp.taggit.service.TaggitService
import com.shiveenp.taggit.util.toUUID
import com.shiveenp.taggit.util.toUri
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.*
import java.util.*


@Component
class TaggitHandler(
    private val service: TaggitService,
    private val externalProperties: ExternalProperties,
    private val redis: RedisService) {

    suspend fun loginOrSignup(req: ServerRequest): ServerResponse {
        val user = service.loginOrRegister()
        val token = saveUserIdAndGenerateSessionToken(user.id)
        return temporaryRedirect("${externalProperties.uiUrl}/user/${user.id}/token?token=$token".toUri()).buildAndAwait()
    }

    suspend fun getUser(req: ServerRequest): ServerResponse {
        val userId = req.pathVariable("userId")
        return ok().bodyValueAndAwait(service.getUser(userId.toUUID())!!)
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

    suspend fun syncRepos(req: ServerRequest): ServerResponse {
        val loggedInUser = getUserIdFromRequestSession(req)
        return ok().bodyAndAwait(service.syncUserRepos(loggedInUser!!))
    }

    suspend fun getRepoTags(req: ServerRequest): ServerResponse {
        val loggedInUser = getUserIdFromRequestSession(req)
        return ok().bodyAndAwait(service.getDistinctTags(loggedInUser!!))
    }

    suspend fun addTagToRepo(req: ServerRequest): ServerResponse {
        val repoId = req.pathVariable("repoId").toUUID()
        val tagInput = req.awaitBody<TagInput>()
        val updatedRepo = service.addRepoTag(repoId, tagInput)
        return if (updatedRepo != null) {
            ok().bodyValueAndAwait(updatedRepo)
        } else {
            notFound().buildAndAwait()
        }
    }

    suspend fun deleteTagFromRepo(req: ServerRequest): ServerResponse {
        val repoId = req.pathVariable("repoId").toUUID()
        val tagToRemove = req.pathVariable("tag")
        val updatedRepo = service.deleteTagFromRepo(repoId, tagToRemove)
        return if (updatedRepo != null) {
            ok().bodyValueAndAwait(updatedRepo)
        } else {
            notFound().buildAndAwait()
        }
    }

    suspend fun searchRepoByTags(req: ServerRequest): ServerResponse {
        val loggedInUser = getUserIdFromRequestSession(req)
        val tags = req.queryParams().get("tag") ?: emptyList()
        return ok().bodyAndAwait(service.searchUserReposByTags(loggedInUser!!, tags))
    }

    private suspend fun saveUserIdAndGenerateSessionToken(userId: UUID): String {
        val token = NanoIdUtils.randomNanoId()
        redis.put(token, userId.toString())
        return token
    }

    private suspend fun getUserIdFromRequestSession(req: ServerRequest): UUID? {
        println("retrieval session id is: ${req.awaitSession().id}")
        val userId = req.awaitSession().attributes[USER_ID_SESSION_KEY]?.toString()
        println("user id string is: $userId")
        return req.awaitSession().attributes[USER_ID_SESSION_KEY]?.toString()?.toUUID()
    }

    companion object {
        const val USER_ID_SESSION_KEY = "userId"
    }
}

