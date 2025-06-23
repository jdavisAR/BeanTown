@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.atomicrobot.beantown.ui.beans.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.atomicrobot.beantown.ui.beans.JellyBeansScreen
import com.atomicrobot.beantown.ui.platform.LocalAnimatedVisibilityScope
import kotlinx.serialization.Serializable

@Serializable
data object JellyBeans

fun NavGraphBuilder.jellyBeans(
    onBeanClicked: (
        beanId: Int,
        flavorName: String,
        imageUrl: String,
    ) -> Unit,
) = composable<JellyBeans> {
    CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
        JellyBeansScreen(onBeanClicked = onBeanClicked)
    }
}
