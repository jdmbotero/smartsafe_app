package com.smartsafe.smartsafe_app.presentation.auth.verifyCode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartsafe.smartsafe_app.data.repository.authWithPhone.SignInWithPhoneState
import com.smartsafe.smartsafe_app.data.repository.user.AddOrUpdateUserState
import com.smartsafe.smartsafe_app.domain.interactor.auth.VerifyCodeUseCase
import com.smartsafe.smartsafe_app.domain.interactor.user.AddOrUpdateUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerifyCodeViewModel @Inject constructor(
    private val verifyCodeUseCase: VerifyCodeUseCase,
    private val addOrUpdateUserUseCase: AddOrUpdateUserUseCase
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
        verifyCodeUseCase.resultFlow.collect { signInWithPhoneState ->
            when (signInWithPhoneState) {
                is SignInWithPhoneState.Success -> {
                    signInWithPhoneState.user?.let {
                        addOrUpdateUserUseCase.launch(Pair(it.uid, null))
                        addOrUpdateUserUseCase.resultFlow.collect { userResponse ->
                            _state.value = when (userResponse) {
                                is AddOrUpdateUserState.Success -> VerifyCodeState.Success(
                                    signInWithPhoneState.user
                                )
                                is AddOrUpdateUserState.Failure -> VerifyCodeState.Error(
                                    userResponse.message
                                )
                            }
                        }
                    }
                }
                is SignInWithPhoneState.Failure -> _state.value =
                    VerifyCodeState.Error(signInWithPhoneState.message)
            }
        }
    }
}