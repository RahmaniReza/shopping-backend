package com.reza.di

import com.reza.repository.AuthRepository
import com.reza.repository.CatalogRepository
import com.reza.repository.ExposedAuthRepository
import com.reza.repository.ExposedCatalogRepository
import com.reza.repository.ExposedProductRepository
import com.reza.repository.ProductRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<AuthRepository> { ExposedAuthRepository() }
    single<CatalogRepository> { ExposedCatalogRepository() }
    single< ProductRepository> { ExposedProductRepository() }
}