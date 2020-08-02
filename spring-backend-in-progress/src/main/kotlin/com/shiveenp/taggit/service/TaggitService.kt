package com.shiveenp.taggit.service

import com.shiveenp.taggit.db.TaggitRepoEntity
import com.shiveenp.taggit.db.TaggitRepoRepository
import com.shiveenp.taggit.db.TaggitUserRepository
import com.shiveenp.taggit.models.TaggitUser
import com.shiveenp.taggit.db.TaggitUserEntity
import com.shiveenp.taggit.models.Metadata
import com.shiveenp.taggit.models.TagInput
import com.shiveenp.taggit.models.TaggitRepo
import com.shiveenp.taggit.models.TaggitUserUpdateDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

@Service
class TaggitService(private val githubService: GithubService,
                    private val repoSyncService: RepoSyncService,
                    private val userRepository: TaggitUserRepository,
                    private val repoRepository: TaggitRepoRepository) {

    suspend fun loginOrRegister(): TaggitUser {
        val githubUser = githubService.getUserData()
        val existingUser = userRepository.findByGithubUserId(githubUser.id)
        return if (existingUser != null) {
            userRepository.save(existingUser
                .toDto()
                .updateUsing(githubUser)
                .toEntity())
                .toDto()
        } else {
            userRepository.save(TaggitUserEntity.from(githubUser)).toDto()
        }
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

    suspend fun getUserStarredRepos(userId: UUID, page: Int?, size: Int?): Flow<TaggitRepo> {
        val pageRequest = PageRequest.of(page ?: DEFAULT_REPO_RESULT_PAGE_NUMBER,
            size ?: DEFAULT_REPO_RESULT_PAGE_SIZE)
        return repoRepository.findAllByUserId(userId, pageRequest).map { it.toDto() }.asFlow()
    }

    suspend fun syncUserRepos(userId: UUID): Flow<String> = repoSyncService.syncUserStargazingData(userId)

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


    companion object {
        const val DEFAULT_REPO_RESULT_PAGE_NUMBER = 1
        const val DEFAULT_REPO_RESULT_PAGE_SIZE = 50
    }
}
