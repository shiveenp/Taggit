package com.shiveenp.taggit.util

import java.net.URI
import java.util.*

fun String.toUUID(): UUID {
    return UUID.fromString(this)
}

fun String.toUri(): URI = URI.create(this)
