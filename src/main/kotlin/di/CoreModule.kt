package com.reza.di

import io.ktor.server.application.Application
import org.koin.dsl.module

fun coreModule(application: Application) = module {
    single { application.environment }
}