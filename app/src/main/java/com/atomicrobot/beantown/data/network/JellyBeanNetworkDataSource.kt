package com.atomicrobot.beantown.data.network

import com.atomicrobot.beantown.data.network.model.NetworkJellyBean
import com.atomicrobot.beantown.data.network.model.NetworkJellyBeans

/**
 * Data source for fetching Jelly Beans from a network backend.
 */
interface JellyBeanNetworkDataSource {

    /**
     * Fetches a page of [NetworkJellyBean] using the specified [pageIndex] and [pageSize].
     * Passing a page index larger than the available pages (determined by the pageSize) will not
     * return a network error but instead an empty list.
     *
     * @return A [NetworkJellyBeans] that contains [List] of [NetworkJellyBean] that contains at
     * most [pageSize] items. The object contains information about the total number of Jelly Beans,
     * the remaining number of Jelly Beans (based on index and page size) as well as the "key"/index
     * to fetch the next page of Jelly Beans.
     */
    suspend fun getJellyBeans(
        pageIndex: Int,
        pageSize: Int,
    ): NetworkJellyBeans
}
