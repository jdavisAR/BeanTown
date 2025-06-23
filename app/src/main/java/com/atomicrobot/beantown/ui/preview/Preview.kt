@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalCoilApi::class)

package com.atomicrobot.beantown.ui.preview

import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import coil3.annotation.ExperimentalCoilApi
import coil3.asImage
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import com.atomicrobot.beantown.R
import com.atomicrobot.beantown.data.local.entity.JellyBeanEntity
import com.atomicrobot.beantown.data.mapper.toDbEntity
import com.atomicrobot.beantown.data.network.NetworkUtils
import com.atomicrobot.beantown.data.network.model.NetworkJellyBeans
import com.atomicrobot.beantown.ui.platform.LocalAnimatedVisibilityScope
import com.atomicrobot.beantown.ui.platform.LocalSharedTransitionScope
import com.atomicrobot.beantown.ui.theme.BeanTownTheme
import java.io.InputStreamReader

@Composable
fun BeanTownPreviewWrapper(content: @Composable (jellyBeans: List<JellyBeanEntity>) -> Unit) {
    BeanTownTheme {
        SharedTransitionLayout {
            AnimatedVisibility(visible = true) {
                CompositionLocalProvider(
                    LocalSharedTransitionScope provides this@SharedTransitionLayout,
                    LocalAnimatedVisibilityScope provides this@AnimatedVisibility,
                    LocalAsyncImagePreviewHandler provides AsyncImagePreviewHandler {
                        AppCompatResources.getDrawable(
                            /* context = */ it.context,
                            /* resId = */ R.drawable.jelly_bean_preview,
                        )!!.asImage()
                    },
                ) {
                    content(jellyBeans())
                }
            }
        }
    }
}

@Composable
private fun jellyBeans(
    jellyBeanSource: String = JELLY_BEANS_JSON,
): List<JellyBeanEntity> {
    val assetManager = LocalContext.current.assets
    return remember {
        with(assetManager) {
            open(jellyBeanSource).use { stream ->
                InputStreamReader(stream)
                    .use { r -> r.readText() }
                    .let { NetworkUtils.json.decodeFromString<NetworkJellyBeans>(it) }
                    .items.map { it.toDbEntity(page = 1) }
            }
        }
    }
}

private const val JELLY_BEANS_JSON: String = "preview_jelly_bean.json"