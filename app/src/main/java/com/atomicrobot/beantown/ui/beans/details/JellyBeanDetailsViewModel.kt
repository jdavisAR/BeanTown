package com.atomicrobot.beantown.ui.beans.details

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.atomicrobot.beantown.data.local.entity.JellyBeanEntity
import com.atomicrobot.beantown.data.repository.JellyBeanRepository
import com.atomicrobot.beantown.ui.BaseViewModel
import com.atomicrobot.beantown.ui.ViewAction
import com.atomicrobot.beantown.ui.ViewEvent
import com.atomicrobot.beantown.ui.ViewState
import com.atomicrobot.beantown.ui.beans.navigation.JellyBeanDetails
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class JellyBeanDetailsViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val repository: JellyBeanRepository,
) : BaseViewModel<JellyBeanDetailsAction, DetailsUiState, JellyBeanDetailsEvent>(
    defaultState = savedStateHandle.toInitialState()
) {

    private val beanId: Int = savedStateHandle.toRoute<JellyBeanDetails>().beanId

    init {
        viewModelScope.launch {
            _state.emit(
                value = repository
                    .getJellyBean(beanId = beanId)
                    ?.let(DetailsUiState::Details)
                    ?: DetailsUiState.NotFound
            )
        }
    }

    override fun handleAction(action: JellyBeanDetailsAction) { /*NO-OP*/ }
}

sealed interface ExtendedDetails {

    data object Loading: ExtendedDetails

    data class Details(
        val groupNames: List<String>,
        val ingredients: List<String>,
        val gluttenFree: Boolean,
        val sugarFree: Boolean,
        val seasonal: Boolean,
        val kosher: Boolean,
    ): ExtendedDetails {

        constructor(jellyBean: JellyBeanEntity): this(
            groupNames = jellyBean.groupName,
            ingredients = jellyBean.ingredients,
            gluttenFree = jellyBean.glutenFree,
            sugarFree = jellyBean.sugarFree,
            seasonal = jellyBean.seasonal,
            kosher = jellyBean.kosher,
        )
    }
}

sealed interface DetailsUiState : ViewState {

    data object NotFound : DetailsUiState

    data class Details(
        val beanId: Int,
        val flavorName: String,
        val imageUrl: String,
        val backdropColor: Color,
        val extendedDetails: ExtendedDetails = ExtendedDetails.Loading,
    ) : DetailsUiState {

        constructor(jellyBean: JellyBeanEntity) : this(
            beanId = jellyBean.beanId,
            flavorName = jellyBean.flavorName,
            imageUrl = jellyBean.imageUrl,
            backdropColor = jellyBean.backgroundColor,
            extendedDetails = ExtendedDetails.Details(jellyBean),
        )

        /**
         * Constructor for creating UIState from route args
         */
        constructor(jellyBean: JellyBeanDetails) : this(
            beanId = jellyBean.beanId,
            flavorName = jellyBean.flavorName,
            imageUrl = jellyBean.imageUrl,
            backdropColor = Color.Transparent,
            extendedDetails = ExtendedDetails.Loading,

        )
    }
}

private fun SavedStateHandle.toInitialState(): DetailsUiState = DetailsUiState.Details(toRoute<JellyBeanDetails>())

object JellyBeanDetailsAction : ViewAction
object JellyBeanDetailsEvent : ViewEvent