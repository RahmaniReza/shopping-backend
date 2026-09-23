package com.reza.di

import com.reza.security.JwtService
import com.reza.security.JwtServiceImpl
import org.koin.dsl.module

val securityModule = module {
    single<JwtService> {
        JwtServiceImpl(
            secret = System.getenv("JWT_SECRET") ?: "your-fallback-dev-secret",
            issuer = "com.reza.shopping"
        )
    }
}