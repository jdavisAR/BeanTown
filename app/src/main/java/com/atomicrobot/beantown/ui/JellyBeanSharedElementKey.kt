package com.atomicrobot.beantown.ui

data class JellyBeanSharedElementKey(
    val beanId: Int,
    val type: JellyBeanSharedElementType
)

enum class JellyBeanSharedElementType {
    Image,
    Title,
    Background,
}