@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.atomicrobot.beantown.ui

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.atomicrobot.beantown.ui.beans.navigation.JellyBeans
import com.atomicrobot.beantown.ui.beans.navigation.jellyBeanDetails
import com.atomicrobot.beantown.ui.beans.navigation.navigateJellyBeanDetails
import com.atomicrobot.beantown.ui.beans.navigation.jellyBeans
import com.atomicrobot.beantown.ui.platform.LocalSharedTransitionScope

@Composable
fun BeanTownApp() {
    SharedTransitionLayout {
        val navController = rememberNavController()
        CompositionLocalProvider(LocalSharedTransitionScope provides this@SharedTransitionLayout) {
            NavHost(
                navController = navController,
                startDestination = JellyBeans,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                jellyBeans(onBeanClicked = navController::navigateJellyBeanDetails)

                jellyBeanDetails()
            }
        }
    }
}