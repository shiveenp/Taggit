package io.shiveenp.taggit.service

import io.shiveenp.taggit.db.TaggitRepoEntity
import io.shiveenp.taggit.db.TaggitRepoRepository
import io.shiveenp.taggit.models.GithubStargazingResponse
import io.shiveenp.taggit.util.notContains
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

/**
 * This is the main service responsible for all the nitty gritty of repo sync logic that
 * needs to stay outside the scope of the other regular CRUDy services
 */
@Suppress("ReactiveStreamsUnusedPublisher")
@Service
class RepoSyncService(
    private val githubService: GithubService,
    private val taggitRepoRepository: TaggitRepoRepository
) {

    private val logger = KotlinLogging.logger { }

    fun syncUserStargazingData() {
        logger.debug { "Initiating repo sync..." }
        val repos = getUserStarredReposToSync()
        val existingRepos = taggitRepoRepository.findAll()
        val currentSyncedRepoIds = existingRepos.map { it.repoId }
        repos.filter {
            currentSyncedRepoIds.notContains(it.id)
        }.forEach {
            taggitRepoRepository.save(TaggitRepoEntity.from(it))
        }
        logger.debug { "Finished syncing repos!" }
    }

    fun getUserStarredReposToSync(): List<GithubStargazingResponse> {
        val startPage = 1
        val userStarredReposList = mutableListOf<GithubStargazingResponse>()
        var stargazingResponse = githubService.getStargazingDataOrNull(startPage)
        if (stargazingResponse != null) {
            logger.debug { "Stargazing response received..." }
            userStarredReposList.addAll(stargazingResponse.body ?: emptyList())
            logger.debug { "Syncing Page 1" }
            val lastPage = getLastPageFromStargazingResponseOrNull(stargazingResponse)
            if (lastPage != null) {
                for (i in 2..lastPage) {
                    logger.debug { "Syncing Page $i" }
                    stargazingResponse = githubService.getStargazingDataOrNull(i)
                    if (stargazingResponse != null) {
                        userStarredReposList.addAll(stargazingResponse.body ?: emptyList())
                    }
                }
            }
        }
        return userStarredReposList
    }

    fun getLastPageFromStargazingResponseOrNull(response: ResponseEntity<MutableList<GithubStargazingResponse>>): Int? {
        val linksHeader = response.headers["Link"]?.get(0)
        return if (linksHeader != null) {
            val lastPageLink = linksHeader.split(",").last()
            GithubService.githubLinkMatchRegex.find(lastPageLink)?.groupValues?.last()?.substringAfter("=")?.toInt()
        } else {
            null
        }
    }
}

