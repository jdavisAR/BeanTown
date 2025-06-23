@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.atomicrobot.beantown.ui.beans.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.atomicrobot.beantown.ui.beans.details.JellyBeanDetailsScreen
import com.atomicrobot.beantown.ui.platform.LocalAnimatedVisibilityScope
import kotlinx.serialization.Serializable

@Serializable
data class JellyBeanDetails(
    val beanId: Int,
    val flavorName: String,
    val imageUrl: String,
)

fun NavController.navigateJellyBeanDetails(
    beanId: Int,
    flavorName: String,
    imageUrl: String,
    navOptions: NavOptions? = null,
) = navigate(
    JellyBeanDetails(
        beanId = beanId,
        flavorName = flavorName,
        imageUrl = imageUrl,
    ), navOptions
)

fun NavGraphBuilder.jellyBeanDetails() {
    composable<JellyBeanDetails> {
        CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
            JellyBeanDetailsScreen()
        }
    }
}