package com.example.routes

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.request.*
import io.ktor.http.*
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.models.*
import java.sql.SQLException
import java.util.Date

fun Route.createUserRoute() {
    post("/users") {
        try {
            val dto = call.receive<CreateUserDto>()
            val user = createUser(dto)
            call.respond(HttpStatusCode.Created, user)
        } catch (e: SQLException) {
            if (e.message?.contains("unique") == true || e.message?.contains("duplicate") == true) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Email already exists"))
            } else {
                throw e
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
        }
    }
}

fun Route.protectedUserRoutes() {
    get("/users") {
        val emailFilter = call.request.queryParameters["email"]
        val users = getAllUsers(emailFilter)
        call.respond(HttpStatusCode.OK, users)
    }

    get("/users/{id}") {
        val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid ID"))
        val user = getUserById(id) ?: return@get call.respond(HttpStatusCode.NotFound, mapOf("error" to "User not found"))
        call.respond(HttpStatusCode.OK, user)
    }

    delete("/users/{id}") {
        val id = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid ID"))
        if (!deleteUser(id)) {
            call.respond(HttpStatusCode.NotFound, mapOf("error" to "User not found"))
        } else {
            call.respond(HttpStatusCode.NoContent)
        }
    }
}

fun Route.loginRoute() {
    post("/login") {
        val dto = call.receive<LoginDto>()
        val userId = authenticateUser(dto.email, dto.password) ?: return@post call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid credentials"))
        val token = JWT.create()
            .withSubject("auth")
            .withClaim("userId", userId)
            .withExpiresAt(Date(System.currentTimeMillis() + 60000 * 60)) // 1 час
            .sign(Algorithm.HMAC256("secret-key"))
        call.respond(HttpStatusCode.OK, LoginResponse(token))
    }
}