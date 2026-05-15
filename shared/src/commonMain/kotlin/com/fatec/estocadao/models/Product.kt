package com.fatec.estocadao.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: String? = null,
    val name: String,
    val description: String?,
    val sku: String,
    val category: String?,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)
