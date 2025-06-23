package com.atomicrobot.beantown.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.paging.RemoteMediator.MediatorResult.Success
import androidx.room.withTransaction
import com.atomicrobot.beantown.data.local.JellyBeanDb
import com.atomicrobot.beantown.data.local.dao.JellyBeanRemoteKeyDao
import com.atomicrobot.beantown.data.local.entity.JellyBeanEntity
import com.atomicrobot.beantown.data.local.entity.JellyBeanRemoteKey
import com.atomicrobot.beantown.data.mapper.toDbEntity
import com.atomicrobot.beantown.data.network.JellyBeanNetworkDataSource
import com.atomicrobot.beantown.data.network.model.NetworkJellyBeans
import com.atomicrobot.beantown.data.network.model.hasReachedEnd
import com.atomicrobot.beantown.data.network.model.isError
import org.koin.core.annotation.Factory
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalPagingApi::class)
@Factory(binds = [JellyBeanRemoteMediator::class])
class JellyBeanRemoteMediator(
    private val jellyBeanAPI: JellyBeanNetworkDataSource,
    private val jellyBeanDB: JellyBeanDb,
) : RemoteMediator<Int, JellyBeanEntity>() {

    override suspend fun initialize(): InitializeAction {
        val cacheTimeout = TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS)
        val creationTime = jellyBeanDB.remoteKeysDao.getCreationTime() ?: 0L
        // We want to expire the cache after 1 hour
        return if ((System.currentTimeMillis() - creationTime) < cacheTimeout) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    private suspend fun PagingState<Int, JellyBeanEntity>.remoteKeyForFirstItemOrNull(
        remoteKeyDao: JellyBeanRemoteKeyDao,
    ): JellyBeanRemoteKey? = firstItemOrNull()?.let { entity ->
        remoteKeyDao
            .getRemoteKeyForPage(page = entity.page)
    }

    private suspend fun PagingState<Int, JellyBeanEntity>.remoteKeyForLastItemOrNull(
        remoteKeyDao: JellyBeanRemoteKeyDao,
    ): JellyBeanRemoteKey? = lastItemOrNull()?.let { entity ->
        remoteKeyDao
            .getRemoteKeyForPage(page = entity.page)
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, JellyBeanEntity>,
    ): MediatorResult = runCatching {
        val beanDao = jellyBeanDB.jellyBeansDao
        val remoteKeyDao = jellyBeanDB.remoteKeysDao
        val pageIndex = when (loadType) {
            LoadType.REFRESH -> 1 //  Start from page 1
            // Grab the first loaded item and check the database to see if there is any previous
            // pages
            LoadType.PREPEND -> state
                .remoteKeyForFirstItemOrNull(remoteKeyDao).let { remoteKey ->
                    remoteKey?.prevPage
                        ?: return@runCatching Success(endOfPaginationReached = remoteKey != null)
                }
            // Grab the first loaded item and check the database to see if there are additional
            // pages
            LoadType.APPEND -> state
                .remoteKeyForLastItemOrNull(remoteKeyDao).let { remoteKey ->
                    remoteKey?.nextPage
                        ?: return@runCatching Success(endOfPaginationReached = remoteKey != null)
                }
        }

        val jellyBeans = jellyBeanAPI.getJellyBeans(
            pageSize = state.config.pageSize,
            pageIndex = pageIndex,
        )

        if (jellyBeans.isError) return MediatorResult.Error(Throwable("Failed to fetch page: $pageIndex of Jelly Beans"))

        val beans = jellyBeans.items.map { it.toDbEntity(page = pageIndex) }
        jellyBeanDB.withTransaction {
            if (loadType == LoadType.REFRESH) {
                remoteKeyDao.clearAll()
                beanDao.clearAll()
            }

            beanDao.insertAll(beans)
            remoteKeyDao.insert(remoteKey = jellyBeans.toRemoteKey(currentPage = pageIndex))
        }
        return Success(endOfPaginationReached = jellyBeans.hasReachedEnd())
    }.getOrElse { MediatorResult.Error(it) }
}

private fun NetworkJellyBeans.toRemoteKey(
    currentPage: Int,
): JellyBeanRemoteKey = JellyBeanRemoteKey(
    prevPage = (currentPage - 1).takeIf { it > 0 },
    currentPage = currentPage,
    nextPage = (currentPage + 1).takeIf { it <= totalPages },
    createdAt = System.currentTimeMillis(),
)