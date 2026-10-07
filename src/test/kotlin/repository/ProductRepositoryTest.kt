package com.reza.repository

import com.reza.db.CategoriesTable
import com.reza.db.ProductsTable
import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.sql.insertAndGetId
import kotlin.test.*

class ProductRepositoryTest : BaseRepositoryTest() {

    private lateinit var productRepository: ProductRepository

    @BeforeTest
    override fun setUp() {
        super.setUp()
        productRepository = ExposedProductRepository(dbFactory)
    }

    @Test
    fun `getProductsByCategory returns empty list when no products exist for category`() = runTest {
        val categoryId = dbFactory.dbQuery {
            CategoriesTable.insertAndGetId {
                it[name] = "Books"
                it[imageUrl] = "https://example.com/books.png"
            }.value
        }

        val products = productRepository.getProductsByCategory(categoryId)
        assertTrue(products.isEmpty())
    }

    @Test
    fun `getProductsByCategory returns empty list for non-existent category id`() = runTest {
        val products = productRepository.getProductsByCategory(99999)
        assertTrue(products.isEmpty())
    }

    @Test
    fun `getProductsByCategory returns only products belonging to the specified category`() = runTest {
        val (cat1Id, cat2Id) = dbFactory.dbQuery {
            val c1 = CategoriesTable.insertAndGetId {
                it[name] = "Electronics"
                it[imageUrl] = "https://example.com/electronics.png"
            }.value

            val c2 = CategoriesTable.insertAndGetId {
                it[name] = "Clothing"
                it[imageUrl] = "https://example.com/clothing.png"
            }.value

            // Products in category 1
            ProductsTable.insertAndGetId {
                it[categoryId] = c1
                it[name] = "Laptop"
                it[description] = "High performance laptop"
                it[price] = 1299.99
                it[imageUrl] = "https://example.com/laptop.png"
            }
            ProductsTable.insertAndGetId {
                it[categoryId] = c1
                it[name] = "Headphones"
                it[description] = "Noise-cancelling headphones"
                it[price] = 199.50
                it[imageUrl] = "https://example.com/headphones.png"
            }

            // Product in category 2
            ProductsTable.insertAndGetId {
                it[categoryId] = c2
                it[name] = "T-Shirt"
                it[description] = "100% Cotton shirt"
                it[price] = 29.99
                it[imageUrl] = "https://example.com/tshirt.png"
            }

            Pair(c1, c2)
        }

        val cat1Products = productRepository.getProductsByCategory(cat1Id)
        assertEquals(2, cat1Products.size)
        assertTrue(cat1Products.all { it.categoryId == cat1Id })

        val laptop = cat1Products.find { it.name == "Laptop" }
        assertNotNull(laptop)
        assertTrue(laptop.id > 0)
        assertEquals("High performance laptop", laptop.description)
        assertEquals(1299.99, laptop.price)
        assertEquals("https://example.com/laptop.png", laptop.imageUrl)

        val headphones = cat1Products.find { it.name == "Headphones" }
        assertNotNull(headphones)
        assertTrue(headphones.id > 0)
        assertEquals("Noise-cancelling headphones", headphones.description)
        assertEquals(199.50, headphones.price)
        assertEquals("https://example.com/headphones.png", headphones.imageUrl)

        val cat2Products = productRepository.getProductsByCategory(cat2Id)
        assertEquals(1, cat2Products.size)
        assertEquals("T-Shirt", cat2Products[0].name)
        assertEquals(cat2Id, cat2Products[0].categoryId)
        assertEquals(29.99, cat2Products[0].price)
    }
}
