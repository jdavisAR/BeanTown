package com.atomicrobot.beantown.ui.beans.details

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.testing.invoke
import androidx.navigation.toRoute
import app.cash.turbine.test
import com.atomicrobot.beantown.data.local.entity.JellyBeanEntity
import com.atomicrobot.beantown.data.mapper.toDbEntity
import com.atomicrobot.beantown.data.network.NetworkUtils
import com.atomicrobot.beantown.data.network.model.NetworkJellyBeans
import com.atomicrobot.beantown.data.repository.JellyBeanRepository
import com.atomicrobot.beantown.ui.beans.JELLY_BEANS_JSON
import com.atomicrobot.beantown.ui.beans.navigation.JellyBeanDetails
import com.atomicrobot.beantown.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.InputStreamReader

@RunWith(RobolectricTestRunner::class)
class JellyBeanDetailsViewModelTest {

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
                    .let { NetworkUtils.json.decodeFromString<NetworkJellyBeans>(it) }
                    .items.map { it.toDbEntity(page = 1) }
            }

        coEvery { jellyBeanRepository.getJellyBean(beanId = any()) } coAnswers {
            delay(100)
            jellyBeans.firstOrNull { it.beanId == args.first() as Int }
        }
    }

    @Test
    fun `When viewmodel is initialized to a given bean, viewmodel has expected state`() = runTest {
        val targetJellyBean = jellyBeans.first()
        // Construct a route object that we will store in the saved state handle for the viewmodel
        val route = JellyBeanDetails(
            beanId = targetJellyBean.beanId,
            flavorName = targetJellyBean.flavorName,
            imageUrl = targetJellyBean.imageUrl,
        )

        val savedStateHandle = SavedStateHandle(route = route)
        val viewModel = JellyBeanDetailsViewModel(
            savedStateHandle = savedStateHandle,
            repository = jellyBeanRepository,
        )
        /*

        I suspect it has something to do with Robolectric and how it handles serialization of
        strings into bundles but JellyBeanDetails.imageUrl is encoded & decoded strangely *ONLY*
        for Unit Test so we want to construct a DetailsUiState.Details the same way the viewmodel
        does it

         */
        val targetDetails = savedStateHandle.toRoute<JellyBeanDetails>()
        viewModel.state.test {
            val partiallyLoadedExpectedState = DetailsUiState.Details(
                beanId = targetDetails.beanId,
                flavorName = targetDetails.flavorName,
                imageUrl = targetDetails.imageUrl,
                backdropColor = Color.Transparent,
                extendedDetails = ExtendedDetails.Loading,
            )
            Assert.assertEquals(partiallyLoadedExpectedState, awaitItem())
            val fullyLoadedExpectedState = DetailsUiState.Details(
                beanId = targetJellyBean.beanId,
                flavorName = targetJellyBean.flavorName,
                imageUrl = targetJellyBean.imageUrl,
                backdropColor = targetJellyBean.backgroundColor,
                extendedDetails = ExtendedDetails.Details(targetJellyBean),
            )
            Assert.assertEquals(fullyLoadedExpectedState, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `When viewmodel is initialized to a given bean, viewmodel state == `() = runTest {
        // Construct a route object that we will store in the saved state handle for the viewmodel
        val route = JellyBeanDetails(
            beanId = 13,
            flavorName = "LOREM IPSUM",
            imageUrl = "www.NotARealSite.com/do_not_click_me",
        )

        val savedStateHandle = SavedStateHandle(route = route)
        val viewModel = JellyBeanDetailsViewModel(
            savedStateHandle = savedStateHandle,
            repository = jellyBeanRepository,
        )
        viewModel.state.test {
            // Throw away item that's emitted while loading from repo
            awaitItem()
            Assert.assertEquals(DetailsUiState.NotFound, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}