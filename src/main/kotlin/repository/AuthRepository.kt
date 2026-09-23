package com.reza.repository

import com.reza.db.UsersTable
import com.reza.models.AuthRequest
import com.reza.plugins.DatabaseFactory.dbQuery
import com.reza.security.JwtConfig
import org.jetbrains.exposed.sql.*
import java.util.UUID

data class UserEntity(
    val id: Int,
    val email: String,
    val passwordHash: String,
    val resetToken: String?
)

interface AuthRepository {
    suspend fun findUserByEmail(email: String): UserEntity?
    suspend fun createUser(request: AuthRequest): UserEntity
    suspend fun saveResetToken(email: String): String
}

class ExposedAuthRepository : AuthRepository {

    override suspend fun findUserByEmail(email: String): UserEntity? = dbQuery {
        UsersTable.selectAll()
            .where { UsersTable.email eq email }
            .map { it.toUserEntity() }
            .singleOrNull()
    }

    override suspend fun createUser(request: AuthRequest): UserEntity = dbQuery {
        val hashedPw = JwtConfig.hashPassword(request.password)
        val insertedId = UsersTable.insertAndGetId {
            it[this.email] = request.email
            it[this.passwordHash] = hashedPw
        }
        UserEntity(insertedId.value, request.email, hashedPw, null)
    }

    override suspend fun saveResetToken(email: String): String = dbQuery {
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