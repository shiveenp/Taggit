package main.kotlin.io.gitstars

import me.liuwj.ktorm.dsl.eq
import me.liuwj.ktorm.dsl.select
import me.liuwj.ktorm.dsl.where
import java.util.*

fun loginOrRegister(token: String): UUID {
    val githubUser = getUserData(token)
    val existingUser = getCurrentUserByGithubUserId(githubUser.id)
    // if existing user is present update, otherwise insert a new user
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

fun updateUserRepos(userId: UUID, token: String) {
    val currentUserRepos = getUserRepos(userId)
    getUserStargazingData(token).forEach {
        if (currentUserRepos.notContains(it.id)) {
            // only add the repo for the user if not previously added
            insertRepo(it, userId)
        }
    }
}

fun addTags(repoId: UUID, tags: Metadata): GitStarsRepo {
    return insertTagsInRepo(repoId, tags)
}
