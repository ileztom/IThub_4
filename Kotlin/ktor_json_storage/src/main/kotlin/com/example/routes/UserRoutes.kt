
package com.example.routes

import com.example.service.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class UserCreate(val name: String, val email: String, val role: String)

@Serializable
data class User(val id: Int, val name: String, val email: String, val role: String)

fun Route.registerUserRoutes(service: UserService) {
    route("/users") {
        get { call.respond(service.getAll()) }
        post {
            val payload = try { call.receive<UserCreate>() } catch (e: Exception) {
                return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid body"))
            }
            val created = service.create(payload)
            call.respond(HttpStatusCode.Created, created)
        }
        route("/{id}") {
            get {
                val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "id required"))
                service.get(id)?.let { call.respond(it) } ?: call.respond(HttpStatusCode.NotFound, mapOf("error" to "User not found"))
            }
            put {
                val id = call.parameters["id"]?.toIntOrNull() ?: return@put call.respond(HttpStatusCode.BadRequest, mapOf("error" to "id required"))
                val payload = try { call.receive<UserCreate>() } catch (e: Exception) {
                    return@put call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid body"))
                }
                val updated = service.update(id, payload)
                if (updated) call.respond(HttpStatusCode.OK, mapOf("updated" to true)) else call.respond(HttpStatusCode.NotFound, mapOf("error" to "Not found"))
            }
            delete {
                val id = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respond(HttpStatusCode.BadRequest, mapOf("error" to "id required"))
                val removed = service.delete(id)
                call.respond(mapOf("deleted" to removed))
            }
        }
    }
}
