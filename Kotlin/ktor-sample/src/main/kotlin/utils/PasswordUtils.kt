package com.example.utils

import at.favre.lib.crypto.bcrypt.BCrypt

fun hashPassword(password: String): String {
    return BCrypt.withDefaults().hashToString(12, password.toCharArray())
}

fun verifyPassword(password: String, hash: String): Boolean {
    val result = BCrypt.verifyer().verify(password.toCharArray(), hash)
    return result.verified
}