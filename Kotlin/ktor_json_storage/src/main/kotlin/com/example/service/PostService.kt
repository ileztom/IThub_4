
package com.example.service

import com.example.routes.Post
import com.example.routes.PostCreate
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import io.ktor.server.config.*

class PostService(config: ApplicationConfig) {
    private val dataFile = File(System.getProperty("user.dir"), "data/posts.json")
    private val json = Json { prettyPrint = true }

    init {
        dataFile.parentFile.mkdirs()
        if (!dataFile.exists()) dataFile.writeText("[]")
    }

    @Synchronized
    fun getAll(): List<Post> = json.decodeFromString(dataFile.readText())

    @Synchronized
    fun create(payload: PostCreate): Post {
        val list = json.decodeFromString<MutableList<Post>>(dataFile.readText())
        val id = (list.maxOfOrNull { it.id } ?: 0) + 1
        val post = Post(id = id, title = payload.title, body = payload.body, date = payload.date)
        list.add(post)
        dataFile.writeText(json.encodeToString(list))
        return post
    }

    @Synchronized
    fun get(id: Int): Post? {
        val list = json.decodeFromString<List<Post>>(dataFile.readText())
        return list.find { it.id == id }
    }

    @Synchronized
    fun update(id: Int, payload: PostCreate): Boolean {
        val list = json.decodeFromString<MutableList<Post>>(dataFile.readText())
        val idx = list.indexOfFirst { it.id == id }
        if (idx == -1) return false
        list[idx] = Post(id = id, title = payload.title, body = payload.body, date = payload.date)
        dataFile.writeText(json.encodeToString(list))
        return true
    }

    @Synchronized
    fun delete(id: Int): Boolean {
        val list = json.decodeFromString<MutableList<Post>>(dataFile.readText())
        val removed = list.removeIf { it.id == id }
        dataFile.writeText(json.encodeToString(list))
        return removed
    }
}
