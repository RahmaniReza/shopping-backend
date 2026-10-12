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
        var finalUrl = config.url
        var finalUser = config.user
        var finalPass = config.pass

        if (finalUrl.startsWith("postgres://") || finalUrl.startsWith("postgresql://")) {
            try {
                val cleanUri = java.net.URI(finalUrl.replace("postgres://", "http://").replace("postgresql://", "http://"))
                val userInfo = cleanUri.userInfo
                if (!userInfo.isNullOrBlank() && userInfo.contains(":")) {
                    val parts = userInfo.split(":", limit = 2)
                    finalUser = parts[0]
                    finalPass = parts[1]
                }
                val host = cleanUri.host
                val port = if (cleanUri.port != -1) cleanUri.port else 5432
                val path = cleanUri.path
                val query = if (cleanUri.query != null) "?${cleanUri.query}" else ""
                finalUrl = "jdbc:postgresql://$host:$port$path$query"
            } catch (_: Exception) {
                // Fall back to original URL
            }
        }

        val hikariConfig = HikariConfig().apply {
            driverClassName = config.driver
            jdbcUrl = finalUrl
            username = finalUser
            password = finalPass
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