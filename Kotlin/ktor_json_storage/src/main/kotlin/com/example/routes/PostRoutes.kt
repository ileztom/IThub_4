
package com.example.routes

import com.example.service.PostService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class PostCreate(val title: String, val body: String, val date: String? = null)

@Serializable
data class Post(val id: Int, val title: String, val body: String, val date: String? = null)

fun Route.registerPostRoutes(service: PostService) {
    route("/posts") {
        get {
            call.respond(service.getAll())
        }
        post {
            val payload = try { call.receive<PostCreate>() } catch (e: Exception) {
                return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid body"))
            }
            val created = service.create(payload)
            call.respond(HttpStatusCode.Created, created)
        }
        route("/{id}") {
            get {
                val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "id required"))
                service.get(id)?.let { call.respond(it) } ?: call.respond(HttpStatusCode.NotFound, mapOf("error" to "Post not found"))
            }
            put {
                val id = call.parameters["id"]?.toIntOrNull() ?: return@put call.respond(HttpStatusCode.BadRequest, mapOf("error" to "id required"))
                val payload = try { call.receive<PostCreate>() } catch (e: Exception) {
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
