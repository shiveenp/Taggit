package com.shiveenp.taggit.api

import com.shiveenp.taggit.config.ExternalProperties
import com.shiveenp.taggit.models.TagInput
import com.shiveenp.taggit.service.TaggitService
import com.shiveenp.taggit.security.TokenHandlerService
import com.shiveenp.taggit.util.toUUID
import com.shiveenp.taggit.util.toUri
import mu.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.*


@Component
class TaggitHandler(
    private val taggitService: TaggitService,
    private val externalProperties: ExternalProperties,
    private val tokenHandlerService: TokenHandlerService
) {

    private val logger = KotlinLogging.logger { }

    suspend fun loginOrSignup(req: ServerRequest): ServerResponse {
        val userAndToken = taggitService.loginOrRegister()
        val token = tokenHandlerService.saveUserIdAndGetJwt(userAndToken.first.id, userAndToken.second)
        return temporaryRedirect("${externalProperties.uiUrl}/user/${userAndToken.first.id}/token?token=$token".toUri()).buildAndAwait()
    }

    suspend fun getUser(req: ServerRequest): ServerResponse {
        val userId = getUserIdFromRequest(req)
        val user = taggitService.getUser(userId)
        return if (user != null) {
            ok().bodyValueAndAwait(user)
        } else {
            logger.warn { "User with id: $userId not found" }
            notFound().buildAndAwait()
        }
    }

    suspend fun updateUser(req: ServerRequest): ServerResponse {
        return ok().bodyValueAndAwait(taggitService.updateUser(getUserIdFromRequest(req), req.awaitBody()))
    }

    suspend fun deleteUser(req: ServerRequest): ServerResponse {
        val userId = getUserIdFromRequest(req)
        taggitService.deleteUser(userId)
        return accepted().bodyValueAndAwait("Accepted")
    }

    suspend fun getRepos(req: ServerRequest): ServerResponse {
        val userId = getUserIdFromRequest(req)
        val page = req.queryParamOrNull("pageNm")
        val size = req.queryParamOrNull("pageSize")
        return ok().bodyValueAndAwait(taggitService.getUserStarredRepos(userId, page?.toIntOrNull(), size?.toIntOrNull()))
    }

    suspend fun syncRepos(req: ServerRequest): ServerResponse {
        val userId = getUserIdFromRequest(req)
        return ok().bodyValueAndAwait(taggitService.syncUserRepos(userId))
    }

    suspend fun getRepoTags(req: ServerRequest): ServerResponse {
        val userId = getUserIdFromRequest(req)
        return ok().bodyValueAndAwait(taggitService.getDistinctTags(userId))
    }

    suspend fun addTagToRepo(req: ServerRequest): ServerResponse {
        val repoId = req.pathVariable("repoId").toUUID()
        val tagInput = req.awaitBody<TagInput>()
        val updatedRepo = taggitService.addRepoTag(repoId, tagInput)
        return if (updatedRepo != null) {
            ok().bodyValueAndAwait(updatedRepo)
        } else {
            notFound().buildAndAwait()
        }
    }

    suspend fun deleteTagFromRepo(req: ServerRequest): ServerResponse {
        val repoId = req.pathVariable("repoId").toUUID()
        val tagToRemove = req.queryParamOrNull("tag")!!
        val updatedRepo = taggitService.deleteTagFromRepo(repoId, tagToRemove)
        return ok().bodyValueAndAwait(updatedRepo)
    }

    suspend fun searchRepoByTags(req: ServerRequest): ServerResponse {
        val loggedInUser = getUserIdFromRequest(req)
        val tags = req.queryParams()["tag"] ?: emptyList()
        return ok().bodyValueAndAwait(taggitService.searchUserReposByTags(loggedInUser, tags))
    }

    private suspend fun getUserIdFromRequest(req: ServerRequest) = req.pathVariable(USER_ID_PATH_VARIABLE).toUUID()

    companion object {
        const val USER_ID_PATH_VARIABLE = "userId"
    }
}

