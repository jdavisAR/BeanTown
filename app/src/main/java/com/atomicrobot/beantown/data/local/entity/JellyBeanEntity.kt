package com.atomicrobot.beantown.data.local.entity

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class JellyBeanEntity(
    @PrimaryKey
    val beanId: Int,
    val groupName: List<String>,
    val ingredients: List<String>,
    val flavorName: String,
    val description: String,
    val colorGroup: String,
    val backgroundColorInt: Int,
    val imageUrl: String,
    val glutenFree: Boolean,
    val sugarFree: Boolean,
    val seasonal: Boolean,
    val kosher: Boolean,
    // Convenience property for quickly and easily determining what page a given been belongs to
    // The remote mediator will use this to fetch the remote keys and lets us know which page to
    // load next or before
    val page: Int,
) {
    val backgroundColor: Color get() = Color(backgroundColorInt)
}