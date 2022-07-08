package com.smartsafe.smartsafe_app.presentation.auth.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartsafe.smartsafe_app.domain.interactor.auth.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    val userIntent = Channel<ProfileIntent>(Channel.UNLIMITED)
    private val _state = MutableStateFlow<ProfileState>(ProfileState.Idle)

    val state: StateFlow<ProfileState>
        get() = _state

    init {
        handleIntent()
    }

    private fun handleIntent() {
        viewModelScope.launch {
            userIntent.consumeAsFlow().collect {
                when (it) {
                    is ProfileIntent.Logout -> logout()
                    is ProfileIntent.UpdateUser -> {}
                }
            }
        }
    }

    private suspend fun logout() {
        _state.value = ProfileState.Loading
        logoutUseCase.launch()
        logoutUseCase.resultFlow.collect { state ->
            _state.value = state
        }
    }
}