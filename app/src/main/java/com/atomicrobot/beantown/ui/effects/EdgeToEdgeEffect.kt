package com.atomicrobot.beantown.ui.effects

import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView

@Composable
fun EdgeToEdgeEffect(
    lightIcons: Boolean = isSystemInDarkTheme(),
    statusBarColor: Color = Color.Transparent,
    navBarColor: Color = Color.Transparent,
) {
    if (!LocalView.current.isInEditMode) {
        val composeAct: ComponentActivity = composeActivity
        LaunchedEffect(
            lightIcons,
            statusBarColor,
            navBarColor,
        ) {
            // Update the edge to edge configuration to match the theme
            composeAct.enableEdgeToEdge(
                statusBarStyle = SystemBarStyle.auto(
                    statusBarColor.toArgb(),
                    statusBarColor.toArgb(),
                ) { lightIcons },
                navigationBarStyle = SystemBarStyle.auto(
                    navBarColor.toArgb(),
                    navBarColor.toArgb(),
                ) { lightIcons },
            )
        }
    }
}

val composeActivity: ComponentActivity
    @Composable
    get() {
        var ctx = LocalContext.current
        while (ctx is ContextWrapper) {
            if (ctx is ComponentActivity) return ctx
            ctx = ctx.baseContext
        }
        throw IllegalArgumentException("No ComponentActivity")
    }