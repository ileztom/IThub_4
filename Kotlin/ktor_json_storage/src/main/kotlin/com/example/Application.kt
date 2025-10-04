package com.example

import com.example.routes.registerPostRoutes
import com.example.routes.registerUserRoutes
import com.example.routes.registerCommentRoutes
import com.example.service.PostService
import com.example.service.UserService
import com.example.service.CommentService
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.event.Level

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        module()
    }.start(wait = true)
}

fun Application.module() {
    install(CallLogging) { level = Level.INFO }
    install(ContentNegotiation) { json() }

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            this@module.environment.log.error("Unhandled exception:", cause)
            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to (cause.message ?: "unknown")))
        }
        status(HttpStatusCode.NotFound) { call, _ ->
            call.respond(HttpStatusCode.NotFound, mapOf("error" to "Resource not found"))
        }
    }

    // Создаём сервисы через Application.environment
    val postService = PostService(this@module.environment.config)
    val userService = UserService(this@module.environment.config)
    val commentService = CommentService(this@module.environment.config)

    routing {
        // Swagger UI на корне
        get("/") {
            val html = """<!doctype html>
            <html>
              <head>
                <meta charset="utf-8"/>
                <title>Swagger UI</title>
                <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/swagger-ui-dist@4.18.3/swagger-ui.css"/>
              </head>
              <body>
                <div id="swagger-ui"></div>
                <script src="https://cdn.jsdelivr.net/npm/swagger-ui-dist@4.18.3/swagger-ui-bundle.js"></script>
                <script>
                  window.onload = function() {
                    const ui = SwaggerUIBundle({
                      url: '/openapi.json',
                      dom_id: '#swagger-ui',
                      presets: [SwaggerUIBundle.presets.apis],
                      layout: 'BaseLayout'
                    });
                    window.ui = ui;
                  };
                </script>
              </body>
            </html>""".trimIndent()
            call.respondText(html, ContentType.Text.Html)
        }

        // отдаём openapi.json
        get("/openapi.json") {
            val stream = this::class.java.classLoader.getResourceAsStream("openapi.json")
                ?: return@get call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "openapi.json not found"))
            val text = stream.bufferedReader().readText()
            call.respondText(text, ContentType.Application.Json)
        }

        // Регистрируем маршруты
        registerPostRoutes(postService)
        registerUserRoutes(userService)
        registerCommentRoutes(commentService)
    }
}
