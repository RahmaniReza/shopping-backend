package com.reza.plugins

import com.reza.di.appModules
import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureDependencyInjection() {
    install(Koin) {
        slf4jLogger()
        modules(appModules(this@configureDependencyInjection))
    }
}