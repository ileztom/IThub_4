package com.example.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.dao.*
import java.sql.SQLException
import com.example.utils.hashPassword
import com.example.utils.verifyPassword
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable

object Users : IdTable<Int>("users") {
    override val id = integer("id").autoIncrement().entityId()
    val name = varchar("name", 255)
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
}

@Serializable
data class UserDto(
    val id: Int,
    val name: String,
    val email: String
)

@Serializable
data class CreateUserDto(
    val name: String,
    val email: String,
    val password: String
)

@Serializable
data class LoginDto(
    val email: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val token: String
)

class UserEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserEntity>(Users)

    var name by Users.name
    var email by Users.email
    var passwordHash by Users.passwordHash
}

fun createUser(dto: CreateUserDto): UserDto = transaction {
    val hashed = hashPassword(dto.password)
    val newUser = UserEntity.new {
        name = dto.name
        email = dto.email
        passwordHash = hashed
    }
    UserDto(newUser.id.value, newUser.name, newUser.email)
}

fun getAllUsers(emailFilter: String? = null): List<UserDto> = transaction {
    val selectQuery = if (emailFilter != null) {
        Users.select { Users.email eq emailFilter }
    } else {
        Users.selectAll()
    }
    UserEntity.wrapRows(selectQuery).map { UserDto(it.id.value, it.name, it.email) }
}

fun getUserById(id: Int): UserDto? = transaction {
    UserEntity.findById(id)?.let { UserDto(it.id.value, it.name, it.email) }
}

fun deleteUser(id: Int): Boolean = transaction {
    UserEntity.findById(id)?.delete() != null
}

fun authenticateUser(email: String, password: String): Int? = transaction {
    val user = UserEntity.find { Users.email eq email }.singleOrNull() ?: return@transaction null
    if (verifyPassword(password, user.passwordHash)) user.id.value else null
}