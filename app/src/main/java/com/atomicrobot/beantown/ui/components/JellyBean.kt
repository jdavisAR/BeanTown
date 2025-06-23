@file:OptIn(ExperimentalCoilApi::class, ExperimentalSharedTransitionApi::class)

package com.atomicrobot.beantown.ui.components

import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.annotation.ExperimentalCoilApi
import coil3.asImage
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import com.atomicrobot.beantown.R
import com.atomicrobot.beantown.ui.JellyBeanSharedElementKey
import com.atomicrobot.beantown.ui.JellyBeanSharedElementType
import com.atomicrobot.beantown.ui.PaletteAsyncImage
import com.atomicrobot.beantown.ui.getContrastingColor
import com.atomicrobot.beantown.ui.platform.LocalAnimatedVisibilityScope
import com.atomicrobot.beantown.ui.platform.LocalSharedTransitionScope

@Composable
fun JellyBean(
    beanId: Int,
    flavorName: String,
    imageUrl: String,
    backdropColor: Color,
    tertiaryText: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) = CompositionLocalProvider(
    LocalAsyncImagePreviewHandler provides AsyncImagePreviewHandler {
        AppCompatResources.getDrawable(
            /* context = */ it.context,
            /* resId = */ R.drawable.jelly_bean_preview,
        )!!.asImage()
    },
) {
    with(LocalSharedTransitionScope.current) {
        var primaryBackground by remember { mutableStateOf(backdropColor) }
        Column(
            modifier = modifier
                .shadow(
                    elevation = 6.dp,
                    shape = MaterialTheme.shapes.medium,
                )
                .background(
                    color = primaryBackground,
                    shape = MaterialTheme.shapes.medium,
                )
                .sharedBounds(
                    rememberSharedContentState(
                        key = JellyBeanSharedElementKey(
                            beanId = beanId,
                            type = JellyBeanSharedElementType.Background,
                        ),
                    ),
                    animatedVisibilityScope = LocalAnimatedVisibilityScope.current,
                )
                .clickable(onClick = { onClick() })
                .padding(
                    horizontal = 8.dp,
                    vertical = 8.dp,
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {

            var titleText by remember { mutableStateOf(backdropColor.getContrastingColor()) }
            var supportingText by remember { mutableStateOf(titleText) }

            PaletteAsyncImage(
                model = imageUrl,
                contentDescription = "$imageUrl image",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(ratio = imageAspectRatio)
                    .sharedBounds(
                        rememberSharedContentState(
                            key = JellyBeanSharedElementKey(
                                beanId = beanId,
                                type = JellyBeanSharedElementType.Image,
                            ),
                        ),
                        animatedVisibilityScope = LocalAnimatedVisibilityScope.current,
                    ),
                onPaletteReady = {
                    it.lightVibrantSwatch?.run {
                        primaryBackground = Color(rgb)
                        titleText = Color(titleTextColor)
                        supportingText = Color(bodyTextColor)
                    }
                },
            )

            Text(
                text = flavorName,
                color = titleText,
                modifier = Modifier
                    .sharedBounds(
                        rememberSharedContentState(
                            key = JellyBeanSharedElementKey(
                                beanId = beanId,
                                type = JellyBeanSharedElementType.Title,
                            ),
                        ),
                        animatedVisibilityScope = LocalAnimatedVisibilityScope.current,
                    ),
                style = MaterialTheme.typography.titleMedium,
            )

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Jelly Belly Official Flavors",
                    color = supportingText,
                    style = MaterialTheme.typography.titleSmall,
                )

                Text(
                    text = tertiaryText,
                    modifier = Modifier.align(Alignment.CenterEnd),
                    color = supportingText,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}

@Preview("JellyBeans Preview")
@Composable
fun JellyBeansPreview() {
    AnimatedVisibility(visible = true) {
        JellyBean(
            beanId = 1,
            flavorName = "7Up",
            imageUrl = "https://cdn-tp1.mozu.com/9046-m1/cms/files/ab692677-5471-4863-91a8-659363ae4cc4",
            backdropColor = Color(0xFFCEDC91),
            tertiaryText = "khaki/1"
        ) {}
    }
}

private const val imageAspectRatio: Float = 1200.0F / 800.0F