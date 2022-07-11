package com.smartsafe.smartsafe_app.data.repository.history

import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    suspend fun fetchHistory(boxId: String): Flow<FetchHistoryState>
}