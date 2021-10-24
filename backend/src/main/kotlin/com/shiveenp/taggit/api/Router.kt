package com.shiveenp.taggit.api

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.coRouter

@Profile("web", "local")
@RestController
class Router {
    @Bean
    fun routes(taggitHandler: TaggitHandler, sessionKeyFilter: SessionKeyFilter) = coRouter {
        "/".nest {
            GET("signin", taggitHandler::loginOrSignup)
        }

        "/user/{userId}".nest {
            GET("", taggitHandler::getUser)
            PUT("", taggitHandler::updateUser)
            DELETE("", taggitHandler::deleteUser)

            "/repos".nest {
                GET("", taggitHandler::getRepos)
                GET("/sync", taggitHandler::syncRepos)
                GET("/tags", taggitHandler::getRepoTags)
                POST("/{repoId}/tag", taggitHandler::addTagToRepo)
                DELETE("/{repoId}/tag", taggitHandler::deleteTagFromRepo)
                GET("/search", taggitHandler::searchRepoByTags)
            }
        }
    }.filter(sessionKeyFilter)
}
