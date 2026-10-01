package com.reza.di

import com.reza.security.JwtService
import com.reza.security.JwtServiceImpl
import io.ktor.server.application.ApplicationEnvironment
import org.koin.dsl.module

val securityModule = module {
    single<JwtService> { (environment: ApplicationEnvironment) ->
        val config = environment.config.config("jwt")

        JwtServiceImpl(
            secret = config.property("secret").getString(),
            issuer = config.property("issuer").getString()
        )
    }
}