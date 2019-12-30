package main.kotlin.io.gitstars

import com.fasterxml.jackson.annotation.JsonProperty
import org.http4k.client.ApacheClient
import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.format.Jackson.auto

data class StargazingResponse(
    val id: Long,
    val name: String,
    @JsonProperty("stargazers_count")
    val stargazersCount: Int,
    val owner: StarredRepoOwner
)

data class StarredRepoOwner(
    @JsonProperty("avatar_url")
    val avatarUrl: String
)

val client = ApacheClient()

fun getUserStargazingData(token: String): List<StargazingResponse> {
    val request = Request(Method.GET, "https://api.github.com/user/starred").header("Authorization", "token $token")

    val stargazingLens = Body.auto<List<StargazingResponse>>().toLens()

    return stargazingLens.extract(client(request))
}
