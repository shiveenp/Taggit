package com.shiveenp.taggit.service

import com.shiveenp.taggit.db.TaggitRepoRepository
import com.shiveenp.taggit.db.TaggitUserRepository
import com.shiveenp.taggit.models.TaggitUser
import com.shiveenp.taggit.db.TaggitUserEntity
import com.shiveenp.taggit.models.TaggitRepo
import com.shiveenp.taggit.models.TaggitUserUpdateDto
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

@Service
class TaggitService(private val githubService: GithubService,
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

    suspend fun syncUserRepos(userId: UUID) {
        coroutineScope {
            val currentSyncdRepoIds = repoRepository.findAllByUserId(userId)
                .map { it.id }

        }
    }

    companion object {
        const val DEFAULT_REPO_RESULT_PAGE_NUMBER = 1
        const val DEFAULT_REPO_RESULT_PAGE_SIZE = 50
    }
}
