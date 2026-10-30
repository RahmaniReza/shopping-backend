package com.reza.db

import com.reza.plugins.DatabaseConfig
import com.reza.plugins.DatabaseFactory
import com.reza.repository.BaseRepositoryTest
import com.reza.repository.ExposedCatalogRepository
import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.sql.insert
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import kotlin.test.*

class DatabaseSeederTest : BaseRepositoryTest() {

    private lateinit var databaseSeeder: DatabaseSeeder
    private lateinit var catalogRepository: ExposedCatalogRepository

    @BeforeTest
    override fun setUp() {
        super.setUp()
        databaseSeeder = ExposedDatabaseSeeder(dbFactory)
        catalogRepository = ExposedCatalogRepository(dbFactory)
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
        } finally {
            stopKoin()
        }
    }
}
