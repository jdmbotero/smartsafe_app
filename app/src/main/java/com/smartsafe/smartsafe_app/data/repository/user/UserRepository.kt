package com.smartsafe.smartsafe_app.data.repository.user

import com.smartsafe.smartsafe_app.domain.entity.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun fetchUser(userId: String): Flow<FetchUserState>
    suspend fun addOrUpdateUser(userId: String, user: User?): Flow<AddOrUpdateUserState>
}