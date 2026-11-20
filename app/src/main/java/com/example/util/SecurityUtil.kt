package com.example.util

import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object SecurityUtil {
    fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Generates a salted PBKDF2WithHmacSHA256 hash ("saltHex:hashHex") so the admin
     * password is never stored in plaintext and cannot be reversed or rainbow-tabled.
     */
    fun hashAdminPassword(password: String): String {
        val salt = ByteArray(16)
        SecureRandom().nextBytes(salt)
        val saltHex = salt.joinToString("") { "%02x".format(it) }
        val hashHex = pbkdf2(password, salt)
        return "$saltHex:$hashHex"
    }

    fun verifyAdminPassword(password: String, storedRecord: String): Boolean {
        val parts = storedRecord.split(":")
        if (parts.size != 2) {
            return sha256(password) == storedRecord
        }
        val saltHex = parts[0]
        val expectedHex = parts[1]
        val salt = ByteArray(saltHex.length / 2) { i ->
            saltHex.substring(i * 2, i * 2 + 2).toInt(16).toByte()
        }
        val actualHex = pbkdf2(password, salt)
        return MessageDigest.isEqual(
            actualHex.toByteArray(Charsets.UTF_8),
            expectedHex.toByteArray(Charsets.UTF_8)
        )
    }

    private fun pbkdf2(password: String, salt: ByteArray): String {
        val spec = PBEKeySpec(password.toCharArray(), salt, 20_000, 256)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val bytes = factory.generateSecret(spec).encoded
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
