package com.atomicrobot.beantown.data.repository

import androidx.paging.cachedIn
import androidx.paging.testing.asSnapshot
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.atomicrobot.beantown.data.local.JellyBeanDb
import com.atomicrobot.beantown.data.mapper.toDbEntity
import com.atomicrobot.beantown.data.network.FakeJellyBeanNetworkDataSource
import com.atomicrobot.beantown.data.network.NetworkUtils
import com.atomicrobot.beantown.data.network.model.NetworkJellyBean
import com.atomicrobot.beantown.data.network.model.NetworkJellyBeans
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.InputStreamReader
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@RunWith(AndroidJUnit4::class)
class JellyBeanRepositoryTest {

    private lateinit var jellyBeans: List<NetworkJellyBean>
    private val jellyBeanDataSource = FakeJellyBeanNetworkDataSource()
    private val jellyBeanDB: JellyBeanDb = JellyBeanDb.buildDB(
        context = ApplicationProvider.getApplicationContext(),
        inMemory = true,
    )
    private val jellyBeanRepository: JellyBeanRepository = ProductionJellyBeanRepository(
        jellyBeanDataSource,
        jellyBeanDB,
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
    fun whenNetworkDataSourcesReturnsDataJellyBeanRepositoryReturnsFlowContainingValidPagingData() = runTest {
        jellyBeanDataSource.addJellyBeans(jellyBeans)
        val jellyBeansFlow = jellyBeanRepository
            .jellyBeans()
            .cachedIn(backgroundScope)

        val entities = jellyBeansFlow.asSnapshot()
        assertTrue(entities.size == jellyBeans.size)
        // Make sure the Contents match
        assertContentEquals(jellyBeans.map { it.toDbEntity(1) }, entities)
    }

    @Test
    fun whenJellyBeanDatabaseDataHasDataJellyBeanRepositoryReturnsValidJellyBeanEntityForValidBeanId() = runTest {
        // Load up the database first
        jellyBeanDB
            .jellyBeansDao
            .insertAll(
                jellyBeans.map { it.toDbEntity(1) }
            )
        val expectedJellyBean = jellyBeans.first().toDbEntity(1)
        val actual = jellyBeanRepository.getJellyBean(beanId = 1)
        assertNotNull(actual)
        assertEquals(expectedJellyBean, actual)
    }

    @Test
    fun whenJellyBeanDatabaseDataHasDataJellyBeanRepositoryReturnsValidJellyBeanEntityForInvalidValidBeanId() = runTest {
        // Load up the database first
        jellyBeanDB
            .jellyBeansDao
            .insertAll(
                jellyBeans.map { it.toDbEntity(1) }
            )

        val actual = jellyBeanRepository.getJellyBean(beanId = jellyBeans.size + 1)
        assertNull(actual)
    }

    @Test
    fun whenJellyBeanDatabaseIsEmptyJellyBeanRepositoryReturnsNullJellyBeanEntity() = runTest {
        // Make sure the database is empty
        jellyBeanDB
            .jellyBeansDao
            .insertAll(emptyList())
        val actual = jellyBeanRepository.getJellyBean(beanId = 1)
        assertNull(actual)
    }
}