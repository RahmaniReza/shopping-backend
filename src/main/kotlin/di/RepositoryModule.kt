package com.reza.di

import com.reza.repository.AuthRepository
import com.reza.repository.CatalogRepository
import com.reza.repository.ExposedAuthRepository
import com.reza.repository.ExposedCatalogRepository
import com.reza.repository.ExposedProductRepository
import com.reza.repository.ProductRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<AuthRepository> {
        ExposedAuthRepository(
            jwtService = get(),
            dbFactory = get()
        )
    }
    single<CatalogRepository> { ExposedCatalogRepository(dbFactory = get()) }
    single<ProductRepository> { ExposedProductRepository(dbFactory = get()) }
}