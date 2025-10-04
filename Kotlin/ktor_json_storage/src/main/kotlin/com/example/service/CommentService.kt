
package com.example.service

import com.example.routes.Comment
import com.example.routes.CommentCreate
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import io.ktor.server.config.*

class CommentService(config: ApplicationConfig) {
    private val dataFile = File(System.getProperty("user.dir"), "data/comments.json")
    private val json = Json { prettyPrint = true }

    init {
        dataFile.parentFile.mkdirs()
        if (!dataFile.exists()) dataFile.writeText("[]")
    }

    @Synchronized
    fun getAll(): List<Comment> = json.decodeFromString(dataFile.readText())

    @Synchronized
    fun create(payload: CommentCreate): Comment {
        val list = json.decodeFromString<MutableList<Comment>>(dataFile.readText())
        val id = (list.maxOfOrNull { it.id } ?: 0) + 1
        val c = Comment(id = id, postId = payload.postId, author = payload.author, content = payload.content, date = payload.date)
        list.add(c)
        dataFile.writeText(json.encodeToString(list))
        return c
    }

    @Synchronized
    fun get(id: Int): Comment? {
        val list = json.decodeFromString<List<Comment>>(dataFile.readText())
        return list.find { it.id == id }
    }

    @Synchronized
    fun update(id: Int, payload: CommentCreate): Boolean {
        val list = json.decodeFromString<MutableList<Comment>>(dataFile.readText())
        val idx = list.indexOfFirst { it.id == id }
        if (idx == -1) return false
        list[idx] = Comment(id = id, postId = payload.postId, author = payload.author, content = payload.content, date = payload.date)
        dataFile.writeText(json.encodeToString(list))
        return true
    }

    @Synchronized
    fun delete(id: Int): Boolean {
        val list = json.decodeFromString<MutableList<Comment>>(dataFile.readText())
        val removed = list.removeIf { it.id == id }
        dataFile.writeText(json.encodeToString(list))
        return removed
    }
}
