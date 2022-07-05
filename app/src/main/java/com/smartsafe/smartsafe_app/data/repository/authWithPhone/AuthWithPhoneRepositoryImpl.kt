package com.smartsafe.smartsafe_app.data.repository.authWithPhone

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.smartsafe.smartsafe_app.SmartSafeApplication
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class AuthWithPhoneRepositoryImpl @Inject constructor(
    private val application: Application
) : AuthWithPhoneRepository {

    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    companion object {
        const val LOG_TAG = "AuthWithPhoneRepository"
        val isUserLogged = FirebaseAuth.getInstance().currentUser != null
        val currentUser = FirebaseAuth.getInstance().currentUser
    }

    override suspend fun verifyPhoneNumber(
        prefix: String,
        phoneNumber: String
    ): Flow<VerifyPhoneNumberState> =
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

            (application as SmartSafeApplication).currentActivity()?.let {
                val options = PhoneAuthOptions.newBuilder(firebaseAuth)
                    .setPhoneNumber(prefix + phoneNumber)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(it)
                    .setCallbacks(verifyPhoneNumberCallbacks)
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)
            }
            awaitClose()
        }

    override suspend fun verifyCode(
        verificationId: String,
        code: String
    ): Flow<SignInWithPhoneState> =
        signInWithPhoneAuthCredential(PhoneAuthProvider.getCredential(verificationId, code))

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) =
        callbackFlow {
            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(application.mainExecutor) { task ->
                    if (task.isSuccessful) {
                        Log.d(LOG_TAG, "signInWithCredential: success")
                        trySend(SignInWithPhoneState.Success(task.result?.user))
                    } else {
                        Log.w(LOG_TAG, "signInWithCredential: failure", task.exception)
                        trySend(SignInWithPhoneState.Failure(task.exception?.message))
                    }
                }
            awaitClose()
        }

    override fun getCurrentUser(): FirebaseUser? = firebaseAuth.currentUser
}