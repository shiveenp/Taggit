package com.shiveenp.taggit.api

import com.shiveenp.taggit.config.ExternalProperties
import com.shiveenp.taggit.models.TagInput
import com.shiveenp.taggit.service.TaggitService
import com.shiveenp.taggit.service.TokenHandlerService
import com.shiveenp.taggit.util.toUUID
import com.shiveenp.taggit.util.toUri
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.*


@Component
class TaggitHandler(
    private val service: TaggitService,
    private val externalProperties: ExternalProperties,
    private val tokenHandlerService: TokenHandlerService) {

    suspend fun loginOrSignup(req: ServerRequest): ServerResponse {
        val userAndToken = service.loginOrRegister()
        val token = tokenHandlerService.saveUserIdAndGetSessionToken(userAndToken.first.id, userAndToken.second)
        return temporaryRedirect("${externalProperties.uiUrl}/user/${userAndToken.first.id}/token?token=$token".toUri()).buildAndAwait()
    }

    suspend fun getUser(req: ServerRequest): ServerResponse {
        val userId = getUserIdFromRequest(req)
        return ok().bodyValueAndAwait(service.getUser(userId)!!)
    }

    suspend fun updateUser(req: ServerRequest): ServerResponse {
        return ok().bodyValueAndAwait(service.updateUser(getUserIdFromRequest(req), req.awaitBody()))
    }

    suspend fun getRepos(req: ServerRequest): ServerResponse {
        val userId = getUserIdFromRequest(req)
        val page = req.queryParamOrNull("pageNm")
        val size = req.queryParamOrNull("pageSize")
        return ok().bodyValueAndAwait(service.getUserStarredRepos(userId, page?.toIntOrNull(), size?.toIntOrNull()))
    }

    suspend fun syncRepos(req: ServerRequest): ServerResponse {
        val userId = getUserIdFromRequest(req)
        return ok().bodyAndAwait(service.syncUserRepos(userId))
    }

    suspend fun getRepoTags(req: ServerRequest): ServerResponse {
        val userId = getUserIdFromRequest(req)
        return ok().bodyValueAndAwait(service.getDistinctTags(userId))
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
        val tagToRemove = req.queryParamOrNull("tag")!!
        val updatedRepo = service.deleteTagFromRepo(repoId, tagToRemove)
        return ok().bodyValueAndAwait(updatedRepo)
    }

    suspend fun searchRepoByTags(req: ServerRequest): ServerResponse {
        val loggedInUser = getUserIdFromRequest(req)
        val tags = req.queryParams()["tag"] ?: emptyList()
        return ok().bodyValueAndAwait(service.searchUserReposByTags(loggedInUser!!, tags))
    }

    private suspend fun getUserIdFromRequest(req: ServerRequest) = req.pathVariable(USER_ID_PATH_VARIABLE).toUUID()

    companion object {
        const val USER_ID_PATH_VARIABLE = "userId"
    }
}

