package io.shiveenp.taggit.util

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.net.URI
import java.util.*

val mapper = jacksonObjectMapper()
    .findAndRegisterModules()

fun String.toUUID(): UUID {
    return UUID.fromString(this)
}

fun String.toUri(): URI = URI.create(this)

/**
 * Checks if the element is contained in the list
 */
fun <T> List<T>.notContains(element: T): Boolean {
    return !this.contains(element)
}

fun Any.toJson(): String {
    return mapper.writeValueAsString(this)
}

inline fun <reified T> fromJson(json: String): T {
    return mapper.readValue(json, object : TypeReference<T>() {})
}