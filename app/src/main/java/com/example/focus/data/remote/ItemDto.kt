package com.example.focus.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class ItemDto(
    val id: String,
    val name: String,
    val description: String? = null,
    val rarity: String,
    val itemType: String,
    val assetUrl: String? = null
)

@Serializable
data class UserInventoryDto(
    val userId: String,
    val itemId: String,
    val isEquipped: Boolean
)
