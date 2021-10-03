package com.shiveenp.taggit

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isPositive
import assertk.assertions.isSuccess
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Encoders
import io.jsonwebtoken.security.Keys
import org.junit.jupiter.api.Test

/**
 * The purpose of this class is to quickly generate new secrets for taggit. This is not really a test per se.
 */
class EncryptionKeyGenerator {

    @Test
    fun `generate new JWT signing key`() {
        val key = Keys.secretKeyFor(SignatureAlgorithm.HS256)
        val encodedKey = Encoders.BASE64.encode(key.encoded)
        println("encoded Key is: $encodedKey")
        assertThat { 1 + 1 }.isSuccess().isPositive()
    }
}
