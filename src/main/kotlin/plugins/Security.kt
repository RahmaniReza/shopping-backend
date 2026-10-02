package com.reza.plugins

import com.reza.security.JwtService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import org.koin.ktor.ext.inject

fun Application.configureSecurity() {
    val jwtService by inject<JwtService>()

    authentication {
        jwt("auth-jwt") {
            verifier(jwtService.makeVerifier())
            validate { credential ->
                val email = credential.payload.getClaim("email").asString()
                if (!email.isNullOrEmpty()) {
                    JWTPrincipal(credential.payload)
                } else null
            }
        }
    }
}