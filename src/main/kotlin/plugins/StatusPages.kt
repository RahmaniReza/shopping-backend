package com.reza.plugins

import com.reza.models.GenericResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.slf4j.LoggerFactory

fun Application.configureStatusPages() {
    val logger = LoggerFactory.getLogger("StatusPages")

    install(StatusPages) {
        exception<BadRequestException> { call, cause ->
            logger.warn("Bad request on [{} {}]: {}", call.request.httpMethod.value, call.request.uri, cause.message)
            call.respond(HttpStatusCode.BadRequest, GenericResponse(cause.message ?: "Invalid request"))
        }

        exception<Throwable> { call, cause ->
            logger.error("Unhandled error processing [{} {}]: {}", call.request.httpMethod.value, call.request.uri, cause.message, cause)
            call.respond(HttpStatusCode.InternalServerError, GenericResponse("An unexpected server error occurred"))
        }

        status(HttpStatusCode.NotFound) { call, status ->
            logger.warn("Route not found: [{} {}]", call.request.httpMethod.value, call.request.uri)
            call.respond(status, GenericResponse("Route '${call.request.uri}' not found"))
        }
    }
}
