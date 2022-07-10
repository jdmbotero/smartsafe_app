package com.smartsafe.smartsafe_app.presentation.main.boxDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartsafe.smartsafe_app.data.repository.box.AddOrUpdateBoxState
import com.smartsafe.smartsafe_app.data.repository.box.FetchBoxState
import com.smartsafe.smartsafe_app.domain.entity.Box
import com.smartsafe.smartsafe_app.domain.entity.DoorAction
import com.smartsafe.smartsafe_app.domain.entity.DoorStatus
import com.smartsafe.smartsafe_app.domain.interactor.box.AddOrUpdateBoxUseCase
import com.smartsafe.smartsafe_app.domain.interactor.box.FetchBoxUseCase
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
    private val addOrUpdateBoxUseCase: AddOrUpdateBoxUseCase
) : ViewModel() {

    val userIntent = Channel<BoxDetailIntent>(Channel.UNLIMITED)
    private val _state = MutableStateFlow<BoxDetailState>(BoxDetailState.Idle)

    val state: StateFlow<BoxDetailState>
        get() = _state

    init {
        handleIntent()
    }

    private fun handleIntent() {
        viewModelScope.launch {
            userIntent.consumeAsFlow().collectLatest {
                when (it) {
                    is BoxDetailIntent.FetchBox -> fetchBox(it.box)
                    is BoxDetailIntent.OpenOrCloseBox -> openOrCloseBox(it.box, it.action)
                }
            }
        }
    }

    private suspend fun fetchBox(box: Box) {
        box.id?.let {
            _state.value = BoxDetailState.Loading
            fetchBoxUseCase.launch(it)
            fetchBoxUseCase.resultFlow.collect { state ->
                _state.value = when (state) {
                    is FetchBoxState.Success -> BoxDetailState.SuccessFetch(state.box)
                    is FetchBoxState.Failure -> BoxDetailState.Error(state.message)
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
}