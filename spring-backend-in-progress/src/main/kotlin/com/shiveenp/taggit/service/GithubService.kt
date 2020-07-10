package com.shiveenp.taggit.service

import com.shiveenp.taggit.models.GithubStargazingResponse
import com.shiveenp.taggit.models.GithubUser
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

@Service
class GithubService(private val githubClient: WebClient) {

    suspend fun getUserData(): GithubUser {
        return githubClient.get()
            .uri("https://api.github.com/user")
            .retrieve()
            .awaitBody()
    }


    fun requestGithubStargazingResponseOrNull(page: Int): ResponseEntity<List<GithubStargazingResponse>>? {
        val uri = "$GITHUB_STARGAZING_BASE_URI?page=$page"
        return githubClient.get()
            .uri(uri)
            .retrieve()
            .toEntityList(GithubStargazingResponse::class.java)
            .block()
    }

    companion object {
        const val GITHUB_STARGAZING_BASE_URI = "https://api.github.com/user/starred"
        val githubLinkMatchRegex = "<(.*?)>".toRegex()
    }
}
