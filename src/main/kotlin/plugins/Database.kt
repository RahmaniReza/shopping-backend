package com.reza.plugins

import com.reza.db.CategoriesTable
import com.reza.db.ProductsTable
import com.reza.db.UsersTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        val config = HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            jdbcUrl = "jdbc:postgresql://localhost:5432/shopping_db"
            username = "postgres"
            password = "your_password"
            maximumPoolSize = 10
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
        val dataSource = HikariDataSource(config)
        Database.connect(dataSource)

        transaction {
            SchemaUtils.create(UsersTable, CategoriesTable, ProductsTable)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}

fun Application.configureDatabase() {
    DatabaseFactory.init()
}