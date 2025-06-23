package com.atomicrobot.beantown.ui.beans

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.testing.ErrorRecovery.THROW
import androidx.paging.testing.LoadErrorHandler
import androidx.paging.testing.asPagingSourceFactory
import androidx.paging.testing.asSnapshot
import app.cash.turbine.test
import com.atomicrobot.beantown.data.local.entity.JellyBeanEntity
import com.atomicrobot.beantown.data.mapper.toDbEntity
import com.atomicrobot.beantown.data.network.NetworkUtils
import com.atomicrobot.beantown.data.network.model.NetworkJellyBeans
import com.atomicrobot.beantown.data.repository.JellyBeanRepository
import com.atomicrobot.beantown.util.ExceptionalPagingSource
import com.atomicrobot.beantown.util.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.InputStreamReader
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class JellyBeansViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var jellyBeans: List<JellyBeanEntity>
    private var jellyBeanRepository: JellyBeanRepository = mockk()

    @Before
    fun setup() {
        jellyBeans = this::class.java
            .classLoader?.getResourceAsStream(JELLY_BEANS_JSON).use { stream ->
                InputStreamReader(stream)
                    .use { r -> r.readText() }
                    .let {
                        NetworkUtils.json.decodeFromString<NetworkJellyBeans>(it)
                    }
                    .items.map { it.toDbEntity(page = 1) }
            }
    }

    private fun getPager(
        pagingSourceFactory: () -> PagingSource<Int, JellyBeanEntity>,
    ): Pager<Int, JellyBeanEntity> = Pager<Int, JellyBeanEntity>(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false,
        ),
        initialKey = null,
        pagingSourceFactory = pagingSourceFactory,
    )

    @Test
    fun `viewmodel has expected empty state`() = runTest {
        val pagingSourceFactory = emptyList<JellyBeanEntity>().asPagingSourceFactory()
        val pager = getPager(pagingSourceFactory = pagingSourceFactory).flow
        every { jellyBeanRepository.jellyBeans(any()) } answers { pager }

        val viewModel = JellyBeansViewModel(jellyBeanRepository = jellyBeanRepository)
        viewModel.state.test {
            Assert.assertEquals(JellyBeansUiState, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `When JellyBeanRepository returns an empty flow, then JellyBeansViewModel_jellyBeans emits empty PagingData`() =
        runTest {
            val pagingSourceFactory = emptyList<JellyBeanEntity>().asPagingSourceFactory()
            val pagedDataFlow = getPager(pagingSourceFactory = pagingSourceFactory).flow
            every { jellyBeanRepository.jellyBeans(any()) } answers { pagedDataFlow }

            val viewModel = JellyBeansViewModel(jellyBeanRepository = jellyBeanRepository)
            viewModel.state.test {
                Assert.assertEquals(JellyBeansUiState, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }

            val beans = viewModel.jellyBeans
            val beansSnapshot = beans.asSnapshot()

            assertTrue(actual = beansSnapshot.isEmpty())
        }

    @Test
    fun `When JellyBeanRepository returns a flow of PagingData, then JellyBeansViewModel_jellyBeans emits non-empty PagingData`() =
        runTest {
            val pagingSourceFactory = jellyBeans.asPagingSourceFactory()
            val pagedDataFlow = getPager(pagingSourceFactory = pagingSourceFactory).flow
            every { jellyBeanRepository.jellyBeans(any()) } answers { pagedDataFlow }

            val viewModel = JellyBeansViewModel(jellyBeanRepository = jellyBeanRepository)
            viewModel.state.test {
                Assert.assertEquals(JellyBeansUiState, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }

            val beans = viewModel.jellyBeans
            val beansSnapshot = beans.asSnapshot()

            assertTrue(
                actual = jellyBeans.isNotEmpty(),
                message = "Jelly Beans data is empty, when it shouldn't be"
            )
            assertEquals(
                expected = beansSnapshot.size,
                actual = jellyBeans.size,
                message = "Jelly Beans snapshot size does not match test data set",
            )

            assertContentEquals(
                jellyBeans.map(::BeanUIState),
                beansSnapshot,
                message = "Jelly Beans snapshot data does not match test data set",
            )
        }

    @Test
    fun `When paging source returns an LoadResult_Error asSnapshot() throws an error`() = runTest {
        val exception = RuntimeException("Uh Oh, the network is down!")
        val exceptionalPagerFlow = getPager { ExceptionalPagingSource(exception) }.flow
        every { jellyBeanRepository.jellyBeans(any()) } answers { exceptionalPagerFlow }

        val viewModel = JellyBeansViewModel(jellyBeanRepository = jellyBeanRepository)
        viewModel.state.test {
            Assert.assertEquals(JellyBeansUiState, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        val beans = viewModel.jellyBeans
        var caughtException: Exception? = null
        try {
            beans.asSnapshot(
                onError = LoadErrorHandler { THROW },
            )
        } catch (e: Exception) {
            caughtException = e
        }

        assertNotNull(
            actual = caughtException,
            message = "We didn't catch an exception when we expected too..."
        )

        assertTrue(
            actual = caughtException is RuntimeException,
            message = "The caught exception was not the expected type, something else has gone wrong."
        )

        assertEquals(
            actual = caughtException.message,
            expected = "Uh Oh, the network is down!"
        )
    }

    @Test
    fun `When item clicked`() = runTest {
        val pagingSourceFactory = jellyBeans.asPagingSourceFactory()
        val pagedDataFlow = getPager(pagingSourceFactory = pagingSourceFactory).flow
        every { jellyBeanRepository.jellyBeans(any()) } answers { pagedDataFlow }

        val viewModel = JellyBeansViewModel(jellyBeanRepository = jellyBeanRepository)
        viewModel.state.test {
            Assert.assertEquals(JellyBeansUiState, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        val beans = viewModel.jellyBeans
        val beansSnapshot = beans.asSnapshot()

        assertTrue(
            actual = jellyBeans.isNotEmpty(),
            message = "Jelly Beans data is empty, when it shouldn't be"
        )
        assertEquals(
            expected = beansSnapshot.size,
            actual = jellyBeans.size,
            message = "Jelly Beans snapshot size does not match test data set",
        )

        assertContentEquals(
            jellyBeans.map(::BeanUIState),
            beansSnapshot,
            message = "Jelly Beans snapshot data does not match test data set",
        )

        beansSnapshot.first().also { expectedJellyBean ->
            viewModel.sendAction(JellyBeansViewAction.BeanClicked(expectedJellyBean))
            val expectedEvent = expectedJellyBean.toViewEvent()
            viewModel.events.test {
                assertEquals(
                    expected = expectedEvent,
                    actual = awaitItem()
                )
            }
        }
    }
}


internal const val JELLY_BEANS_JSON = "jelly_bean.json"
