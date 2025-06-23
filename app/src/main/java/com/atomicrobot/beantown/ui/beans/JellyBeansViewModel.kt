package com.atomicrobot.beantown.ui.beans

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.atomicrobot.beantown.data.local.entity.JellyBeanEntity
import com.atomicrobot.beantown.data.repository.JellyBeanRepository
import com.atomicrobot.beantown.ui.BaseViewModel
import com.atomicrobot.beantown.ui.ViewAction
import com.atomicrobot.beantown.ui.ViewEvent
import com.atomicrobot.beantown.ui.ViewState
import com.atomicrobot.beantown.ui.beans.BeansViewEvent.ViewJellyBean
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class JellyBeansViewModel(
    jellyBeanRepository: JellyBeanRepository,
) : BaseViewModel<JellyBeansViewAction, JellyBeansUiState, BeansViewEvent>(JellyBeansUiState) {

    val jellyBeans: Flow<PagingData<BeanUIState>> = jellyBeanRepository
        .jellyBeans()
        .map(PagingData<JellyBeanEntity>::toUiModel)
        .cachedIn(viewModelScope)

    override fun handleAction(action: JellyBeansViewAction) {
        when (action) {
            is JellyBeansViewAction.BeanClicked -> onBeanClicked(action.clickedBean)
        }
    }

    private fun onBeanClicked(clickedBean: BeanUIState) =
        sendEvent(element = clickedBean.toViewEvent())
}

object JellyBeansUiState : ViewState

data class BeanUIState(
    val beanId: Int,
    val flavorName: String,
    val imageUrl: String,
    val backdropColor: Color,
    val colorGroup: String,
) {
    val debugInfo: String = "${colorGroup}/${beanId}"

    constructor(bean: JellyBeanEntity) : this(
        beanId = bean.beanId,
        flavorName = bean.flavorName,
        imageUrl = bean.imageUrl,
        backdropColor = bean.backgroundColor,
        colorGroup = bean.colorGroup,
    )
}

sealed interface JellyBeansViewAction : ViewAction {
    data class BeanClicked(val clickedBean: BeanUIState) : JellyBeansViewAction
}

sealed interface BeansViewEvent : ViewEvent {

    data class ViewJellyBean(
        val beanId: Int,
        val flavorName: String,
        val imageUrl: String,
    ) : BeansViewEvent {

        constructor(beanUiState: BeanUIState) : this(
            beanId = beanUiState.beanId,
            flavorName = beanUiState.flavorName,
            imageUrl = beanUiState.imageUrl,
        )
    }
}

private fun PagingData<JellyBeanEntity>.toUiModel(): PagingData<BeanUIState> =
    this.map(::BeanUIState)

internal fun BeanUIState.toViewEvent(): ViewJellyBean = ViewJellyBean(this)