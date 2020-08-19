package com.shiveenp.taggit.service

import com.shiveenp.taggit.db.TaggitRepoEntity
import com.shiveenp.taggit.db.TaggitRepoRepository
import com.shiveenp.taggit.models.GithubStargazingResponse
import com.shiveenp.taggit.util.GithubAuthException
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.asFlow
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.stereotype.Service
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.toMono
import java.util.*
import javax.xml.bind.JAXBElement

/**
 * This is the main service responsible for all the nitty gritty of repo sync logic that
 * needs to stay outside the scope of the other regular CRUDy services
 */
@Suppress("ReactiveStreamsUnusedPublisher")
@Service
class RepoSyncService(val githubService: GithubService,
                      val clientService: ReactiveOAuth2AuthorizedClientService,
                      val taggitRepoRepository: TaggitRepoRepository,
                      val tokenHandlerService: TokenHandlerService) {

    private val logger = KotlinLogging.logger { }

    suspend fun syncUserStargazingData(userId: UUID): Flow<GithubStargazingResponse> {
        logger.info { "Syncing repos for user: $userId" }
        val token = tokenHandlerService.getAuthTokenFromUserIdOrNull(userId)
        return if (token != null) {
            val allRepos = getUserStarredRepos(token)
            allRepos.forEach {
                // FixMe: Add deduplicater in case repos have already been syncd
                taggitRepoRepository.save(TaggitRepoEntity.from(userId, it))
            }
            allRepos.asFlow()
        } else {
            throw GithubAuthException("Unable to sync repos for $userId")
        }
    }

    fun getUserStarredRepos(token: String): List<GithubStargazingResponse> {
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
