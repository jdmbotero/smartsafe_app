package com.smartsafe.smartsafe_app.presentation.auth.verifyCode

import com.google.firebase.auth.FirebaseUser

sealed class VerifyCodeState {
    object Idle : VerifyCodeState()
    object Loading : VerifyCodeState()
    data class Success(val user: FirebaseUser?) : VerifyCodeState()
    data class Error(val message: String?) : VerifyCodeState()
}