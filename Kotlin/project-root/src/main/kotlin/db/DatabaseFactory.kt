package com.example.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import db.Users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init(jdbcUrl: String, user: String?, password: String?) {
        val config = HikariConfig().apply {
            this.jdbcUrl = jdbcUrl
            this.username = user
            this.password = password
            maximumPoolSize = 10
            driverClassName = "org.postgresql.Driver"
        }
        val ds = HikariDataSource(config)
        Database.connect(ds)
        transaction {
            SchemaUtils.createMissingTablesAndColumns(Users)
        }
    }
}
