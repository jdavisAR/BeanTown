@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.atomicrobot.beantown.ui.platform

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.staticCompositionLocalOf

val LocalSharedTransitionScope = staticCompositionLocalOf<SharedTransitionScope> {
    noLocalProvidedFor("LocalSharedTransitionScope")
}

val LocalAnimatedVisibilityScope = staticCompositionLocalOf<AnimatedVisibilityScope> {
    noLocalProvidedFor("LocalAnimatedContentScope")
}

/**
 * Provides an exceptional default value that plays nicely with the type system...
 * Copied from [androidx.compose.ui.platform.AndroidCompositionLocals.android.kt]
 */
private fun noLocalProvidedFor(name: String): Nothing {
    error("CompositionLocal $name not present")
}