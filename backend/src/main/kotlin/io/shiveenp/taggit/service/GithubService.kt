package io.shiveenp.taggit.service

import io.shiveenp.taggit.config.ExternalProperties
import io.shiveenp.taggit.models.GithubStargazingResponse
import io.shiveenp.taggit.models.GithubUser
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import reactor.core.scheduler.Schedulers


@Service
class GithubService(val externalProperties: ExternalProperties) {

    private val webClient = WebClient.builder()
        .defaultHeaders { header ->
            header.setBasicAuth(
                externalProperties.githubUserName,
                externalProperties.githubAccessToken
            )
        }
        .build()

    suspend fun getUserData(): GithubUser {
        return webClient.get()
            .uri("https://api.github.com/user")
            .retrieve()
            .awaitBody()
    }


    fun getStargazingDataOrNull(page: Int): ResponseEntity<MutableList<GithubStargazingResponse>>? {
        val uri = "$GITHUB_STARGAZING_BASE_URI?page=$page"
        return webClient.get()
            .uri(uri)
            .retrieve()
            .toEntityList(GithubStargazingResponse::class.java)
            .publishOn(Schedulers.boundedElastic())
            .block()
    }

    companion object {
        const val GITHUB_STARGAZING_BASE_URI = "https://api.github.com/user/starred"
        val githubLinkMatchRegex = "<(.*?)>".toRegex()
    }
}
