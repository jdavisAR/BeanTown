package com.atomicrobot.beantown.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.atomicrobot.beantown.data.local.JellyBeanDb
import com.atomicrobot.beantown.data.local.entity.JellyBeanEntity
import com.atomicrobot.beantown.data.network.JellyBeanNetworkDataSource
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

/**
 * Network/database backed implementation of [JellyBeanRepository].
 */
@OptIn(ExperimentalPagingApi::class)
@Single
class ProductionJellyBeanRepository(
    private val jellyBeanAPI: JellyBeanNetworkDataSource,
    private val jellyBeanDB: JellyBeanDb,
) : JellyBeanRepository {

    override fun jellyBeans(
        pageSize: Int,
    ): Flow<PagingData<JellyBeanEntity>> = Pager(
        config = PagingConfig(
            pageSize = pageSize,
            enablePlaceholders = false,
        ),
        remoteMediator = JellyBeanRemoteMediator(
            jellyBeanAPI = jellyBeanAPI,
            jellyBeanDB = jellyBeanDB,
        ),
        pagingSourceFactory = {
            jellyBeanDB.jellyBeansDao.pagingSource()
        }
    ).flow

    /**
     * Attempts to fetch the [JellyBeanEntity] with a matching [beanId] from the [JellyBeanDb].
     */
    override suspend fun getJellyBean(beanId: Int): JellyBeanEntity? =
        jellyBeanDB
            .jellyBeansDao
            .getBeanForBeanId(beanId = beanId)
}