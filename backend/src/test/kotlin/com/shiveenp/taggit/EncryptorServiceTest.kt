package com.shiveenp.taggit

import com.shiveenp.taggit.security.EncryptorService
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test

class EncryptorServiceTest {
    private val encryptorService = EncryptorService()

    @Test
    fun `should be able to encrypt properly`() {
        val message = "hello-this-is-me"
        val cipherKey = "82F940D07AAE76C9EC881E32B4DBA801"
        val encryptedMessage = encryptorService.encrypt(message, cipherKey)
        encryptedMessage shouldNotBe message
    }

    @Test
    fun `should be able to decrypt properly`() {
        val message = "hello-this-is-me"
        val cipherKey = "82F940D07AAE76C9EC881E32B4DBA801"
        val encryptedMessage = encryptorService.encrypt(message, cipherKey)
        val decryptedMessage = encryptorService.decrypt(encryptedMessage, cipherKey)
        decryptedMessage shouldBe message
    }
}
