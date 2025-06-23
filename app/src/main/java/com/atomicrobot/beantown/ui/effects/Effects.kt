package com.atomicrobot.beantown.ui.effects

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.atomicrobot.beantown.ui.BaseViewModel
import com.atomicrobot.beantown.ui.ViewAction
import com.atomicrobot.beantown.ui.ViewEvent
import com.atomicrobot.beantown.ui.ViewState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
fun <A : ViewAction, S : ViewState, E : ViewEvent> BaseViewModel<A, S, E>.EventEffect(
    onEvent: (event: E) -> Unit,
) = LaunchedEffect(
    Unit
) {
    events
        .onEach(onEvent)
        .launchIn(this)
}