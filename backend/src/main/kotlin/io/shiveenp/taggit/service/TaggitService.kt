package io.shiveenp.taggit.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.vladmihalcea.hibernate.type.json.JsonNodeBinaryType
import io.shiveenp.taggit.db.*
import io.shiveenp.taggit.models.*
import io.shiveenp.taggit.util.toUUID
import mu.KotlinLogging
import org.hibernate.type.IntegerType
import org.hibernate.type.LongType
import org.hibernate.type.StringType
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*
import javax.persistence.EntityManagerFactory

@Service
class TaggitService(
    private val githubService: GithubService,
    private val userRepository: TaggitUserRepository,
    private val repoRepository: TaggitRepoRepository,
    private val entityManagerFactory: EntityManagerFactory,
    private val requestQueueRepository: RequestQueueRepository,
    private val mapper: ObjectMapper
) {

    private val logger = KotlinLogging.logger { }


    @Transactional
    suspend fun loginOrRegister(): TaggitUser {
        val githubUser = githubService.getUserData()
        val existingUser = userRepository.findByGithubUserId(githubUser.id)
        val user = if (existingUser != null) {
            userRepository.save(
                existingUser
                    .toDto()
                    .updateUsing(githubUser)
                    .toEntity()
            )
                .toDto()
        } else {
            userRepository.save(TaggitUserEntity.from(githubUser)).toDto()
        }
        return user
    }

    @Transactional(readOnly = true)
    suspend fun getUser(): TaggitUser? {
        return userRepository.findAll().firstOrNull()?.toDto()
    }

    @Transactional(readOnly = true)
    suspend fun getUserStarredRepos(page: Int?, size: Int?): PagedResponse<TaggitRepo> {
        val pageNumber = page ?: DEFAULT_REPO_RESULT_PAGE_NUMBER
        val pageSize = size ?: DEFAULT_REPO_RESULT_PAGE_SIZE
        val pageRequest = PageRequest.of(
            pageNumber,
            pageSize,
            Sort.by("repoName").ascending()
        ) // default sort is repos in ascending order
        val dbReturnedPage = repoRepository.findAll(pageRequest)
        val reposToReturn = dbReturnedPage.toList().map { it.toDto() }
        return PagedResponse(
            data = reposToReturn,
            pageNum = pageNumber,
            pageSize = pageSize,
            total = dbReturnedPage.totalElements
        )
    }

    suspend fun syncUserRepos() {
        val requestQueueEntity = RequestQueueEntity(
            UUID.randomUUID(),
            RequestQueueType.GITHUB_REPO_SYNC.value,
            null,
            RequestQueueStatus.PENDING.value,
            OffsetDateTime.now(ZoneOffset.UTC),
            OffsetDateTime.now(ZoneOffset.UTC)
        )
        requestQueueRepository.save(requestQueueEntity)
    }

    suspend fun getDistinctTags(): Set<String> {
        return repoRepository.findAll()
            .mapNotNull {
                it.metadata
            }
            .flatMap {
                it.tags
            }.toSortedSet()
    }

    suspend fun addRepoTag(repoId: UUID, tagInput: TagInput): TaggitRepoEntity? {
        val isUntaggedKeyword = tagInput.tag.equals(UNTAGGED_KEYWORD, true)
        if (isUntaggedKeyword) {
            throw IllegalArgumentException("Cannot use 'untagged' as it's reserved")
        }
        return repoRepository.findByIdOrNull(repoId)?.let {
            val updatedMetadata = addTagToMetadata(it.metadata, tagInput.tag)
            repoRepository.save(it.withUpdated(metadata = updatedMetadata))
        }
    }

    private fun addTagToMetadata(metadata: TagMetadata?, tag: String): TagMetadata {
        return if (metadata != null) {
            val updatedTags = metadata.tags.toMutableSet()
                .apply {
                    this.add(tag)
                }
            TagMetadata(tags = updatedTags.toList())
        } else {
            TagMetadata(tags = listOf(tag))
        }
    }

    fun deleteTagFromRepo(repoId: UUID, tagToDelete: String): TaggitRepoEntity {
        val updatedRepoWithTagDeleted = repoRepository.findByIdOrNull(repoId)?.let {
            val updatedMetadata = deleteTagFromMetadataOrNull(it.metadata, tagToDelete)
            repoRepository.save(it.withUpdated(metadata = updatedMetadata))
        }
        return updatedRepoWithTagDeleted!!
    }

    private fun deleteTagFromMetadataOrNull(metadata: TagMetadata?, tagToRemove: String): TagMetadata? {
        return metadata?.let {
            val updatedTags = it.tags.filter { it != tagToRemove }
            TagMetadata(tags = updatedTags)
        }
    }

    suspend fun getUntaggedRepos(): List<Any> {
        val sqlToExecute = "SELECT * FROM repo r WHERE r.metadata->>'tags' IS NULL order by r.repo_name asc"
        return executeSqlOnRepos(sqlToExecute)
    }

    suspend fun searchReposByText(text: List<String>): List<TaggitRepo> {
        val textToSearch = text.joinToString(" & ")
        val sqlToExecute = """
            select * from repo
            where repo_name_ts @@ to_tsquery('english', '$textToSearch') OR github_description_ts @@ to_tsquery('english', '$textToSearch')
            ORDER BY ts_rank(repo_name_ts, to_tsquery('english', '$textToSearch')) DESC;
        """.trimIndent()
        return executeSqlOnRepos(sqlToExecute);
    }

    suspend fun searchUserReposByTags(tags: List<String>): List<TaggitRepo> {
        val tagsJsonBQuery = tags.joinToString(" OR ") {
            "r.metadata @> '{\"tags\":[\"$it\"]}'"
        }
        val sqlToExecute =
            "SELECT * FROM repo r WHERE $tagsJsonBQuery order by r.repo_name asc"
        return executeSqlOnRepos(sqlToExecute)
    }

    @Transactional(readOnly = true)
    fun executeSqlOnRepos(sqlToExecute: String): List<TaggitRepo> {
        val entityManager = entityManagerFactory.createEntityManager()
        try {
            return entityManager.createNativeQuery(sqlToExecute)
                .unwrap(org.hibernate.query.NativeQuery::class.java)
                .addScalar("id", StringType.INSTANCE)
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
                        repoId = node[1].asLong(),
                        repoName = node[2].asText(),
                        githubLink = node[3].asText(),
                        githubDescription = node[4].asText(),
                        starCount = node[5].asLong(),
                        ownerAvatarUrl = node[6].asText(),
                        metadata = mapper.convertValue(node[7], TagMetadata::class.java)
                    )
                }
        } catch (ex: Exception) {
            logger.error(ex) { "Unable to extract result set" }
            // todo: add proper exception and rethrow here
            return emptyList()
        } finally {
            entityManager.close()
        }
    }


    companion object {
        const val DEFAULT_REPO_RESULT_PAGE_NUMBER = 1
        const val DEFAULT_REPO_RESULT_PAGE_SIZE = 50
        const val UNTAGGED_KEYWORD = "untagged";
    }
}
