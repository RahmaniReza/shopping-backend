package com.reza.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.callid.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.origin
import io.ktor.server.request.*
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import java.util.UUID

fun Application.configureLogging() {
    // Install CallId to assign a unique ID to each request
    install(CallId) {
        header(HttpHeaders.XRequestId)
        generate { UUID.randomUUID().toString() }
        verify { callId: String -> callId.isNotEmpty() }
        replyToHeader(HttpHeaders.XRequestId)
    }

    // Install CallLogging for structured, informative HTTP access logs
    install(CallLogging) {
        level = Level.INFO
        logger = LoggerFactory.getLogger("HttpCallLogger")

        // Populate SLF4J MDC (Mapped Diagnostic Context) so all logs within this request include these attributes
        callIdMdc("requestId")
        mdc("httpMethod") { call -> call.request.httpMethod.value }
        mdc("uri") { call -> call.request.uri }
        mdc("user") { call ->
            call.principal<JWTPrincipal>()?.payload?.getClaim("email")?.asString() ?: "anonymous"
        }
        mdc("clientIp") { call ->
            call.request.origin.remoteHost
        }

        // Optional: filter out noise such as health check endpoints or static assets
        filter { call ->
            !call.request.path().startsWith("/health")
        }

        // Custom, human-readable yet informative log format
        format { call ->
            val status = call.response.status()
            val method = call.request.httpMethod.value
            val uri = call.request.uri
            val duration = call.processingTimeMillis()
            val user = call.principal<JWTPrincipal>()?.payload?.getClaim("email")?.asString() ?: "anonymous"
            val clientIp = call.request.origin.remoteHost
            val callId = call.callId ?: "-"
            val userAgent = call.request.headers[HttpHeaders.UserAgent] ?: "-"

            "HTTP $method $uri -> ${status?.value ?: "-"} ${status?.description ?: ""} in ${duration}ms [User: $user | IP: $clientIp | CallId: $callId | UA: $userAgent]"
        }
    }
}
