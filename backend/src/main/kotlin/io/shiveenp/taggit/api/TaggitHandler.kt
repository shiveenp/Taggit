package io.shiveenp.taggit.api

import io.shiveenp.taggit.config.ExternalProperties
import io.shiveenp.taggit.models.TagInput
import io.shiveenp.taggit.service.TaggitService
import io.shiveenp.taggit.util.toUUID
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.*

@Component
class TaggitHandler(
    private val taggitService: TaggitService,
    private val externalProperties: ExternalProperties
) {
    suspend fun login(req: ServerRequest): ServerResponse {
        val passwordProvided = req.queryParamOrNull("password")
        if (passwordProvided.isNullOrBlank()) {
            return status(HttpStatus.FORBIDDEN).buildAndAwait()
        }
        if (externalProperties.appPassword == passwordProvided) {
            return ok().buildAndAwait()
        }
        return status(HttpStatus.FORBIDDEN).buildAndAwait()
    }
    suspend fun getUser(req: ServerRequest): ServerResponse {
        val existingUser = taggitService.getUser()
        return if (existingUser != null) {
            ok().bodyValueAndAwait(existingUser)
        } else {
            // user logging in first time or user does not exist
            val createdUser = taggitService.loginOrRegister()
            ok().bodyValueAndAwait(createdUser)
        }
    }

    suspend fun getRepos(req: ServerRequest): ServerResponse {
        val page = req.queryParamOrNull("pageNm")
        val size = req.queryParamOrNull("pageSize")
        return ok().bodyValueAndAwait(taggitService.getUserStarredRepos(page?.toIntOrNull(), size?.toIntOrNull()))
    }

    suspend fun syncRepos(req: ServerRequest): ServerResponse {
        return accepted().bodyValueAndAwait(taggitService.syncUserRepos())
    }

    suspend fun getRepoTags(req: ServerRequest): ServerResponse {
        return ok().bodyValueAndAwait(taggitService.getDistinctTags())
    }

    suspend fun addTagToRepo(req: ServerRequest): ServerResponse {
        return try {
            val repoId = req.pathVariable("repoId").toUUID()
            val tagInput = req.awaitBody<TagInput>()
            val updatedRepo = taggitService.addRepoTag(repoId, tagInput)
            if (updatedRepo != null) {
                ok().bodyValueAndAwait(updatedRepo)
            } else {
                notFound().buildAndAwait()
            }
        } catch (ex: Exception) {
            badRequest().bodyValueAndAwait(ex.localizedMessage)
        }
    }

    suspend fun deleteTagFromRepo(req: ServerRequest): ServerResponse {
        val repoId = req.pathVariable("repoId").toUUID()
        val tagToRemove = req.queryParamOrNull("tag")!!
        val updatedRepo = taggitService.deleteTagFromRepo(repoId, tagToRemove)
        return ok().bodyValueAndAwait(updatedRepo)
    }

    suspend fun searchRepoByTags(req: ServerRequest): ServerResponse {
        val tags = req.queryParams()["tag"] ?: emptyList()
        return ok().bodyValueAndAwait(taggitService.searchUserReposByTags(tags))
    }

    suspend fun searchUntaggedRepos(req: ServerRequest): ServerResponse {
        return ok().bodyValueAndAwait(taggitService.getUntaggedRepos())
    }
}

