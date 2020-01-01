package main.kotlin.io.gitstars

import org.http4k.client.ApacheClient
import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.format.Jackson.auto

val client = ApacheClient()

fun getUserData(token: String): GithubUser {
    val request = Request(Method.GET, "https://api.github.com/user").header("Authorization", "token $token")
    val userLens = Body.auto<GithubUser>().toLens()

    return userLens.extract(client(request))
}

fun getUserStargazingData(token: String): List<StargazingResponse> {
    val request = Request(Method.GET, "https://api.github.com/user/starred").header("Authorization", "token $token")

    val stargazingLens = Body.auto<List<StargazingResponse>>().toLens()

    return stargazingLens.extract(client(request))
}
