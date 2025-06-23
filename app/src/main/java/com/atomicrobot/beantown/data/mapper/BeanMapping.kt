package com.atomicrobot.beantown.data.mapper

import androidx.compose.ui.graphics.toArgb
import com.atomicrobot.beantown.data.local.entity.JellyBeanEntity
import com.atomicrobot.beantown.data.network.model.NetworkJellyBean

/**
 * Converts a [NetworkJellyBean] (remote) to [JellyBeanEntity] (local).
 *
 * @param page Index of the page of [NetworkJellyBean] this bean corresponds too.
 * Note: This value is only reliable if consistent page sizes are used.
 */
fun NetworkJellyBean.toDbEntity(
    page: Int,
): JellyBeanEntity = JellyBeanEntity(
    beanId = this.beanId,
    groupName = this.groupName,
    ingredients = this.ingredients,
    flavorName = this.flavorName,
    description = this.description,
    colorGroup = this.colorGroup,
    backgroundColorInt = this.backgroundColor.toArgb(),
    imageUrl = this.imageUrl,
    glutenFree = this.glutenFree,
    sugarFree = this.sugarFree,
    seasonal = this.seasonal,
    kosher = this.kosher,
    page = page,
)
