package com.atomicrobot.beantown.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * A [ViewModel] that implements [Unidirectional Data Flow](https://developer.android.com/jetpack/compose/architecture#udf)
 * using a [Channel] and [StateFlow].
 *
 * @param Action The UI-side events that this ViewModel can handle,
 * typically a sealed class
 * should implement the marker interface [ViewAction] to inherit basic type checking.
 * @param State A state object that has all the data necessary to paint the screen.  This should
 * typically be a data class or a sealed type of data classes.  The UI layer should not need
 * to make any decisions using this state.  Contents should be a perfect match for a specific screen
 * should implement the marker interface [ViewState] to inherit basic type checking.
 * @param Event A (usually `sealed`) type of one-shot events that this ViewModel can emit.  Events
 * are distinct from [State] in that they don't persist as a [StateFlow.value] across configuration
 * changes or re-subscription.  A common example of an [Event] is a navigation: you'd never want a
 * ViewModel to _re-emit_ a navigation command multiple times as part of the [State].  If the UI layer
 * goes through a lifecycle destruction, an in-flight [Event] is allowed to be dropped,
 * should implement the marker interface [ViewEvent] to inherit basic type checking.
 * @param defaultState A [State] suitable for use while this ViewModel is initializing.
 * It's often the case that a ViewModel will need to wait for asynchronous delivery of data
 * from the lower layers of the application, and during that time the [StateFlow.value] inside [state]
 * must still be present and initialized.
 */
@Suppress(
    "PropertyName", // allow underscores on protected fields
    "MemberVisibilityCanBePrivate", // allow subclass visibility
)
abstract class BaseViewModel<Action : ViewAction, State : ViewState, Event : ViewEvent>(
    private val defaultState: State,
) : ViewModel() {
    /**
     * The internal channel that this ViewModel can use to accept and handle [Action]s from UI.
     */
    private val _actions = Channel<Action>(capacity = UNLIMITED)

    /**
     * The channel used by UI to send new actions into this view model for handling.
     */
    val actions: SendChannel<Action> = _actions

    /**
     * An internal mutable instance backing the public-visible [state].
     * This is used by subclasses of [BaseViewModel] to manage emitting new states.
     *
     * Use `_state.value = `
     */
    protected val _state = MutableStateFlow(defaultState)

    /**
     * The [StateFlow] that will emit new screen-ready instances of [State] up to the UI layer.
     */
    val state: StateFlow<State> = _state

    /**
     * The internal event channel used to emit one-shot events into UI.
     */
    private val _eventChannel = Channel<Event>(capacity = UNLIMITED)

    /**
     * The [SendChannel] visible to subclasses, where they can send one-shot events into UI.
     */
    protected val _events: SendChannel<Event> = _eventChannel

    /**
     * The flow that will emit one-shot [Event]s into to the UI layer.
     */
    val events: Flow<Event> = _eventChannel.receiveAsFlow()

    init {
        _actions.consumeAsFlow()
            .onEach {
                handleAction(it)
            }
            .launchIn(viewModelScope)
    }

    /**
     * The helper functions for handling the [Action] in the ViewModel
     */
    fun sendAction(element: Action) {
        _actions.trySend(element)
    }

    /**
     * The helper functions for handling the [State] in the ViewModel
     *
     * updateState { innerState ->
     *      innerState.copy(field = value)
     * }
     */
    protected fun updateState(update: (State) -> State) {
        _state.value = update(_state.value)
    }

    /**
     * The helper functions for handling the [Event] in the ViewModel
     */
    protected fun sendEvent(element: Event) {
        _events.trySend(element)
    }

    /**
     * Subclasses must override this to handle all [Action]s that get pushed through the publicly
     * visible [actions] channel.
     *
     * A reminder that [Action] should virtually always be a sealed class of simple data classes or
     * raw `object`s that express UI-side interactions and events.
     *
     * Your implementation of [handleAction] should therefore be a simple branching `when (action) {`
     * statement that fans out to other private handling methods.
     *
     * ```kotlin
     * override fun handleAction(action: ProfileAction) {
     *   when (action) {
     *     is SignOutClicked -> handleSignOut()
     *     //... other variations of the sealed type [ProfileAction]
     *   }
     * }
     *
     * private fun handleSignOut() {
     *   //...
     * }
     * ```
     */
    protected abstract fun handleAction(action: Action)
}

/**
 * A marker interface for the [BaseViewModel] actions,
 * which get piped into your VM from the view layer.
 * @see BaseViewModel
 */
interface ViewAction

/**
 * A marker interface for the [BaseViewModel] state,
 * which determines the state of the view to be driven from the viewmodel.
 * @see BaseViewModel
 */
interface ViewState

/**
 * A marker interface for the [BaseViewModel] events,
 * which get piped from your VM to the view layer.
 * @see BaseViewModel
 */
interface ViewEvent

