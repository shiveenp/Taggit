package io.shiveenp.taggit.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.core.annotation.Order

@Order(0)
@ConstructorBinding
@ConfigurationProperties(prefix = "external")
data class ExternalProperties(
    val githubUserName: String,
    val githubAccessToken: String,
    val appPassword: String,
)
