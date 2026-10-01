package com.reza.di

import io.ktor.server.application.Application

fun appModules(application: Application) = listOf(
    coreModule(application),
    securityModule,
    databaseModule,
    repositoryModule
)