package com.reza

import com.reza.models.GenericResponse
import com.reza.plugins.configureLogging
import com.reza.plugins.configureSerialization
import com.reza.plugins.configureStatusPages
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlin.test.*

class ServerTest {

    @Test
    fun `test root endpoint returns OK and X-Request-ID header`() = testApplication {
        application {
            configureLogging()
            configureStatusPages()
            configureSerialization()
            routing {
                get("/") {
                    call.respond(HttpStatusCode.OK, GenericResponse("Shopping Backend API is running"))
                }
            }
        }

        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.headers.contains(HttpHeaders.XRequestId))
    }

    @Test
    fun `test custom X-Request-ID is preserved and returned`() = testApplication {
        application {
            configureLogging()
            configureStatusPages()
            configureSerialization()
            routing {
                get("/") {
                    call.respond(HttpStatusCode.OK, GenericResponse("Shopping Backend API is running"))
                }
            }
        }

        val customRequestId = "custom-client-req-999"
        val response = client.get("/") {
            header(HttpHeaders.XRequestId, customRequestId)
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(customRequestId, response.headers[HttpHeaders.XRequestId])
    }

    @Test
    fun `test not found route returns 404 and requestId`() = testApplication {
        application {
            configureLogging()
            configureStatusPages()
            configureSerialization()
            routing {
                get("/") {
                    call.respond(HttpStatusCode.OK, GenericResponse("Shopping Backend API is running"))
                }
            }
        }

        val response = client.get("/non-existing-route")
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertTrue(response.headers.contains(HttpHeaders.XRequestId))
    }

    @Test
    fun `test authenticated request logging and token verification`() = testApplication {
        val jwtService = com.reza.security.JwtServiceImpl("test-secret-12345", "com.reza.shopping")
        application {
            configureLogging()
            configureStatusPages()
            configureSerialization()
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
            routing {
                authenticate("auth-jwt") {
                    get("/secure/profile") {
                        val email = call.principal<JWTPrincipal>()?.payload?.getClaim("email")?.asString()
                        call.respond(HttpStatusCode.OK, GenericResponse("Welcome $email"))
                    }
                }
            }
        }

        val token = jwtService.generateToken("shopper@example.com")
        val response = client.get("/secure/profile") {
            bearerAuth(token)
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.headers.contains(HttpHeaders.XRequestId))
    }
}
