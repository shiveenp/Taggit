package com.shiveenp.taggit.security

import org.springframework.security.crypto.encrypt.Encryptors
import org.springframework.security.crypto.keygen.KeyGenerators
import org.springframework.stereotype.Service


@Service
class EncryptorService: Encryptor {
    override fun encrypt(plainMessage: String, cipherKey: String): String {
        val salt = KeyGenerators.string().generateKey()
        val textEncryptor =  Encryptors.text(cipherKey, salt)
        val encryptedMessage = textEncryptor.encrypt(plainMessage)
        return "$encryptedMessage:$salt"
    }

    override fun decrypt(encryptedMessage: String, cipherKey: String): String {
        val actualEncryptedMessage = encryptedMessage.split(":").first()
        val salt = encryptedMessage.split(":").last()
        val decryptor = Encryptors.text(cipherKey, salt)
        return decryptor.decrypt(actualEncryptedMessage)
    }
}
