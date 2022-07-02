package com.smartsafe.smartsafe_app.presentation.auth.phoneNumber

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartsafe.smartsafe_app.data.repository.VerifyPhoneNumberState
import com.smartsafe.smartsafe_app.domain.interactor.auth.VerifyPhoneNumberUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhoneNumberViewModel @Inject constructor(
    private val verifyPhoneNumberUseCase: VerifyPhoneNumberUseCase
) : ViewModel() {

    val userIntent = Channel<PhoneNumberIntent>(Channel.UNLIMITED)
    private val _state = MutableStateFlow<PhoneNumberState>(PhoneNumberState.Idle)

    val state: StateFlow<PhoneNumberState>
        get() = _state

    init {
        handleIntent()
    }

    private fun handleIntent() {
        viewModelScope.launch {
            userIntent.consumeAsFlow().collect {
                when (it) {
                    is PhoneNumberIntent.VerifyPhoneNumber -> verifyPhoneNumber(
                        it.prefix,
                        it.phoneNumber
                    )
                }
            }
        }
    }

    private suspend fun verifyPhoneNumber(prefix: String, phoneNumber: String) {
        _state.value = PhoneNumberState.Loading
        verifyPhoneNumberUseCase.launch(Pair(prefix, phoneNumber))
        verifyPhoneNumberUseCase.resultFlow.collect {
            _state.value = when (it) {
                is VerifyPhoneNumberState.VerificationCompleted -> PhoneNumberState.SuccessVerification
                is VerifyPhoneNumberState.VerificationFailed -> PhoneNumberState.Error(it.message)
                is VerifyPhoneNumberState.CodeSent -> PhoneNumberState.Success(it.verificationId)
            }
        }
    }
}