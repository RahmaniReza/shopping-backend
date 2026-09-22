package com.reza.db

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp

object UsersTable : IntIdTable("users") {
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val resetToken = varchar("reset_token", 255).nullable()
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
}

object CategoriesTable : IntIdTable("categories") {
    val name = varchar("name", 100)
    val imageUrl = varchar("image_url", 500)
}

object ProductsTable : IntIdTable("products") {
    val categoryId = reference("category_id", CategoriesTable)
    val name = varchar("name", 255)
    val description = text("description")
    val price = double("price")
    val imageUrl = varchar("image_url", 500)
}