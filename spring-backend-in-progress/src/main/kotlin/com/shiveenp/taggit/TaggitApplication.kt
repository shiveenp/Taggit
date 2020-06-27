package com.shiveenp.taggit

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TaggitApplication

fun main(args: Array<String>) {
	runApplication<TaggitApplication>(*args)
}
