package db

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object Users : Table("users") {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 50).uniqueIndex()
    val password = varchar("password", 64)
    val role = varchar("role", 10).default("user")
    val createdAt = datetime("created_at")

    override val primaryKey = PrimaryKey(id)
}

object Messages : Table("messages") {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 50)
    val content = text("content")
    val timestamp = datetime("timestamp")

    override val primaryKey = PrimaryKey(id)
}
