@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)

package com.atomicrobot.beantown.ui.beans.details

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.atomicrobot.beantown.R
import com.atomicrobot.beantown.ui.JellyBeanSharedElementKey
import com.atomicrobot.beantown.ui.JellyBeanSharedElementType
import com.atomicrobot.beantown.ui.PaletteAsyncImage
import com.atomicrobot.beantown.ui.effects.EdgeToEdgeEffect
import com.atomicrobot.beantown.ui.getContrastingColor
import com.atomicrobot.beantown.ui.platform.LocalAnimatedVisibilityScope
import com.atomicrobot.beantown.ui.platform.LocalSharedTransitionScope
import com.atomicrobot.beantown.ui.preview.BeanTownPreviewWrapper
import org.koin.androidx.compose.koinViewModel

@Composable
fun JellyBeanDetailsScreen(viewModel: JellyBeanDetailsViewModel = koinViewModel()) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    JellyBeanDetailsScreen(uiState = uiState)
}

@Composable
private fun JellyBeanDetailsScreen(uiState: DetailsUiState) {
    when (uiState) {
        is DetailsUiState.Details -> Details(
            uiState = uiState,
            modifier = Modifier
                .fillMaxSize(),
        )
        // This should be impossible...
        DetailsUiState.NotFound -> JellyBeanNotFound(Modifier.fillMaxSize())
    }
}

@Composable
private fun JellyBeanNotFound(modifier: Modifier = Modifier) = Box(
    modifier = modifier
        .background(color = MaterialTheme.colorScheme.error),
    contentAlignment = Alignment.Center,
) {
    CompositionLocalProvider(
        LocalContentColor provides MaterialTheme.colorScheme.onError
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = stringResource(R.string.warning_icon),
                modifier = Modifier.size(100.dp),
            )
            Text(
                text = stringResource(R.string.jelly_bean_details_error),
                textAlign = TextAlign.Center,
            )
        }
    }
}

private fun Color.isLight(): Boolean = this.luminance() > 0.5F

