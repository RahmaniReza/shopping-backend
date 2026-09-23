package com.reza.security

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import org.mindrot.jbcrypt.BCrypt
import java.util.Date

interface JwtService {
    fun generateToken(email: String): String
    fun makeVerifier(): JWTVerifier
    fun hashPassword(password: String): String
    fun verifyPassword(password: String, hash: String): Boolean
}

class JwtServiceImpl(
    private val secret: String,
    private val issuer: String,
    private val validityInMs: Long = 3600_000L * 24 * 7 // 7 Days
) : JwtService {

    private val algorithm: Algorithm = Algorithm.HMAC256(secret)

    override fun generateToken(email: String): String {
        return JWT.create()
            .withIssuer(issuer)
            .withClaim("email", email)
            .withExpiresAt(Date(System.currentTimeMillis() + validityInMs))
            .sign(algorithm)
    }

    override fun makeVerifier(): JWTVerifier = JWT.require(algorithm)
        .withIssuer(issuer)
        .build()

    override fun hashPassword(password: String): String =
        BCrypt.hashpw(password, BCrypt.gensalt())

    override fun verifyPassword(password: String, hash: String): Boolean =
        BCrypt.checkpw(password, hash)
}