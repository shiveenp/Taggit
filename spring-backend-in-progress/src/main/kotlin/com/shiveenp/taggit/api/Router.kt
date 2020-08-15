package com.shiveenp.taggit.api

import org.springframework.context.annotation.Bean
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.coRouter

@RestController
class Router {

    @Bean
    fun routes(taggitHandler: TaggitHandler) = coRouter {
        "/".nest {
            GET("signin", taggitHandler::loginOrSignup)
        }

        // Todo: delete api needs to be added yet
        "/user".nest {
            GET("/{userId}", taggitHandler::getUser)
            PUT("/{userId}", taggitHandler::updateUser)
        }

        "/repo".nest {
            GET("", taggitHandler::getRepos)
            POST("/sync", taggitHandler::syncRepos)
            GET("/tags", taggitHandler::getRepoTags)
            POST("{repoId}/tag", taggitHandler::addTagToRepo)
            DELETE("{repoId}/tag/{tag}", taggitHandler::deleteTagFromRepo)
            POST("/search", taggitHandler::searchRepoByTags)
        }
    }
}
