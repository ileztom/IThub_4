package com.example

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import routes.wsRoutes

fun Application.configureRouting() {
    routing {
        authenticate {
            wsRoutes()
        }
    }
}
