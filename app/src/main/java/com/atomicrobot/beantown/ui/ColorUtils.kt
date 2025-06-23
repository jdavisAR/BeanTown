package com.atomicrobot.beantown.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils

fun Color.getContrastingColor(): Color {
    val luminance = ColorUtils.calculateLuminance(this.toArgb())
    return if (luminance > 0.5) Color.Black else Color.White
}