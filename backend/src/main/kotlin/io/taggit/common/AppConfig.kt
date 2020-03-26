package main.kotlin.io.taggit.common

import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.lens.string

object AppProperties {
    val env = Environment.ENV
    val githubClientId = EnvironmentKey.required("github.client.id")
    val githubClientSecret = EnvironmentKey.required("github.client.secret")
    val dbUrl = EnvironmentKey.required("database.url")
    val dbUser = EnvironmentKey.required("database.user")
    val dbPassword = EnvironmentKey.string().required("database.password")
}
