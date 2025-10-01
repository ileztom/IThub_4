package com.example

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.models.Users
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SchemaUtils
import com.example.routes.*
import io.ktor.server.response.*
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    val logger = LoggerFactory.getLogger("Application")

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            logger.error("Server error: ${cause.message}", cause)
            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to cause.message))
        }
    }

    Database.connect(
        url = "jdbc:postgresql://localhost:5432/ktor_demo",
        driver = "org.postgresql.Driver",
        user = "postgres",
        password = "password"
    )

    transaction {
        logger.info("Dropping and creating table Users")
        SchemaUtils.drop(Users) // Удаляем таблицу для пересоздания (только для разработки)
        SchemaUtils.create(Users)
        logger.info("Table Users created successfully")
    }

    install(Authentication) {
        jwt("auth-jwt") {
            val jwtVerifier = JWT.require(Algorithm.HMAC256("secret-key")).build()
            verifier(jwtVerifier)
            validate { credential ->
                val userId = credential.payload.getClaim("userId").asInt()
                if (userId != null) {
                    JWTPrincipal(credential.payload)
                } else null
            }
        }
    }

    routing {
        createUserRoute()
        authenticate("auth-jwt") {
            protectedUserRoutes()
        }
        loginRoute()
    }
}