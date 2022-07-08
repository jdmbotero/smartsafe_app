package com.smartsafe.smartsafe_app.presentation.auth.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartsafe.smartsafe_app.data.repository.authWithPhone.AuthWithPhoneRepositoryImpl
import com.smartsafe.smartsafe_app.data.repository.user.AddOrUpdateUserState
import com.smartsafe.smartsafe_app.data.repository.user.FetchUserState
import com.smartsafe.smartsafe_app.domain.entity.User
import com.smartsafe.smartsafe_app.domain.interactor.auth.LogoutUseCase
import com.smartsafe.smartsafe_app.domain.interactor.user.AddOrUpdateUserUseCase
import com.smartsafe.smartsafe_app.domain.interactor.user.FetchUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val fetchUserUseCase: FetchUserUseCase,
    private val addOrUpdateUserUseCase: AddOrUpdateUserUseCase
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
                    is ProfileIntent.FetchUser -> fetchUser()
                    is ProfileIntent.UpdateUser -> updateUser(it.user)
                }
            }
        }
    }

    private suspend fun fetchUser() {
        AuthWithPhoneRepositoryImpl.currentUser?.let {
            _state.value = ProfileState.Loading
            fetchUserUseCase.launch(it.uid)
            fetchUserUseCase.resultFlow.collect { state ->
                _state.value = when (state) {
                    is FetchUserState.Success -> ProfileState.FetchUserSuccess(state.user)
                    is FetchUserState.Failure -> ProfileState.Error(state.message)
                }
            }
        }
    }

    private suspend fun updateUser(user: User) {
        AuthWithPhoneRepositoryImpl.currentUser?.let {
            _state.value = ProfileState.Loading
            user.id = it.uid
            addOrUpdateUserUseCase.launch(Pair(it.uid, user))
            addOrUpdateUserUseCase.resultFlow.collect { state ->
                _state.value = when (state) {
                    is AddOrUpdateUserState.Success -> ProfileState.UpdateUserSuccess
                    is AddOrUpdateUserState.Failure -> ProfileState.Error(state.message)
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