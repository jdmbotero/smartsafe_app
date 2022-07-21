package com.smartsafe.smartsafe_app.presentation.main.boxDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartsafe.smartsafe_app.data.repository.box.AddOrUpdateBoxState
import com.smartsafe.smartsafe_app.data.repository.box.FetchBoxState
import com.smartsafe.smartsafe_app.data.repository.history.FetchHistoryState
import com.smartsafe.smartsafe_app.domain.entity.Box
import com.smartsafe.smartsafe_app.domain.entity.DoorAction
import com.smartsafe.smartsafe_app.domain.interactor.box.AddOrUpdateBoxUseCase
import com.smartsafe.smartsafe_app.domain.interactor.box.FetchBoxUseCase
import com.smartsafe.smartsafe_app.domain.interactor.history.FetchHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BoxDetailViewModel @Inject constructor(
    private val fetchBoxUseCase: FetchBoxUseCase,
    private val addOrUpdateBoxUseCase: AddOrUpdateBoxUseCase,
    private val fetchHistoryUseCase: FetchHistoryUseCase
) : ViewModel() {

    val userIntent = Channel<BoxDetailIntent>(Channel.UNLIMITED)
    val userIntentFetch = Channel<BoxDetailFetchIntent>(Channel.UNLIMITED)
    val userIntentHistory = Channel<BoxDetailHistoryIntent>(Channel.UNLIMITED)
    private val _state = MutableStateFlow<BoxDetailState>(BoxDetailState.Idle)
    private val _stateFetch = MutableStateFlow<BoxDetailFetchState>(BoxDetailFetchState.Idle)
    private val _stateHistory = MutableStateFlow<BoxDetailHistoryState>(BoxDetailHistoryState.Idle)

    val state: StateFlow<BoxDetailState>
        get() = _state
    val stateFetch: StateFlow<BoxDetailFetchState>
        get() = _stateFetch
    val stateHistory: StateFlow<BoxDetailHistoryState>
        get() = _stateHistory

    init {
        handleIntent()
    }

    private fun handleIntent() {
        viewModelScope.launch {
            userIntentFetch.consumeAsFlow().collectLatest {
                when (it) {
                    is BoxDetailFetchIntent.FetchBox -> fetchBox(it.box)
                }
            }
        }
        viewModelScope.launch {
            userIntent.consumeAsFlow().collectLatest {
                when (it) {
                    is BoxDetailIntent.OpenOrCloseBox -> openOrCloseBox(it.box, it.action)
                }
            }
        }
        viewModelScope.launch {
            userIntentHistory.consumeAsFlow().collectLatest {
                when (it) {
                    is BoxDetailHistoryIntent.FetchHistory -> fetchHistory(it.boxId)
                }
            }
        }
    }

    private suspend fun fetchBox(box: Box) {
        box.id?.let {
            fetchBoxUseCase.launch(it)
            fetchBoxUseCase.resultFlow.collect { state ->
                _stateFetch.value = when (state) {
                    is FetchBoxState.Success -> BoxDetailFetchState.SuccessFetch(state.box)
                    is FetchBoxState.Failure -> BoxDetailFetchState.Error(state.message)
                }
            }
        }
    }

    private suspend fun openOrCloseBox(box: Box, action: DoorAction) {
        box.id?.let {
            _state.value = BoxDetailState.Loading
            box.doorAction = action
            addOrUpdateBoxUseCase.launch(box)
            addOrUpdateBoxUseCase.resultFlow.collect { state ->
                _state.value = when (state) {
                    is AddOrUpdateBoxState.Success -> BoxDetailState.SuccessOpenOrClose(state.box)
                    is AddOrUpdateBoxState.Failure -> BoxDetailState.Error(state.message)
                }
            }
        }
    }

    private suspend fun fetchHistory(boxId: String?) {
        boxId?.let {
            fetchHistoryUseCase.launch(it)
            fetchHistoryUseCase.resultFlow.collect { state ->
                _stateHistory.value = when (state) {
                    is FetchHistoryState.Success -> BoxDetailHistoryState.SuccessFetchHistory(state.history)
                    is FetchHistoryState.Failure -> BoxDetailHistoryState.Error(state.message)
                }
            }
        }
    }
}