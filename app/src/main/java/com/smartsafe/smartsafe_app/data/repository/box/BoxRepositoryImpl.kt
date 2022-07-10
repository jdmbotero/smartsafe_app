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
            box.id?.let {
                firestoreDb.collection(FIRESTORE_BOX_COLLECTION).document(it)
                    .set(box)
                    .addOnSuccessListener {
                        Log.d(LOG_TAG, "DocumentSnapshot successfully written!")
                        trySend(AddOrUpdateBoxState.Success(box))
                    }
                    .addOnFailureListener { e ->
                        Log.w(LOG_TAG, "Error writing document", e)
                        trySend(AddOrUpdateBoxState.Failure(e.message))
                    }
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

    override suspend fun fetchBox(boxId: String): Flow<FetchBoxState> = callbackFlow {
        val docRef = firestoreDb.collection(FIRESTORE_BOX_COLLECTION).document(boxId)
        val registration = docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(LOG_TAG, "Listen failed.", e)
                trySend(FetchBoxState.Failure(e.message))
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                Log.d(LOG_TAG, "Current data: ${snapshot.data}")
                trySend(FetchBoxState.Success(snapshot.toObject(Box::class.java)))
            } else {
                Log.d(LOG_TAG, "Current data: null")
                trySend(FetchBoxState.Success())
            }
        }

        awaitClose {
            registration.remove()
        }
    }
}