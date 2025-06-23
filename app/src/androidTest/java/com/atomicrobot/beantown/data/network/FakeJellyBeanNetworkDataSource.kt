package com.atomicrobot.beantown.data.network

import com.atomicrobot.beantown.data.network.model.INVALID_RESPONSE
import com.atomicrobot.beantown.data.network.model.NetworkJellyBean
import com.atomicrobot.beantown.data.network.model.NetworkJellyBeans
import kotlinx.io.IOException

class FakeJellyBeanNetworkDataSource : JellyBeanNetworkDataSource {

    private val _jellyBeans: MutableList<NetworkJellyBean> = mutableListOf()

    var simulateNetworkError: Boolean = false
    var simulateException: Boolean = false

    fun addJellyBeans(
        beans: List<NetworkJellyBean>,
    ) {
        _jellyBeans.addAll(beans)
    }

    fun clearJellyBeans() {
        _jellyBeans.clear()
    }

    override suspend fun getJellyBeans(
        pageIndex: Int,
        pageSize: Int,
    ): NetworkJellyBeans {
        if(simulateException) {
            throw IOException("Simulated exception while trying to fetch page: $pageIndex of size $pageSize Jelly Beans!")
        }
        else if(simulateNetworkError) return INVALID_RESPONSE

        return _jellyBeans
            .chunked(size = pageSize).let { pages ->
                NetworkJellyBeans(
                    totalCount = _jellyBeans.size,
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