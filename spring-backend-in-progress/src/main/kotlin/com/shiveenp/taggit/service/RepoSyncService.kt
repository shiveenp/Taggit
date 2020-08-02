package com.shiveenp.taggit.service

import com.shiveenp.taggit.db.TaggitRepoEntity
import com.shiveenp.taggit.db.TaggitRepoRepository
import com.shiveenp.taggit.models.GithubStargazingResponse
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
                      val taggitRepoRepository: TaggitRepoRepository) {

    private val logger = KotlinLogging.logger { }

    suspend fun syncUserStargazingData(userId: UUID): Flow<String> {
        return ReactiveSecurityContextHolder.getContext()
            .flatMap {
                var token = "";
                val authToken = it.authentication as OAuth2AuthenticationToken
                clientService.loadAuthorizedClient<OAuth2AuthorizedClient>(authToken.authorizedClientRegistrationId, authToken.name).doOnSuccess {
                    token = it.accessToken.tokenValue
                }.subscribe()
                val startPage = 1
                val userStarredReposList = mutableListOf<GithubStargazingResponse>()
                var stargazingResponse = githubService.getStargazingDataOrNull(token, startPage)
                if (stargazingResponse != null) {
                    userStarredReposList.addAll(stargazingResponse.body ?: emptyList())
                    val lastPage = getLastPageFromStargazingResponseOrNull(stargazingResponse)
                    if (lastPage != null) {
                        for (i in 2..lastPage) {
                            stargazingResponse = githubService.getStargazingDataOrNull(token, i)
                            if (stargazingResponse != null) {
                                userStarredReposList.addAll(stargazingResponse.body ?: emptyList())
                            }
                        }
                    }
                }
                userStarredReposList.toMono()
            }.doOnNext {
                it.forEach {
                    taggitRepoRepository.save(TaggitRepoEntity.from(userId, it))
                }
            }
            .subscribeOn(Schedulers.boundedElastic())
            .thenReturn("Finished")
            .asFlow()
    }

    fun getLastPageFromStargazingResponseOrNull(reponse: ResponseEntity<MutableList<GithubStargazingResponse>>): Int? {
        val linksHeader = reponse.headers["Link"]?.get(0)
        return if (linksHeader != null) {
            val lastPageLink = linksHeader.split(",").last()
            GithubService.githubLinkMatchRegex.find(lastPageLink)?.groupValues?.last()?.substringAfter("=")?.toInt()
        } else {
            null
        }
    }
}
