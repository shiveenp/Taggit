package main.kotlin.io.gitstars

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
    return UsersTable.select(UsersTable.id).where {
        UsersTable.githubUserId eq githubUser.id
    }
        .map { queryRowSet -> queryRowSet[UsersTable.id] }[0]!!
}

fun getUser(userId: UUID): GitstarUser {
    return getGitStarUser(userId)[0]
}

fun syncUserRepos(userId: UUID): UUID {
    createNewRepoSyncJob(userId)
    val syncJob = getMostRecentUnfinishedRepoSyncJob(userId)
    GlobalScope.launch {
        val token = getUserToken(userId)
        updateUserRepos(userId, token)
        completeRepoSyncJob(syncJob.id)
    }
    return syncJob.id
}

fun getsyncJob(jobId: UUID): RepoSyncJob {
    return getRepoSyncJobUsingId(jobId)
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
