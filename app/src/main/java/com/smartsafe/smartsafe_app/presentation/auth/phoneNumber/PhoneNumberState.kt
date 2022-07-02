package com.smartsafe.smartsafe_app.presentation.auth.phoneNumber

sealed class PhoneNumberState {
    object Idle : PhoneNumberState()
    object Loading : PhoneNumberState()
    data class Success(val verificationId: String) : PhoneNumberState()
    object SuccessVerification : PhoneNumberState()
    data class Error(val message: String?) : PhoneNumberState()
}