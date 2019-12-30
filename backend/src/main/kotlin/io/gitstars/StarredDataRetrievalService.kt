package main.kotlin.io.gitstars

import com.fasterxml.jackson.annotation.JsonProperty
import org.http4k.client.ApacheClient
import org.http4k.core.Method
import org.http4k.core.Request

data class StargazingResponse(
    val id: Long,
    val name: String,
    @JsonProperty("stargazers_count")
    val stargazersCount: Int
)

data class StarredRepoOwner(
    @JsonProperty("avatar_url")
    val avatarUrl: String
)

class GithubStarredDataRetrievalService(token: String) {
    val client = ApacheClient()

    val request = Request(Method.GET, "https://api.github.com/user/starred").apply {
        this.header("Authorization", "token $token")
    }
}
