package com.reza.repository

import com.reza.db.CategoriesTable
import com.reza.models.CategoryDto
import com.reza.plugins.DatabaseFactory
import org.jetbrains.exposed.sql.selectAll

interface CatalogRepository {
    suspend fun getAllCategories(): List<CategoryDto>
}

class ExposedCatalogRepository(
    private val dbFactory: DatabaseFactory
) : CatalogRepository {
    override suspend fun getAllCategories(): List<CategoryDto> = dbFactory.dbQuery {
        CategoriesTable.selectAll().map {
            CategoryDto(
                id = it[CategoriesTable.id].value,
                name = it[CategoriesTable.name],
                imageUrl = it[CategoriesTable.imageUrl]
            )
        }
    }
}