package com.shiveenp.taggit.service

import com.shiveenp.taggit.db.TaggitRepoEntity
import com.shiveenp.taggit.db.TaggitRepoRepository
import com.shiveenp.taggit.db.TaggitUserRepository
import com.shiveenp.taggit.db.TaggitUserEntity
import com.shiveenp.taggit.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.stereotype.Service
import reactor.kotlin.core.publisher.toMono
import java.util.*

@Service
class TaggitService(private val githubService: GithubService,
                    private val repoSyncService: RepoSyncService,
                    private val userRepository: TaggitUserRepository,
                    private val repoRepository: TaggitRepoRepository,
                    private val clientService: ReactiveOAuth2AuthorizedClientService) {

    suspend fun loginOrRegister(): Pair<TaggitUser, String> {
        val githubUser = githubService.getUserData()
        val existingUser = userRepository.findByGithubUserId(githubUser.id)
        val user = if (existingUser != null) {
            userRepository.save(existingUser
                .toDto()
                .updateUsing(githubUser)
                .toEntity())
                .toDto()
        } else {
            userRepository.save(TaggitUserEntity.from(githubUser)).toDto()
        }
        val githubToken = getGithubOauthToken()
        return (user to githubToken)
    }

    suspend fun getGithubOauthToken(): String {
        return ReactiveSecurityContextHolder.getContext().flatMap {
            var token = "";
            val authToken = it.authentication as OAuth2AuthenticationToken
            clientService.loadAuthorizedClient<OAuth2AuthorizedClient>(authToken.authorizedClientRegistrationId, authToken.name)
                .doOnSuccess {
                    token = it.accessToken.tokenValue
                }.subscribe()
            token.toMono()
        }.asFlow()
            .first()
    }

    suspend fun getUser(userId: UUID): TaggitUser? {
        return userRepository.findByIdOrNull(userId)?.toDto()
    }

    suspend fun updateUser(userId: UUID, updateDto: TaggitUserUpdateDto): TaggitUser {
        val currentUser = userRepository.findByIdOrNull(userId)
        return if (currentUser != null) {
            val updatedEntity = currentUser.toDto().updateUsing(updateDto).toEntity()
            userRepository.save(updatedEntity).toDto()
        } else {
            throw Exception("User does not exist")
        }
    }

    suspend fun getUserStarredRepos(userId: UUID, page: Int?, size: Int?): PagedResponse<TaggitRepo> {
        val pageNumber = page ?: DEFAULT_REPO_RESULT_PAGE_NUMBER
        val pageSize = size ?: DEFAULT_REPO_RESULT_PAGE_SIZE
        val pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by("repoName").ascending()) // default sort is repos in ascending order
        val dbReturnedPage = repoRepository.findAllByUserId(userId, pageRequest)
        val reposToReturn = dbReturnedPage.toList().map { it.toDto() }
        return PagedResponse(
            data = reposToReturn,
            pageNum = pageNumber,
            pageSize = pageSize,
            total = dbReturnedPage.totalElements
        )
    }

    suspend fun syncUserRepos(userId: UUID): Flow<List<GithubStargazingResponse>> = repoSyncService.syncUserStargazingData(userId)

    suspend fun getDistinctTags(userId: UUID): Flow<String> {
        return repoRepository.findAllByUserId(userId)
            .mapNotNull {
                it.metadata
            }
            .flatMap {
                it.tags
            }.toSortedSet().asFlow()
    }

    suspend fun addRepoTag(repoId: UUID, tagInput: TagInput): TaggitRepoEntity? {
        return repoRepository.findByIdOrNull(repoId)?.let {
            val updatedMetadata = addTagToMetadata(it.metadata, tagInput.tag)
            repoRepository.save(it.withUpdated(metadata = updatedMetadata))
        }
    }

    private fun addTagToMetadata(metadata: Metadata?, tag: String): Metadata {
        return if (metadata != null) {
            val updatedTags = metadata.tags.toMutableSet()
                .apply {
                    this.add(tag)
                }
            Metadata(tags = updatedTags.toList())
        } else {
            Metadata(tags = listOf(tag))
        }
    }

    fun deleteTagFromRepo(repoId: UUID, tag: String) {
        repoRepository.findByIdOrNull(repoId)?.let {
            val updatedMetadata = deleteTagFromMetadataOrNull(it.metadata, tag)
            repoRepository.save(it.withUpdated(metadata = updatedMetadata))
        }
    }

    private fun deleteTagFromMetadataOrNull(metadata: Metadata?, tag: String): Metadata? {
        return metadata?.let {
            val updatedTags = it.tags.apply {
                this.toMutableList().run {
                    this.remove(tag)
                }
            }
            Metadata(tags = updatedTags)
        }
    }

    suspend fun searchUserReposByTags(userId: UUID, tags: List<String>): Flow<TaggitRepoEntity> {
        val tagsJsonBQuery = tags.map {
            "r.metadata @> '{\"tags\":[\"$it\"]}'"
        }.joinToString(" OR ")
        return repoRepository.findAllByMetadataTags(userId, tagsJsonBQuery).asFlow()

    }


    companion object {
        const val DEFAULT_REPO_RESULT_PAGE_NUMBER = 1
        const val DEFAULT_REPO_RESULT_PAGE_SIZE = 50
    }
}
