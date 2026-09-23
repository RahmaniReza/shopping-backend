package com.reza.repository

import com.reza.db.CategoriesTable
import com.reza.models.CategoryDto
import com.reza.plugins.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.selectAll

interface CatalogRepository {
    suspend fun getAllCategories(): List<CategoryDto>
}

class ExposedCatalogRepository : CatalogRepository {
    override suspend fun getAllCategories(): List<CategoryDto> = dbQuery {
        CategoriesTable.selectAll().map {
            CategoryDto(
                id = it[CategoriesTable.id].value,
                name = it[CategoriesTable.name],
                imageUrl = it[CategoriesTable.imageUrl]
            )
        }
    }
}