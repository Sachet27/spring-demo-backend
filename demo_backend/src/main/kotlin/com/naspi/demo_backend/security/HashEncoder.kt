package com.naspi.demo_backend.security

import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
class HashEncoder {
    private val bCrypt = BCryptPasswordEncoder()

    fun encode(raw: String): String= bCrypt.encode(raw)

    fun checkMatch(raw: String, hashed: String): Boolean= bCrypt.matches(raw, hashed)
}