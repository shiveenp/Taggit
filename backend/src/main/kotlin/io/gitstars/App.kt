package io.gitstars

import org.http4k.client.ApacheClient
import org.http4k.core.*
import org.http4k.core.Method.*
import org.http4k.core.Status.Companion.OK
import org.http4k.filter.ServerFilters
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.security.InsecureCookieBasedOAuthPersistence
import org.http4k.security.OAuthProvider
import org.http4k.security.gitHub
import org.http4k.server.Netty
import org.http4k.server.asServer

fun main() {

    val githubClientId = System.getenv("GITHUB_CLIENT_ID")
    val githubClientSecret = System.getenv("GITHUB_CLIENT_SECRET")
    val port = 9000

    val callbackUri = Uri.of("http://localhost:$port/callback")

    val oauthPersistence = InsecureCookieBasedOAuthPersistence("gitstars")


    val oauthProvider = OAuthProvider.gitHub(
        ApacheClient(),
        Credentials(githubClientId, githubClientSecret),
        callbackUri,
        oauthPersistence
    )

    val app: HttpHandler =
        routes(
            callbackUri.path bind GET to oauthProvider.callback,
            "/" bind GET to oauthProvider.authFilter.then {
                println(oauthPersistence.retrieveToken(it)?.value)

                Response(OK).body("hello!")
            }
        )

    ServerFilters.CatchAll()
        .then(app)
        .asServer(Netty(port)).start().block()
}
