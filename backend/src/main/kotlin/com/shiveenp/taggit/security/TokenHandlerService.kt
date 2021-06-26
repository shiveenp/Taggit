package com.shiveenp.taggit.security

import com.shiveenp.taggit.config.ExternalProperties
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.io.Encoders
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import java.util.*

@Service
class TokenHandlerService(private val externalProperties: ExternalProperties) {

    fun saveUserIdAndGetJwt(userId: UUID, authToken: String): String {
        return Jwts.builder()
            .setSubject(userId.toString())
            .setIssuer("taggit")
            .setIssuedAt(Date())
            .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(externalProperties.jwtSigningKey)))
            .compact()
    }

    fun getUserIdFromJwtClaims(jwt: String): String? {
        return try {
            val claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(externalProperties.jwtSigningKey)))         // (2)
                .build()
                .parseClaimsJws(jwt)
            claims.body.get("sub", String::class.java)
        } catch (ex: JwtException) {
            null
        }
    }
}
