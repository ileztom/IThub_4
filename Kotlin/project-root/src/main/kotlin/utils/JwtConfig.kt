package utils

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

object JwtConfig {
    private const val secret = "super_secret_jwt_key" // ⚠️ Замени на более надёжный ключ
    private const val issuer = "ktor-server"
    private const val audience = "ktor-users"
    private const val validityInMs = 36_000_00 * 24 // 24 часа

    private val algorithm = Algorithm.HMAC256(secret)

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .withAudience(audience)
        .build()

    fun generateToken(username: String, role: String): String {
        val now = System.currentTimeMillis()
        return JWT.create()
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim("username", username)
            .withClaim("role", role)
            .withExpiresAt(Date(now + validityInMs))
            .sign(algorithm)
    }
}
