package main.kotlin.io.gitstars

import me.liuwj.ktorm.schema.Table
import me.liuwj.ktorm.schema.datetime
import me.liuwj.ktorm.schema.int
import me.liuwj.ktorm.schema.text

object Users : Table<Nothing>("users") {
    val id by int("id").primaryKey()
    val userName by text("user_name")
    val password by text("password")
    val githubUserName by text("github_user_name")
    val githubUserId by text("github_user_id")
    val accessToken by text("access_token")
    val tokenRefreshedAt by text("token_refreshed_at")
    val lastLoginAt by datetime("last_login_at")
    val createdAt by datetime("createdAt")
    val updatedAt by datetime("updatedAt")
}

