package com.reza.repository

import com.reza.plugins.DatabaseConfig
import com.reza.plugins.DatabaseFactory
import java.util.UUID
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

abstract class BaseRepositoryTest {

    protected lateinit var dbFactory: DatabaseFactory

    @BeforeTest
    open fun setUp() {
        val dbName = "test_${this::class.simpleName?.lowercase()}_${UUID.randomUUID().toString().replace("-", "_")}"
        dbFactory = DatabaseFactory(
            DatabaseConfig(
                driver = "org.h2.Driver",
                url = "jdbc:h2:mem:$dbName;DB_CLOSE_DELAY=-1;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE",
                user = "sa",
                pass = "",
                maxPoolSize = 5,
                transactionIsolation = "TRANSACTION_READ_COMMITTED"
            )
        ).apply { init() }
    }

    @AfterTest
    open fun tearDown() {
        dbFactory.close()
    }
}
