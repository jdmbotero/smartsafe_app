package com.smartsafe.smartsafe_app.data.repository.authWithPhone

import com.google.firebase.auth.FirebaseUser

sealed class SignInWithPhoneState {
    data class Success(val user: FirebaseUser?) : SignInWithPhoneState()
    data class Failure(val message: String? = null, val code: String? = null) :
        SignInWithPhoneState()
}
