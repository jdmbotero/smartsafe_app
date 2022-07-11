package com.smartsafe.smartsafe_app.data.repository.history

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.smartsafe.smartsafe_app.domain.entity.History
import com.smartsafe.smartsafe_app.util.Constants
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor() : HistoryRepository {

    private val firestoreDb: FirebaseFirestore by lazy {
        Firebase.firestore
    }

    companion object {
        const val LOG_TAG = "HistoryRepository"
    }

    override suspend fun fetchHistory(boxId: String): Flow<FetchHistoryState> = callbackFlow {
        val collectionRef = firestoreDb.collection(Constants.FIRESTORE_HISTORY_COLLECTION)
        val registration = collectionRef
            .whereEqualTo("boxId", boxId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(LOG_TAG, "Listen failed.", e)
                    trySend(FetchHistoryState.Failure(e.message))
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    Log.d(LOG_TAG, "Current data: ${snapshot.documents}")
                    trySend(FetchHistoryState.Success(snapshot.toObjects(History::class.java)))
                } else {
                    Log.d(LOG_TAG, "Current data: null")
                    trySend(FetchHistoryState.Success())
                }
            }

        awaitClose {
            registration.remove()
        }
    }
}