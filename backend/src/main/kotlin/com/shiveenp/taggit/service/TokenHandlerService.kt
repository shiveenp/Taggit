package com.shiveenp.taggit.service

import com.aventrix.jnanoid.jnanoid.NanoIdUtils
import org.springframework.stereotype.Service
import java.util.*

@Service
class TokenHandlerService(private val redis: RedisService) {
    fun saveUserIdAndGetSessionToken(userId: UUID, authToken: String): String {
        val sessionToken = NanoIdUtils.randomNanoId()
        redis.put(userId.toString(), generateSessionAndAuthTokenCombined(sessionToken, authToken))
        return sessionToken
    }

    fun getSessionTokenFromUserIdOrNull(userId: UUID): String? {
        return redis.getOrNull(userId.toString())?.split(SESSION_AND_AUTH_TOKEN_DELIMITER)?.first()
    }

    fun getAuthTokenFromUserIdOrNull(userId: UUID): String? {
        return redis.getOrNull(userId.toString())?.split(SESSION_AND_AUTH_TOKEN_DELIMITER)?.last()
    }

    companion object {
        const val SESSION_AND_AUTH_TOKEN_DELIMITER = ":"
        fun generateSessionAndAuthTokenCombined(sessionToken: String, githubToken: String): String {
            return "$sessionToken$SESSION_AND_AUTH_TOKEN_DELIMITER$githubToken"
        }
    }
}
