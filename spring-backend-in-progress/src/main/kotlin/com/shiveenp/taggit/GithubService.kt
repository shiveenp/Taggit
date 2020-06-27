package com.shiveenp.taggit

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.bodyToFlow

@Service
class GithubService(private val githubClient: WebClient) {

    suspend fun getUserData(): GithubUser {
        return githubClient.get()
            .uri("https://api.github.com/user")
            .retrieve()
            .awaitBody()
    }
}
