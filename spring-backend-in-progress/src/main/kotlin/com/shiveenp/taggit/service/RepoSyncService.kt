package com.shiveenp.taggit.service

import com.shiveenp.taggit.models.GithubStargazingResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

/**
 * This is the main service responsible for all the nitty gritty of repo sync logic that
 * needs to stay outside the scope of the other regular CRUDy services
 */
@Service
class RepoSyncService(val githubService: GithubService) {

    suspend fun syncUserStargazingData(): Flow<GithubStargazingResponse> {
        val startPage = 1
        val userStarredReposList = mutableListOf<GithubStargazingResponse>()

        var stargazingResponse = githubService.requestGithubStargazingResponseOrNull(startPage)
        if (stargazingResponse != null) {
            userStarredReposList.addAll(stargazingResponse.body ?: emptyList())
            val lastPage = getLastPageFromStargazingResponseOrNull(stargazingResponse)
            if (lastPage != null) {
                for (i in 2..lastPage) {
                    stargazingResponse = githubService.requestGithubStargazingResponseOrNull(i)
                    if (stargazingResponse != null) {
                        userStarredReposList.addAll(stargazingResponse.body ?: emptyList())
                    }
                }
            }
        }
        return userStarredReposList.asFlow()
    }

    fun getLastPageFromStargazingResponseOrNull(reponse: ResponseEntity<List<GithubStargazingResponse>>): Int? {
        val linksHeader = reponse.headers["Link"]?.get(0)
        return if (linksHeader != null) {
            val lastPageLink = linksHeader.split(",").last()
            GithubService.githubLinkMatchRegex.find(lastPageLink)?.groupValues?.last()?.substringAfter("=")?.toInt()
        } else {
            null
        }

    }
}
