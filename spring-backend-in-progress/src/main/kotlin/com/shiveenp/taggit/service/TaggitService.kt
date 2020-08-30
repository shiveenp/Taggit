package com.shiveenp.taggit.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.shiveenp.taggit.db.TaggitRepoEntity
import com.shiveenp.taggit.db.TaggitRepoRepository
import com.shiveenp.taggit.db.TaggitUserRepository
import com.shiveenp.taggit.db.TaggitUserEntity
import com.shiveenp.taggit.models.*
import com.shiveenp.taggit.util.toUUID
import com.vladmihalcea.hibernate.type.json.JsonNodeBinaryType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.reactive.asFlow
import org.hibernate.type.IntegerType
import org.hibernate.type.LongType
import org.hibernate.type.StringType
import org.hibernate.type.UUIDBinaryType
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
import javax.persistence.EntityManagerFactory

@Service
class TaggitService(private val githubService: GithubService,
                    private val repoSyncService: RepoSyncService,
                    private val userRepository: TaggitUserRepository,
                    private val repoRepository: TaggitRepoRepository,
                    private val clientService: ReactiveOAuth2AuthorizedClientService,
                    private val entityManagerFactory: EntityManagerFactory,
                    private val mapper: ObjectMapper) {

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

    suspend fun getDistinctTags(userId: UUID): Set<String> {
        return repoRepository.findAllByUserId(userId)
            .mapNotNull {
                it.metadata
            }
            .flatMap {
                it.tags
            }.toSortedSet()
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

    fun deleteTagFromRepo(repoId: UUID, tag: String): List<TaggitRepoEntity> {
        val updatedRepoWithTagDeleted = repoRepository.findByIdOrNull(repoId)?.let {
            val updatedMetadata = deleteTagFromMetadataOrNull(it.metadata, tag)
            repoRepository.save(it.withUpdated(metadata = updatedMetadata))
        }
        return if(updatedRepoWithTagDeleted != null) {
            listOf(updatedRepoWithTagDeleted)
        } else {
            emptyList()
        }
    }

    private fun deleteTagFromMetadataOrNull(metadata: Metadata?, tagToRemove: String): Metadata? {
        return metadata?.let {
            val updatedTags = it.tags.filter { it != tagToRemove }
            Metadata(tags = updatedTags)
        }
    }

    suspend fun searchUserReposByTags(userId: UUID, tags: List<String>): List<Any> {
        val tagsJsonBQuery = tags.map {
            "r.metadata @> '{\"tags\":[\"$it\"]}'"
        }.joinToString(" OR ")
        val sqlToExecute = "SELECT * FROM repo r WHERE r.user_id = '$userId' and ${tagsJsonBQuery} order by r.repo_name asc"
        entityManagerFactory.createEntityManager()
        return entityManagerFactory.createEntityManager()
            .createNativeQuery(sqlToExecute)
            .unwrap(org.hibernate.query.NativeQuery::class.java)
            .addScalar("id", StringType.INSTANCE)
            .addScalar("user_id", StringType.INSTANCE)
            .addScalar("repo_id", LongType.INSTANCE)
            .addScalar("repo_name", StringType.INSTANCE)
            .addScalar("github_link", StringType.INSTANCE)
            .addScalar("github_description", StringType.INSTANCE)
            .addScalar("star_count", IntegerType.INSTANCE)
            .addScalar("owner_avatar_url", StringType.INSTANCE)
            .addScalar("metadata", JsonNodeBinaryType.INSTANCE)
            .resultList.map {
                val node = mapper.valueToTree<JsonNode>(it)

                TaggitRepo(
                    id = node[0].asText().toUUID(),
                    userId = node[1].asText().toUUID(),
                    repoId = node[2].asLong(),
                    repoName = node[3].asText(),
                    githubLink = node[4].asText(),
                    githubDescription = node[5].asText(),
                    ownerAvatarUrl = node[7].asText(),
                    metadata = mapper.convertValue(node[8], Metadata::class.java)
                )
            }
    }


    companion object {
        const val DEFAULT_REPO_RESULT_PAGE_NUMBER = 1
        const val DEFAULT_REPO_RESULT_PAGE_SIZE = 50
    }
}
