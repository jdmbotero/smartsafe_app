package com.smartsafe.smartsafe_app.data.repository

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthWithPhoneRepository {
    suspend fun verifyPhoneNumber(prefix: String, phoneNumber: String): Flow<VerifyPhoneNumberState>
    suspend fun verifyCode(verificationId: String, code: String): Flow<SignInWithPhoneState>
    fun getCurrentUser(): FirebaseUser?
}