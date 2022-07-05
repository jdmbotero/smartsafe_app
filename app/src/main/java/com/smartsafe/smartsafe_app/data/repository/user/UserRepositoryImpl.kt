package com.smartsafe.smartsafe_app.data.repository.user

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.smartsafe.smartsafe_app.domain.entity.User
import com.smartsafe.smartsafe_app.domain.interactor.GenericResponseState
import com.smartsafe.smartsafe_app.util.Constants.FIRESTORE_USER_COLLECTION
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor() : UserRepository {

    private val firestoreDb: FirebaseFirestore by lazy {
        Firebase.firestore
    }

    companion object {
        const val LOG_TAG = "UserRepository"
    }

    override suspend fun addOrUpdateUser(userId: String, user: User?) =
        callbackFlow {
            firestoreDb.collection(FIRESTORE_USER_COLLECTION).document(userId)
                .set(user ?: User(userId))
                .addOnSuccessListener {
                    Log.d(LOG_TAG, "DocumentSnapshot successfully written!")
                    trySend(GenericResponseState.Success<User>())
                }
                .addOnFailureListener { e ->
                    Log.w(LOG_TAG, "Error writing document", e)
                    trySend(GenericResponseState.Failure(e.message))
                }
            awaitClose()
        }
}