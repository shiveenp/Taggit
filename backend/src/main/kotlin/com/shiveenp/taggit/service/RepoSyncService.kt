package com.shiveenp.taggit.service

import com.shiveenp.taggit.config.ExternalProperties
import com.shiveenp.taggit.db.TaggitRepoEntity
import com.shiveenp.taggit.db.TaggitRepoRepository
import com.shiveenp.taggit.db.TaggitUserRepository
import com.shiveenp.taggit.models.GithubStargazingResponse
import com.shiveenp.taggit.security.EncryptorService
import com.shiveenp.taggit.util.GithubAuthException
import com.shiveenp.taggit.util.notContains
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.*

/**
 * This is the main service responsible for all the nitty gritty of repo sync logic that
 * needs to stay outside the scope of the other regular CRUDy services
 */
@Suppress("ReactiveStreamsUnusedPublisher")
@Service
class RepoSyncService(
    private val githubService: GithubService,
    private val userRepository: TaggitUserRepository,
    private val taggitRepoRepository: TaggitRepoRepository,
    private val encryptorService: EncryptorService,
    private val externalProperties: ExternalProperties
) {

    private val logger = KotlinLogging.logger { }

    fun syncUserStargazingData(userId: UUID) {
        logger.info { "Syncing repos for user: $userId" }
        val encryptedToken = userRepository.findById(userId).orElse(null)?.githubToken
        if (encryptedToken != null) {
            val repos = getUserStarredReposToSync(
                encryptorService.decrypt(
                    encryptedToken,
                    externalProperties.githubTokenEncryptionKey
                )
            )
            val existingRepos = taggitRepoRepository.findAll()
            val currentSyncedRepoIds = existingRepos.map { it.repoId }
            repos.filter {
                currentSyncedRepoIds.notContains(it.id)
            }.forEach {
                taggitRepoRepository.save(TaggitRepoEntity.from(userId, it))
            }
            logger.info { "Finished syncing repos for user: $userId" }
        } else {
            logger.error { "Encountered error while syncing github repos for user: $userId" }
            throw GithubAuthException("Unable to sync repos for $userId")
        }
    }

    fun getUserStarredReposToSync(token: String): List<GithubStargazingResponse> {
        val startPage = 1
        val userStarredReposList = mutableListOf<GithubStargazingResponse>()
        var stargazingResponse = githubService.getStargazingDataOrNull(token, startPage)
        if (stargazingResponse != null) {
            logger.debug { "Stargazing response received..." }
            userStarredReposList.addAll(stargazingResponse.body ?: emptyList())
            logger.debug { "Syncing Page 1" }
            val lastPage = getLastPageFromStargazingResponseOrNull(stargazingResponse)
            if (lastPage != null) {
                for (i in 2..lastPage) {
                    logger.debug { "Syncing Page $i" }
                    stargazingResponse = githubService.getStargazingDataOrNull(token, i)
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

