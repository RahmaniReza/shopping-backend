package com.reza.plugins

import com.reza.routes.authRoutes
import com.reza.routes.catalogRoutes
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        authRoutes()
        catalogRoutes()
    }
}