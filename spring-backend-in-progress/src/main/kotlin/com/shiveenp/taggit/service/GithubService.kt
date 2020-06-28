package com.shiveenp.taggit.service

import com.shiveenp.taggit.models.GithubUser
import com.shiveenp.taggit.models.GithubStargazingResponse
import kotlinx.coroutines.flow.Flow
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

    suspend fun getUserStargazingData(): Flow<GithubStargazingResponse> {
        val startPage = 1
        var lastPage: Int? = null
        return githubClient.get()
            .
    }
}
