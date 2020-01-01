package main.kotlin.io.gitstars

import me.liuwj.ktorm.dsl.eq
import me.liuwj.ktorm.dsl.select
import me.liuwj.ktorm.dsl.where

fun loginOrRegister(token: String): Int {
    val githubUser = getUserData(token)
    val existingUser = getCurrentUserByGithubUserId(githubUser.id)
    if (existingUser.isNotEmpty()) {
        updateGitstarsUser(githubUser, existingUser[0].accessToken, token)
    } else {
        insertGitstarsUser(githubUser, token)
    }

    return Users.select(Users.id).where {
        Users.githubUserId eq githubUser.id
    }
        .map { queryRowSet -> queryRowSet[Users.id] }[0]!!
}

fun updateUserRepos(userId: Int, token: String) {
    getUserStargazingData(token).forEach {
        insertRepo(it, userId)
    }

}
