package com.reza.repository

import com.reza.models.AuthRequest
import com.reza.security.JwtService
import com.reza.security.JwtServiceImpl
import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.exceptions.ExposedSQLException
import kotlin.test.*

class AuthRepositoryTest : BaseRepositoryTest() {

    private lateinit var jwtService: JwtService
    private lateinit var authRepository: AuthRepository

    @BeforeTest
    override fun setUp() {
        super.setUp()
        jwtService = JwtServiceImpl(
            secret = "test-secret-key-12345",
            issuer = "test.shopping.com"
        )
        authRepository = ExposedAuthRepository(jwtService, dbFactory)
    }

    @Test
    fun `createUser successfully creates user with hashed password and null resetToken`() = runTest {
        val request = AuthRequest(email = "test@example.com", password = "SecurePassword123!")

        val createdUser = authRepository.createUser(request)

        assertTrue(createdUser.id > 0)
        assertEquals("test@example.com", createdUser.email)
        assertNotEquals("SecurePassword123!", createdUser.passwordHash)
        assertTrue(jwtService.verifyPassword("SecurePassword123!", createdUser.passwordHash))
        assertNull(createdUser.resetToken)

        // Verify user can be retrieved from database
        val fetchedUser = authRepository.findUserByEmail("test@example.com")
        assertNotNull(fetchedUser)
        assertEquals(createdUser.id, fetchedUser.id)
        assertEquals(createdUser.email, fetchedUser.email)
        assertEquals(createdUser.passwordHash, fetchedUser.passwordHash)
        assertNull(fetchedUser.resetToken)
    }

    @Test
    fun `createUser throws exception when registering duplicate email`() = runTest {
        val request = AuthRequest(email = "duplicate@example.com", password = "Password1")
        authRepository.createUser(request)

        assertFailsWith<ExposedSQLException> {
            authRepository.createUser(request)
        }
    }

    @Test
    fun `findUserByEmail returns null when user does not exist`() = runTest {
        val user = authRepository.findUserByEmail("nonexistent@example.com")
        assertNull(user)
    }

    @Test
    fun `saveResetToken generates token and persists it to database`() = runTest {
        val user = authRepository.createUser(AuthRequest(email = "reset@example.com", password = "Password123"))
        assertNull(user.resetToken)

        val resetToken = authRepository.saveResetToken("reset@example.com")
        assertTrue(resetToken.isNotBlank())

        val updatedUser = authRepository.findUserByEmail("reset@example.com")
        assertNotNull(updatedUser)
        assertEquals(resetToken, updatedUser.resetToken)
    }

    @Test
    fun `saveResetToken updates token with a new value on subsequent calls`() = runTest {
        authRepository.createUser(AuthRequest(email = "multi-reset@example.com", password = "Password123"))

        val firstToken = authRepository.saveResetToken("multi-reset@example.com")
        val secondToken = authRepository.saveResetToken("multi-reset@example.com")

        assertNotEquals(firstToken, secondToken)
        val user = authRepository.findUserByEmail("multi-reset@example.com")
        assertNotNull(user)
        assertEquals(secondToken, user.resetToken)
    }
}
