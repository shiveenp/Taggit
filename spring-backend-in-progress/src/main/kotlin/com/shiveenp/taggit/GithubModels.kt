package com.shiveenp.taggit

import com.fasterxml.jackson.annotation.JsonProperty

data class GithubUser(
    val id: Long,
    val login: String,
    @JsonProperty("avatar_url")
    val avatarUrl: String,
    val name: String?,
    val email: String?
)
