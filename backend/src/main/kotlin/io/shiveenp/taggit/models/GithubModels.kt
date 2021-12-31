package io.shiveenp.taggit.models

import com.fasterxml.jackson.annotation.JsonProperty

data class GithubUser(
    val id: Long,
    val login: String,
    @JsonProperty("avatar_url")
    val avatarUrl: String,
    val name: String?,
    val email: String?,
    val githubToken: String?
)

data class GithubStargazingResponse(
    val id: Long,
    val name: String,
    @JsonProperty("stargazers_count")
    val stargazersCount: Long,
    @JsonProperty("html_url")
    val url: String,
    val description: String?,
    val owner: StarredRepoOwner
)

data class StarredRepoOwner(
    @JsonProperty("avatar_url")
    val avatarUrl: String
)
