package com.smartsafe.smartsafe_app.data.repository

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class AuthWithPhoneRepositoryImpl @Inject constructor(
    private val application: Application
) : AuthWithPhoneRepository {

    private val auth = FirebaseAuth.getInstance()

    companion object {
        const val LOG_TAG = "LoginService"
    }

    override suspend fun verifyPhoneNumber(phoneNumber: String): Flow<VerifyPhoneNumberState> =
        callbackFlow {
            val verifyPhoneNumberCallbacks =
                object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                        Log.d(LOG_TAG, "onVerificationCompleted: $credential")
                        trySend(VerifyPhoneNumberState.VerificationCompleted)
                        signInWithPhoneAuthCredential(credential)
                    }

                    override fun onVerificationFailed(e: FirebaseException) {
                        Log.w(LOG_TAG, "onVerificationFailed", e)
                        trySend(VerifyPhoneNumberState.VerificationFailed(e.message))
                    }

                    override fun onCodeSent(
                        verificationId: String,
                        token: PhoneAuthProvider.ForceResendingToken
                    ) {
                        Log.d(LOG_TAG, "onCodeSent: $verificationId")
                        trySend(VerifyPhoneNumberState.CodeSent(verificationId, token))
                    }
                }

            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                //.setActivity(this) // Activity (for callback binding)
                .setCallbacks(verifyPhoneNumberCallbacks)
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        }

    override suspend fun verifyCode(
        verificationId: String,
        code: String
    ): Flow<SignInWithPhoneState> =
        signInWithPhoneAuthCredential(PhoneAuthProvider.getCredential(verificationId, code))

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) =
        callbackFlow {
            auth.signInWithCredential(credential)
                .addOnCompleteListener(application.mainExecutor) { task ->
                    if (task.isSuccessful) {
                        Log.d(LOG_TAG, "signInWithCredential: success")
                        trySend(SignInWithPhoneState.Success(task.result?.user))
                    } else {
                        Log.w(LOG_TAG, "signInWithCredential: failure", task.exception)
                        trySend(SignInWithPhoneState.Failure(task.exception?.message))
                        /*if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        }*/
                    }
                }
        }
}