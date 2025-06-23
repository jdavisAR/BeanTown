package com.atomicrobot.beantown.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator.MediatorResult
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.atomicrobot.beantown.data.local.JellyBeanDb
import com.atomicrobot.beantown.data.local.entity.JellyBeanEntity
import com.atomicrobot.beantown.data.network.FakeJellyBeanNetworkDataSource
import com.atomicrobot.beantown.data.network.NetworkUtils
import com.atomicrobot.beantown.data.network.model.NetworkJellyBean
import com.atomicrobot.beantown.data.network.model.NetworkJellyBeans
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.io.InputStreamReader

@ExperimentalPagingApi
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class JellyBeanRemoteMediatorTest {

    private lateinit var jellyBeans: List<NetworkJellyBean>
    private val jellyBeanDataSource = FakeJellyBeanNetworkDataSource()
    private val jellyBeanDB: JellyBeanDb = JellyBeanDb.buildDB(
        context = ApplicationProvider.getApplicationContext(),
        inMemory = true,
    )

    @Before
    fun setup() {
        jellyBeans = this::class.java
            .classLoader?.getResourceAsStream(JELLY_BEANS_JSON).use { stream ->
                InputStreamReader(stream)
                    .use { r -> r.readText() }
                    .let {
                        NetworkUtils.json.decodeFromString<NetworkJellyBeans>(it)
                    }
                    .items
            }
    }

    @After
    fun tearDown() {
        jellyBeanDB.clearAllTables()
        jellyBeanDataSource.clearJellyBeans()
        jellyBeanDataSource.simulateException = false
        jellyBeanDataSource.simulateNetworkError = false
    }

    @Test
    fun whenNetworkResponseReturnsPartialJellyBeansLoadReturnsMediatorResultSuccessEndOfPaginationReachedNotReached() = runTest {
        jellyBeanDataSource.addJellyBeans(jellyBeans)
        val remoteMediator = JellyBeanRemoteMediator(
            jellyBeanAPI = jellyBeanDataSource,
            jellyBeanDB = jellyBeanDB,
        )
        val pagingState = PagingState<Int, JellyBeanEntity>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(10),
            leadingPlaceholderCount = 0
        )
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is MediatorResult.Success)
        assertFalse((result as MediatorResult.Success).endOfPaginationReached)
    }

    @Test
    fun whenNetworkResponseReturnsAllJellyBeansLoadReturnsMediatorResultSuccessEndOfPaginationReached() = runTest {
        jellyBeanDataSource.addJellyBeans(jellyBeans)
        val remoteMediator = JellyBeanRemoteMediator(
            jellyBeanAPI = jellyBeanDataSource,
            jellyBeanDB = jellyBeanDB,
        )
        val pagingState = PagingState<Int, JellyBeanEntity>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(12),
            leadingPlaceholderCount = 0
        )
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is MediatorResult.Success)
        assertTrue((result as MediatorResult.Success).endOfPaginationReached)
    }

    @Test
    fun whenLoadingBeyondJellyBeansSetBoundsLoadReturnsMediatorResultSuccessEndOfPaginationReached() = runTest {
        jellyBeanDataSource.addJellyBeans(jellyBeans)
        val remoteMediator = JellyBeanRemoteMediator(
            jellyBeanAPI = jellyBeanDataSource,
            jellyBeanDB = jellyBeanDB,
        )
        val pagingState = PagingState<Int, JellyBeanEntity>(
            pages = listOf(),
            anchorPosition = 15,
            config = PagingConfig(12),
            leadingPlaceholderCount = 0
        )
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is MediatorResult.Success)
        assertTrue((result as MediatorResult.Success).endOfPaginationReached)
    }

    @Test
    fun whenNetworkResponseReturnsEmptyDataSetLoadReturnsMediatorResultSuccessEndOfPaginationReached() = runTest {
        val remoteMediator = JellyBeanRemoteMediator(
            jellyBeanAPI = jellyBeanDataSource,
            jellyBeanDB = jellyBeanDB,
        )
        val pagingState = PagingState<Int, JellyBeanEntity>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(10),
            leadingPlaceholderCount = 0
        )
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is MediatorResult.Success)
        assertTrue((result as MediatorResult.Success).endOfPaginationReached)
    }

    @Test
    fun whenSimulatingExceptionalNetworkOperationLoadReturnsMediatorResultError() = runTest {
        jellyBeanDataSource.simulateException = true
        val remoteMediator = JellyBeanRemoteMediator(
            jellyBeanAPI = jellyBeanDataSource,
            jellyBeanDB = jellyBeanDB,
        )
        val pagingState = PagingState<Int, JellyBeanEntity>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(10),
            leadingPlaceholderCount = 0
        )
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is MediatorResult.Error)
        val error = result as MediatorResult.Error
        assertTrue(error.throwable is IOException)
        val throwable = error.throwable
        assertTrue(throwable.message == "Simulated exception while trying to fetch page: 1 of size 10 Jelly Beans!")
    }

    @Test
    fun whenSimulatingErroneousNetworkResponseLoadReturnsMediatorResultError() = runTest {
        jellyBeanDataSource.simulateNetworkError = true
        val remoteMediator = JellyBeanRemoteMediator(
            jellyBeanAPI = jellyBeanDataSource,
            jellyBeanDB = jellyBeanDB,
        )
        val pagingState = PagingState<Int, JellyBeanEntity>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(10),
            leadingPlaceholderCount = 0
        )
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is MediatorResult.Error)
        val error = result as MediatorResult.Error
        assertTrue(error.throwable.message == "Failed to fetch page: 1 of Jelly Beans")
    }
}

private const val JELLY_BEANS_JSON = "jelly_bean.json"