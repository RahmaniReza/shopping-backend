package com.reza.di

import com.reza.db.DatabaseSeeder
import com.reza.db.ExposedDatabaseSeeder
import com.reza.plugins.DatabaseConfig
import com.reza.plugins.DatabaseFactory
import io.ktor.server.application.ApplicationEnvironment
import org.koin.dsl.module

val databaseModule = module {
    single {
        val environment: ApplicationEnvironment = get()
        val config = environment.config.config("database")

        DatabaseFactory(
            DatabaseConfig(
                driver = config.property("driver").getString(),
                url = config.property("url").getString(),
                user = config.property("user").getString(),
                pass = config.property("password").getString(),
                autoSeed = config.propertyOrNull("autoSeed")?.getString()?.toBoolean() ?: true
            )
        )
    }

    single<DatabaseSeeder> {
        ExposedDatabaseSeeder(dbFactory = get())
    }
}
