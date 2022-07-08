package com.smartsafe.smartsafe_app.presentation.main.boxList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartsafe.smartsafe_app.data.repository.authWithPhone.AuthWithPhoneRepositoryImpl
import com.smartsafe.smartsafe_app.data.repository.box.FetchBoxesState
import com.smartsafe.smartsafe_app.domain.interactor.box.FetchBoxesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BoxListViewModel @Inject constructor(
    private var fetchBoxesUseCase: FetchBoxesUseCase
) : ViewModel() {

    val userIntent = Channel<BoxListIntent>(Channel.UNLIMITED)
    private val _state = MutableStateFlow<BoxListState>(BoxListState.Idle)

    val state: StateFlow<BoxListState>
        get() = _state

    init {
        handleIntent()
    }

    private fun handleIntent() {
        viewModelScope.launch {
            userIntent.consumeAsFlow().collectLatest {
                when (it) {
                    is BoxListIntent.FetchBoxes -> fetchBoxes()
                }
            }
        }
    }

    private suspend fun fetchBoxes() {
        AuthWithPhoneRepositoryImpl.currentUser?.let {
            _state.value = BoxListState.Loading
            fetchBoxesUseCase.launch(it.uid)
            fetchBoxesUseCase.resultFlow.collect { state ->
                _state.value = when (state) {
                    is FetchBoxesState.Success ->
                        BoxListState.Success(state.boxes ?: listOf())
                    is FetchBoxesState.Failure -> BoxListState.Error(state.message)
                }
            }
        }
    }
}