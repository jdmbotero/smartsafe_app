package com.smartsafe.smartsafe_app.presentation.main.boxNew

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartsafe.smartsafe_app.data.repository.authWithPhone.AuthWithPhoneRepositoryImpl
import com.smartsafe.smartsafe_app.data.repository.box.AddOrUpdateBoxState
import com.smartsafe.smartsafe_app.data.repository.box.FetchBoxState
import com.smartsafe.smartsafe_app.domain.entity.Box
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
class BoxNewViewModel @Inject constructor(
    private val fetchBoxUseCase: FetchBoxUseCase,
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
            userIntent.consumeAsFlow().collectLatest {
                when (it) {
                    is BoxNewIntent.AddOrUpdateBox -> addOrUpdateBox(it.box)
                }
            }
        }
    }

    private suspend fun addOrUpdateBox(box: Box) {
        AuthWithPhoneRepositoryImpl.currentUser?.let { user ->
            box.id?.let { boxId ->
                _state.value = BoxNewState.Loading
                fetchBoxUseCase.launch(boxId)
                fetchBoxUseCase.resultFlow.collect { boxState ->
                    when (boxState) {
                        is FetchBoxState.Success -> {
                            if (boxState.box == null) {
                                _state.value = BoxNewState.Error("La caja no existe")
                            } else if (boxState.box.userId != null) {
                                _state.value =
                                    BoxNewState.Error("La caja ya está asociada a otro usuario")
                            } else {
                                val newBox = boxState.box
                                newBox.userId = user.uid
                                newBox.id = box.id
                                newBox.name = box.name

                                addOrUpdateBoxUseCase.launch(newBox)
                                addOrUpdateBoxUseCase.resultFlow.collect { state ->
                                    _state.value = when (state) {
                                        is AddOrUpdateBoxState.Success -> BoxNewState.Success(state.box)
                                        is AddOrUpdateBoxState.Failure -> BoxNewState.Error(state.message)
                                    }
                                }
                            }
                        }
                        is FetchBoxState.Failure -> {
                            _state.value = BoxNewState.Error(boxState.message)
                        }
                    }
                }
            }

        }
    }
}