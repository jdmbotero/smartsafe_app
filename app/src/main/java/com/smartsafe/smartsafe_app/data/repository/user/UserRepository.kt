package com.smartsafe.smartsafe_app.data.repository.user

import com.smartsafe.smartsafe_app.domain.entity.User
import com.smartsafe.smartsafe_app.domain.interactor.GenericResponseState
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun addOrUpdateUser(userId: String, user: User?): Flow<GenericResponseState>
}