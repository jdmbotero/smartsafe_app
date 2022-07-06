package com.smartsafe.smartsafe_app.data.repository.box

import com.smartsafe.smartsafe_app.domain.entity.Box
import com.smartsafe.smartsafe_app.domain.interactor.GenericResponseState
import kotlinx.coroutines.flow.Flow

interface BoxRepository {
    suspend fun addOrUpdateBox(box: Box): Flow<AddOrUpdateBoxState>

    suspend fun fetchBoxes(userId: String): Flow<FetchBoxesState>
}