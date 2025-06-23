package com.atomicrobot.beantown.data.network

import com.atomicrobot.beantown.data.network.model.NetworkJellyBean
import com.atomicrobot.beantown.data.network.model.NetworkJellyBeans
import com.atomicrobot.beantown.ui.beans.JELLY_BEANS_JSON
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.withContext
import java.io.InputStreamReader

class TestJellyBeanNetworkDataSource : JellyBeanNetworkDataSource {

    override suspend fun getJellyBeans(
        pageIndex: Int,
        pageSize: Int,
    ): NetworkJellyBeans {
        return getJellyBeans().let {
            it.chunked(size = pageSize).let { pages ->
                NetworkJellyBeans(
                    totalCount = it.size,
                    pageSize = pageSize,
                    currentPage = pageIndex,
                    totalPages = pages.size,
                    // THe indexes from the RemoteMediator aren't zero based
                    items = (pageIndex - 1)
                        .coerceIn(
                            minimumValue = 0,
                            maximumValue = pages.size,
                        ).let { resolvedIndex ->
                            if (resolvedIndex > pages.lastIndex) {
                                emptyList()
                            } else pages[resolvedIndex]
                        }
                )
            }
        }
    }

    private suspend fun getJellyBeans(): List<NetworkJellyBean> =
        withContext(UnconfinedTestDispatcher()) {
            this::class.java
                .classLoader?.getResourceAsStream(JELLY_BEANS_JSON).use { stream ->
                    InputStreamReader(stream)
                        .use { r -> r.readText() }
                        .let { NetworkUtils.json.decodeFromString<NetworkJellyBeans>(it) }
                        .items
                }
        }
}