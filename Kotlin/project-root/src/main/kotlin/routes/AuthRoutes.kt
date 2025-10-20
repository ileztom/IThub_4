package routes

import db.Users
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import utils.JwtConfig
import java.time.LocalDateTime

data class RegisterRequest(val username: String, val password: String)
data class LoginRequest(val username: String, val password: String)

fun Route.authRoutes() {
    route("/auth") {

        post("/register") {
            val request = call.receive<RegisterRequest>()
            val hashedPassword = BCrypt.hashpw(request.password, BCrypt.gensalt())

            val exists = transaction {
                Users.select { Users.username eq request.username }.count() > 0
            }
            if (exists) {
                call.respondText("User already exists")
                return@post
            }

            transaction {
                Users.insert {
                    it[username] = request.username
                    it[password] = hashedPassword
                    it[role] = "user"
                    it[createdAt] = LocalDateTime.now()
                }
            }

            call.respondText("User registered successfully")
        }

        post("/login") {
            val request = call.receive<LoginRequest>()
            val user = transaction {
                Users.select { Users.username eq request.username }.singleOrNull()
            }

            if (user == null || !BCrypt.checkpw(request.password, user[Users.password])) {
                call.respondText("Invalid credentials")
                return@post
            }

            val token = JwtConfig.generateToken(request.username, user[Users.role])
            call.respond(mapOf("token" to token))
        }

        authenticate {
            get("/me") {
                val principal = call.principal<JWTPrincipal>()
                val username = principal!!.payload.getClaim("username").asString()
                val role = principal.payload.getClaim("role").asString()
                call.respond(mapOf("username" to username, "role" to role))
            }
        }
    }
}
