package com.smartsafe.smartsafe_app.presentation.main.boxNew

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartsafe.smartsafe_app.data.repository.authWithPhone.AuthWithPhoneRepositoryImpl
import com.smartsafe.smartsafe_app.data.repository.box.AddOrUpdateBoxState
import com.smartsafe.smartsafe_app.domain.entity.Box
import com.smartsafe.smartsafe_app.domain.interactor.box.AddOrUpdateBoxUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BoxNewViewModel @Inject constructor(
    private val addOrUpdateBoxUseCase: AddOrUpdateBoxUseCase
) : ViewModel() {

    val userIntent = Channel<BoxNewIntent>(Channel.UNLIMITED)
    private val _state = MutableStateFlow<BoxNewState>(BoxNewState.Idle)

    val state: StateFlow<BoxNewState>
        get() = _state

    init {
        handleIntent()
    }

    private fun handleIntent() {
        viewModelScope.launch {
            userIntent.consumeAsFlow().collect {
                when (it) {
                    is BoxNewIntent.AddOrUpdateBox -> addOrUpdateBox(it.box)
                }
            }
        }
    }

    private suspend fun addOrUpdateBox(box: Box) {
        AuthWithPhoneRepositoryImpl.currentUser?.let {
            _state.value = BoxNewState.Loading
            box.userId = it.uid
            addOrUpdateBoxUseCase.launch(box)
            addOrUpdateBoxUseCase.resultFlow.collect { state ->
                _state.value = when (state) {
                    is AddOrUpdateBoxState.Success -> BoxNewState.Success(state.box)
                    is AddOrUpdateBoxState.Failure -> BoxNewState.Error(state.message)
                }
            }
        }
    }
}