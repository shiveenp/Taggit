package com.shiveenp.taggit.util

import java.util.*

fun String.toUUID(): UUID {
    return UUID.fromString(this)
}
