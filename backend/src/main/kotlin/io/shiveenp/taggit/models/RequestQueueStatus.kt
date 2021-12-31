package io.shiveenp.taggit.models

enum class RequestQueueStatus(val value: String) {
    PENDING("PENDING"),
    PROCESSING("PROCESSING"),
    DONE("DONE"),
    ERROR("ERROR")
}
