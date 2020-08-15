package com.shiveenp.taggit.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "external")
data class ExternalProperties(
    val uiUrl: String
)
