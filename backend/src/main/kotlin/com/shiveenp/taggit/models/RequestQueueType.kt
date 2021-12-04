package com.shiveenp.taggit.models

enum class RequestQueueType(val value: String) {
    GITHUB_REPO_SYNC("github_repo_sync"),
    UNKNOWN("unknown"); // this value should never happen but it's included here as it helps with composition and avoid too many exceptions being thrown around

    companion object {
        fun fromString(enumString: String): RequestQueueType {
            return try {
                valueOf(enumString.toUpperCase())
            } catch (ex: Exception) {
                UNKNOWN
            }
        }
    }
}