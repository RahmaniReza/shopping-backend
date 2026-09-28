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
import org.koin.ktor.ext.inject

class DatabaseFactory(private val config: DatabaseConfig) {

    fun init() {
        val hikariConfig = HikariConfig().apply {
            driverClassName = config.driver
            jdbcUrl = config.url
            username = config.user
            password = config.pass
            maximumPoolSize = config.maxPoolSize
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }

        val dataSource = HikariDataSource(hikariConfig)
        Database.connect(dataSource)

        transaction {
            SchemaUtils.create(UsersTable, CategoriesTable, ProductsTable)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}

fun Application.configureDatabase() {
    val databaseFactory: DatabaseFactory by inject()
    databaseFactory.init()
}

data class DatabaseConfig(
    val driver: String,
    val url: String,
    val user: String,
    val pass: String,
    val maxPoolSize: Int = 10
)