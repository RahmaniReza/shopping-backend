package com.reza.db

import com.reza.plugins.DatabaseFactory
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.slf4j.LoggerFactory

data class CategorySeed(
    val name: String,
    val imageUrl: String
)

interface DatabaseSeeder {
    fun seedCategories(): Int

    companion object {
        val defaultCategories: List<CategorySeed> = listOf(
            CategorySeed(
                name = "Electronics",
                imageUrl = "https://images.unsplash.com/photo-1498049794561-7780e7231661?w=500&q=80"
            ),
            CategorySeed(
                name = "Clothing",
                imageUrl = "https://images.unsplash.com/photo-1445205170230-053b83016050?w=500&q=80"
            ),
            CategorySeed(
                name = "Home & Kitchen",
                imageUrl = "https://images.unsplash.com/photo-1556911220-e15b29be8c8f?w=500&q=80"
            ),
            CategorySeed(
                name = "Beauty & Personal Care",
                imageUrl = "https://images.unsplash.com/photo-1522337360788-8b13dee7a37e?w=500&q=80"
            ),
            CategorySeed(
                name = "Books",
                imageUrl = "https://images.unsplash.com/photo-1524995997946-a1c2e315a42f?w=500&q=80"
            ),
            CategorySeed(
                name = "Sports & Outdoors",
                imageUrl = "https://images.unsplash.com/photo-1517838277536-f5f99be501cd?w=500&q=80"
            ),
            CategorySeed(
                name = "Toys & Games",
                imageUrl = "https://images.unsplash.com/photo-1566576912321-d58ddd7a6088?w=500&q=80"
            ),
            CategorySeed(
                name = "Groceries",
                imageUrl = "https://images.unsplash.com/photo-1542838132-92c53300491e?w=500&q=80"
            )
        )
    }
}

class ExposedDatabaseSeeder(
    private val dbFactory: DatabaseFactory,
    private val categoriesToSeed: List<CategorySeed> = DatabaseSeeder.defaultCategories
) : DatabaseSeeder {
    private val logger = LoggerFactory.getLogger(ExposedDatabaseSeeder::class.java)

    override fun seedCategories(): Int = dbFactory.inTransaction {
        val existingNames = CategoriesTable.selectAll()
            .map { it[CategoriesTable.name].lowercase() }
            .toSet()

        val missingCategories = categoriesToSeed.filter { it.name.lowercase() !in existingNames }

        if (missingCategories.isEmpty()) {
            logger.info("Category seeding skipped: all default categories already exist in database.")
            return@inTransaction 0
        }

        missingCategories.forEach { category ->
            CategoriesTable.insert {
                it[name] = category.name
                it[imageUrl] = category.imageUrl
            }
        }

        logger.info("Database successfully seeded with {} new categories.", missingCategories.size)
        missingCategories.size
    }
}
