package com.reza.repository

import com.reza.db.CategoriesTable
import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.sql.insert
import kotlin.test.*

class CatalogRepositoryTest : BaseRepositoryTest() {

    private lateinit var catalogRepository: CatalogRepository

    @BeforeTest
    override fun setUp() {
        super.setUp()
        catalogRepository = ExposedCatalogRepository(dbFactory)
    }

    @Test
    fun `getAllCategories returns empty list when database has no categories`() = runTest {
        val categories = catalogRepository.getAllCategories()
        assertTrue(categories.isEmpty())
    }

    @Test
    fun `getAllCategories returns all categories mapped correctly`() = runTest {
        dbFactory.dbQuery {
            CategoriesTable.insert {
                it[name] = "Electronics"
                it[imageUrl] = "https://example.com/electronics.png"
            }
            CategoriesTable.insert {
                it[name] = "Clothing"
                it[imageUrl] = "https://example.com/clothing.png"
            }
            CategoriesTable.insert {
                it[name] = "Home & Kitchen"
                it[imageUrl] = "https://example.com/home.png"
            }
        }

        val categories = catalogRepository.getAllCategories()

        assertEquals(3, categories.size)
        val electronics = categories.find { it.name == "Electronics" }
        assertNotNull(electronics)
        assertTrue(electronics.id > 0)
        assertEquals("https://example.com/electronics.png", electronics.imageUrl)

        val clothing = categories.find { it.name == "Clothing" }
        assertNotNull(clothing)
        assertTrue(clothing.id > 0)
        assertEquals("https://example.com/clothing.png", clothing.imageUrl)

        val home = categories.find { it.name == "Home & Kitchen" }
        assertNotNull(home)
        assertTrue(home.id > 0)
        assertEquals("https://example.com/home.png", home.imageUrl)
    }
}
