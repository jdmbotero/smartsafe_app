package com.smartsafe.smartsafe_app.data.repository

import kotlinx.coroutines.flow.Flow

interface AuthWithPhoneRepository {
    suspend fun verifyPhoneNumber(phoneNumber: String): Flow<VerifyPhoneNumberState>
    suspend fun verifyCode(verificationId: String, code: String): Flow<SignInWithPhoneState>
}