@Composable
private fun Details(
    uiState: DetailsUiState.Details,
    modifier: Modifier = Modifier,
) {
    EdgeToEdgeEffect(
        lightIcons = !uiState.backdropColor.isLight()
    )
    // Simple modifier for parallax the JellyBean image when the sheet is scrolled
    fun Modifier.parallaxLayoutModifier(scrollState: ScrollState, rate: Int) =
        layout { measurable, constraints ->
            measurable.measure(constraints).let {
                layout(it.width, it.height) {
                    it.place(0, scrollState.value / rate)
                }
            }
        }

    with(LocalSharedTransitionScope.current) {
        val verticalScrollState = rememberScrollState()
        Column(
            modifier = modifier
                .background(color = uiState.backdropColor)
                .sharedBounds(
                    rememberSharedContentState(
                        key = JellyBeanSharedElementKey(
                            beanId = uiState.beanId,
                            type = JellyBeanSharedElementType.Background,
                        ),
                    ),
                    animatedVisibilityScope = LocalAnimatedVisibilityScope.current,
                )
                .verticalScroll(state = verticalScrollState),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            /*
                Initialize the sheet color to the same color as the screen background...
                could use Color.Transparent but decided against it
             */
            var sheetBackground by remember { mutableStateOf(uiState.backdropColor) }
            var foregroundColor by remember { mutableStateOf(uiState.backdropColor.getContrastingColor()) }
            PaletteAsyncImage(
                model = uiState.imageUrl,
                contentDescription = uiState.flavorName,
                modifier = Modifier
                    .sharedBounds(
                        rememberSharedContentState(
                            key = JellyBeanSharedElementKey(
                                beanId = uiState.beanId,
                                type = JellyBeanSharedElementType.Image,
                            ),
                        ),
                        animatedVisibilityScope = LocalAnimatedVisibilityScope.current,
                    )
                    .fillMaxWidth()
                    .aspectRatio(1F)
                    .parallaxLayoutModifier(verticalScrollState, 2),
                onPaletteReady = {
                    it.darkMutedSwatch?.run {
                        sheetBackground = Color(rgb)
                        foregroundColor = sheetBackground.getContrastingColor()
                    }
                }
            )

            CompositionLocalProvider(LocalContentColor provides foregroundColor) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .background(
                            color = sheetBackground,
                            shape = MaterialTheme.shapes.medium,
                        )
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = uiState.flavorName,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .sharedBounds(
                                rememberSharedContentState(
                                    key = JellyBeanSharedElementKey(
                                        beanId = uiState.beanId,
                                        type = JellyBeanSharedElementType.Title,
                                    ),
                                ),
                                animatedVisibilityScope = LocalAnimatedVisibilityScope.current,
                            )
                            .fillMaxWidth(),
                    )

                    when (uiState.extendedDetails) {
                        ExtendedDetails.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .size(64.dp),
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }

                        is ExtendedDetails.Details -> {
                            JellyBeanNutritionalFacts(
                                glutenFree = uiState.extendedDetails.gluttenFree,
                                sugarFree = uiState.extendedDetails.sugarFree,
                                seasonal = uiState.extendedDetails.seasonal,
                                kosher = uiState.extendedDetails.kosher,
                                modifier = Modifier.fillMaxWidth(),
                            )

                            JellyBeanGroups(
                                groups = uiState.extendedDetails.groupNames,
                                modifier = Modifier.fillMaxWidth(),
                            )

                            JellyBeanIngredients(
                                ingredients = uiState.extendedDetails.ingredients,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun JellyBeanGroups(
    groups: List<String>,
    modifier: Modifier = Modifier,
) = CompositionLocalProvider(
    LocalTextStyle provides MaterialTheme.typography.bodyMedium
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = stringResource(R.string.groups),
            style = MaterialTheme.typography.titleMedium,
        )
        groups.forEach {
            Text(
                text = it,
                modifier = Modifier.padding(horizontal = 16.dp),
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun JellyBeanIngredients(
    ingredients: List<String>,
    modifier: Modifier = Modifier,
) = CompositionLocalProvider(
    LocalTextStyle provides MaterialTheme.typography.bodyMedium
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = stringResource(R.string.ingredients),
            style = MaterialTheme.typography.titleMedium,
        )

        val paragraphStyle = ParagraphStyle(
            textIndent = TextIndent(
                firstLine = 12.sp,
                restLine = 0.sp,
            )
        )
        Text(
            text = buildAnnotatedString {
                ingredients.forEach { ingredient ->
                    withStyle(style = paragraphStyle) {
                        append(bullet)
                        append("\t\t")
                        append(ingredient)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth(),
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun JellyBeanNutritionalFacts(
    glutenFree: Boolean,
    sugarFree: Boolean,
    seasonal: Boolean,
    kosher: Boolean,
    modifier: Modifier = Modifier,
) = CompositionLocalProvider(
    LocalTextStyle provides MaterialTheme.typography.bodyMedium
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = stringResource(R.string.nutritional_facts),
            style = MaterialTheme.typography.titleMedium,
        )


        Row {
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(stringResource(R.string.gluten))
                        append("\t\t")
                    }
                    withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                        append(glutenFree.toString())
                    }
                },
                modifier = Modifier.weight(1F),
            )
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(stringResource(R.string.sugar))
                        append("\t\t")
                    }
                    withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                        append(sugarFree.toString())
                    }
                }, modifier = Modifier.weight(1F)
            )
        }
        Row {
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(stringResource(R.string.seasonal))
                        append("\t\t")
                    }
                    withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                        append(seasonal.toString())
                    }
                }, modifier = Modifier.weight(1F)
            )
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(stringResource(R.string.kosher))
                        append("\t\t")
                    }
                    withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                        append(kosher.toString())
                    }
                }, modifier = Modifier.weight(1F)
            )
        }
    }
}

@Preview("Jelly Bean Details Screen Preview")
@Composable
fun JellyBeanDetailsScreenPreview() {
    BeanTownPreviewWrapper {
        val uiState = it
            .first()
            .let(DetailsUiState::Details)
        JellyBeanDetailsScreen(
            uiState = uiState,
        )
    }
}

@Preview("Jelly Bean Details Screen Error Preview")
@Composable
fun JellyBeanDetailsScreenErrorPreview() {
    BeanTownPreviewWrapper {
        JellyBeanDetailsScreen(
            uiState = DetailsUiState.NotFound,
        )
    }
}

private const val bullet = "\u2022"
