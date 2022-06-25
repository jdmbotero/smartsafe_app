package com.smartsafe.smartsafe_app.presentation.auth.verifyCode

sealed class VerifyCodeIntent {
    data class VerifyCodeNumber(val verificationId: String, val code: String) : VerifyCodeIntent()
}