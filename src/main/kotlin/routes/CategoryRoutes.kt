package com.reza.routes

import com.reza.models.GenericResponse
import com.reza.repository.CatalogRepository
import com.reza.repository.ProductRepository
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.catalogRoutes() {
    val catalogRepository by inject<CatalogRepository>()
    val productRepository by inject<ProductRepository>()

    authenticate("auth-jwt") {
        get("/categories") {
            val categories = catalogRepository.getAllCategories()
            call.respond(HttpStatusCode.OK, categories)
        }

        get("/categories/{categoryId}/products") {
            val categoryId = call.parameters["categoryId"]?.toIntOrNull()
            if (categoryId == null) {
                call.respond(HttpStatusCode.BadRequest, GenericResponse("Invalid Category ID"))
                return@get
            }

            val products = productRepository.getProductsByCategory(categoryId)
            call.respond(HttpStatusCode.OK, products)
        }
    }
}