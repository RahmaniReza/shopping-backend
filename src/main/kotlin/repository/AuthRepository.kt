package com.reza.repository

import com.reza.db.UsersTable
import com.reza.models.AuthRequest
import com.reza.plugins.DatabaseFactory
import com.reza.security.JwtService
import org.jetbrains.exposed.sql.*
import java.util.*

interface AuthRepository {
    suspend fun findUserByEmail(email: String): UserEntity?
    suspend fun createUser(request: AuthRequest): UserEntity
    suspend fun saveResetToken(email: String): String
}

class ExposedAuthRepository(
    private val jwtService: JwtService,
    private val dbFactory: DatabaseFactory
) :
    AuthRepository {

    override suspend fun findUserByEmail(email: String): UserEntity? = dbFactory.dbQuery {
        UsersTable.selectAll()
            .where { UsersTable.email eq email }
            .map { it.toUserEntity() }
            .singleOrNull()
    }

    override suspend fun createUser(request: AuthRequest): UserEntity = dbFactory.dbQuery {
        val hashedPw = jwtService.hashPassword(request.password)
        val insertedId = UsersTable.insertAndGetId {
            it[this.email] = request.email
            it[this.passwordHash] = hashedPw
        }
        UserEntity(insertedId.value, request.email, hashedPw, null)
    }

    override suspend fun saveResetToken(email: String): String = dbFactory.dbQuery {
        val token = UUID.randomUUID().toString()
        UsersTable.update({ UsersTable.email eq email }) {
            it[UsersTable.resetToken] = token
        }
        token
    }

    private fun ResultRow.toUserEntity() = UserEntity(
        id = this[UsersTable.id].value,
        email = this[UsersTable.email],
        passwordHash = this[UsersTable.passwordHash],
        resetToken = this[UsersTable.resetToken]
    )
}

data class UserEntity(
    val id: Int,
    val email: String,
    val passwordHash: String,
    val resetToken: String?
)