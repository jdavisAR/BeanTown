package com.atomicrobot.beantown.util

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.atomicrobot.beantown.data.local.entity.JellyBeanEntity

/**
 * A [PagingSource] that will always returning a [LoadResult.Error] containing the specified
 * [Throwable].
 */
class ExceptionalPagingSource(
    private val throwable: Throwable
) : PagingSource<Int, JellyBeanEntity>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, JellyBeanEntity> =
        LoadResult.Error(throwable)

    override fun getRefreshKey(state: PagingState<Int, JellyBeanEntity>): Int? = null
}