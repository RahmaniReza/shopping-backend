package com.reza.repository

import com.reza.db.ProductsTable
import com.reza.models.ProductDto
import com.reza.plugins.DatabaseFactory
import org.jetbrains.exposed.sql.selectAll

interface ProductRepository {
    suspend fun getProductsByCategory(categoryId: Int): List<ProductDto>
}

class ExposedProductRepository(
    private val dbFactory: DatabaseFactory
) : ProductRepository {
    override suspend fun getProductsByCategory(categoryId: Int): List<ProductDto> = dbFactory.dbQuery {
        ProductsTable.selectAll()
            .where { ProductsTable.categoryId eq categoryId }
            .map {
                ProductDto(
                    id = it[ProductsTable.id].value,
                    categoryId = it[ProductsTable.categoryId].value,
                    name = it[ProductsTable.name],
                    description = it[ProductsTable.description],
                    price = it[ProductsTable.price],
                    imageUrl = it[ProductsTable.imageUrl]
                )
            }
    }
}