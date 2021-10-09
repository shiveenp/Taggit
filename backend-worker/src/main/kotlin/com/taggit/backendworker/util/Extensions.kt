package com.taggit.backendworker.util

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper


val utilObjectMapper = jacksonObjectMapper()

inline fun <reified T> fromJson(json: String): T {
    return utilObjectMapper.readValue(json, object : TypeReference<T>() {})
}
