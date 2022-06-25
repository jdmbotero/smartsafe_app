package com.smartsafe.smartsafe_app.presentation.auth.verifyCode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartsafe.smartsafe_app.data.repository.SignInWithPhoneState
import com.smartsafe.smartsafe_app.domain.interactor.auth.VerifyCodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerifyCodeViewModel @Inject constructor(
    private val verifyCodeUseCase: VerifyCodeUseCase
) : ViewModel() {

    val userIntent = Channel<VerifyCodeIntent>(Channel.UNLIMITED)
    private val _state = MutableStateFlow<VerifyCodeState>(VerifyCodeState.Idle)

    val state: StateFlow<VerifyCodeState>
        get() = _state

    init {
        handleIntent()
    }

    private fun handleIntent() {
        viewModelScope.launch {
            userIntent.consumeAsFlow().collect {
                when (it) {
                    is VerifyCodeIntent.VerifyCodeNumber -> verifyCode(it.verificationId, it.code)
                }
            }
        }
    }

    private suspend fun verifyCode(verificationId: String, code: String) {
        _state.value = VerifyCodeState.Loading
        verifyCodeUseCase.launch(Pair(verificationId, code))
        verifyCodeUseCase.resultFlow.collect {
            _state.value = when (it) {
                is SignInWithPhoneState.Success -> VerifyCodeState.Success(it.user)
                is SignInWithPhoneState.Failure -> VerifyCodeState.Error(it.message)
            }
        }
    }
}