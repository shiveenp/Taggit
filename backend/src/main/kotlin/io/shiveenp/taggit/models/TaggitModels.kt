package io.shiveenp.taggit.models

import io.shiveenp.taggit.db.UserEntity
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
    fun updateUsing(githubUser: GithubUser): TaggitUser {
        return this.copy(
            userName = githubUser.name ?: githubUser.login,
            email = githubUser.email,
            avatarUrl = githubUser.avatarUrl,
            githubUserName = githubUser.login,
            githubUserId = githubUser.id
        )
    }

    fun toEntity(): UserEntity {
        return UserEntity(
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

data class TagMetadata(
    val tags: List<String>
)

data class TaggitRepo(
    val id: UUID,
    val repoId: Long,
    val repoName: String,
    val githubLink: String,
    val githubDescription: String?,
    val starCount: Long,
    val ownerAvatarUrl: String,
    val metadata: TagMetadata?
)

data class TagInput(
    val tag: String
)

data class SearchInput(
    val keys: List<String>
)

data class PagedResponse<T>(
    val data: List<T>,
    val pageNum: Int,
    val pageSize: Int,
    val total: Long
)
