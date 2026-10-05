package com.reza.plugins

import com.reza.models.GenericResponse
import com.reza.routes.authRoutes
import com.reza.routes.catalogRoutes
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respond(HttpStatusCode.OK, GenericResponse("Shopping Backend API is running"))
        }

        get("/health") {
            call.respond(HttpStatusCode.OK, GenericResponse("UP"))
        }

        authRoutes()
        catalogRoutes()
    }
}