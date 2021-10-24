package com.shiveenp.taggit.util

import java.lang.RuntimeException

class GithubAuthException(msg: String?): Exception(msg)

class SyncException(msg: String?) : Exception(msg)

class ParseException(msg: String): RuntimeException(msg)