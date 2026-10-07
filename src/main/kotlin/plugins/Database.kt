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
    private var dataSource: HikariDataSource? = null
    private var database: Database? = null

    fun init() {
        val hikariConfig = HikariConfig().apply {
            driverClassName = config.driver
            jdbcUrl = config.url
            username = config.user
            password = config.pass
            maximumPoolSize = config.maxPoolSize
            isAutoCommit = false
            transactionIsolation = config.transactionIsolation
            validate()
        }

        val ds = HikariDataSource(hikariConfig)
        dataSource = ds
        database = Database.connect(ds)

        transaction(database!!) {
            SchemaUtils.create(UsersTable, CategoriesTable, ProductsTable)
        }
    }

    fun close() {
        dataSource?.close()
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO, db = database) { block() }
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
    val maxPoolSize: Int = 10,
    val transactionIsolation: String = "TRANSACTION_REPEATABLE_READ"
)