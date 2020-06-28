package com.shiveenp.taggit.models

import com.shiveenp.taggit.db.TaggitUserEntity
import java.time.LocalDateTime
import java.util.*

data class TaggitUser(
    val id: UUID,
    val userName: String,
    val email: String?,
    val avatarUrl: String?,
    val githubUserName: String,
    val githubUserId: Long,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    fun updateUsing(updateDto: TaggitUserUpdateDto): TaggitUser {
        return this.copy(
            email = updateDto.email ?: this.email,
            userName = updateDto.userName ?: this.userName
        )
    }

    fun updateUsing(githubUser: GithubUser): TaggitUser {
        return this.copy(
            userName = githubUser.name ?: githubUser.login,
            email = githubUser.email,
            avatarUrl = githubUser.avatarUrl,
            githubUserName = githubUser.login,
            githubUserId = githubUser.id
        )
    }

    fun toEntity(): TaggitUserEntity {
        return TaggitUserEntity(
            id = this.id,
            userName = this.userName,
            email = this.email,
            avatarUrl = this.avatarUrl,
            githubUserName = this.githubUserName,
            githubUserId = this.githubUserId,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }
}

data class TaggitUserUpdateDto(
    val userName: String?,
    val email: String?
)

data class Metadata(
    val tags: List<String>
)

data class TaggitRepo(
    val id: UUID,
    val userId: UUID,
    val repoId: Long,
    val repoName: String,
    val githubLink: String,
    val githubDescription: String?,
    val ownerAvatarUrl: String,
    val metadata: Metadata?
)

data class RepoSyncJob(
    val id: UUID,
    val userId: UUID,
    val completed: Boolean,
    val createdAt: LocalDateTime,
    val error: String?,
    val progressPercent: Float,
    val status: String? = ""
)

data class PagedResponse<T>(
    val data: List<T>,
    val pageNum: Int,
    val pageSize: Int,
    val total: Int
)
