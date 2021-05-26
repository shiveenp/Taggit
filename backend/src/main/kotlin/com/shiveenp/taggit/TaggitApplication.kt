package com.shiveenp.taggit

import com.shiveenp.taggit.config.ExternalProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@EnableConfigurationProperties(ExternalProperties::class)
@SpringBootApplication
class TaggitApplication

fun main(args: Array<String>) {
	runApplication<TaggitApplication>(*args)
}
