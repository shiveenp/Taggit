package com.shiveenp.taggit.util

class GithubAuthException(msg: String?): Exception(msg)

class SyncException(msg: String?, ex: Throwable) : RuntimeException(msg, ex)