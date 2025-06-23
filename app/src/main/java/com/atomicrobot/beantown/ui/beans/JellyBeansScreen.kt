@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.atomicrobot.beantown.ui.beans

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.atomicrobot.beantown.R
import com.atomicrobot.beantown.ui.components.JellyBean
import com.atomicrobot.beantown.ui.effects.EdgeToEdgeEffect
import com.atomicrobot.beantown.ui.effects.EventEffect
import com.atomicrobot.beantown.ui.getContrastingColor
import com.atomicrobot.beantown.ui.preview.BeanTownPreviewWrapper
import kotlinx.coroutines.flow.flowOf
import org.koin.androidx.compose.koinViewModel

@Composable
fun JellyBeansScreen(
    viewModel: JellyBeansViewModel = koinViewModel(),
    onBeanClicked: (
        beanId: Int,
        flavorName: String,
        imageUrl: String,
    ) -> Unit,
) {
    // We want dark icons to make sure that they remain visible behind the jelly beans
    EdgeToEdgeEffect(lightIcons = false)

    val beansItems: LazyPagingItems<BeanUIState> =
        viewModel.jellyBeans.collectAsLazyPagingItems()

    viewModel.EventEffect {
        when (it) {
            is BeansViewEvent.ViewJellyBean -> {
                onBeanClicked(
                    it.beanId,
                    it.flavorName,
                    it.imageUrl,
                )
            }
        }
    }

    JellyBeansScreen(
        beansItems = beansItems,
        onJellyBeanClicked = {
            viewModel.sendAction(JellyBeansViewAction.BeanClicked(it))
        },
    )
}

@Composable
private fun JellyBeansScreen(
    beansItems: LazyPagingItems<BeanUIState>,
    onJellyBeanClicked: (BeanUIState) -> Unit,
) = Surface {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = WindowInsets.systemBars.listPadding(),
    ) {

        refreshingJellyBeans(
            refreshLoadState = beansItems.loadState.refresh,
            onRefreshClicked = beansItems::refresh
        )

        jellyBeansItems(
            items = beansItems,
            onJellyBeanClicked = onJellyBeanClicked,
        )
    }
}

@Composable
private fun WindowInsets.listPadding(): PaddingValues = this.asPaddingValues().let {
    PaddingValues(
        start = 12.dp,
        top = it.calculateTopPadding(),
        end = 12.dp,
        bottom = it.calculateBottomPadding(),
    )
}

private fun LazyListScope.refreshingJellyBeans(
    refreshLoadState: LoadState,
    onRefreshClicked: () -> Unit,
) = when (refreshLoadState) {
    is LoadState.Error -> item {
        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colorScheme.onError
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.error,
                        shape = MaterialTheme.shapes.medium,
                    )
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "",
                    modifier = Modifier.size(100.dp)
                )

                Text(
                    text = stringResource(R.string.oh_no_something_has_gone_wrong),
                    style = MaterialTheme.typography.bodyLarge,
                )

                Button(
                    onClick = onRefreshClicked,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    )
                ) {
                    Text(text = stringResource(R.string.retry))
                }
            }
        }
    }

    LoadState.Loading -> item { Loading(Modifier.fillMaxWidth()) }

    is LoadState.NotLoading -> { /*NO-OP*/
    }
}

@Preview
@Composable
private fun Loading(
    modifier: Modifier = Modifier,
    background: Color = MaterialTheme.colorScheme.surfaceVariant,
) {
    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium,
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(64.dp),
            color = Color.White,
        )

        Text(
            text = stringResource(R.string.loading_jelly_beans),
            color = background.getContrastingColor(),
        )
    }
}

private fun LazyListScope.jellyBeansItems(
    items: LazyPagingItems<BeanUIState>,
    onJellyBeanClicked: (BeanUIState) -> Unit,
) {
    // We don't want to show items while loading
    if (items.loadState.refresh != LoadState.Loading) {
        pagedItems(
            items = items,
            key = { idx, it -> it.beanId },
        ) { i, jellyBean ->
            JellyBean(
                beanId = jellyBean.beanId,
                flavorName = jellyBean.flavorName,
                imageUrl = jellyBean.imageUrl,
                backdropColor = jellyBean.backdropColor,
                tertiaryText = jellyBean.debugInfo,
                onClick = { onJellyBeanClicked(jellyBean) },
                modifier = Modifier
            )
        }
    }

    if (items.loadState.append == LoadState.Loading) {
        item {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(100.dp)
            )
        }
    }
}

private inline fun <T : Any> LazyListScope.pagedItems(
    items: LazyPagingItems<T>,
    noinline key: ((index: Int, item: T) -> Any),
    crossinline itemContent: @Composable LazyItemScope.(index: Int, item: T) -> Unit,
) {
    var nullItemCount = 0
    items(
        count = items.itemCount,
        key = { index ->
            // We MUST "peek" items otherwise if we index to high, new jelly beans will be loaded
            items.peek(index)?.let { key(index, it) } ?:
            // When null items are returned use an artificial key
            "null item key: ${nullItemCount++}"
        },
    ) { items[it]?.also { item -> itemContent(it, item) } }
}

@Preview(" Preview")
@Composable
private fun Preview() {
    BeanTownPreviewWrapper { jellyBeans ->
        val beansItems = remember(jellyBeans) {
            jellyBeans
                .map(::BeanUIState)
                .let {
                    flowOf(
                        PagingData.from(
                            data = it,
                            sourceLoadStates = LoadStates(
                                LoadState.NotLoading(endOfPaginationReached = true),
                                LoadState.NotLoading(endOfPaginationReached = true),
                                LoadState.NotLoading(endOfPaginationReached = true),
                            ),
                        )
                    )
                }
        }.collectAsLazyPagingItems()
        JellyBeansScreen(
            beansItems = beansItems,
            onJellyBeanClicked = { /* NO-OP*/ },
        )
    }
}