package com.reza.db

import com.reza.plugins.DatabaseConfig
import com.reza.plugins.DatabaseFactory
import com.reza.repository.BaseRepositoryTest
import com.reza.repository.ExposedCatalogRepository
import com.reza.repository.ExposedProductRepository
import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.sql.insert
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import kotlin.test.*

class DatabaseSeederTest : BaseRepositoryTest() {

    private lateinit var databaseSeeder: DatabaseSeeder
    private lateinit var catalogRepository: ExposedCatalogRepository
    private lateinit var productRepository: ExposedProductRepository

    @BeforeTest
    override fun setUp() {
        super.setUp()
        databaseSeeder = ExposedDatabaseSeeder(dbFactory)
        catalogRepository = ExposedCatalogRepository(dbFactory)
        productRepository = ExposedProductRepository(dbFactory)
    }

    @Test
    fun `seedCategories inserts all default categories into empty database`() = runTest {
        val inserted = databaseSeeder.seedCategories()

        assertEquals(DatabaseSeeder.defaultCategories.size, inserted)

        val categories = catalogRepository.getAllCategories()
        assertEquals(DatabaseSeeder.defaultCategories.size, categories.size)

        for (defaultCategory in DatabaseSeeder.defaultCategories) {
            val found = categories.find { it.name == defaultCategory.name }
            assertNotNull(found, "Category ${defaultCategory.name} was not found")
            assertEquals(defaultCategory.imageUrl, found.imageUrl)
            assertTrue(found.id > 0)
        }
    }

    @Test
    fun `seedCategories is idempotent and does not duplicate categories on repeated execution`() = runTest {
        val firstRun = databaseSeeder.seedCategories()
        assertEquals(DatabaseSeeder.defaultCategories.size, firstRun)

        val secondRun = databaseSeeder.seedCategories()
        assertEquals(0, secondRun)

        val categories = catalogRepository.getAllCategories()
        assertEquals(DatabaseSeeder.defaultCategories.size, categories.size)
    }

    @Test
    fun `seedCategories only inserts missing categories when partial data exists`() = runTest {
        dbFactory.dbQuery {
            CategoriesTable.insert {
                it[name] = "Electronics"
                it[imageUrl] = "https://example.com/custom-electronics.png"
            }
        }

        val inserted = databaseSeeder.seedCategories()

        assertEquals(DatabaseSeeder.defaultCategories.size - 1, inserted)

        val categories = catalogRepository.getAllCategories()
        assertEquals(DatabaseSeeder.defaultCategories.size, categories.size)

        // Ensure the pre-existing custom category was preserved
        val electronics = categories.find { it.name == "Electronics" }
        assertNotNull(electronics)
        assertEquals("https://example.com/custom-electronics.png", electronics.imageUrl)
    }

    @Test
    fun `seedCategories is case-insensitive when checking existing categories`() = runTest {
        dbFactory.dbQuery {
            CategoriesTable.insert {
                it[name] = "electronics"
                it[imageUrl] = "https://example.com/custom-electronics.png"
            }
        }

        val inserted = databaseSeeder.seedCategories()

        assertEquals(DatabaseSeeder.defaultCategories.size - 1, inserted)
        val categories = catalogRepository.getAllCategories()
        assertEquals(DatabaseSeeder.defaultCategories.size, categories.size)
    }

    @Test
    fun `ExposedDatabaseSeeder can be configured with custom categories for flexibility`() = runTest {
        val customCategories = listOf(
            CategorySeed("Music & Instruments", "https://example.com/music.png"),
            CategorySeed("Video Games", "https://example.com/games.png")
        )
        val customSeeder = ExposedDatabaseSeeder(dbFactory, customCategories)

        val inserted = customSeeder.seedCategories()
        assertEquals(2, inserted)

        val categories = catalogRepository.getAllCategories()
        assertEquals(2, categories.size)
        assertTrue(categories.any { it.name == "Music & Instruments" })
        assertTrue(categories.any { it.name == "Video Games" })
    }

    @Test
    fun `DatabaseSeeder can be resolved through Koin DI`() = runTest {
        val testModule = module {
            single<DatabaseFactory> { dbFactory }
            single<DatabaseSeeder> { ExposedDatabaseSeeder(dbFactory = get()) }
        }

        val koinApp = startKoin {
            modules(testModule)
        }

        try {
            val resolvedSeeder = koinApp.koin.get<DatabaseSeeder>()
            assertNotNull(resolvedSeeder)

            val inserted = resolvedSeeder.seedCategories()
            assertEquals(DatabaseSeeder.defaultCategories.size, inserted)
            val productsInserted = resolvedSeeder.seedProducts()
            assertEquals(DatabaseSeeder.defaultProducts.size, productsInserted)
        } finally {
            stopKoin()
        }
    }

    @Test
    fun `seedProducts returns 0 when no categories exist in database`() = runTest {
        val inserted = databaseSeeder.seedProducts()
        assertEquals(0, inserted)
    }

    @Test
    fun `seedProducts inserts all default products when categories exist`() = runTest {
        databaseSeeder.seedCategories()
        val inserted = databaseSeeder.seedProducts()

        assertEquals(DatabaseSeeder.defaultProducts.size, inserted)

        val categories = catalogRepository.getAllCategories()
        for (category in categories) {
            val expectedProducts = DatabaseSeeder.defaultProducts.filter { it.categoryName.equals(category.name, ignoreCase = true) }
            val actualProducts = productRepository.getProductsByCategory(category.id)
            assertEquals(expectedProducts.size, actualProducts.size, "Mismatch in product count for category ${category.name}")
            for (expected in expectedProducts) {
                val found = actualProducts.find { it.name == expected.name }
                assertNotNull(found, "Product ${expected.name} not found in category ${category.name}")
                assertEquals(expected.description, found.description)
                assertEquals(expected.price, found.price)
                assertEquals(expected.imageUrl, found.imageUrl)
                assertEquals(category.id, found.categoryId)
            }
        }
    }

    @Test
    fun `seedProducts is idempotent and does not duplicate products on repeated execution`() = runTest {
        databaseSeeder.seedCategories()
        val firstRun = databaseSeeder.seedProducts()
        assertEquals(DatabaseSeeder.defaultProducts.size, firstRun)

        val secondRun = databaseSeeder.seedProducts()
        assertEquals(0, secondRun)
    }

    @Test
    fun `seedProducts only inserts missing products when partial products exist`() = runTest {
        databaseSeeder.seedCategories()
        val categories = catalogRepository.getAllCategories()
        val electronics = categories.first { it.name == "Electronics" }

        dbFactory.dbQuery {
            ProductsTable.insert {
                it[categoryId] = electronics.id
                it[name] = "Wireless Noise-Canceling Headphones"
                it[description] = "Pre-existing headphones"
                it[price] = 150.0
                it[imageUrl] = "https://example.com/existing.png"
            }
        }

        val inserted = databaseSeeder.seedProducts()
        assertEquals(DatabaseSeeder.defaultProducts.size - 1, inserted)

        val products = productRepository.getProductsByCategory(electronics.id)
        val headphones = products.find { it.name == "Wireless Noise-Canceling Headphones" }
        assertNotNull(headphones)
        assertEquals("Pre-existing headphones", headphones.description)
        assertEquals(150.0, headphones.price)
    }
}
