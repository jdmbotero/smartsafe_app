package com.smartsafe.smartsafe_app.data.repository.box

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.smartsafe.smartsafe_app.domain.entity.Box
import com.smartsafe.smartsafe_app.util.Constants.FIRESTORE_BOX_COLLECTION
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class BoxRepositoryImpl @Inject constructor() : BoxRepository {

    private val firestoreDb: FirebaseFirestore by lazy {
        Firebase.firestore
    }

    companion object {
        const val LOG_TAG = "UserRepository"
    }

    override suspend fun addOrUpdateBox(box: Box): Flow<AddOrUpdateBoxState> =
        callbackFlow {
            firestoreDb.collection(FIRESTORE_BOX_COLLECTION).document(box.id!!)
                .set(box)
                .addOnSuccessListener {
                    Log.d(LOG_TAG, "DocumentSnapshot successfully written!")
                    trySend(AddOrUpdateBoxState.Success(box))
                }
                .addOnFailureListener { e ->
                    Log.w(LOG_TAG, "Error writing document", e)
                    trySend(AddOrUpdateBoxState.Failure(e.message))
                }
            awaitClose()
        }

    override suspend fun fetchBoxes(userId: String): Flow<FetchBoxesState> =
        callbackFlow {
            firestoreDb.collection(FIRESTORE_BOX_COLLECTION)
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener {
                    Log.d(LOG_TAG, "DocumentSnapshot successfully written!")
                    trySend(FetchBoxesState.Success(it.toObjects(Box::class.java)))
                }
                .addOnFailureListener { e ->
                    Log.w(LOG_TAG, "Error writing document", e)
                    trySend(FetchBoxesState.Failure(e.message))
                }
            awaitClose()
        }
}