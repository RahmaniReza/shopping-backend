package com.reza.models

import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
    val id: Int,
    val categoryId: Int,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String
)