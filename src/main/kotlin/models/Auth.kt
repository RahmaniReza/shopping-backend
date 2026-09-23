package com.reza.models

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(val email: String, val password: String)

@Serializable
data class AuthResponse(val token: String, val email: String)

@Serializable
data class ForgotPasswordRequest(val email: String)

@Serializable
data class ForgotPasswordResponse(val message: String, val resetToken: String? = null)

@Serializable
data class GenericResponse(val message: String)