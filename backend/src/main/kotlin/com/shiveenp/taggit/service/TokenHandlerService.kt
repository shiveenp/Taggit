package com.shiveenp.taggit.service

import com.shiveenp.taggit.config.ExternalProperties
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import java.util.*

@Service
class TokenHandlerService(private val redis: RedisService, private val externalProperties: ExternalProperties) {
    fun saveUserIdAndGetJwt(userId: UUID, authToken: String): String {
        val jwt = Jwts.builder()
            .setSubject(userId.toString())
            .setIssuer("taggit")
            .setIssuedAt(Date())
            .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(externalProperties.jwtSigningKey)))
            .compact()
        redis.put(userId.toString(), generateJwtAndGithubTokenCombined(jwt, authToken))
        return jwt
    }

    fun getSessionTokenFromUserIdOrNull(userId: UUID): String? {
        return redis.getOrNull(userId.toString())?.split(SESSION_AND_AUTH_TOKEN_DELIMITER)?.first()
    }

    fun getAuthTokenFromUserIdOrNull(userId: UUID): String? {
        return redis.getOrNull(userId.toString())?.split(SESSION_AND_AUTH_TOKEN_DELIMITER)?.last()
    }

    companion object {
        const val SESSION_AND_AUTH_TOKEN_DELIMITER = ":"
        fun generateJwtAndGithubTokenCombined(sessionToken: String, githubToken: String): String {
            return "$sessionToken$SESSION_AND_AUTH_TOKEN_DELIMITER$githubToken"
        }
    }
}
