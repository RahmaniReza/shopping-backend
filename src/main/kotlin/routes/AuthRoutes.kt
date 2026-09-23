package com.reza.routes

import com.reza.models.*
import com.reza.repository.AuthRepository
import com.reza.security.JwtConfig
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.authRoutes() {
    val authRepository by inject<AuthRepository>()

    route("/auth") {

        post("/register") {
            val req = call.receive<AuthRequest>()

            if (authRepository.findUserByEmail(req.email) != null) {
                call.respond(HttpStatusCode.Conflict, GenericResponse("Email already registered"))
                return@post
            }

            authRepository.createUser(req)
            val token = JwtConfig.generateToken(req.email)
            call.respond(HttpStatusCode.Created, AuthResponse(token, req.email))
        }

        post("/login") {
            val req = call.receive<AuthRequest>()
            val user = authRepository.findUserByEmail(req.email)

            if (user == null || !JwtConfig.verifyPassword(req.password, user.passwordHash)) {
                call.respond(HttpStatusCode.Unauthorized, GenericResponse("Invalid credentials"))
                return@post
            }

            val token = JwtConfig.generateToken(req.email)
            call.respond(HttpStatusCode.OK, AuthResponse(token, req.email))
        }

        post("/forgot-password") {
            val req = call.receive<ForgotPasswordRequest>()
            val user = authRepository.findUserByEmail(req.email)

            if (user == null) {
                call.respond(HttpStatusCode.NotFound, GenericResponse("User not found"))
                return@post
            }

            val resetToken = authRepository.saveResetToken(req.email)
            call.respond(HttpStatusCode.OK, ForgotPasswordResponse("Reset token generated", resetToken))
        }
    }
}