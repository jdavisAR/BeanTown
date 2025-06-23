package com.atomicrobot.beantown.data.repository

import androidx.paging.PagingData
import com.atomicrobot.beantown.data.local.entity.JellyBeanEntity
import kotlinx.coroutines.flow.Flow

/**
 * Interface for accessing [JellyBeanEntity].
 */
interface JellyBeanRepository {

    /**
     * Returns a [Flow] of [PagingData] representing [JellyBeanEntity] that have been fetched a
     * pageable network data source. The [PagingData] will internally handle triggering new
     * [PagingData] to be emitted when the ends have been reached. The paging data contains
     * properties about the loading state (prepend/append/refresh).
     *
     * @param pageSize Number of [JellyBeanEntity] contained in a single page.
     * @return A [Flow] of [PagingData] containing [JellyBeanEntity].
     */
    fun jellyBeans(
        pageSize: Int = defaultPageSize,
    ) : Flow<PagingData<JellyBeanEntity>>

    /**
     * Attempts to fetch a [JellyBeanEntity] that matches the specified [beanId].
     * @param beanId The unique Id of the target [JellyBeanEntity] to return.
     * @return The [JellyBeanEntity] that matches the specified [beanId] or null if one doesn't
     * exist/been previously fetched from the network data source.
     */
    suspend fun getJellyBean(beanId: Int): JellyBeanEntity?
}

const val defaultPageSize: Int = 15