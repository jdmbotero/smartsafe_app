package com.smartsafe.smartsafe_app.data.repository

import com.google.firebase.auth.PhoneAuthProvider

sealed class VerifyPhoneNumberState {
    object VerificationCompleted : VerifyPhoneNumberState()
    data class VerificationFailed(val message: String? = null) : VerifyPhoneNumberState()
    data class CodeSent(
        val verificationId: String,
        val resendingToken: PhoneAuthProvider.ForceResendingToken
    ) : VerifyPhoneNumberState()
}
