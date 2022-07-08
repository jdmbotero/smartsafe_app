package com.smartsafe.smartsafe_app.data.repository.box

import com.smartsafe.smartsafe_app.domain.entity.Box
import kotlinx.coroutines.flow.Flow

interface BoxRepository {
    suspend fun addOrUpdateBox(box: Box): Flow<AddOrUpdateBoxState>
    suspend fun fetchBoxes(userId: String): Flow<FetchBoxesState>
    suspend fun fetchBox(boxId: String): Flow<FetchBoxState>
}