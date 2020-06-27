package com.shiveenp.taggit

import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "users")
data class TaggitUserEntity(
    @Id
    val id: UUID,
    val userName: String,
    val email: String?,
    val avatarUrl: String?,
    val githubUserName: String,
    val githubUserId: Long,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    fun toDto(): TaggitUser {
        return TaggitUser(
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

    companion object {
        fun from(githubUser: GithubUser): TaggitUserEntity {
            return TaggitUserEntity(
                id = UUID.randomUUID(),
                userName = githubUser.name ?: githubUser.login,
                email = githubUser.email,
                avatarUrl = githubUser.avatarUrl,
                githubUserName = githubUser.login,
                githubUserId = githubUser.id,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        }
    }
}
