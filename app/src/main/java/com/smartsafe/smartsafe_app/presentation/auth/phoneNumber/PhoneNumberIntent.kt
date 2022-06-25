package com.smartsafe.smartsafe_app.presentation.auth.phoneNumber

sealed class PhoneNumberIntent {
    data class VerifyPhoneNumber(val phoneNumber: String) : PhoneNumberIntent()
}