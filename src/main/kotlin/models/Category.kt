package com.reza.models

import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
    val id: Int,
    val name: String,
    val imageUrl: String
)