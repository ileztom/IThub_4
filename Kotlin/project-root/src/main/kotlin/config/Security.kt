package com.example.config

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.auth.*

data class JwtConfig(val secret: String, val issuer: String, val audience: String, val realm: String)

fun Application.configureSecurity(jwtConfig: JwtConfig) {
    install(Authentication) {
        jwt("auth-jwt") {
            realm = jwtConfig.realm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtConfig.secret))
                    .withAudience(jwtConfig.audience)
                    .withIssuer(jwtConfig.issuer)
                    .build()
            )
            validate { credential ->
                val username = credential.payload.getClaim("username").asString()
                val role = credential.payload.getClaim("role").asString()
                if (username != null && role != null) {
                    JWTPrincipal(credential.payload)
                } else null
            }
        }
    }
}

fun generateToken(secret: String, issuer: String, audience: String, username: String, role: String, expiresSec: Long): String {
    val algo = Algorithm.HMAC256(secret)
    val now = System.currentTimeMillis() / 1000
    return JWT.create()
        .withIssuer(issuer)
        .withAudience(audience)
        .withClaim("username", username)
        .withClaim("role", role)
        .withExpiresAt(java.util.Date((now + expiresSec) * 1000))
        .sign(algo)
}
