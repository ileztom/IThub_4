package plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import utils.JwtConfig

fun Application.configureAuth() {
    install(Authentication) {
        jwt {
            verifier(JwtConfig.verifier)
            validate { credential ->
                val username = credential.payload.getClaim("username").asString()
                if (username.isNotBlank()) JWTPrincipal(credential.payload) else null
            }
        }
    }
}
