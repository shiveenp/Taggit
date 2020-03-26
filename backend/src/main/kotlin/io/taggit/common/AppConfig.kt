package main.kotlin.io.taggit.common

import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.lens.string

object AppProperties {
    val env = Environment.ENV
    val githubClientId = EnvironmentKey.required("GITHUB_CLIENT_ID")
    val githubClientSecret = EnvironmentKey.required("GITHUB_CLIENT_SECRET")
    val dbUrl = EnvironmentKey.required("DATABASE_URL")
    val dbUser = EnvironmentKey.required("DATABASE_USER")
    val dbPassword = EnvironmentKey.required("DATABASE_PASSWORD")
}
