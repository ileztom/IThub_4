package com.example

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.http.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

@Serializable
data class PostItem(
    val id: Long,
    val title: String,
    val content: String,
    val tags: List<String> = emptyList()
)

@Serializable
data class CreatePostRequest(
    val title: String,
    val content: String,
    val tags: List<String> = emptyList()
)

class InMemoryRepo {
    private val idCounter = AtomicLong(1)
    private val store = ConcurrentHashMap<Long, PostItem>()

    fun all(): List<PostItem> = store.values.sortedBy { it.id }

    fun find(id: Long): PostItem? = store[id]

    fun create(req: CreatePostRequest): PostItem {
        val id = idCounter.getAndIncrement()
        val item = PostItem(id = id, title = req.title, content = req.content, tags = req.tags)
        store[id] = item
        return item
    }

    fun delete(id: Long): Boolean = store.remove(id) != null

    // helper for query search by tag or title contains
    fun query(tag: String?, titleContains: String?): List<PostItem> {
        return all().filter { item ->
            (tag == null || item.tags.contains(tag)) &&
                    (titleContains == null || item.title.contains(titleContains, ignoreCase = true))
        }
    }
}

fun main() {
    val repo = InMemoryRepo()

    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }

        install(Routing) {
            route("/posts") {
                // GET /posts?tag=tagName&title=substring
                get {
                    val tag = call.request.queryParameters["tag"]
                    val titleContains = call.request.queryParameters["title"]
                    val result = repo.query(tag, titleContains)
                    call.respond(HttpStatusCode.OK, result)
                }

                // GET /posts/{id}
                get("{id}") {
                    val idParam = call.parameters["id"]
                    val id = idParam?.toLongOrNull()
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid id parameter"))
                        return@get
                    }
                    val post = repo.find(id)
                    if (post == null) {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "Post not found"))
                        return@get
                    }
                    call.respond(HttpStatusCode.OK, post)
                }

                // POST /posts
                post {
                    val req = try {
                        call.receive<CreatePostRequest>()
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid JSON: ${e.message}"))
                        return@post
                    }

                    if (req.title.isBlank() || req.content.isBlank()) {
                        call.respond(HttpStatusCode.UnprocessableEntity, mapOf("error" to "title and content must not be empty"))
                        return@post
                    }

                    val created = repo.create(req)
                    call.respond(HttpStatusCode.Created, created)
                }

                // DELETE /posts/{id}
                delete("{id}") {
                    val idParam = call.parameters["id"]
                    val id = idParam?.toLongOrNull()
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid id parameter"))
                        return@delete
                    }
                    val removed = repo.delete(id)
                    if (!removed) {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "Post not found"))
                        return@delete
                    }
                    call.respond(HttpStatusCode.NoContent) // успешное удаление — 204 No Content
                }
            }

            // health-check
            get("/health") {
                call.respond(HttpStatusCode.OK, mapOf("status" to "ok"))
            }
        }
    }.start(wait = true)
}
