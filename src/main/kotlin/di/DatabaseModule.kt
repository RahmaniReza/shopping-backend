package com.reza.di

import com.reza.plugins.DatabaseConfig
import com.reza.plugins.DatabaseFactory
import org.koin.dsl.module

val databaseModule = module {
    single {
        DatabaseFactory(
            // Load settings from environment or Ktor application.conf
            DatabaseConfig(
                driver = System.getenv("DB_DRIVER") ?: "org.postgresql.Driver",
                url = System.getenv("DB_URL") ?: "jdbc:postgresql://localhost:5432/shopping_db",
                user = System.getenv("DB_USER") ?: "postgres",
                pass = System.getenv("DB_PASSWORD") ?: "your_password"
            )
        )
    }
}