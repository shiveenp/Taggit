package com.shiveenp.taggit

import com.shiveenp.taggit.config.ExternalProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling

@EnableAsync
@EnableScheduling
@EnableConfigurationProperties(ExternalProperties::class)
@SpringBootApplication
class TaggitApplication

fun main(args: Array<String>) {
	runApplication<TaggitApplication>(*args)
}
