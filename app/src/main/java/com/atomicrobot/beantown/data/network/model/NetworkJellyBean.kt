package com.atomicrobot.beantown.data.network.model

import androidx.compose.ui.graphics.Color
import com.atomicrobot.beantown.serialization.ComposeGraphicsColorSerializer
import kotlinx.serialization.Serializable

@Serializable
data class NetworkJellyBeans(
    val totalCount: Int,
    val pageSize: Int,
    val currentPage: Int,
    val totalPages: Int,
    val items: List<NetworkJellyBean>,
)

fun NetworkJellyBeans.hasReachedEnd(): Boolean = currentPage >= totalPages

internal val INVALID_RESPONSE: NetworkJellyBeans = NetworkJellyBeans(
    totalCount = 0,
    pageSize = 0,
    currentPage = 0,
    totalPages = 0,
    items = emptyList()
)

val NetworkJellyBeans.isError: Boolean get() = this == INVALID_RESPONSE

@Serializable
data class NetworkJellyBean(
    val beanId: Int,
    val groupName: List<String>,
    val ingredients: List<String>,
    val flavorName: String,
    val description: String,
    val colorGroup: String,
    @Serializable(ComposeGraphicsColorSerializer::class)
    val backgroundColor: Color = Color.Unspecified,
    val imageUrl: String = "",
    val glutenFree: Boolean = false,
    val sugarFree: Boolean = false,
    val seasonal: Boolean = false,
    val kosher: Boolean = false,
)