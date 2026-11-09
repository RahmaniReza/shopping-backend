package com.reza.db

import com.reza.plugins.DatabaseFactory
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.slf4j.LoggerFactory

data class CategorySeed(
    val name: String,
    val imageUrl: String
)

data class ProductSeed(
    val categoryName: String,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String
)

interface DatabaseSeeder {
    fun seedCategories(): Int
    fun seedProducts(): Int

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

        val defaultProducts: List<ProductSeed> = listOf(
            // Electronics
            ProductSeed(
                categoryName = "Electronics",
                name = "Wireless Noise-Canceling Headphones",
                description = "High-fidelity audio with active noise cancellation and 30-hour battery life.",
                price = 199.99,
                imageUrl = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500&q=80"
            ),
            ProductSeed(
                categoryName = "Electronics",
                name = "Smartphone 128GB",
                description = "6.5-inch OLED display with pro-grade camera system.",
                price = 699.00,
                imageUrl = "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=500&q=80"
            ),
            ProductSeed(
                categoryName = "Electronics",
                name = "Ultra-Thin Laptop 15\"",
                description = "Lightning-fast processor with 16GB RAM and 512GB SSD storage.",
                price = 1199.99,
                imageUrl = "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=500&q=80"
            ),

            // Clothing
            ProductSeed(
                categoryName = "Clothing",
                name = "Classic Crewneck T-Shirt",
                description = "100% organic cotton, regular fit casual t-shirt.",
                price = 24.99,
                imageUrl = "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=500&q=80"
            ),
            ProductSeed(
                categoryName = "Clothing",
                name = "Slim Fit Denim Jeans",
                description = "Durable stretch denim with classic five-pocket styling.",
                price = 59.99,
                imageUrl = "https://images.unsplash.com/photo-1542272604-780c96856592?w=500&q=80"
            ),
            ProductSeed(
                categoryName = "Clothing",
                name = "Waterproof Winter Parka",
                description = "Insulated winter jacket designed for extreme weather protection.",
                price = 149.99,
                imageUrl = "https://images.unsplash.com/photo-1544441893-675973e31985?w=500&q=80"
            ),

            // Home & Kitchen
            ProductSeed(
                categoryName = "Home & Kitchen",
                name = "Stainless Steel Cookware Set",
                description = "10-piece premium stainless steel pots and pans.",
                price = 189.99,
                imageUrl = "https://images.unsplash.com/photo-1584269600464-37b1b58a9fe7?w=500&q=80"
            ),
            ProductSeed(
                categoryName = "Home & Kitchen",
                name = "Espresso Coffee Machine",
                description = "15-bar pump espresso maker with milk frother steam wand.",
                price = 129.99,
                imageUrl = "https://images.unsplash.com/photo-1517668808822-9ebb02f2a0e6?w=500&q=80"
            ),

            // Beauty & Personal Care
            ProductSeed(
                categoryName = "Beauty & Personal Care",
                name = "Hydrating Facial Serum",
                description = "Vitamin C and Hyaluronic Acid anti-aging serum.",
                price = 28.50,
                imageUrl = "https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=500&q=80"
            ),
            ProductSeed(
                categoryName = "Beauty & Personal Care",
                name = "Organic Botanical Shampoo",
                description = "Gentle sulfate-free daily cleansing shampoo.",
                price = 18.00,
                imageUrl = "https://images.unsplash.com/photo-1535585209827-a15fcdbc4c2d?w=500&q=80"
            ),

            // Books
            ProductSeed(
                categoryName = "Books",
                name = "Kotlin in Action",
                description = "Comprehensive guide to building production applications with Kotlin.",
                price = 44.99,
                imageUrl = "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=500&q=80"
            ),
            ProductSeed(
                categoryName = "Books",
                name = "Clean Architecture",
                description = "A craftsman's guide to software structure and design.",
                price = 39.99,
                imageUrl = "https://images.unsplash.com/photo-1532012164546-f432f2e3777a?w=500&q=80"
            ),

            // Sports & Outdoors
            ProductSeed(
                categoryName = "Sports & Outdoors",
                name = "Non-Slip Yoga Mat",
                description = "Eco-friendly high-density cushioning fitness mat.",
                price = 34.99,
                imageUrl = "https://images.unsplash.com/photo-1601925260368-ae2f83cf8b7f?w=500&q=80"
            ),
            ProductSeed(
                categoryName = "Sports & Outdoors",
                name = "Insulated Stainless Steel Bottle",
                description = "Double-wall vacuum flask keeping drinks cold for 24 hours.",
                price = 22.99,
                imageUrl = "https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=500&q=80"
            ),

            // Toys & Games
            ProductSeed(
                categoryName = "Toys & Games",
                name = "Strategy Board Game",
                description = "Award-winning tabletop strategy game for 2-4 players.",
                price = 49.99,
                imageUrl = "https://images.unsplash.com/photo-1610890716171-6b1bb98ffd09?w=500&q=80"
            ),
            ProductSeed(
                categoryName = "Toys & Games",
                name = "Educational Building Blocks",
                description = "Creative construction set for early childhood development.",
                price = 29.99,
                imageUrl = "https://images.unsplash.com/photo-1587654780291-39c9404d746b?w=500&q=80"
            ),

            // Groceries
            ProductSeed(
                categoryName = "Groceries",
                name = "Organic Cold-Pressed Olive Oil",
                description = "Extra virgin Mediterranean olive oil 500ml.",
                price = 16.99,
                imageUrl = "https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?w=500&q=80"
            ),
            ProductSeed(
                categoryName = "Groceries",
                name = "Artisan Dark Roast Coffee Beans",
                description = "Single-origin Arabica whole bean coffee 1kg.",
                price = 19.99,
                imageUrl = "https://images.unsplash.com/photo-1559056199-641a0ac8b55e?w=500&q=80"
            )
        )
    }
}

class ExposedDatabaseSeeder(
    private val dbFactory: DatabaseFactory,
    private val categoriesToSeed: List<CategorySeed> = DatabaseSeeder.defaultCategories,
    private val productsToSeed: List<ProductSeed> = DatabaseSeeder.defaultProducts
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

    override fun seedProducts(): Int = dbFactory.inTransaction {
        val categoryMap = CategoriesTable.selectAll()
            .associate { it[CategoriesTable.name].lowercase() to it[CategoriesTable.id].value }

        if (categoryMap.isEmpty()) {
            logger.warn("Product seeding skipped: no categories found in database.")
            return@inTransaction 0
        }

        val existingProducts = ProductsTable.selectAll()
            .map { it[ProductsTable.categoryId].value to it[ProductsTable.name].lowercase() }
            .toSet()

        val missingProducts = productsToSeed.filter { seed ->
            val catId = categoryMap[seed.categoryName.lowercase()]
            catId != null && (catId to seed.name.lowercase()) !in existingProducts
        }

        if (missingProducts.isEmpty()) {
            logger.info("Product seeding skipped: all default products already exist in database.")
            return@inTransaction 0
        }

        missingProducts.forEach { product ->
            val catId = categoryMap[product.categoryName.lowercase()]!!
            ProductsTable.insert {
                it[categoryId] = catId
                it[name] = product.name
                it[description] = product.description
                it[price] = product.price
                it[imageUrl] = product.imageUrl
            }
        }

        logger.info("Database successfully seeded with {} new products.", missingProducts.size)
        missingProducts.size
    }
}
