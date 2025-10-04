
package com.example.service

import com.example.routes.User
import com.example.routes.UserCreate
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import io.ktor.server.config.*

class UserService(config: ApplicationConfig) {
    private val dataFile = File(System.getProperty("user.dir"), "data/users.json")
    private val json = Json { prettyPrint = true }

    init {
        dataFile.parentFile.mkdirs()
        if (!dataFile.exists()) dataFile.writeText("[]")
    }

    @Synchronized
    fun getAll(): List<User> = json.decodeFromString(dataFile.readText())

    @Synchronized
    fun create(payload: UserCreate): User {
        val list = json.decodeFromString<MutableList<User>>(dataFile.readText())
        val id = (list.maxOfOrNull { it.id } ?: 0) + 1
        val user = User(id = id, name = payload.name, email = payload.email, role = payload.role)
        list.add(user)
        dataFile.writeText(json.encodeToString(list))
        return user
    }

    @Synchronized
    fun get(id: Int): User? {
        val list = json.decodeFromString<List<User>>(dataFile.readText())
        return list.find { it.id == id }
    }

    @Synchronized
    fun update(id: Int, payload: UserCreate): Boolean {
        val list = json.decodeFromString<MutableList<User>>(dataFile.readText())
        val idx = list.indexOfFirst { it.id == id }
        if (idx == -1) return false
        list[idx] = User(id = id, name = payload.name, email = payload.email, role = payload.role)
        dataFile.writeText(json.encodeToString(list))
        return true
    }

    @Synchronized
    fun delete(id: Int): Boolean {
        val list = json.decodeFromString<MutableList<User>>(dataFile.readText())
        val removed = list.removeIf { it.id == id }
        dataFile.writeText(json.encodeToString(list))
        return removed
    }
}
