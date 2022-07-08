package com.smartsafe.smartsafe_app.data.repository.authWithPhone

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthWithPhoneRepository {
    suspend fun verifyPhoneNumber(prefix: String, phoneNumber: String): Flow<VerifyPhoneNumberState>
    suspend fun verifyCode(verificationId: String, code: String): Flow<SignInWithPhoneState>
    suspend fun logout()
    fun getCurrentUser(): FirebaseUser?
}