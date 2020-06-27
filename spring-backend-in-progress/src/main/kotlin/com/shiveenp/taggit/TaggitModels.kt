package com.shiveenp.taggit

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
    fun updateUsing(githubUser: GithubUser): TaggitUser{
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
