package com.shiveenp.taggit.util

import java.net.URI
import java.util.*

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
