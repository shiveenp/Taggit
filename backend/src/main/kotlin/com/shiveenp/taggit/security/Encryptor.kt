package com.shiveenp.taggit.security

interface Encryptor {
    fun encrypt(plainMessage: String, cipherKey: String): String
    fun decrypt(encryptedMessage: String, cipherKey: String): String
}
